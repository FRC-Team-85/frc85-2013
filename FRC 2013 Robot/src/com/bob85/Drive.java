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
    
    private Servo leftDriveServo; //reference to left PTO servo
    private Servo rightDriveServo; //reference to right PTO servo
    
    private Encoder leftDriveEncoder; //reference to left PTO encoder
    private Encoder rightDriveEncoder; //reference to right PTO encoder
    
    private Gyro gyro; //reference to drive gyro
    
    Joystick leftDriveJoystick; //reference to left drive joystick
    Joystick rightDriveJoystick; //reference to right drive joystick
    
    public static final int kLEFTDRIVE_VICTORS = 1; //left drive PWM channel
    public static final int kRIGHTDRIVE_VICTORS = 2; //right drive PWM channel
    
    public static final int kLEFTDRIVE_SERVO = 3; //left drive PTO PWM channel
    public static final int kRIGHTDRIVE_SERVO = 4; //right drive PTO PWM channel
    
    public static final int kLEFTDRIVE_ENCODER_A = 1; //left drive encoder A Digital I/O Channel
    public static final int kLEFTDRIVE_ENCODER_B = 2; //left drive encoder B Digital I/O Channel
    public static final int kRIGHTDRIVE_ENCODER_A = 3; //right drive encoder A Digital I/O Channel
    public static final int kRIGHTDRIVE_ENCODER_B = 4; //right drive encoder B Digital I/O Channel
    
    public static final int kGYRO = 1; //drive gyro analog I/O channel
    
    private static final int kBUTTON_SHIFT_DRIVE = 3; //shift PTOs to drive joystick button
    private static final int kBUTTON_SHIFT_CLIMB = 2; //shift PTOs to climb joystick button
    private static final int kBUTTON_SHIFT_CLIMB_LEFT = 4; //shift left PTO to climb joystick button
    private static final int kBUTTON_SHIFT_CLIMB_RIGHT = 5; //shift right PTO to climb joystick button
    
    private boolean isEncodersStarted = false; //holds encoder enable state
    
    private double leftMotorsOutput; //left drive motor output setting
    private double rightMotorsOutput; //right drive motor output setting
    private double leftLinearMotorsOutput; //left drive linearized actual motor output setting
    private double rightLinearMotorsOutput; //right drive linearized actual motor output setting
            
    private double deadband = 0.2; //Deadband for drive motor output
    private final double changeLimit_val = 0.25; //maxmimum change limit value for motor output
    private double changeLimit = changeLimit_val; //change limit variable
    
    private double leftDriveServoDrivePosition = 0; //left PTO drive servo position
    private double rightDriveServoDrivePosition = 1; //right PTO drive servo position
    private double leftDriveServoClimbPosition = 1; //left PTO climb servo position
    private double rightDriveServoClimbPosition = 0; //right PTO climb servo position
    
    private int encoderCPR = 250; //Encoder counts per revolution value
    private double encoderDistanceRatio = (((4 * Math.PI) / 10.3) / encoderCPR); //Each encoder pulse = 1.22 inches traveled
    
    private final int kDriveState = 0; //drive finite state
    private final int kLeftClimbRightDriveState = 1; //left climb right drive finite state
    private final int kRightClimbLeftDriveState = 2; //right climb left drive finite state
    public static final int kClimbState = 3; //climb finite state
    private int driveState = kDriveState; //current drive state
    
    private final int kDriveLimitedDriveState = 0;
    private final int kDriveFullDriveState = 1;
    private final int kDriveReverseDriveState = 2;
    private int driveDriveState = kDriveLimitedDriveState; //Drive states in the kDriveState
    
    /**
     * Initialize sensor settings
     */
    private void initDriveSettings() {
        gyro.setSensitivity(.007);
        initEncoders();
    }
    
    /**
     * Constructs Drive object with drive motors, servos, encoders, gyro, and joysticks
     * @param leftDriveMotors Left Drive Speed Controllers & Motors
     * @param rightDriveMotors Right Drive Speed Controllers & Motors
     * @param leftDriveServo Left Drive PTO Servo Shifter
     * @param rightDriveServo Right Drive PTO Servo Shifter
     * @param leftDriveEncoder Left Drive PTO Encoder
     * @param rightDriveEncoder Right Drive PTO Encoder
     * @param gyro Drive Gyro
     * @param leftDriveJoystick //Joystick 1
     * @param rightDriveJoystick  //Joystick 2
     */
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
        initDriveSettings();
    }
    
    /**
     * Assigns the Y Axis of the left and right joysticks to MotorsOutput variable
     * @param reverseLeft Reverse left drive input
     * @param reverseRight Reverse right drive input
     * @param scaleFactor multiply input
     */
    public void getJoystickYAxisInputs(boolean reverseLeft, boolean reverseRight, double scaleFactor) {
        leftMotorsOutput = (reverseLeft) ? leftDriveJoystick.getY() : -leftDriveJoystick.getY();
        rightMotorsOutput = (reverseRight) ? rightDriveJoystick.getY() : -rightDriveJoystick.getY();
        leftMotorsOutput *= scaleFactor;
        rightMotorsOutput *= scaleFactor;
    }
    
    /**
     * Assigns the Y Axis of the left joystick to the rightMotorsOutput and right joystick to leftMotorsOutput
     * @param reverseLeft  reverse left drive input
     * @param reverseRight  reverse right drive input
     * @param scaleFactor  multiply inputs
     */
    public void getSwappedJoystickYAxisInputs(boolean reverseLeft, boolean reverseRight, double scaleFactor){
         leftMotorsOutput = (reverseLeft) ? rightDriveJoystick.getY() : -rightDriveJoystick.getY();
        rightMotorsOutput = (reverseRight) ? leftDriveJoystick.getY() : -leftDriveJoystick.getY();
        leftMotorsOutput *= scaleFactor;
        rightMotorsOutput *= scaleFactor;
    }
    
    /**
     * Maps the motor outputs to the joysticks' Y axis
     */
    private void getTankDriveJoystickInput() {
            switch(driveDriveState) {
                    case kDriveLimitedDriveState:
                           if (leftDriveJoystick.getTrigger()) {
                                    driveDriveState = kDriveReverseDriveState;
                            } else if (rightDriveJoystick.getTrigger()) {
                                    driveDriveState = kDriveFullDriveState;
                            }
                            break;
                    case kDriveFullDriveState:
                              if (leftDriveJoystick.getTrigger()) {
                                    driveDriveState = kDriveReverseDriveState;
                            }
                              else  if (!rightDriveJoystick.getTrigger()) {
                                    driveDriveState = kDriveLimitedDriveState;
                            } 
                            break;
                    case kDriveReverseDriveState:
                            if (!leftDriveJoystick.getTrigger() && !rightDriveJoystick.getTrigger()) {
                                    driveDriveState = kDriveLimitedDriveState;
                            } else if (!leftDriveJoystick.getTrigger() && rightDriveJoystick.getTrigger()) {
                                    driveDriveState = kDriveFullDriveState;
                            }
                            break;
            }
            switch (driveDriveState) {
                    case kDriveLimitedDriveState:
                            getJoystickYAxisInputs(false, false, 0.675);
                            break;
                    case kDriveFullDriveState:
                            getJoystickYAxisInputs(false, false, 1);
                            break;
                    case kDriveReverseDriveState:
                           getSwappedJoystickYAxisInputs(false, false, 0.675);
                            break;
            }
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
     * Sets motor input setting to a linearized desired output
     */
    public void setLinearizedOutput() {
        leftLinearMotorsOutput = MotorLinearization.calculateLinearOutput(leftMotorsOutput);
        rightLinearMotorsOutput = MotorLinearization.calculateLinearOutput(rightMotorsOutput);
        leftDriveMotors.set(leftLinearMotorsOutput);
        rightDriveMotors.set(-rightLinearMotorsOutput);
    }
    
    /**
     * Limits maximum change in motor input limited to linear output until I inverse equation
     * @param isLeft enable limit for left input
     * @param isRight enable limit for right input
     */
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
    
    /**
     * Resets PTO encoders counts to 0
     */
    public void resetEncoders() {
        if (leftDriveEncoder != null && rightDriveEncoder != null) {
            leftDriveEncoder.reset();
            rightDriveEncoder.reset();
        }
    }
    
    /**
     * Start counting on PTO encoders
     */
    public void enableEncoders() {
        if (!isEncodersStarted && leftDriveEncoder != null && rightDriveEncoder != null) {
            leftDriveEncoder.start();
            rightDriveEncoder.start();
            isEncodersStarted = true;
        }    
    }
    
    /**
     * Initialize encoder distance per pulse and direction settings
     */
    public void initEncoders() {            
            leftDriveEncoder.setDistancePerPulse(encoderDistanceRatio);
            rightDriveEncoder.setDistancePerPulse(encoderDistanceRatio);
            
            rightDriveEncoder.setReverseDirection(true);
    }
    
    /**
     * Stop counting on PTO encoders
     */
    public void disableEncoders() {
        if (isEncodersStarted) {
            leftDriveEncoder.stop();
            rightDriveEncoder.stop();
            isEncodersStarted = false;
        }
    }
    
    /**
     * Return average distance of the two drive PTO encoders
     * @return 
     */
    public double getAverageEncodersDistance() {
        return (leftDriveEncoder.getDistance() + rightDriveEncoder.getDistance()) / 2;
    }
    
    /**
     * Sends diagnostics to SmartDashboard
     */
    public void runEncoderDiagnostics() {
        SmartDashboard.putNumber("Left Drive Encoder Dist", leftDriveEncoder.getDistance());
        SmartDashboard.putNumber("Right Drive Encoder Dist", rightDriveEncoder.getDistance());
        SmartDashboard.putNumber("Left Drive Encoder", leftDriveEncoder.get());
        SmartDashboard.putNumber("Right Drive Encoder", rightDriveEncoder.get());
 
    }
    
    /**
     * Gets current robot angle
     * @return robot angle in degrees (right is positive motion, left is negative)
     */
    public double getAngle() {
        return gyro.getAngle();
    }
    
    /**
     * Resets gyro angle to 0
     */
    public void resetGyro() {
        gyro.reset();
    }
    
    /**
     * Returns if servos are in drive position
     * @return are servos in drive position
     */
    public boolean getServoDrivePosition() {
        if (leftDriveServo.get() == leftDriveServoDrivePosition && 
                rightDriveServo.get() == rightDriveServoDrivePosition) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns if servos are in climb position
     * @return are servos in climb position
     */
    public boolean getServoClimbPosition() {
       if (leftDriveServo.get() == leftDriveServoClimbPosition && 
                rightDriveServo.get() == rightDriveServoClimbPosition) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Sets servo positions to shift PTO to drive
     */
    public void setServoDrivePosition() {
        leftDriveServo.set(leftDriveServoDrivePosition);
        rightDriveServo.set(rightDriveServoDrivePosition);
    }
    
    /**
     * Sets servo positions to shift PTO to climb
     */
    public void setServoClimbPosition() {
        leftDriveServo.set(leftDriveServoClimbPosition);
        rightDriveServo.set(rightDriveServoClimbPosition);
    }
    
    /**
     * Sets servo positions to shift Left PTO to climb and right to drive
     */
    public void setleftServoClimbPosition() {
        leftDriveServo.set(leftDriveServoClimbPosition);
        rightDriveServo.set(rightDriveServoDrivePosition);
    }
    
    /**
     * Sets servo positions to shift left pto to drive and right to climb
     */
    public void setRightServoClimbPosition() {
        leftDriveServo.set(leftDriveServoDrivePosition);
        rightDriveServo.set(rightDriveServoClimbPosition);
    }
    
    /**
     * Sends SmartDashboard diagnostics of Drive State
     */
    private void sendServoPositionsDiagnostics() {
        SmartDashboard.putNumber("leftDriveServo", leftDriveServo.get());
        SmartDashboard.putNumber("rightDriveServo", rightDriveServo.get());
    }
    
    private void sendDriveStateDiagnostics() {
        SmartDashboard.putNumber("Drive State", driveState);
    }
    
    /**
     * Applies deadband and limited output change before setting a linearized
     * motor output based on current motor output settings
     */
    public void setFilteredMotorOutput() {
        setMotorOutputDeadbands();
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }     
    
    /**
     * Increase motor output at a constant rate until max speed is reached
     * @param maxSpeed  max speed set point
     */
    public void runRampUpTrapezoidalMotionProfile(double maxSpeed) {
        setMotorOutputSetting(maxSpeed, maxSpeed);
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }
    
    /**
     * Decrease motor output at a constant rate until minimum speed is reached
     * @param minSpeed min speed set point
     */
    public void runRampDownTrapezoidalMotionProfile(double minSpeed) {
        leftMotorsOutput = minSpeed;
        rightMotorsOutput = minSpeed;
        limitMotorsOutputChange(true, true);
        setLinearizedOutput();
    }
    
    /**
     * Switches between Drive States based on joystick button toggles
     */
    public void switchDriveStates() {
        switch (driveState) {
            case kDriveState:
                if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB_RIGHT)) {
                    driveState = kRightClimbLeftDriveState;
                } else if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB_LEFT)) {
                    driveState = kLeftClimbRightDriveState;
                } else if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB)) {
                    driveState = kClimbState;
                }
                break;
            case kLeftClimbRightDriveState:
                if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_DRIVE)) {
                    driveState = kDriveState;
                } else if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB_RIGHT)) {
                    driveState = kClimbState;
                } else if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB)) {
                    driveState = kClimbState;
                }
                break;
            case kRightClimbLeftDriveState:
                if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_DRIVE)) {
                    driveState = kDriveState;
                } else if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB_LEFT)) {
                    driveState = kClimbState;
                } else if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_CLIMB)) {
                    driveState = kClimbState;
                }
                break;
            case kClimbState:
                if (leftDriveJoystick.getRawButton(kBUTTON_SHIFT_DRIVE)) {
                    driveState = kDriveState;
                }
                break;
        }
    }
       
    /**
     * Runs Drive controls based on Drive State
     */
    public void runDriveStates() {
        switch (driveState) {
            case kDriveState:
                initEncoders();
                setServoDrivePosition();
                getTankDriveJoystickInput();
                setFilteredMotorOutput();
                break;
            case kLeftClimbRightDriveState:
                setleftServoClimbPosition();
                getJoystickYAxisInputs(true, false, .75);
                setFilteredMotorOutput();
                break;
            case kRightClimbLeftDriveState:
                setRightServoClimbPosition();
                getJoystickYAxisInputs(false, true, .75);
                setFilteredMotorOutput();
                break;
            case kClimbState:
                setServoClimbPosition();
                break;
        }
    }
    
    /**
     * Returns current Drive State
     * @return 0 = drive, 1 = left climb right drive, 2 = left drive right climb
     */
    public int getDriveState() {
        return driveState;
    }
    
    /**
     * Reset and set actuators, sensors, drive state for Drive
     */
    public void initDrive() {
        resetGyro();
        resetEncoders();
        enableEncoders();
        setServoDrivePosition();
        driveState = kDriveState;
    }
    
    /**
     * Reset and disable actuators & sensors for Drive
     */
    public void disableDrive() {
        disableEncoders();
        resetEncoders();
        gyro.reset();
    }
    
    /**
     * Enables actuators and sensors for Drive in teleop
     */
    public void runDrive() {
        enableEncoders();
        sendDriveStateDiagnostics();
        runEncoderDiagnostics();
        switchDriveStates();
        runDriveStates();        
    }
}
