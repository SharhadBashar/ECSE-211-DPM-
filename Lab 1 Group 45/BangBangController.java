//Sharhad Bashar
//Ecse 211
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandWith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 300;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C, centerMotor = Motor.B;
	private int distance;

	
	public BangBangController(int bandCenter, int bandWith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandWith = bandWith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;

		
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		
		leftMotor.forward();
		rightMotor.forward();
		//holds the sensor is one place
		centerMotor.setSpeed(0);
		centerMotor.forward();
		
		}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		
       //checks if the robot is within the tolerance band
		if (this.distance >= (bandCenter - bandWith) && this.distance <= (bandCenter + bandWith)){
			//when the robot is in the right place just make it go straight
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			}

		//if too close
		else if (this.distance < (bandCenter - bandWith)) {
			//when too close increase the speed of the left motor and decrease for the right one.
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorLow);
			}

		//if too far
		else if (this.distance > (bandCenter +bandWith)) {
			//when too far, turn left to go back to a wall, we don't want it to come back to fast so that
			//when it does a 180 it doesn't hit the wall, we kept the left motor at the same speed.
			leftMotor.setSpeed(motorStraight); 
			rightMotor.setSpeed(motorHigh);
		}
		
	}
	

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
