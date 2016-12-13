//Sharhad Bashar
//Ecse 211
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class LightLocalizer {
	
	private Odometer odo;
	private TwoWheeledRobot R2D2;
	private ColorSensor cs;
	private static int ROTATION_SPEED = 30;
	private static int FORWARD_SPEED = 150;
	private int cBL=500; //value of black
	private Navigation nav;
	private int lightValue;
	private double sensorDist=11.5; //distance from sensor to center of robot
	
	public LightLocalizer(Odometer odo, ColorSensor cs,Navigation nav) {
		this.odo = odo;
		this.R2D2 = odo.getTwoWheeledRobot();
		this.cs = cs;
		this.nav=nav;
		
	}
	
	public void doLocalization() {
		LCD.clear();
		try {Thread.sleep(1000);} 
		catch (InterruptedException e) {}
		// drive to location listed in tutorial
		
		lightValue=cs.getNormalizedLightValue();
		LCD.drawInt(lightValue, 0, 4);
//drive strait until it sees a line and stop
		while(cs.getNormalizedLightValue()>cBL){
			R2D2.setForwardSpeed(FORWARD_SPEED);
			lightValue=cs.getNormalizedLightValue();
			LCD.drawInt(lightValue, 0, 4);
		}
		
		R2D2.stop();
		Sound.beep();

		try {Thread.sleep(500);} 
		catch (InterruptedException e) {}
		//turn 90 degrees to go forward compared to the x-axis
		nav.turnOf(90);
//drive strait until it sees a line and stop
		while(cs.getNormalizedLightValue()>cBL){
			R2D2.setForwardSpeed(FORWARD_SPEED);
			lightValue=cs.getNormalizedLightValue();
			LCD.drawInt(lightValue, 0, 4);
		}
		
		
		R2D2.stop();
		Sound.beep();

		try {Thread.sleep(2000);} 
		catch (InterruptedException e) {}

		lightValue=cs.getNormalizedLightValue();
		LCD.drawInt(lightValue, 0, 4);
		LCD.clear();
		//do a pre localisation and use the distance between the sensor and center and put it as X and Y value
		odo.setX(this.sensorDist);
		odo.setY(this.sensorDist);
		
		//Travel to the point to do the light sensor localisation
		nav.travelTo(-5,-5);
		nav.turnOf(180);	//look at the origin
		try {Thread.sleep(2000);} 
		catch (InterruptedException e) {}
		
		// start rotating and clock all 4 gridlines
		while(cs.getNormalizedLightValue()>cBL){
			R2D2.setSpeeds(0,ROTATION_SPEED);
			lightValue=cs.getNormalizedLightValue();
			LCD.drawInt(lightValue, 0, 4);
		}
		Sound.beep();

		try {Thread.sleep(100);} 
		catch (InterruptedException e) {}

		lightValue=cs.getNormalizedLightValue();
		LCD.drawInt(lightValue, 0, 4);
		//take 1st theta
		double thetX1=odo.getTheta();
		LCD.drawInt((int)thetX1,0,5);
		
		while(cs.getNormalizedLightValue()>cBL){
			R2D2.setSpeeds(0,ROTATION_SPEED);
			lightValue=cs.getNormalizedLightValue();
			LCD.drawInt(lightValue, 0, 4);
		}
		Sound.beep();

		try {Thread.sleep(100);} 
		catch (InterruptedException e) {}

		lightValue=cs.getNormalizedLightValue();
		LCD.drawInt(lightValue, 0, 4);
		//take 2nd theta

		double thetY1=odo.getTheta();
		LCD.drawInt((int)thetY1,0,6);
		
		
		while(cs.getNormalizedLightValue()>cBL){
			R2D2.setSpeeds(0,ROTATION_SPEED);
			lightValue=cs.getNormalizedLightValue();
			LCD.drawInt(lightValue, 0, 4);
		}		
		Sound.beep();

		try {Thread.sleep(100);} 
		catch (InterruptedException e) {}

		lightValue=cs.getNormalizedLightValue();
		LCD.drawInt(lightValue, 0, 4);
		//take 3rd theta

		double thetX2=odo.getTheta();
		LCD.drawInt((int)thetX2,5,5);
		
		
		while(cs.getNormalizedLightValue()>cBL){
			R2D2.setSpeeds(0,ROTATION_SPEED);
			lightValue=cs.getNormalizedLightValue();
			LCD.drawInt(lightValue, 0, 4);
		}
		Sound.beep();
		R2D2.stop();

		try {Thread.sleep(100);} 
		catch (InterruptedException e) {}

		lightValue=cs.getNormalizedLightValue();
		LCD.drawInt(lightValue, 0, 4);
		//take 4th theta

		double thetY2=odo.getTheta();
		LCD.drawInt((int)thetY2,5,6);
		
		// do trig to compute (0,0) and 0 degrees
		double thetX=thetX2-thetX1;
		double thetY=thetY2-thetY1;
		
		double updtX=-sensorDist*Math.cos(Math.toRadians(thetY/2));
		double updtY=-sensorDist*Math.cos(Math.toRadians(thetX/2));
		
		double delTheta = (thetY/2) +90 -(thetY2-180);
//update position
		odo.setPosition(new double[] {updtX,updtY,delTheta+odo.getTheta()}, new boolean[] {true, true, true});
		// when done travel to (0,0) and turn to 0 degrees
		nav.travelTo(0, 0);
		nav.turnTo(0);
	}

}
