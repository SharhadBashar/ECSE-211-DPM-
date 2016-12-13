//Sharhad Bashar
//Ecse 211

import lejos.nxt.*;
import lejos.util.Delay;


public class weirdLoop extends Thread {
	private final int FORWARD_SPEED = 200; //forward wheel speed
	private final int ROTATE_SPEED = 100;	//Rotate speed
	private final int FILTER_OUT = 20;	
	private final double radius = 2.01;	//Wheel radius
	private final double width = 15.3;	//Robot width
	private NXTRegulatedMotor leftMotor = Motor.A;	//Declaring the motors
	private NXTRegulatedMotor centerMotor = Motor.B;
	private NXTRegulatedMotor rightMotor = Motor.C;
	private Odometer odometer;
	private boolean isRunning = false; //Start off isRunning as false
	private double posEr = 3;	//Error in position
	private double turnEr = 5;	//Error in turning
	private SensorPort usPort = SensorPort.S1;	//Us sensor declaration
	private UltrasonicSensor ultraSonic = new UltrasonicSensor(usPort);
	private int bandCenter = 20 ;	//Distance robot must maintain from block
	private int bandWidth = 5;	//Bandwidth error that robot can work in
	private int distance;	
	private int filterControl;
	private int currentLeftSpeed;
	private int currentRightSpeed;

	/*
	 * public weirdLoop(Odometer odometer) --> bring odometer public void
	 * drive(Double[][] positions) --> drive to given positions public void
	 * travelTo(double x, double y) --> travel to a certain point in space
	 * private void avoid(double x, double y, double theta) --> if obstacle
	 * found, go arround it using Pcontroller public void turnTo(double theta)
	 * --> turn to an angle private int convertDistance(double radius, double
	 * distance) --> convert a distance to the number of rotations private int
	 * convertAngle(double radius, double width, double angle) --> convert an
	 * angle to the number of rotation to go to it public boolean isNavigating()
	 * --> check if navigating public double findTheta(double x, double y) -->
	 * find the theta needed to go to arrive at the destination
	 */
	public weirdLoop(Odometer odometer) {
		this.odometer = odometer;
	}

	public void drive(Double[][] positions) {
		// reset the motors
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor,
				rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}
		// keep the sensor at its place
		centerMotor.setSpeed(0);
		centerMotor.forward();

		for (int i = 0; i < positions.length; i++) {
			// travel to different positions
			travelTo(positions[i][0], positions[i][1]);
		}
	}

	public void travelTo(double x, double y) {
		int counter = 0;
		double theta;
		long threadStartTime, threadEndTime;
		LCD.drawInt((int) x, 0, 3);
		LCD.drawInt((int) y, 0, 4);

		isRunning = true; // for the running method

		theta = findTheta(x, y); // find theta
		if (Math.abs(odometer.getTheta() - theta) > turnEr) {
			turnTo(theta);
		}
		try { // short wait period to minimize error
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//both motors travelling straight
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();


		int curDis = ultraSonic.getDistance();
		  LCD.drawInt(curDis, 0,6); //check what the current distance is rudimentary filter 

		threadStartTime = System.currentTimeMillis();

		// go forward until we arrive at destination
		while (true) {
			threadEndTime = System.currentTimeMillis();
			LCD.drawString(String.valueOf(odometer.getX() - x), 4, 5);
			LCD.drawString(String.valueOf(odometer.getY() - y), 4, 6);
			curDis = ultraSonic.getDistance();
			if (Math.abs(odometer.getX() - x) < posEr
					&& Math.abs(odometer.getY() - y) < posEr) {
				Sound.beep();
		
				// will break loop when at destination
				break;
			}

			if (threadEndTime - threadStartTime > 10) {
				this.distance = ultraSonic.getDistance();
				threadStartTime+=10;
			}

			if (this.distance - bandCenter <= 0) {
				counter++;

			} else counter = 0;
			if (counter >= 3) {
				Sound.beep(); //beep to tell robot is at set coordinate
				avoid(x,y,theta);
				counter = 0;
			}
		}				

		//stop motors
		leftMotor.stop();
		rightMotor.stop();
	}

	
	  private void avoid(double x, double y, double theta) { //stop the motors
		  int curdistance=0;
		  leftMotor.stop();
		  rightMotor.stop();
		  centerMotor.setSpeed(100);//rotate the center motor so it faces outwards and can detect the block
		  centerMotor.rotate(-75);	//amount and direction of rotation
		  Delay.msDelay(500);		//delay to minimize error
		  int curDis = ultraSonic.getDistance();
		  LCD.drawInt(curDis, 0,6); //check what the current distance is rudimentary filter 
			leftMotor.rotate(-convertAngle(radius, width, 90), true);
			rightMotor.rotate(convertAngle(radius, width, 90), false);
			double temp=odometer.getTheta();
		  leftMotor.forward();
		  rightMotor.forward();
		  while(temp+Math.PI>=odometer.getTheta()+0.1){
			curdistance = ultraSonic.getDistance();
			  LCD.drawInt(curdistance, 0,6); //check what the current distance is // rudimentary filter 

			if (curdistance == 255 && filterControl < FILTER_OUT) {
				// bad value, do not set the distance variable, however do increment
				// the filter value
				filterControl++;
			} else if (curdistance == 255) {
				// true 255, therefore set distance to 255
				this.distance = curdistance;
				leftMotor.setSpeed(600);
				rightMotor.setSpeed(FORWARD_SPEED+100);
			} else {
				// distance went below 255, therefore reset everything.
				filterControl = 0;
				this.distance = curdistance;
				int error = Math.abs(this.distance - bandCenter);
				if(error==0){error=1;}

				// TODO: process a movement based on the us distance passed in (P
				// style)
				// P code from first lab
				if (this.distance >= (bandCenter - bandWidth)
						&& this.distance <= (bandCenter + bandWidth)) {
					leftMotor.setSpeed(FORWARD_SPEED);
					rightMotor.setSpeed(FORWARD_SPEED);
				}
				// too close move off
				else if (this.distance > (bandCenter - bandWidth)) {
					
					currentLeftSpeed = FORWARD_SPEED * error / (bandWidth);
					currentRightSpeed = FORWARD_SPEED * (bandWidth) / error;
					if (currentLeftSpeed > 450) {
						currentLeftSpeed = 450;
					}
					if (currentRightSpeed < 150) {
						currentRightSpeed = 150;
					}

					leftMotor.setSpeed(currentLeftSpeed);
					rightMotor.setSpeed(currentRightSpeed);
				}
				// too far move close
				else if (this.distance < (bandCenter - bandWidth)) {
					
					currentRightSpeed = FORWARD_SPEED * error / (bandWidth);
					currentLeftSpeed = FORWARD_SPEED * (bandWidth) / error;
					if (currentRightSpeed > 450) {
						currentRightSpeed = 450;
					}
					if (currentLeftSpeed < 150) {
						currentLeftSpeed = 150;
					}
					leftMotor.setSpeed(currentLeftSpeed);
					rightMotor.setSpeed(currentRightSpeed);
				}
			}
		}
		  leftMotor.stop();
		  rightMotor.stop();
		  
		  centerMotor.rotate(75);
/*			leftMotor.rotate(-convertAngle(radius, width, 90), true);
			rightMotor.rotate(convertAngle(radius, width, 90), false);*/
		  leftMotor.setSpeed(FORWARD_SPEED);
		  rightMotor.setSpeed(FORWARD_SPEED);
		  travelTo(x,y);

	  }
	 

	public void turnTo(double theta) {
		isRunning = true;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(radius, width, theta), true);
		rightMotor.rotate(-convertAngle(radius, width, theta), false);

		leftMotor.stop();
		rightMotor.stop();
		isRunning = false;

	}

	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public boolean isNavigating() {
		return isRunning;
	}

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


		return shortT(theta);

	}

	public double shortT(double theta) {
		double thetDer = theta - Math.toDegrees(odometer.getTheta());

		if (thetDer < -180) {
			return thetDer + 360;
		} else if (thetDer > 180) {
			return thetDer - 360;
		} else {
			return thetDer;
		}

	}

}