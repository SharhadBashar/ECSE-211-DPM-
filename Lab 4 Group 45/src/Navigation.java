//Sharhad Bashar
//Ecse 211
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.robotics.RegulatedMotor;
import lejos.util.Delay;

public class Navigation {
	final static double FAST = 200, SLOW = 50, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.5;
	private Odometer odometer;
	private TwoWheeledRobot R2D2;
	private final double ERROR_IN_POSITION=1, ERROR_IN_ANGLE=0.1;
	private static boolean isNavigating= false;
	private double curTheta;
	
	
	//constructor
	public Navigation(Odometer odo) {
		this.odometer = odo;

		this.R2D2 = odo.getTwoWheeledRobot();


		// set acceleration
		R2D2.setAcceleration(ACCELERATION);
	}
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getTheta())) * distance, Math.cos(Math.toRadians(this.odometer.getTheta())) * distance);

	}
	//travel to a point in space
	public void travelTo(double x, double y) {
		double theta;
		LCD.drawInt((int) x, 0, 3);
		LCD.drawInt((int) y, 0, 4);

		isNavigating = true; // for the running method

		theta = findTheta(x, y); // find theta
		if (Math.abs(odometer.getTheta() - theta) > DEG_ERR) {
			turnTo(theta);//turn to the right theta
		}
		try { // short wait period to minimize error
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//both motors travelling straight
		R2D2.setForwardSpeed(FAST);

		// go forward until we arrive at destination
		while (true) {
			LCD.drawString(String.valueOf(odometer.getX() - x), 4, 5);
			LCD.drawString(String.valueOf(odometer.getY() - y), 4, 6);
			if (Math.abs(odometer.getX() - x) < CM_ERR
					&& Math.abs(odometer.getY() - y) < CM_ERR) {
				Sound.beep();
		
				// will break loop when at destination
				break;
			}
		}				

		//stop motors
		R2D2.stop();
		try {Thread.sleep(500);} 
		catch (InterruptedException e) {}
	}

	
	 
	 

	public void turnTo(double theta) {
		isNavigating = true;
		
		theta=shortT(theta); //make theta the amount to change compared to the current theta
		LCD.drawInt((int) odometer.getTheta(), 0, 6);
		//turn the theta amount
		R2D2.getLeftMotor().rotate(convertAngle(R2D2.RADIUS, R2D2.WIDTH, theta), true);
		R2D2.getRightMotor().rotate(-convertAngle(R2D2.RADIUS, R2D2.WIDTH, theta), false);

		R2D2.stop();
		isNavigating = false;
		try {Thread.sleep(500);} 
		catch (InterruptedException e) {}
	}
	
	public void turnOf(double theta) {
		isNavigating = true;
		R2D2.setRotationSpeed(SLOW);
//turn the robot of theta
		R2D2.getLeftMotor().rotate(convertAngle(R2D2.RADIUS, R2D2.WIDTH, theta), true);
		R2D2.getRightMotor().rotate(-convertAngle(R2D2.RADIUS, R2D2.WIDTH, theta), false);

		R2D2.stop();
		isNavigating = false;
		try {Thread.sleep(500);} 
		catch (InterruptedException e) {}
	}

	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public boolean isNavigating() {
		return isNavigating;
	}

	//find the theta we need to go to a position from looking at 0
	public double findTheta(double x, double y) {
		double theta, curX, curY, dX, dY;
		curX = odometer.getX();
		curY = odometer.getY();
		dX = x - curX;
		dY = y - curY;
		if (dX == 0) {
			dX = 0.1;
		}
		if (dY == 0) {
			dY = 0.1;
		}

		if (dY > 0) {
			theta = Math.toDegrees(Math.atan(dX / dY));
		}

		else if ((dY < 0) && (dX < 0)) {
			theta = Math.toDegrees(Math.atan(dX / dY) - Math.PI);
		} else {
			theta = Math.toDegrees(Math.atan(dX / dY) + Math.PI);
		}


		return theta;

	}
//get the shortest theta to turn
	public double shortT(double theta) {
		double thetDer = theta - odometer.getTheta();

		if (thetDer < -180) {
			return thetDer + 360;
		} else if (thetDer > 180) {
			return thetDer - 360;
		} else {
			return thetDer;
		}

	}
}