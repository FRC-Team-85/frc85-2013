package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
    public static final int kDIO_CLIMBER_LIMITSWITCH_BOT = 8;  
    public static final int kDIO_CLIMBER_LIMITSWITCH_TOP = 9;
    public static final int kDIO_CLIMBER_LIMITSWITCH_TILT_REST = 10;
    public static final int kDIO_CLIMBER_LIMITSWITCH_TILT_EXTENT = 12;
    
    public static final int kPWM_CLIMBER_VICTOR_TILT = 9;
    
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
    F310Gamepad gamepad;
    
    private int encoderCPR = 250;
    private double encoderDistanceRatio = ((2 * Math.PI) / encoderCPR); //Every encoder revolution is 6.283 linear inches moved on the climber
    private int kClimberEncoderTopDist = 270; //maximum climber height in encoder distance in inches
    private int kClimberFullStrokeExtend = 262; //level 1+ extend climb
    private int kClimberPartialStrokeExtend = 140; //bottom level climb
    private int kClimberFullStrokeRetract = 0;
    
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
    
    private int climberLevel = 0; //current level the robot is on
    
    private static final int kClimbLatch_Null_State = 0;
    private static final int kClimbLatch_Extend_State = 1;
    private static final int kClimbLatch_Latch_State = 2;
    private static final int kClimbLatch_Complete_State = 3;
    private int climberLatchState = kClimbLatch_Null_State; 
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        setClimberEncodersDirection();
    }
   
    public Climber(Drive drive, Joystick leftStick, Joystick rightStick, F310Gamepad gamepad,
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
        this.gamepad = gamepad;
        this.drive = drive;
    }
    
    private boolean getNuclearLaunchDetected() {
        if (leftStick.getTrigger() && rightStick.getTrigger() && 
            gamepad.getButton(ButtonType.kLStick) && gamepad.getButton(F310Gamepad.ButtonType.kRStick)) {
                return true;
        }
        return false;
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
    
    private boolean getIsClimberTiltRest() {
        return climberTiltRestLimitSwitch.get();
    }
    
    private boolean getIsClimberTiltExtent() {
        return climberTiltExtendLimitSwitch.get();
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
        return topClimberLimitSwitch.get();
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
        resetClimberEncoderAtBottom();
        encoderClimberDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
        return encoderClimberDistance;
    }
    
    /**
     * Resets the encoders if the 
     */
    private void resetClimberEncoderAtBottom(){
        if (getIsClimberBot()){
            leftClimberEncoder.reset();
            rightClimberEncoder.reset();
        }
    }
    
    /**
     * Drives the Climber tilting in and out with limits
     */
    private void setClimberTilt(){
        resetClimberEncoderAtBottom();
        
        if (gamepad.getButton(ButtonType.kLB) && !getIsClimberTiltExtent()){
            climberTiltMotor.set(climberTiltOutput);
        } else if (gamepad.getButton(ButtonType.kLF) && !getIsClimberTiltRest()){
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
    public boolean setClimberToPresetHeight(double presetHeight, double speed){
            if (presetHeight == 0) {
                if (!getIsClimberBot() && getEncoderDistance() > 10) {
                    climberMotorOutput = -speed;
                    setClimberMotors();
                } else if(!getIsClimberBot() && getEncoderDistance() < 10) {
                    climberMotorOutput = speed * -0.65;
                    setClimberMotors();
                } else {
                    stopClimb();
                    return true;
                }
            } else if (presetHeight > 0) {
                if (!getIsClimberTop() && getEncoderDistance() < presetHeight - 10) {
                    climberMotorOutput = speed;
                    setClimberMotors();
                } else if (!getIsClimberTop() && getEncoderDistance() > (presetHeight - 10) && 
                    getEncoderDistance() < presetHeight) {
                    climberMotorOutput = speed *0.65;
                    setClimberMotors();
                } else {
                    stopClimb();
                    return true;
                }
            }
            return false;
    }
    
    public boolean runClimberAutoDriveUpwardsComplete(double desiredPosition, double driveSpeed, double scaleFactor){
        if (getEncoderDistance() >= desiredPosition){
            climberMotorOutput = 0;
            return true;
        } else {
            climberMotorOutput = (driveSpeed * scaleFactor);
            return false;
        }
    }
    
    public boolean runClimberAutoDriveDownwardsComplete(double desiredPosition, double driveSpeed, double scaleFactor){
        if (getEncoderDistance() <= desiredPosition){
            climberMotorOutput = 0;
            return true;
        } else {
            climberMotorOutput = (driveSpeed * scaleFactor);
            return false;
        }
    }
    
    public boolean setClimberAutoLatch(double desiredPosition, double driveSpeed) {
        switch (climberLatchState){
            case kClimbLatch_Null_State:
                climberMotorOutput = 0;
                climberLatchState = kClimbLatch_Extend_State;
                break;
            case kClimbLatch_Extend_State:
                if (setClimberToPresetHeight(desiredPosition, driveSpeed)){
                    climberLatchState = kClimbLatch_Complete_State;
                } else {
                    climberLatchState = kClimbLatch_Extend_State;
                }
                break;
            case kClimbLatch_Complete_State:
                climberMotorOutput = 0;
                return true;
        }    
        return false;
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
                } else if (drive.getDriveState() == Drive.kClimbState && getNuclearLaunchDetected()){
                    climberState = kClimbAutoState;
                }
                break;
            case kClimbAutoState:
                if (drive.getDriveState() != Drive.kClimbState){
                    climberState = kDriveState;
                } else if (drive.getDriveState() == Drive.kClimbState && gamepad.getButton(ButtonType.kB)) {
                    climberState = kClimbManualState;
                }
        }

    }
    
    /**
     * Case 0 = do nothing
     * Case 1 = set the Joysticks for manualDrive
     * Case 2 = set AutoClimb
     * 
     */
    public void runClimbStates() {
        
        setClimberTilt();
        
        switch (climberState) {
            case kDriveState:        
                break;
            case kClimbManualState:
                initEncoderSetting();     
                //getJoystickInput(rightStick);
                getClimbJoystickInputWithHardLimit();
                setLinearClimbOutput();
                break;
            case kClimbAutoState:
                initEncoderSetting();
                runAutoClimbStates();
                break;
        }
    }
    
    public void runAutoClimbStates() {
        switch (climberAutoState) {
            case kClimbAuto_ManualState:
                if (climberState == kClimbAutoState) {
                    climberAutoState = climberAutoSavedState;
                }
                break;
            case kClimbAuto_TopInState:
                if (climberLevel == 0) {
                    if (setClimberToPresetHeight(kClimberPartialStrokeExtend, 0.8)) {
                        climberLatchState = kClimbLatch_Null_State;
                        climberAutoState = kClimbAuto_TopOutState;
                    } else {
                        climberAutoSavedState = kClimbAuto_TopInState;
                    }
                } else if (climberLevel >= 1) {
                    if (setClimberToPresetHeight(kClimberFullStrokeExtend, 0.8)) {
                        climberLatchState = kClimbLatch_Null_State;
                        climberAutoState = kClimbAuto_TopOutState;
                    } else {
                        climberAutoSavedState = kClimbAuto_TopInState;
                    }
                }
                    
                break;
            case kClimbAuto_TopOutState:
                if (setClimberToPresetHeight(kClimberFullStrokeRetract, 1)) {
                    climberLatchState = kClimbLatch_Null_State;
                    climberLevel++;
                    if (climberLevel < 3) {
                    climberAutoState = kClimbAuto_BotInState;
                    } else {
                        climberAutoSavedState = kClimbAuto_TopOutState;
                    }
                } else {
                    climberAutoSavedState = kClimbAuto_TopOutState;
                }
                break;
            case kClimbAuto_BotInState:
                if (setClimberToPresetHeight(kClimberFullStrokeExtend, 0.8)) {
                    climberLatchState = kClimbLatch_Null_State;
                    climberAutoState = kClimbAuto_BotOutState;
                } else {
                    climberAutoSavedState = kClimbAuto_BotInState;
                }
                break;
            case kClimbAuto_BotOutState:
                if (setClimberToPresetHeight(kClimberFullStrokeRetract, 1)) {              
                    climberLatchState = kClimbLatch_Null_State;
                    climberAutoState = kClimbAuto_TopInState;
                } else {
                    climberAutoSavedState = kClimbAuto_BotOutState;
                }
        }
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
        SmartDashboard.putBoolean("Climber Rest Limit", getIsClimberTiltRest());
        SmartDashboard.putBoolean("Climber Extent Limit", getIsClimberTiltExtent());
        //SmartDashboard.putNumber("Climber Level", climberLevel);
        //SmartDashboard.putNumber("Climber State", climberState);
        //SmartDashboard.putNumber("Climber Auto State", climberAutoState);
        //SmartDashboard.putNumber("Climber Auto Saved State", climberAutoSavedState);
    } 
    
    /**
     * Set values when starting the Robot
     */
    public void initClimber() {
            climberState = kDriveState;
            climberLevel = 0;
            climberAutoState = kClimbAuto_ManualState;
            climberAutoSavedState = kClimbAuto_TopInState;
    } 
    
    /**
     * Executes Methods for the Main Robot Class
     */
    public void runClimber() {
        getEncoderDistance();
        //runDiagnostics();
        switchClimbStates();
        runClimbStates();
        setClimberTilt();

    }
}

