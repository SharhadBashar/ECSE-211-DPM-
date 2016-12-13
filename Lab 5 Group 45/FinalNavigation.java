//Sharhad Bashar
//Ecse 211
import java.util.ArrayList;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

public class FinalNavigation {
	private static int x;
	private static int y;
	private static int t;

	private static int initialX;
	private static int initialY;
	private static int initialT;

	private static double distance;

	private static Map mapClass = new Map();
	private static boolean[][] wallMap = mapClass.wallMap();

	private static ArrayList<Integer> directions = new ArrayList<Integer>();

	private static TwoWheeledRobot Walle = new TwoWheeledRobot(Motor.A, Motor.B);
	private static Odometer odometer = new Odometer(Walle, true);

	private static Navigation navigation = new Navigation(odometer);

	private static Orienteer orientate = new Orienteer();
//Sharhad Bashar
//Micheal Abdallah
	// executes navigation to final location
	public static void run() {

		calculate();

		// begin by facing robot north
		navigation.turnTo(0, odometer.getAngle());

		// robot travels path based on path taken to reach current location
		for (int i = 0; i < directions.size(); i++) {
			if (directions.get(i) == 0) {
				navigation.turnTo(0, odometer.getAngle());
				navigation.travelThirty();
			} else if (directions.get(i) == 1) {
				navigation.turnTo(270, odometer.getAngle());
				navigation.travelThirty();
			} else if (directions.get(i) == 2) {
				navigation.turnTo(180, odometer.getAngle());
				navigation.travelThirty();
			} else {
				navigation.turnTo(90, odometer.getAngle());
				navigation.travelThirty();
			}
		}

		// robot ends facing north
		navigation.turnTo(0, odometer.getAngle());

		// displays initial orientation
		LCD.clear();
		boolean go = true;
		while (go) {
			LCD.drawString("Initial X: " + initialX, 0, 1);
			LCD.drawString("Initial Y: " + initialY, 0, 2);
			LCD.drawString("Initial H: " + initialT, 0, 3);
			LCD.drawString("75,75,0", 0, 5);//final position, which is known to be 75,75,0
		}

	}

	// calculates path needed to travel to final location
	// based on path taken to reach current location
	public static void calculate() {

		Orienteer determineInitial = new Orienteer();

		// grabs initial orientation from Orienteer class
		x = determineInitial.getX();
		y = determineInitial.getY();
		t = determineInitial.getT();

		// converts x and y values to actual coordinates of final values
		x = (3 - x) * 30 - 15;
		y = y * 30 - 15;

		// swaps x and y values because y represented columns and x represented
		// rows
		// in initial calculations
		int temp;
		temp = x;
		x = y;
		y = temp;

		initialX = x;
		initialY = y;

		// converts t to actual degrees for odometer
		if (t == 0) {
			initialT = 0;
		} else if (t == 1) {
			initialT = 270;
		} else if (t == 2) {
			initialT = 180;
		} else {
			initialT = 90;
		}

		String pathString = orientate.getPath();
		String[] pathArray = new String[pathString.length()];

		// use path taken to reach current location to determine path needed to
		// go to final location

		for (int a = 0; a < pathString.length(); a++) {
			pathArray[a] = pathString.charAt(a) + "";
		}

		for (int b = 0; b < pathArray.length; b++) {
			if (pathArray[b].equals("T")) {
				rotateCCW();
			} else {
				travelThirty();
			}
		}

		odometer.setX(x);
		odometer.setY(y);

		if (t == 0) {
			odometer.setAngle(0);
		} else if (t == 1) {
			odometer.setAngle(270);
		} else if (t == 2) {
			odometer.setAngle(180);
		} else {
			odometer.setAngle(90);
		}

		// simulates path needed to go to final destination
		if (t == 1) {
			rotateCW();
			calculateDistance();
		} else if (t == 2) {
			rotateCW();
			calculateDistance();
			rotateCW();
			calculateDistance();
		} else if (t == 3) {
			rotateCCW();
			calculateDistance();
		} else {
			// do nothing
		}

		boolean finished = false;

		// determines actual path needed to reach final destination
		while (finished == false) {

			calculateDistance();

			// if see no wall north, go north
			if (distance != 5) {
				directions.add(t);
				travelThirty();
				calculateDistance();
			}
			// if see wall north, go east
			else {
				rotateCW();
				calculateDistance();

				if (distance != 5) {
					directions.add(t);
					travelThirty();
					calculateDistance();
					rotateCCW();
					calculateDistance();
				}
				// if after going east there is still wall, go west instead
				else {
					rotateCCW();
					calculateDistance();
					rotateCCW();
					calculateDistance();
					directions.add(t);
					travelThirty();
					calculateDistance();
					rotateCW();
					calculateDistance();

					// if after going west and still wall north, go west again
					if (distance == 5) {
						rotateCCW();
						directions.add(t);
						travelThirty();
						rotateCW();
					}
				}
			}

			if (x == 75 && y == 75) {
				finished = true;
			}

		}

	}

	// determines new angle after counter-clockwise rotation
	public static void rotateCCW() {
		if (t == 3) {
			t = 0;
		} else {
			t++;
		}
	}

	// determines new angle after clockwise rotation
	public static void rotateCW() {
		if (t == 0) {
			t = 3;
		} else {
			t--;
		}
	}

	// determines new location after traveling 30 cm
	public static void travelThirty() {
		if (t == 0) {
			y += 30;
		} else if (t == 1) {
			x -= 30;
		} else if (t == 2) {
			y -= 30;
		} else {
			x += 30;
		}

	}

	// when printing simulation, displays corresponding arrow based on
	// orientation
	public static char arrow(int n) {
		if (n == 0) {
			return '^';
		} else if (n == 1) {
			return '<';
		} else if (n == 2) {
			return 'v';
		} else {
			return '>';
		}
	}

	// simulates distance read by robot
	// if wall, distance is 5
	// if no wall, distance is 50
	// values chosen arbitrarily below and above 35
	public static void calculateDistance() {

		int xLook = (y + 15) / 30;
		xLook = 3 - xLook;
		int yLook = (x + 15) / 30;

		if (t == 0) {
			xLook--;
		} else if (t == 1) {
			yLook--;
		} else if (t == 2) {
			xLook++;
		} else {
			yLook++;
		}

		if ((xLook == 0 && yLook == 0) || (xLook == 1 && yLook == 2)
				|| (xLook == 1 && yLook == 3) || (xLook == 3 && yLook == 1)
				|| (xLook == -1) || (xLook == 4) || (yLook == -1)
				|| (yLook == 4)) {
			distance = 5;
		} else {
			distance = 50;
		}
	}

}