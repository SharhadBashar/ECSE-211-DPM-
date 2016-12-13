//Sharhad Bashar
//Ecse 211
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;

//navigation code from previous lab
public class Navigation {
	// put your navigation code here 

	private NXTRegulatedMotor leftMotor, rightMotor; //declares the motors
	
	private Odometer odometer;
	private TwoWheeledRobot Walle;
	
	private Lab5 lab5 = new Lab5();
	
	public Navigation(Odometer odometer) {
		this.odometer = odometer;
		this.Walle = odometer.getTwoWheeledRobot();
	}
	
	//method to rotate counter-clockwise 90 degrees
	public void rotateCCW() {
		
		double initialAngle = odometer.getAngle();
		double finalAngle = initialAngle - 90;
		
		turnTo(finalAngle, initialAngle);
		
	}
	
	//method to rotate clockwise 90 degrees
	public void rotateCW() {
		double initialAngle = odometer.getAngle();
		double finalAngle = initialAngle + 90;
		
		turnTo(finalAngle, initialAngle);
	}
	
	// move forward 30 cm
	public void travelThirty() {
		
		double x = odometer.getX();
		double y = odometer.getY();
		
		boolean finished = false;
		double approximation = 1;
		
		if (odometer.getAngle() > 315 && odometer.getAngle() <= 360 
				|| odometer.getAngle() >= 0 && odometer.getAngle() <= 45) {
			y += 30;
			
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(y - odometer.getY()) < approximation) {
					finished = true;
				}
			}
			
		}
		else if (odometer.getAngle() > 45 && odometer.getAngle() <= 135) {
			x += 30;
			
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(x - odometer.getX()) < approximation) {
					finished = true;
				}
			}
			
		}
		else if (odometer.getAngle() > 135 && odometer.getAngle() <= 225) {
			y += -30;
			
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(y - odometer.getY()) < approximation) {
					finished = true;
				}
			}
			
		}
		else {
			x += -30;
			
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			Walle.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(x - odometer.getX()) < approximation) {
					finished = true;
				}
			}
			
		}
		Walle.setForwardSpeed(0);
		lab5.setFinishedNavigation(true);
	}
	
	//Get the angle we want the robot to be facing
	
	public double getDesiredAngle(double x,double y){
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		
		double diffX = x - currentX;
		double diffY = y - currentY;
		
		double angle;
		
		if (diffX <= 0) {
			if (diffY <= 0) {
				angle = 0 - Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // -X -Y
			}
			else {
				angle = 0 + Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // -X +Y
			}
		}
		else {
			if (diffY <= 0) {
				angle = 45 + Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // +X -Y
			}
			else {
				angle = 45 - Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // +X +Y
			}
		}
		return angle;
	}
	public void travelTo(double x, double y) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
	    
		//Get current coordinates
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		
		// Compute difference between current coordinates and coordinates of destination
		double diffX = x - currentX;
		double diffY = y - currentY;
		
		
		//Get the angle we want the robot to be facing
		double angle = getDesiredAngle(x,y);
		
		//Turn the robot to face angle we want
		turnTo(angle, nfa(odometer.getAngle()));
		
		this.setForwardSpeed(3);
		
		boolean finishedMoving = false;
	
		LCD.clear(); //clear screen
		
		double initialX = odometer.getX();
		double initialY = odometer.getY();
		
		//Loop to be executed while the robot is still not finished moving
		while (finishedMoving == false) {	
			
			//Display values
			this.drawToLCD(initialX, initialY);
			
			// Condition to be met for the robot to stop moving (destination reached)
			if (isFinished(diffX,diffY,initialX,initialY)) {
				finishedMoving = true;
			}
			
		}
		
		//Stop Robot
		this.setForwardSpeed(0);
		
		lab5.setFinishedNavigation(true);

	}
	//Set the forward speed (robot skipped over the speed setting so a quick fix was to set it multiple times)
	public boolean isFinished(double diffX,double diffY,double initialX,double initialY){
		if (Math.abs(Math.sqrt(diffX*diffX + diffY*diffY)
				- Math.sqrt((initialX-odometer.getX())*(initialX-odometer.getX())
						+ (initialY-odometer.getY())*(initialY-odometer.getY()))) < 1) {
			return true;
		}
		else{
			return false;
		}
	}
	
	//Set forward speed of robot
	public void setForwardSpeed(int speed){
		for(int i=0;i<3;i++){
			Walle.setForwardSpeed(speed);

		}
		
	}
	//Set rotation speed of robot
	public void setRotationSpeed(int speed){
		for(int i=0;i<3;i++){
			Walle.setRotationSpeed(speed);

		}
		
	}
	
	//Draw values to screen
	public void drawToLCD(double x,double y){
		LCD.drawString("Initial X: " + odometer.getX(), 0, 1);
		LCD.drawString("Initial Y: " + odometer.getY(), 0, 2);
		LCD.drawString("Final X: " + x, 0, 3);
		LCD.drawString("Final Y: " + y, 0, 4);
		
	}
	
	public void turnTo(double angle, double currentAngle) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		
		double approximation = 0.6;
		
		boolean finishedTurning = false;
		
		//current45 angle10
		
		if (currentAngle < angle) {
			if (angle-currentAngle <= 180) {
				this.setRotationSpeed(35);
			}
			else {
				this.setRotationSpeed(-35);
			}
		}
		else {
			if (currentAngle-angle <= 180) {
				this.setRotationSpeed(-35);
			}
			else {
				this.setRotationSpeed(35);
			}
		}
		
		double difference = 0;
			
		while (finishedTurning == false) {
			
			difference = Math.abs(nfa(odometer.getAngle()) - nfa(angle));
			
			if ( (difference >= 0 && difference <= approximation) || difference <= 360 && difference >= 360-approximation) {
				finishedTurning = true;
			}
			LCD.drawString("Current: " + odometer.getAngle(), 0, 1);
			LCD.drawString("Initial: " + currentAngle, 0, 2);
			LCD.drawString("Final: " + angle, 0, 3);
			LCD.drawString("Diff: " + difference, 0, 4);
		}
		
		// Stop turning
		this.setRotationSpeed(0);
		
		lab5.setFinishedNavigation(true);
		
	}
	
	// Get normalized angle (in accord with the robot's theta from the odometer class)
	public double nfa(double angle) {
		if (angle < 0) {
			angle = 360 + angle;
			return angle;
		}
		else if (angle > 360) {
			angle = angle - 360;
			return angle;
		}
		else {
			return angle;
		}
	}
	
}
