//Sharhad Bashar
//Ecse 211
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private TwoWheeledRobot R2D2;
	private Navigation nav;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private int distance;
	private int count=0;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType,Navigation nav) {
		this.odo = odo;
		this.R2D2 = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		this.nav = nav;
		

	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB,delT;
		LCD.clear();
		LCD.drawInt(getFilteredData(), 0, 7);
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees  no wall
			while (getFilteredData() < 50){
				LCD.drawInt(getFilteredData(), 0, 7);
				R2D2.setRotationSpeed(ROTATION_SPEED);
			}
			R2D2.stop();

			try {Thread.sleep(500);}catch (InterruptedException e) {}
			// keep rotating until the robot sees a wall, then latch the angle

			while (getFilteredData() == 50){
				LCD.drawInt(getFilteredData(), 0, 7);
				R2D2.setRotationSpeed(ROTATION_SPEED);
			}
			R2D2.stop();
			Sound.beep();



			angleA=odo.getTheta();
			LCD.drawInt((int)angleA,0,4);
			LCD.drawInt(getFilteredData(),5,4);
			// switch direction and wait until it sees no wall
			while (getFilteredData() < 50){
				LCD.drawInt(getFilteredData(), 0, 7);

				R2D2.setRotationSpeed(-ROTATION_SPEED);
			}
			R2D2.stop();

			// keep rotating until the robot sees a wall, then latch the angle

			while (getFilteredData() == 50){
				LCD.drawInt(getFilteredData(), 0, 7);

				R2D2.setRotationSpeed(-ROTATION_SPEED);
			}
			R2D2.stop();
			Sound.beep();


			angleB=odo.getTheta();
			LCD.drawInt((int)angleB, 0, 5);
			LCD.drawInt(getFilteredData(),5,5);

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			//get the good deltaT
			if(angleA<angleB){delT=225-((angleA+angleB)/2);}
			else {delT=45-((angleA+angleB)/2);}
			// update the odometer position (example to follow:)

			LCD.drawInt((int)(odo.getTheta()+delT),0,6);
			
			odo.setPosition(new double [] {odo.getX(), odo.getY(),odo.getTheta()+delT}, new boolean [] {true, true, true});
			try {Thread.sleep(500);} 
			catch (InterruptedException e) {}
			//turn to 0 deg
			nav.turnTo(0);
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			// rotate the robot until it sees a wall
			while (getFilteredData() == 50){
				LCD.drawInt(getFilteredData(), 0, 7);
				R2D2.setRotationSpeed(ROTATION_SPEED);
			}
			//keep rotating until the robot sees no wall, then latch the angle
			while (getFilteredData() < 50){
				LCD.drawInt(getFilteredData(), 0, 7);
				R2D2.setRotationSpeed(ROTATION_SPEED);
			}
			try {Thread.sleep(500);}catch (InterruptedException e) {}
			R2D2.stop();
			Sound.beep();
			angleA=odo.getTheta();
			LCD.drawInt((int)angleA,0,4);
			LCD.drawInt(getFilteredData(),5,4);
			// switch direction and wait until it sees a wall

			while (getFilteredData() == 50){
				LCD.drawInt(getFilteredData(), 0, 7);
				R2D2.setRotationSpeed(-ROTATION_SPEED);
			}
			// keep rotating until the robot sees no wall, then latch the angle

			while (getFilteredData() < 50){
				LCD.drawInt(getFilteredData(), 0, 7);

				R2D2.setRotationSpeed(-ROTATION_SPEED);
			}
			R2D2.stop();
			Sound.beep();
			angleB=odo.getTheta();
			LCD.drawInt((int)angleB, 0, 5);
			LCD.drawInt(getFilteredData(),5,5);

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			if(angleA<angleB){delT=225-((angleA+angleB)/2);}
			else {delT=43-((angleA+angleB)/2);}
			// update the odometer position (example to follow:)

			LCD.drawInt((int)(odo.getTheta()+delT),0,6);
			
			Sound.beep();
			odo.setPosition(new double [] {odo.getX(), odo.getY(),odo.getTheta()+delT}, new boolean [] {true, true, true});
			try {Thread.sleep(500);} 
			catch (InterruptedException e) {}
			nav.turnTo(0);
		}

		}
	
	private int getFilteredData() {
		int dist;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		dist = us.getDistance();
		//use a filter to filter out false positives or negatives
		if(dist>50 && count<=3){
			count++; return distance;
			}
		else if(dist>50 && count>3){
			return 50;
			}
		else{count=0;
			distance=dist;
			return dist;
			}
	}

}
