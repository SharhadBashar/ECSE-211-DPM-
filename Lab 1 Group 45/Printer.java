//Sharhad Bashar
//Ecse 211
import lejos.nxt.Button;
import lejos.nxt.LCD;

public class Printer extends Thread {
	
	private UltrasonicController cont;
	private final int option;
	
	public Printer(int option, UltrasonicController cont) {
		this.cont = cont;
		this.option = option;
	}
	
	public void run() {
		while (true) {
			LCD.clear();
			LCD.drawString("Controller Type is... ", 0, 0);
			if (this.option == Button.ID_LEFT)
				LCD.drawString("BangBang", 0, 1);
			else if (this.option == Button.ID_RIGHT)
				LCD.drawString("P type", 0, 1);
			LCD.drawString("US Distance: " + cont.readUSDistance(), 0, 2 );
						
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public static void printMainMenu() {
		LCD.clear();
		LCD.drawString("HI, I'm Wall-E", 0, 0); //Adds a few more lines on the LCD screen
		LCD.drawString("Left = BangBang",  0, 2);
		LCD.drawString("Right = P Type", 0, 3);
	}
}
