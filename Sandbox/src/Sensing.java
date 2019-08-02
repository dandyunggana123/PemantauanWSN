


import com.virtenio.driver.device.ADT7410;
import com.virtenio.driver.device.MPL115A2;
import com.virtenio.driver.device.SHT21;
import com.virtenio.driver.gpio.GPIO;
import com.virtenio.driver.gpio.NativeGPIO;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.NativeI2C;


public class Sensing {
	private NativeI2C i2c;
	private ADT7410 temperatureSensor;
	private boolean open;
	private SHT21 sht21;
	private MPL115A2 pressureSensor;

	private void init() throws Exception {
		System.out.println("I2C(Init)");
		i2c = NativeI2C.getInstance(1);
		i2c.open(I2C.DATA_RATE_400);
		
		System.out.println("ADT7410(Init)");
		temperatureSensor = new ADT7410(i2c, ADT7410.ADDR_0, null, null);
		temperatureSensor.open();
		temperatureSensor.setMode(ADT7410.CONFIG_MODE_CONTINUOUS);
		
		sht21 = new SHT21(i2c);
		sht21.open();
		sht21.setResolution(SHT21.RESOLUTION_RH12_T14);
		
		GPIO resetPin = NativeGPIO.getInstance(24);
		GPIO shutDownPin = NativeGPIO.getInstance(12);
		
		pressureSensor = new MPL115A2(i2c, resetPin, shutDownPin);
		pressureSensor.open();
		pressureSensor.setReset(false);
		pressureSensor.setShutdown(false);

		System.out.println("Done(Init)");
		open=true;
	}

	public String run() throws Exception {
		if(!open) {
			init();
		}
		String res="";
			try {
				//suhu
				float celsius = temperatureSensor.getTemperatureCelsius();

				//humidity
				sht21.startRelativeHumidityConversion();
				Thread.sleep(SHT21.MAX_HUMIDITY_CONVERSION_TIME_R12);
				int rawRH = sht21.getRelativeHumidityRaw();
				float humidity = SHT21.convertRawRHToRHw(rawRH);
				
				//Tekanan udara
				pressureSensor.startBothConversion();
				Thread.sleep(MPL115A2.BOTH_CONVERSION_TIME);
				int pressurePr = pressureSensor.getPressureRaw();
				int raw = pressureSensor.getTemperatureRaw();
				float pressure = pressureSensor.compensate(pressurePr, raw);
				
				res=celsius+"#"+humidity+"#"+pressure;
				
			} catch (Exception e) {
				System.out.println("sensing failed");
			}
			return res;
	}
}
