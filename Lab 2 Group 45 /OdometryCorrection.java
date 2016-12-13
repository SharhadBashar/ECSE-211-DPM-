//Sharhad Bashar
//Ecse 211
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.ColorSensor.*;
import lejos.util.Delay;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor colSens;
	private int counterX = 0;
	private int counterY = 0;
	private double error = 0.2;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		colSens = new ColorSensor(SensorPort.S1, Color.RED);
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) { // Check the values
			correctionStart = System.currentTimeMillis();
			LCD.drawInt(counterX, 0, 4);
			LCD.drawInt(counterY, 0, 5);
			LCD.drawInt(colSens.getNormalizedLightValue(), 0, 6);

			// Check if there is a black line
			if (colSens.getNormalizedLightValue() < 500) {
				Delay.msDelay(100); // Delay if line found
				Sound.beep();
				if (Math.cos(odometer.getTheta()) <= 1 + error
						&& Math.cos(odometer.getTheta()) >= 1 - error) { // Look
																			// for
																			// direction
					odometer.setY(15 + counterY * 30 - 6); // Setting the Y
															// coordinate of the
															// line for the
															// robot
					counterY++; // increment beacuse going away from origin
					LCD.drawInt(counterX, 0, 4);
					LCD.drawInt(counterY, 0, 5);

				}

				if (Math.sin(odometer.getTheta()) <= 1 + error
						&& Math.sin(odometer.getTheta()) >= 1 - error) {
					odometer.setX(15 + counterX * 30 - 6); // Setting the X
															// coordinate of the
															// line for the
															// robot
					counterX++; // increment beacuse going away from origin

					LCD.drawInt(counterX, 0, 4);
					LCD.drawInt(counterY, 0, 5);
				}

				if (Math.cos(odometer.getTheta()) <= -1 + error
						&& Math.cos(odometer.getTheta()) >= -1 - error) {
					counterY--; // decrement beacuse going away from origin
					odometer.setY(15 + counterY * 30 + 6); // Setting the Y
															// coordinate of the
															// line for the
															// robot
					LCD.drawInt(counterX, 0, 4);
					LCD.drawInt(counterY, 0, 5);

				}
				if (Math.sin(odometer.getTheta()) <= -1 + error
						&& Math.sin(odometer.getTheta()) >= -1 - error) {

					counterX--; // decrement beacuse going away from origin
					odometer.setX(15 + counterX * 30 + 6); // Setting the X
															// coordinate of the
															// line for the
															// robot
					LCD.drawInt(counterX, 0, 4);
					LCD.drawInt(counterY, 0, 5);

				}

			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}