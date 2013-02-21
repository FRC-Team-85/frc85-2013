package com.bob85;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive {
    
    private SpeedController leftDriveMotors; //class reference to left drive
    private SpeedController rightDriveMotors; //class reference to right drive
    
    private Servo leftDriveServo;
    private Servo rightDriveServo;
    
    private Encoder leftDriveEncoder;
    private Encoder rightDriveEncoder;
    
    private Gyro gyro;
    
    Joystick leftDriveJoystick; //reference to left drive joystick
    Joystick rightDriveJoystick; //reference to right drive joystick
    
    public static final int kLEFTDRIVE_VICTORS = 1;
    public static final int kRIGHTDRIVE_VICTORS = 2;
    
    public static final int kLEFTDRIVE_SERVO = 3;
    public static final int kRIGHTDRIVE_SERVO = 4;
    
    public static final int kLEFTDRIVE_ENCODER_A = 1;
    public static final int kLEFTDRIVE_ENCODER_B = 2;
    public static final int kRIGHTDRIVE_ENCODER_A = 3;
    public static final int kRIGHTDRIVE_ENCODER_B = 4;
    
    public static final int kGYRO = 1;
    
    private static final int kSHIFT_DRIVE = 3;
    private static final int kSHIFT_CLIMB = 2;
    private static final int kSHIFT_CLIMB_LEFT = 4;
    private static final int kSHIFT_CLIMB_RIGHT = 5;
    
    private boolean isEncodersStarted = false;
    
    private double leftMotorsOutput; //left drive motor output setting
    private double rightMotorsOutput; //right drive motor output setting
    private double leftLinearMotorsOutput;
    private double rightLinearMotorsOutput;
            
    private double deadband = 0.1; //Deadband for drive motor output
    private double changeLimit_val = 0.25;
    private double changeLimit = changeLimit_val;
    
    private double leftDriveServoDrivePosition = 0;
    private double rightDriveServoDrivePosition = 1;
    private double leftDriveServoClimbPosition = 1;
    private double rightDriveServoClimbPosition = 0;
    
    private int encoderCPR = 250;
    private double encoderDistanceRatio = (((4 * Math.PI) / 10.3) / encoderCPR); //Each encoder pulse = 1.22 inches traveled
    private double encoderClimberRatio = (2 * Math.PI / encoderCPR);
    
    private boolean isDrive = false;
    private boolean isClimb = false;
    private int driveState; //0 is drive 1 is left climb 2 is right climb 3 is both climb
    
    /**
     * Constructs a Robot Drive with two PWM channels and joystick input
     * 
     * @param leftDriveMotors left drive PWM channel
     * @param rightDriveMotors right drive PWM channel
     * @param leftDriveJoystick left drive joystick
     * @param rightDriveJoystick right drive joystick
     */
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors,
            Joystick leftDriveJoystick, Joystick rightDriveJoystick) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        this.leftDriveJoystick = leftDriveJoystick;
        this.rightDriveJoystick = rightDriveJoystick;
    }
    
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors, Servo leftDriveServo, Servo rightDriveServo,
            Encoder leftDriveEncoder, Encoder rightDriveEncoder, Gyro gyro,
            Joystick leftDriveJoystick, Joystick rightDriveJoystick) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        this.leftDriveJoystick = leftDriveJoystick;
        this.rightDriveJoystick = rightDriveJoystick;
        this.leftDriveServo = leftDriveServo;
        this.rightDriveServo = rightDriveServo;
        this.leftDriveEncoder = leftDriveEncoder;
        this.rightDriveEncoder = rightDriveEncoder;
        this.gyro = gyro;
    }
    
    /**
     * Maps the motor outputs to the joysticks Y axis
     */
    private void getTankDriveJoystickInput(double scaleFactor) {
        if (rightDriveJoystick.getTrigger()) {
            leftMotorsOutput = -leftDriveJoystick.getY();
            rightMotorsOutput = -rightDriveJoystick.getY();
        } else {
            leftMotorsOutput = -leftDriveJoystick.getY() * scaleFactor;
            rightMotorsOutput = -rightDriveJoystick.getY() * scaleFactor;
        }
    }
    
    private void getLeftClimbRightDriveJoystickInput(double scaleFactor) {
        leftMotorsOutput = scaleFactor * leftDriveJoystick.getY();
        rightMotorsOutput = scaleFactor * -rightDriveJoystick.getY();
    }
    
    private void getRightClimbLeftDriveJoystickInput(double scaleFactor) {
        leftMotorsOutput = scaleFactor * -leftDriveJoystick.getY();
        rightMotorsOutput = scaleFactor * rightDriveJoystick.getY();
    }
    
    /**
     * Sets motor output setting to zero if it falls under the deadband
     */    
    public void setMotorOutputDeadbands() {
        if (Math.abs(leftMotorsOutput) < deadband) {
            leftMotorsOutput = 0;
        }
        
        if (Math.abs(rightMotorsOutput) < deadband) {
            rightMotorsOutput = 0;
        }
    }
    
    /**
     * Sets motor output setting to a linearized desired output
     */
    public void setLinearizedOutput() {
        leftLinearMotorsOutput = MotorLinearization.calculateLinearOutput(leftMotorsOutput);
        rightLinearMotorsOutput = MotorLinearization.calculateLinearOutput(rightMotorsOutput);
        leftDriveMotors.set(leftLinearMotorsOutput);
        rightDriveMotors.set(-rightLinearMotorsOutput);
    }
    
    public void limitMotorsOutputChange(boolean isLeft, boolean isRight) {       
        if (isLeft) {
            if (leftLinearMotorsOutput - leftDriveMotors.get() > changeLimit) {
                leftLinearMotorsOutput = leftDriveMotors.get() + changeLimit;
            } else if (leftLinearMotorsOutput - leftDriveMotors.get() < -changeLimit) {
                leftLinearMotorsOutput = leftDriveMotors.get() - changeLimit;
            }
        }
        if (isRight) {
            if (rightLinearMotorsOutput - rightDriveMotors.get() > changeLimit) {
                rightLinearMotorsOutput = rightDriveMotors.get() + changeLimit;
            } else if (rightLinearMotorsOutput - rightDriveMotors.get() < -changeLimit) {
                leftLinearMotorsOutput = rightDriveMotors.get() - changeLimit;
            }
        }
    }
    
    /**
     * Sets the motors output settings values
     * @param leftMotorsOutput
     * @param rightMotorsOutput 
     */
    public void setMotorOutputSetting(double leftMotorsOutput, double rightMotorsOutput) {
        this.leftMotorsOutput = leftMotorsOutput;
        this.rightMotorsOutput = rightMotorsOutput;
    }
    
    public void resetEncoders() {
        if (leftDriveEncoder != null && rightDriveEncoder != null) {
            leftDriveEncoder.reset();
            rightDriveEncoder.reset();
        }
    }
    

    public void initEncoders() {
        if (!isEncodersStarted && leftDriveEncoder != null && rightDriveEncoder != null) {
            leftDriveEncoder.start();
            rightDriveEncoder.start();
            
            resetEncoders();

            leftDriveEncoder.setDistancePerPulse(encoderDistanceRatio);
            rightDriveEncoder.setDistancePerPulse(encoderDistanceRatio);
            
            isEncodersStarted = true;
        }
    }
    
    public void disableEncoders() {
        if (isEncodersStarted) {
            leftDriveEncoder.stop();
            rightDriveEncoder.stop();
            isEncodersStarted = false;
        }
    }
    
    public double getEncodersDistance() {
        return (leftDriveEncoder.getDistance() + rightDriveEncoder.getDistance()) / 2;
    }
    
    public void sendEncoderDriveDiagnosticsSDB() {
        SmartDashboard.putNumber("Left Drive Encoder Dist", leftDriveEncoder.getDistance());
        SmartDashboard.putNumber("Right Drive Encoder Dist", rightDriveEncoder.getDistance());
        SmartDashboard.putNumber("Left Drive Encoder", leftDriveEncoder.get());
        SmartDashboard.putNumber("Right Drive Encoder", rightDriveEncoder.get());
 
    }
    
    public double getAngle() {
        return gyro.getAngle();
    }
    
    public void resetGyro() {
        gyro.reset();
    }
    
    public boolean getServoDrivePosition() {
        if (leftDriveServo.get() == leftDriveServoDrivePosition && 
                rightDriveServo.get() == rightDriveServoDrivePosition) {
            isDrive = true;
            isClimb = false;
            return isDrive;
        }
        else {
            isDrive = false;
            return isDrive;
        }
    }

    public boolean getServoClimbPosition() {
       if (leftDriveServo.get() == leftDriveServoClimbPosition && 
                rightDriveServo.get() == rightDriveServoClimbPosition) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public void setServoDrivePosition() {
        leftDriveServo.set(leftDriveServoDrivePosition);
        rightDriveServo.set(rightDriveServoDrivePosition);
    }
    
    public void setServoClimbPosition() {
        leftDriveServo.set(leftDriveServoClimbPosition);
        rightDriveServo.set(rightDriveServoClimbPosition);
    }
    
    public void setleftServoClimbPosition() {
        leftDriveServo.set(leftDriveServoClimbPosition);
    }
    
    public void setRightServoClimbPosition() {
        rightDriveServo.set(rightDriveServoClimbPosition);
    }
    
    public boolean getIsDrive() {
        return isDrive;
    }
    
    public boolean getIsClimb() {
        return isClimb;
    }
    
    private void sendDriveStateDiagnostics() {
        SmartDashboard.putBoolean("isDrive", isDrive);
        SmartDashboard.putBoolean("isClimb", isClimb);
    }
    

    
    public void setJoystickBasedPTOShift() {
        if (leftDriveJoystick.getRawButton(kSHIFT_DRIVE)) {
            setServoDrivePosition();
        } else if (leftDriveJoystick.getRawButton(kSHIFT_CLIMB)) {
            setServoClimbPosition();
        } else if (leftDriveJoystick.getRawButton(kSHIFT_CLIMB_LEFT)) {
            setleftServoClimbPosition();
        } else if (leftDriveJoystick.getRawButton(kSHIFT_CLIMB_RIGHT)) {
            setRightServoClimbPosition();
        }
    }
    
    /**
     * Uses two joysticks in a tank drive setup to run the motors
     */
    public void joystickBasedTankDrive() {
        getTankDriveJoystickInput(0.5);
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }
    
    public void joystickBasedLeftClimbDrive() {
        getLeftClimbRightDriveJoystickInput(1);
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }
    
    public void joystickBasedRightClimbDrive() {
        getRightClimbLeftDriveJoystickInput(1);
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }
    
    public void encoderTestDrive() {
        joystickBasedTankDrive();
        sendEncoderDriveDiagnosticsSDB();
        limitMotorsOutputChange(true, true);
    }       
    
    public void runRampUpTrapezoidalMotionProfile(double maxSpeed) {
        setMotorOutputSetting(maxSpeed, maxSpeed);
        limitMotorsOutputChange(true, true);
    }
    
    public void runRampDownTrapezoidalMotionProfile(double minSpeed) {
        leftMotorsOutput = minSpeed;
        rightMotorsOutput = minSpeed;
        limitMotorsOutputChange(true, true);
    }
    
    public void switchDriveStates() {
        if (leftDriveJoystick.getRawButton(kSHIFT_DRIVE)) {
            driveState = 0;
        } else if (leftDriveJoystick.getRawButton(kSHIFT_CLIMB_LEFT)) {
            driveState = 1;
        } else if (leftDriveJoystick.getRawButton(kSHIFT_CLIMB_RIGHT)) {
            driveState = 2;
        } else if (leftDriveJoystick.getRawButton(kSHIFT_CLIMB)) {
            driveState = 3;
        } else if (driveState == 1 && leftDriveJoystick.getRawButton(kSHIFT_CLIMB_RIGHT)) {
            driveState = 3;
        } else if (driveState == 2 && leftDriveJoystick.getRawButton(kSHIFT_CLIMB_LEFT)) {
            driveState = 3;
        }
    }
       
    public void runDriveStates() {
        setJoystickBasedPTOShift();
        switch (driveState) {
            case 0:
                joystickBasedTankDrive();
                break;
            case 1:
                joystickBasedLeftClimbDrive();
                break;
            case 2:
                joystickBasedRightClimbDrive();
                break;
            case 3:
                break;
        }
    }
    
    public int getDriveState() {
        return driveState;
    }
    
    public void driveInit() {
        gyro.setSensitivity(.007);
        initEncoders();
        setServoDrivePosition();
    }
    
    public void disabledInit() {
        disableEncoders();
        gyro.reset();
    }
    
    public void runDrive() {
        switchDriveStates();
        runDriveStates();        
    }
}
