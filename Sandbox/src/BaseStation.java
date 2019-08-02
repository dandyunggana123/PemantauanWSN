
import java.io.OutputStream;

import com.virtenio.driver.device.at86rf231.AT86RF231;

import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.preon32.examples.common.USARTConstants;
import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.vm.Time;

import com.virtenio.driver.usart.NativeUSART;
import com.virtenio.driver.usart.USART;
import com.virtenio.driver.usart.USARTParams;

public class BaseStation {

	private int COMMON_CHANNEL = 16;
	private int COMMON_PANID = 0xCACA;
	private int BASE = 0xAFFE; // BASE station
	private int BROADCAST = 0xFFFF;
	private final int[] allAddress = { 0xAAAA, 0xBBBB, 0xCCCC, 0xDDDD, 0xEEEE, 0xBABA, 0xCACA, 0xDADA, 0xBABE, 0xCACE,
			0xDADE, 0xBEBE, 0xCECE, 0xDEDE, 0xBEBA, 0xCECA, 0xDEDA, 0xBFBF, 0xCFCF, 0xDFDF, 0xBAB1, 0xCAC1, 0xDAD1,
			0xBEB1, 0xCEC1, 0xDED1, 0xBFB1, 0xCFC1, 0xDFD1, 0xBAB2, 0xCAC2, 0xDAD2, 0xBEB2, 0xCEC2, 0xDED2, 0xBFB2,
			0xCFC2, 0xDFD2, 0xBAB3, 0xCAC3, 0xDAD3, 0xBEB3, 0xCEC3, 0xDED3, 0xBFB3, 0xCFC3 };

	private static USART usart;
	private AT86RF231 radio;
	private static OutputStream out;

	public static void main(String[] args) throws Exception {
		try {
			BaseStation.useUSART();
			out = usart.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new BaseStation().run();
	}

	public void run() throws Exception {
		radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(BASE);
		// terdapat thread pada kedua method send() dan receive()
		send();
		receive();
	}

	// method untuk mengirim pesan ke node sensor untuk restart, turn off, ataupun
	// melakukan sensing
	private void send() {
		Thread t = new Thread() {
			public void run() {
				String msg = "sense";
				int choice;
				while (true) {
					try {
						choice = BaseStation.usart.read();
						if (choice == 50) {
							msg = "sense";
						} else if (choice < 50) {
							msg = Integer.toHexString(allAddress[choice]) + "#" + "stop";
						} else if (choice > 99) {
							msg = Integer.toHexString(allAddress[choice - 100]) + "#" + "restart";
						}
						String message = msg;
						// ///////////////////////////////////////////////////////////////////////
						Frame frame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST | Frame.DST_ADDR_16
								| Frame.INTRA_PAN | Frame.SRC_ADDR_16);
						// mengisi source address dan destination address pada frame
						frame.setSrcAddr(BASE);
						frame.setSrcPanId(COMMON_PANID);
						frame.setDestAddr(BROADCAST);
						frame.setDestPanId(COMMON_PANID);
						// state radio untuk mengirim pesan
						radio.setState(AT86RF231.STATE_TX_ARET_ON);
						frame.setPayload(message.getBytes()); // penting untuk mengantar paket ke frame
						// radio mengantar frame menuju alamat broadcast
						radio.transmitFrame(frame);
						// ///////////////////////////////////////////////////////////////////////
					} catch (Exception e) {
					}
				}
			}
		};
		t.start();
	}

	// method ini menunggu pesan dari node-node sensor yang nantinya pesan tersebut
	// akan dikirim ke pc(GUI)
	private void receive() {
		Thread t = new Thread() {
			public void run() {
				String toPC, str, hex_addr;
				long timeRaw;
				byte[] dg;
				while (true) {
					Frame f = null;
					try {
						f = new Frame();
						// state radio menerima pesan
						radio.setState(AT86RF231.STATE_RX_AACK_ON);
						radio.waitForFrame(f, 0);
						if (f != null) {
							timeRaw = Time.currentTimeMillis();
							dg = f.getPayload();
							//mengubah pesan dari byte menjadi string
							str = new String(dg, 0, dg.length);
							//mengambil nilai alamat dan diubah menjadi bentuk string untuk dijadikan nama
							hex_addr = Integer.toHexString((int) f.getSrcAddr());

							toPC = hex_addr + "#" + str + "#" + timeRaw + "%";
							out.write(toPC.getBytes(), 0, toPC.length());
						}
					} catch (Exception e) {
					}
				}
			}
		};
		t.start();
	}

	public static void useUSART() throws Exception {
		usart = configUSART();
	}

	private static USART configUSART() throws Exception {
		int instanceID = 0;
		USARTParams params = USARTConstants.PARAMS_115200;
		NativeUSART usart = NativeUSART.getInstance(instanceID);
		try {
			usart.close();
			usart.open(params);
			return usart;
		} catch (Exception e) {
			return null;
		}
	}
}
