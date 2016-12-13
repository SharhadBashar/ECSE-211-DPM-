//Sharhad Bashar
//Ecse 211
import lejos.nxt.Motor;

/*
 * Odometer.java
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	public static int lastTachoL;  /* Tacho L at last sample */
	public static int lastTachoR;  /* Tacho R at last sample */
	public static int nowTachoL;/* Current tacho L */
	public static int nowTachoR;  /* Current tacho R */



	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		Motor.A.resetTachoCount(); 
		Motor.C.resetTachoCount(); 
		Motor.A.flt(); 
		Motor.C.flt(); 
		lastTachoL=Motor.A.getTachoCount(); 
		lastTachoL=Motor.C.getTachoCount();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				double distL, distR, deltaD, deltaT,dX,dY;
				double WR=2.05;
				double WB=15.3;
				nowTachoL = Motor.A.getTachoCount(); /* get tacho counts */
				nowTachoR = Motor.C.getTachoCount();
				distL = Math.PI*WR*(nowTachoL-lastTachoL)/180;
				distR = Math.PI*WR*(nowTachoR-lastTachoR)/180;
				lastTachoL=nowTachoL;
				lastTachoR=nowTachoR;
				deltaD = 0.5*(distL+distR);
				deltaT = (distL-distR)/WB;
				theta += deltaT;
				dX = deltaD * Math.sin(theta);
				dY = deltaD * Math.cos(theta);
				x = x + dX;
				y = y + dY;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}