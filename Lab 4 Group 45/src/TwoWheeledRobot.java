//Sharhad Bashar
//Ecse 211
import lejos.nxt.NXTRegulatedMotor;

public class TwoWheeledRobot {
	public static final double RADIUS = 2.01;
	public static final double WIDTH = 15.3;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	private double acceleration;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   double width,
						   double leftRadius,
						   double rightRadius) {
		this.setLeftMotor(leftMotor);
		this.setRightMotor(rightMotor);
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(leftMotor, rightMotor, WIDTH, RADIUS, RADIUS);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width) {
		this(leftMotor, rightMotor, width, RADIUS,RADIUS);
	}
	
	// accessors
	public double getDisplacement() {
		return (getLeftMotor().getTachoCount() * leftRadius +
				getRightMotor().getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (getLeftMotor().getTachoCount() * leftRadius -
				getRightMotor().getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = getLeftMotor().getTachoCount();
		rightTacho = getRightMotor().getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setFloat() {
		this.getLeftMotor().stop();
		this.getRightMotor().stop();
		this.getLeftMotor().flt(true);
		this.getRightMotor().flt(true);
	}
	
	public void stop(){
		this.getLeftMotor().stop();
		this.getRightMotor().stop();
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed; 

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			getLeftMotor().forward();
		else {
			getLeftMotor().backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			getRightMotor().forward();
		else {
			getRightMotor().backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			getLeftMotor().setSpeed(100);
		else
			getLeftMotor().setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			getRightMotor().setSpeed(100);
		else
			getRightMotor().setSpeed((int)rightSpeed);
	}

	public double getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
		getLeftMotor().setAcceleration((int)acceleration);
		getRightMotor().setAcceleration((int)acceleration);
	}

	public NXTRegulatedMotor getRightMotor() {
		return rightMotor;
	}

	public void setRightMotor(NXTRegulatedMotor rightMotor) {
		this.rightMotor = rightMotor;
	}

	public NXTRegulatedMotor getLeftMotor() {
		return leftMotor;
	}

	public void setLeftMotor(NXTRegulatedMotor leftMotor) {
		this.leftMotor = leftMotor;
	}

	public void moveForward(double distance){
	      leftMotor.rotate(convertDistance(leftRadius, distance), true); 
	      rightMotor.rotate(convertDistance(rightRadius, distance), false); 
	        
	  } 
	  
	//Methods from square driver
	  private  int convertDistance(double radius, double distance) {
	      //tells robot how much to move forward
	      return (int) ((180.0 * distance) / (Math.PI * radius));
	  } 
	  
	   
}
