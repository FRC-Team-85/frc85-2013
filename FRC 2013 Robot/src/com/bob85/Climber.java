package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
    public static final int kDIO_CLIMBER_LIMITSWITCH_BOT = 8;  
    public static final int kDIO_CLIMBER_LIMITSWITCH_TOP = 9;
    public static final int kDIO_CLIMBER_LIMITSWITCH_TILT_REST = 10;
    public static final int kDIO_CLIMBER_LIMITSWITCH_TILT_EXTENT = 11;
    
    public static final int kPWM_CLIMBER_VICTOR_TILT = 10;
    
    public static final int kBUTTON_CLIMBER_REST = 3;
    public static final int kBUTTON_CLIMBER_EXTEND = 2;
    public static final int kBUTTON_CLIMBER_LOCK = 3; //joystick button to lock pin in climber gear
    public static final int kBUTTON_CLIMBER_UNLOCK = 2; //joystick button to unlock pin in climber gear
    
    Victor leftClimberMotors;
    Victor rightClimberMotors;
    Victor climberTiltMotor;
    Encoder leftClimberEncoder;
    Encoder rightClimberEncoder;
    Joystick leftStick;
    Joystick rightStick;
    
    private int encoderCPR = 250;
    private double encoderDistanceRatio = ((2 * Math.PI) / encoderCPR); //Every encoder revolution is 6.283 linear inches moved on the climber
    private int kClimberEncoderTopDist = 50; //maximum climber height in encoder distance in inches
    
    private double encoderClimberDistance;
    
    private double climberTiltOutput = 1;
    
    private double climberMotorOutput;
    
    private int climberStage;

    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    DigitalInput climberTiltRestLimitSwitch;
    DigitalInput climberTiltExtendLimitSwitch;
    
    Drive drive;
    
    private static final int kDriveState = 0; //at least one side of the drive is in drive
    private static final int kClimbState = 1; //both PTOs are in climb
    private static final int kClimbManualState = 1;
    private static final int kClimbAutoState = 2;
    private int climberState = kDriveState;
    
    private static final int kClimbAuto_ManualState = 0;
    private static final int kClimbAuto_TopInState = 1;
    private static final int kClimbAuto_TopOutState = 2;
    private static final int kClimbAuto_BotInState = 3;
    private static final int kClimbAuto_BotOutState = 4;
    private static final int kClimbAuto_NextLevelInState = 5; //latch hooks to go over corner
    private static final int kClimbAuto_NextLevelPullState = 6; //pull robot over corner
    private int climberAutoState = kClimbAuto_ManualState;
    private int climberAutoSavedState = kClimbAuto_TopInState;
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        setClimberEncodersDirection();
    }
   
    public Climber(Drive drive, Joystick leftStick, Joystick rightStick,
            Victor leftClimberMotors, Victor rightClimberMotors, Victor climberTiltMotor, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder,
            DigitalInput bottomClimberLimitSwitch, DigitalInput topClimberLimitSwitch, 
            DigitalInput limit_Climber_Tilt_Rest, DigitalInput limit_Climber_Tilt_Extend) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.climberTiltMotor = climberTiltMotor;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        this.climberTiltRestLimitSwitch = limit_Climber_Tilt_Rest;
        this.climberTiltExtendLimitSwitch = limit_Climber_Tilt_Extend;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        this.drive = drive;
    }
    
    /**
     * Resets the ClimberEncoders
     */
    private void resetClimberEncoders(){
        leftClimberEncoder.reset();
        rightClimberEncoder.reset();
    }
    
    /**
     * reverses the encoder reads when in Climber Mode
     */
    private void setClimberEncodersDirection(){
            leftClimberEncoder.setReverseDirection(false);
            rightClimberEncoder.setReverseDirection(true);
    }
    
    /**
     * Returns if bottom climber limit switch is reached
     * 
     * @return is climber elevator at bottom
     */
    private boolean getIsClimberBot() {
        return bottomClimberLimitSwitch.get();
    }
    
    /**
     * Returns if top climber limit switch is reached
     * @return is climber elevator at top
     */
    private boolean getIsClimberTop() {
        return !topClimberLimitSwitch.get();
    }
    
    /**
     * Get the Joystick outputs in the climberMode
     * @param joystick 
     */
    private void getJoystickInput(Joystick joystick) {
        climberMotorOutput = -joystick.getY();
    }
    
    /**
     * Stops the Climber Motors
     */
    private void stopClimb() {
        climberMotorOutput = 0;
        setClimberMotors();
    }
    
    /**
     * Assigns motorOutput to joystick Y axis and prevents climber from continuing past the limit switches
     */
    private void getClimbJoystickInputWithHardLimit() {
        getJoystickInput(rightStick);

        if (leftStick.getRawButton(5)) {
            getJoystickInput(rightStick);
    } else if (climberMotorOutput > 0 && getIsClimberTop()) {
            climberMotorOutput = 0;
        } else if (climberMotorOutput < 0 && getIsClimberBot()) {
            climberMotorOutput = 0;
        }  
    }

    /**
     * Changes the Motor output into a linear value correlating to the output setting
     */
    private void setLinearClimbOutput() {
        leftClimberMotors.set(-MotorLinearization.calculateLinearOutput(climberMotorOutput));
        rightClimberMotors.set(MotorLinearization.calculateLinearOutput(climberMotorOutput));
    }
    
    /**
     * Sets the Motors to the climberMotorOutput value
     */
    private void setClimberMotors() {
        leftClimberMotors.set(-climberMotorOutput);
        rightClimberMotors.set(climberMotorOutput);
    }
    
    /**
     * Computes the average climber distance and resets the values when the bottomLimitSwitch is hit 
     */
    private double getEncoderDistance() {        
        if (getIsClimberBot()) {
            rightClimberEncoder.reset();
            leftClimberEncoder.reset();
        }
        encoderClimberDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
        return encoderClimberDistance;
    }
    
    private void limitSwitchEncoderReset(){
        if (climberTiltRestLimitSwitch.get() || climberTiltExtendLimitSwitch.get()){
            leftClimberEncoder.reset();
            rightClimberEncoder.reset();
        }
    }
    
    /**
     * Drives the Climber tilting in and out with limits
     */
    private void setClimberTilt(){
        limitSwitchEncoderReset();
        
        if (leftStick.getRawButton(kBUTTON_CLIMBER_REST) && (!climberTiltRestLimitSwitch.get())){
            climberTiltMotor.set(climberTiltOutput);
        } else if (leftStick.getRawButton(kBUTTON_CLIMBER_EXTEND) && (!climberTiltExtendLimitSwitch.get())){
            climberTiltMotor.set(-climberTiltOutput);
        } else {
            climberTiltMotor.set(0);
        }
    }
    
    /**
     * Drives the Hooks and sets softLimits on the Hook Movement
     * 
     * @param joyStick Joystick input
     */
    public void manualJoystickElevDrive(Joystick joyStick){
        getEncoderDistance();
        drive.setMotorOutputDeadbands();
        getJoystickInput(joyStick);        
        
            if (getIsClimberTop() && -joyStick.getY() < 0) {
                stopClimb();
            } else if (getIsClimberBot() && -joyStick.getY() > 0) {
                stopClimb();
            } else {
                setLinearClimbOutput();
            }
        
    }
    
    /**
     * Sets a Button to a certain height in encoderCounts
     * 
     * @param joystick Joystick Input
     * @param topButton Button to be pressed
     * @param presetHeight an Encoder Value set as a Max Height Setpoint
     */
    public void setClimberToPresetHeight(Joystick joystick, int topButton, double presetHeight){
        if (joystick.getRawButton(topButton) == true){
            if (presetHeight > encoderClimberDistance && !getIsClimberTop()){
                climberMotorOutput = -0.5;
                setClimberMotors();
            } else {
                stopClimb();
            }
        }
    }
    
    /**
     * Sets the parameters for switching into Manual Climb
     */
    public void switchClimbStates() {
        switch (climberState) {
            case kDriveState:
                if (drive.getDriveState() == Drive.kClimbState) {
                    climberState = kClimbManualState;
                    resetClimberEncoders(); //reset encoders when robot swaps to climb
                } else if (drive.getDriveState() != Drive.kClimbState) {
                    climberState = kDriveState;
                }
                break;
            case kClimbManualState:
                if (drive.getDriveState() != Drive.kClimbState){
                    climberState = kDriveState;
                } else if (drive.getDriveState() == Drive.kClimbState && rightStick.getRawButton(12)){
                    climberState = kClimbAutoState;
                } else if (drive.getDriveState() == Drive.kClimbState && !rightStick.getRawButton(12)) {
                    climberState = kClimbManualState;
                }
                break;
            case kClimbAutoState:
                climberState = kClimbManualState;
        }

    }
    
    /**
     * Case 0 = do nothing
     * Case 1 = set the Joysticks for manualHookDriving
     * 
     */
    public void runClimbStates() {
        switch (climberState) {
            case kDriveState:
                
                break;
            case kClimbState:

                initEncoderSetting();     
                //getJoystickInput(rightStick);
                getClimbJoystickInputWithHardLimit();
                setLinearClimbOutput();
                break;
        }
    }
    
    public void switchAutoClimbStates() {
        switch (climberAutoState) {
            case kClimbAuto_ManualState:
                if (climberState == kClimbAutoState) {
                    climberAutoState = climberAutoSavedState;
                }
                break;
            case kClimbAuto_TopInState:
                if (true) {
                    climberAutoState = kClimbAuto_TopOutState;
                } else {
                    climberAutoSavedState = kClimbAuto_TopInState;
                }
                
            }
    }
    
    public void runAutoClimbStates() {
        
    }
     
    /**
     * Puts values for LimitSwitches on the SmartDashboard
     */
    public void runDiagnostics() {
        //SmartDashboard.putNumber("Encoder Avg Dist", encoderClimberDistance);
        //SmartDashboard.putNumber("Gear Lock Servo Pos.", gearLockServo.get());
        //SmartDashboard.putNumber("Latch Servo Pos.", hardStopLockServo.get());
        SmartDashboard.putBoolean("Climber Top Limit", getIsClimberTop());
        SmartDashboard.putBoolean("Climber Bot Limit", getIsClimberBot());
    } 
    
    /**
     * Set values when starting the Robot
     */
    public void initClimber() {
            climberState = kDriveState;
    } 
    
    /**
     * Executes Methods for the Main Robot Class
     */
    public void runClimber() {
        runDiagnostics();
        switchClimbStates();
        runClimbStates();
        setClimberTilt();
    }
}

