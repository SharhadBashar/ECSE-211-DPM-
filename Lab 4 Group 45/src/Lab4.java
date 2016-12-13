//Sharhad Bashar
//Ecse 211
import lejos.nxt.*;
import lejos.nxt.ColorSensor.Color;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot R2D2 = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(R2D2, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		ColorSensor cs = new ColorSensor(SensorPort.S4,Color.RED);
		Navigation nav = new Navigation(odo);
		// perform the ultrasonic localization
		
		int buttonChoice = 0;
		while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT){
			// clear the display
			LCD.clear();

			// ask the user whether the car should use the UV sensor to find pos Y Axis
			//Or  use the light sensor to find a defined position
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" UV    | UV And  ", 0, 2);
			LCD.drawString("Locali | Light" , 0, 3);
			LCD.drawString("       | Locali" , 0, 4);

			buttonChoice = Button.waitForAnyPress();
		}
		LCDInfo lcd = new LCDInfo(odo);
		//If left button is selected, 
		if (buttonChoice == Button.ID_LEFT) {

			LCD.clear();
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE,nav);
			usl.doLocalization();
		}
		else {
			USLocalizer usl1 = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE,nav);
			usl1.doLocalization();
			buttonChoice=0;

			LightLocalizer ll = new LightLocalizer(odo, cs,nav);
			ll.doLocalization();
			
		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
	}

	}
