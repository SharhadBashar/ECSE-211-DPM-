//Sharhad Bashar
//Ecse 211
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class PController implements UltrasonicController {

	private final int bandCenter, bandWith;
	private final int motorStraight = 275, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C,
			centerMotor = Motor.B;
	private int distance;
	private int currentLeftSpeed;
	private int currentRightSpeed;
	private int filterControl;

	public PController(int bandCenter, int bandWith) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandWith = bandWith;

		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		
		leftMotor.forward();
		rightMotor.forward();
		
		//Hold the sensor in place
		centerMotor.setSpeed(0);
		centerMotor.forward();
		
		currentLeftSpeed = motorStraight;
		currentRightSpeed = motorStraight;
		filterControl = 0;
	}

	@Override
	public void processUSData(int distance) {
		// rudimentary filter
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance variable, however do increment
			// the filter value
			filterControl=filterControl+2;//add 2 instead of 1
		} else if (distance == 255) {
			// true 255, therefore set distance to 255
			this.distance = distance;
			//with this distance we want the robot to go back to a wall
			leftMotor.setSpeed(motorStraight+100);
			rightMotor.setSpeed(600);
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
			//here create the error variable to use through out the code
			int error = Math.abs(this.distance - bandCenter);
			//later on we divide by the error, therefore if the error is 0, we change it to 1 such taht there is no exception
			if(error==0){error=1;}

			// TODO: process a movement based on the us distance passed in (P
			// style)
			if (this.distance >= (bandCenter - bandWith)
					&& this.distance <= (bandCenter + bandWith)) {
				//the robot is in place so we make it go straight
				leftMotor.setSpeed(motorStraight);
				rightMotor.setSpeed(motorStraight);
			}
			// too close move off
			else if (this.distance < (bandCenter - bandWith)) {
				//when too close we turn right, do that we increase the speed of the left motor in proportion to the error and the bandwidth
				//and decrease the speed of the right motor the opposite way
				currentLeftSpeed = motorStraight * error / (bandWith);
				currentRightSpeed = motorStraight * (bandWith) / error;
				//we don't want the motor to run too fast, so we add a min and max speed
				if (currentLeftSpeed > 400) {
					currentLeftSpeed = 425; //change speed
				}
				if (currentRightSpeed < 100) {
					currentRightSpeed = 125;//change speed
				}
				//give the current speed to each motor
				leftMotor.setSpeed(currentLeftSpeed);
				rightMotor.setSpeed(currentRightSpeed);
			}
			// too far move close
			else if (this.distance > (bandCenter - bandWith)) {
				//when too far we turn left, do that we increase the speed of the right motor in proportion to the error and the bandwidth
				//and decrease the speed of the left motor the opposite way
				currentRightSpeed = motorStraight * error / (bandWith);
				currentLeftSpeed = motorStraight * (bandWith) / error;
				
				//we don't want the motor to run too fast, so we add a min and max speed

				if (currentRightSpeed > 400) {
					currentRightSpeed = 425;//change speed
				}
				if (currentLeftSpeed < 100) {
					currentLeftSpeed = 125;//change speed
				}
				
				//give the current speed to each motor
				leftMotor.setSpeed(currentLeftSpeed);
				rightMotor.setSpeed(currentRightSpeed);
			}
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}