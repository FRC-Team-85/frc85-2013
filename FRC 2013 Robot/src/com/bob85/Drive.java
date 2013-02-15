/*
 * MAKE SURE EACH SIDE OF THE DRIVE HAVE THE SAME POLARITY WHEN USING Y SPLITTER
 */
package com.bob85;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Drive {
        boolean isClimbMode;
    
    private SpeedController leftDriveMotors; //class reference to left drive
    private SpeedController rightDriveMotors; //class reference to right drive
    
    private Servo leftDriveServo;
    private Servo rightDriveServo;
    
    private Encoder leftDriveEncoder;
    private Encoder rightDriveEncoder;
    
    Joystick leftDriveJoystick; //reference to left drive joystick
    Joystick rightDriveJoystick; //reference to right drive joystick
    Joystick m_testDriveJoystick;
    
    public static final int kLEFTDRIVE_VICTORS = 1;
    public static final int kRIGHTDRIVE_VICTORS = 2;
    
    public static final int kLEFTDRIVE_SERVO = 3;
    public static final int kRIGHTDRIVE_SERVO = 4;
    
    public static final int kLEFTDRIVE_ENCODER_A = 1;
    public static final int kLEFTDRIVE_ENCODER_B = 2;
    public static final int kRIGHTDRIVE_ENCODER_A = 3;
    public static final int kRIGHTDRIVE_ENCODER_B = 4;
    
    private boolean isEncodersStarted = false;
    
    private double leftMotorsOutput; //left drive motor output setting
    private double rightMotorsOutput; //right drive motor output setting
    private double leftLinearMotorsOutput;
    private double rightLinearMotorsOutput;

    private double leftOldOutput;
    private double rightOldOutput;
            
    private double deadband = 0.1; //Deadband for drive motor output
    private double changeLimit_val = 0.25;
    private double changeLimit = changeLimit_val;
    
    private double leftDriveServoDrivePosition = 0;
    private double rightDriveServoDrivePosition = 1;
    private double leftDriveServoClimbPosition = 1;
    private double rightDriveServoClimbPosition = 0;
    
    private double encoderDistanceRatio = 1.22; //Each encoder pulse = 1.22inches traveled
    private int encoderCPR = 250;
    
    /**
     * Constructs a Robot Drive with two PWM channels
     * 
     * @param leftDriveMotors left drive PWM channel
     * @param rightDriveMotors right drive PWM channel
     */
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
    }
    
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors,
            Joystick testDriveJoystick) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        m_testDriveJoystick = testDriveJoystick;
    }
    
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
            Encoder leftDriveEncoder, Encoder rightDriveEncoder,
            Joystick leftDriveJoystick, Joystick rightDriveJoystick) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        this.leftDriveJoystick = leftDriveJoystick;
        this.rightDriveJoystick = rightDriveJoystick;
        this.leftDriveServo = leftDriveServo;
        this.rightDriveServo = rightDriveServo;
        
        this.leftDriveEncoder = leftDriveEncoder;
        this.rightDriveEncoder = rightDriveEncoder;
    }
    
    /**
     * Maps the motor outputs to the joysticks Y axis
     */
    private void getTankDriveJoystickInput() {
        leftMotorsOutput = -leftDriveJoystick.getY();
        rightMotorsOutput = rightDriveJoystick.getY();
    }
    
    private void getTestDriveJoystickInput() {
        leftMotorsOutput = m_testDriveJoystick.getRawAxis(2);
        rightMotorsOutput = m_testDriveJoystick.getRawAxis(4);
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
        rightDriveMotors.set(rightLinearMotorsOutput);
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
    
    private void motorsChangeLimit() {
        
        if (Math.abs(leftMotorsOutput-leftOldOutput)>0.5) {     //checks if the change is above 0.5
            if (leftMotorsOutput>0) {                           //checks if positive and rewrites
                leftOldOutput +=0.5;                            //oldoutput to change by +0.5
                }
            else if (leftMotorsOutput<0) {                      //check if negative and rewrites
                leftOldOutput -=0.5;                            //oldoutput to change by -0.5
                }                                           
        }
        else {                                                  //if change is acceptable
            leftOldOutput = leftMotorsOutput;                   //set change to oldoutput
        }
        
        if (Math.abs(rightMotorsOutput-rightOldOutput)>0.5) {   //same for right
            if (rightMotorsOutput>0) {
                rightOldOutput += 0.5;
                }
            else if (rightMotorsOutput<0) {
                rightOldOutput -= 0.5;
                }
        }
        else {
            rightOldOutput = rightMotorsOutput;
        }
        
        leftMotorsOutput = leftOldOutput;                       //sets changes to motors
        rightMotorsOutput = rightOldOutput;
            
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
    
    /**
     * Sets the motors output with the motor output settings
     */
    private void setMotorsOutput(double leftMotorsOutput, double rightMotorsOutput) {
     leftDriveMotors.set(leftMotorsOutput);
     rightDriveMotors.set(rightMotorsOutput);
    }
    
    /**
     * Sends input and output of joystickBasedTestDrive()
     */
    private void sendTestDriveDiagnosticsSDB() {
        SmartDashboard.putNumber("Left Drive Input", m_testDriveJoystick.getRawAxis(2));
        SmartDashboard.putNumber("Right Drive Input", m_testDriveJoystick.getRawAxis(4));
        SmartDashboard.putNumber("Left Drive Output", leftDriveMotors.get());
        SmartDashboard.putNumber("Right Drive Output", rightDriveMotors.get());
    }
    
    public void resetEncoders() {
        leftDriveEncoder.reset();
        rightDriveEncoder.reset();
    }
    
    public void initEncoders() {
        if (!isEncodersStarted) {
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
    
    public boolean getServoDrivePosition() {
        if (leftDriveServo.get() == leftDriveServoDrivePosition && 
                rightDriveServo.get() == rightDriveServoDrivePosition) {
            return true;
        }
        else {
            return false;
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

        if (!getServoDrivePosition()){
            leftDriveServo.set(leftDriveServoDrivePosition);
            rightDriveServo.set(rightDriveServoDrivePosition);
        } 
    }
    
    public void setServoClimbPosition() {
        if (!getServoClimbPosition()) {
            leftDriveServo.set(leftDriveServoClimbPosition);
            rightDriveServo.set(rightDriveServoClimbPosition);
        }
    }
    
    /**
     * Uses two joysticks in a tank drive setup to run the motors
     */
    public void joystickBasedTankDrive() {
        getTankDriveJoystickInput();
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }
    
    public void joystickBasedTestDrive() {
        getTestDriveJoystickInput();
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
        sendTestDriveDiagnosticsSDB();
    }
    
    public void autoBasedDrive(double leftMotorOutput, double rightMotorOutput) {
        leftMotorsOutput = leftMotorOutput;
        rightMotorsOutput = rightMotorOutput;
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
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
    
    public void encoderTestDrive() {
        if (!isClimbMode) {
            joystickBasedTankDrive();          
        } else if (isClimbMode) {
            autoBasedDrive(-leftDriveJoystick.getY(), leftDriveJoystick.getY());
        }
        sendEncoderDriveDiagnosticsSDB();
        if (leftDriveJoystick.getTrigger()) {
            setServoDrivePosition();
            isClimbMode = false;
        } else if (rightDriveJoystick.getTrigger()) {
            setServoClimbPosition();
            isClimbMode = true;
        }
    }
    
    public void driveInit() {
        initEncoders();
        setServoDrivePosition();
    }
    
    public void disabledInit() {
        disableEncoders();
    }
    
    public void runDrive() {
        encoderTestDrive();
    }
}
