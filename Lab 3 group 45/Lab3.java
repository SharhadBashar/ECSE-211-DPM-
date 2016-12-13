//Sharhad Bashar
//Ecse 211
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;


public class Lab3 {

	public static void main(String[] args) {
			int buttonChoice;

			// some objects that need to be instantiated
			Odometer odometer = new Odometer();
			OdometryDisplay odDis = new OdometryDisplay(odometer);

			//OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
			final weirdLoop wL = new weirdLoop(odometer); //Create a new object that will travel the first loop with four coordinates
			final Double[][] positions = new Double[][] {{60.0,30.0},{30.0,30.0},{30.0,60.0},{60.0,0.0}}; //Putting the four coordinates in a double array
			final Double[][] avoid = new Double[][] {{0.0,60.0},{60.0,0.0}};//putting the two coordinates for the second path in a double array

			do {
				// clear the display
				LCD.clear();

				// ask the user whether the motors should drive in a square or float
				LCD.drawString("< Left | Right >", 0, 0);
				LCD.drawString("       |        ", 0, 1);
				LCD.drawString(" Avoid | Do a  ", 0, 2);
				LCD.drawString("Object | weird  ", 0, 3);
				LCD.drawString("       | loop ", 0, 4);

				buttonChoice = Button.waitForAnyPress();
			} while (buttonChoice != Button.ID_LEFT
					&& buttonChoice != Button.ID_RIGHT);

			if (buttonChoice == Button.ID_LEFT) {
				for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
					motor.forward();
					motor.flt();
				}

				// start only the odometer and the odometry display
				odometer.start();
				odDis.start();
				
				(new Thread() {
					public void run() {
						wL.drive(avoid);
					}
				}).start();
				
			} else {
				// start the odometer and the odometry display
				odometer.start();
				odDis.start();
				

				// spawn a new Thread to avoid SquareDriver.drive() from blocking
				(new Thread() {
					public void run() {
						wL.drive(positions);
					}
				}).start();
			}
			
			while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);
		}
	}