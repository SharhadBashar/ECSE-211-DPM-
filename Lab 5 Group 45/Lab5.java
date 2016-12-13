//Sharhad Bashar
//Ecse 211

import lejos.nxt.*;

public class Lab5 {

	private static Orienteer orientate = new Orienteer();
	private static FinalNavigation navigateToNorthEast = new FinalNavigation();
	
	private static boolean finishedLocalization = false;
	private static boolean finishedNavigation = false;
	
	public void setFinishedLocalization(boolean trueOrFalse) {
		this.finishedLocalization = trueOrFalse;
	}
	public void setFinishedNavigation(boolean trueOrFalse) {
		this.finishedNavigation = trueOrFalse;
	}
	
	
	public static void main(String[] args) {
		int buttonChoice;
		
		do {
			// clear the display
			LCD.clear();

			LCD.drawString(" Hi, I'm Wall-E ", 0, 0);
			LCD.drawString("        |       ", 0, 1);
			LCD.drawString("  Left  | Right ", 0, 2);
			LCD.drawString("        |       ", 0, 3);
			LCD.drawString("  Stoch |Determ ", 0, 4);
			LCD.drawString("  astic |inistic", 0, 5);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		if (buttonChoice == Button.ID_RIGHT){
			
			//DETERMINISTIC	
			
			//allows robot to run until determines initial position
			//boolean input indicates need to travel 360 before doing deterministic algorithm
			//in case there is location identical to another location
			orientate.run(true);
			
			//allows robot to travel to final destination
			navigateToNorthEast.run();
			
		} else {
			
			//STOCHASTIC
			
			//allows robot to run until determines initial position
			//no need to travel 360, thats why its false
			//causes the Orienteer class to run random turn/move method
			orientate.run(false);
			
			//allows robot to travel to final destination
			navigateToNorthEast.run();
			
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
	}

}
