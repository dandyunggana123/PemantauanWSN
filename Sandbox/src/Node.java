
import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.preon32.cpu.CPUConstants;
import com.virtenio.preon32.cpu.CPUHelper;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.radio.ieee_802_15_4.Frame;

public class Node {

	private int COMMON_CHANNEL = 16;
	private int COMMON_PANID = 0xCACA;
	private int BASE = 0xAFFE; // BASE_station
	private int NODE; // node receiver
	private final int allAddress[] = new int[] { 0xAAAA, 0xBBBB, 0xCCCC, 0xDDDD, 0xEEEE, 0xBABA, 0xCACA, 0xDADA, 0xBABE,
			0xCACE, 0xDADE, 0xBEBE, 0xCECE, 0xDEDE, 0xBEBA, 0xCECA, 0xDEDA, 0xBFBF, 0xCFCF, 0xDFDF, 0xBAB1, 0xCAC1,
			0xDAD1, 0xBEB1, 0xCEC1, 0xDED1, 0xBFB1, 0xCFC1, 0xDFD1, 0xBAB2, 0xCAC2, 0xDAD2, 0xBEB2, 0xCEC2, 0xDED2,
			0xBFB2, 0xCFC2, 0xDFD2, 0xBAB3, 0xCAC3, 0xDAD3, 0xBEB3, 0xCEC3, 0xDED3, 0xBFB3, 0xCFC3 };

	public static void main(String[] args) throws Exception {
		new Node().run();
	}

	public void run() throws Exception {
		// alamat node yang akan diupload harus diinput secara manual misal node pertama:
		// NODE=allAddress[0] selanjutnya node kedua yang akan diupload: NODE=allAddress[1]
		NODE = allAddress[32];
		final AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(NODE);

		Thread t = new Thread() {
			public void run() {
				Sensing st = new Sensing();
				while (true) {
					Frame f = null;
					try {
						f = new Frame();
						radio.setState(AT86RF231.STATE_RX_AACK_ON);
						radio.waitForFrame(f);
					} catch (Exception e) {
					}
					if (f != null) {
						byte[] dg = f.getPayload();
						//mengubah pesan dari byte menjadi string
						String str = new String(dg, 0, dg.length);

						String name = "";
						String order = "";
						String message = "";
						boolean done = false;
						while (!done) {
							try {
								if (!str.equalsIgnoreCase("sense")) {
									name = str.substring(0, 4);
									order = str.substring(5);
									if (name.equalsIgnoreCase(Integer.toHexString(NODE))) {
										Thread.sleep(88);
										message = order;
									} else {
										message = st.run();
									}
								} else {
									message = st.run();
								}
								// ///////////////////////////////////////////////////////////////////////
								Frame frame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST | Frame.DST_ADDR_16
										| Frame.INTRA_PAN | Frame.SRC_ADDR_16);
								frame.setSrcAddr(NODE);
								frame.setSrcPanId(COMMON_PANID);
								frame.setDestAddr(BASE);
								frame.setDestPanId(COMMON_PANID);
								radio.setState(AT86RF231.STATE_TX_ARET_ON);
								frame.setPayload(message.getBytes()); // penting
								radio.transmitFrame(frame);
								System.out.println(message);
								// ///////////////////////////////////////////////////////////////////////
								done = true;
							} catch (Exception e) {
							}
						}
						if (name.equalsIgnoreCase(Integer.toHexString(NODE))) {
							try {
								if (order.equalsIgnoreCase("stop")) {
									CPUHelper.setPowerState(CPUConstants.V_POWER_STATE_OFF, Long.MAX_VALUE);
								} else if (order.equalsIgnoreCase("restart")) {
									CPUHelper.setPowerState(CPUConstants.V_POWER_STATE_OFF, 4000);

								}
							} catch (Exception e) {
							}
						}
					}
				}
			}

		};
		t.start();
	}

}
