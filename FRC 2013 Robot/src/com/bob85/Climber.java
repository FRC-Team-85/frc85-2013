package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
    public static final int kBOTTOM_LIMITSWITCH_CHANNEL = 8;  
    public static final int kTOP_LIMITSWITCH_CHANNEL = 9;
    
    public static final int kCLIMBERLOCK_SERVO = 9;
    
    Victor leftClimberMotors;
    Victor rightClimberMotors;
    Encoder leftClimberEncoder;
    Encoder rightClimberEncoder;
    Joystick leftStick;
    Joystick rightStick;
    Servo lockClimberServo;
    
    private int encoderCPR = 250;
    private double encoderDistanceRatio = ((2 * Math.PI) / encoderCPR); //Every encoder revolution is 6.283 linear inches moved on the climber
    
    private double linearClimberMotorOutputCoefficient = -0.058;
    private double linearClimberMotorOutputOffset = 1.5;
    boolean speedLimitReached = false;
    
    private double encoderClimberDistance;
    
    private double climberMotorOutput;
    
    private int climberStage;
    
    private double speedSwitchPoint = -0.8;
    private double topEncoderLimitValue;

    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    
    Drive drive;
    
    private static final int kDriveState = 0; //at least one side of the drive is in drive
    private static final int kClimbState = 1; //both PTOs are in climb
    private int climberState = kDriveState;
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
    }
    
    public Climber(Drive drive, Joystick leftStick, Joystick rightStick,
            Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder,
            DigitalInput bottomClimberLimitSwitch, DigitalInput topClimberLimitSwitch,
            Servo lockClimberServo) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        this.lockClimberServo = lockClimberServo;
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
            leftClimberEncoder.setReverseDirection(true);
            rightClimberEncoder.setReverseDirection(false);
    }
    
    private void lockClimberGearServo() {
        lockClimberServo.set(1);
    }
    
    private void unlockClimberGearServo() {
        lockClimberServo.set(0);
    }
    
    /**
     * Shifts climber gear locking pin
     */
    private void shiftClimberGearLockJoystickInput() {
        if (leftStick.getRawButton(3)) {
            lockClimberGearServo();
        } else if (leftStick.getRawButton(2)) {
            unlockClimberGearServo();
        }
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
        
        if (climberMotorOutput > 0 && getIsClimberTop()) {
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
     * Finds the average encoder count value of both Drive Encoders
     */
    private void getAvgEncDistance() {
        encoderClimberDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
    }
    
    /**
     * Computes the average EncoderCounts and resets the values when the bottomLimitSwitch is hit 
     */
    private void getEncoderDistance() {
        getAvgEncDistance();
        
        if (getIsClimberBot()) {
            encoderClimberDistance = 0;
            rightClimberEncoder.reset();
            leftClimberEncoder.reset();
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
     * Sets the Hooks to drive upwards to an encoder count and stops
     * 
     * NOT TESTED, STILL THEORECTICAL
     * 
     */
    private void scaleStage1LinearClimberMotorOutputUp() {
        getEncoderDistance();
        
        if (!getIsClimberTop() && encoderClimberDistance < topEncoderLimitValue){
        climberMotorOutput = (linearClimberMotorOutputCoefficient * encoderClimberDistance + linearClimberMotorOutputOffset);
        } else {
            stopClimb();
        }
    }
    
    /**
     * Drives the ClimberHooks moves downward in a scaling fashion
     * 
     * NOT TESTED, STILL THEORETICAL 
     */
    private void scaleStage2LinearClimberMotorOutputDown() {

        if (!getIsClimberBot()) {
            if (climberMotorOutput > speedSwitchPoint && speedLimitReached != true) {//Speed increases
                climberMotorOutput = (-linearClimberMotorOutputCoefficient * encoderClimberDistance - linearClimberMotorOutputOffset);
            } else if (climberMotorOutput <= speedSwitchPoint) {//Speed decreases 
                speedLimitReached = true;
                climberMotorOutput = (linearClimberMotorOutputCoefficient * encoderClimberDistance - 0.1);
            } else {
                stopClimb();
            }
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
     * Case 0 = sets the Hooks on a Manual Control
     * Case 1 = auto drives up
     * Case 2 = auto drives down
     * 
     * NOT TESTED, STILL THEORECTICAL 
     */
     public void runAutoClimb() {
        
        switch(climberStage) {
            case 0:
                manualJoystickElevDrive(rightStick);
                break;
            case 1:
                scaleStage1LinearClimberMotorOutputUp();
                break;
            case 2:
                scaleStage2LinearClimberMotorOutputDown();
                break;
            default:
                stopClimb();
        }
    }
    
     public void switchAutoClimb(Joystick joystick, int startAutoClimbButton){
         switch(climberStage){
             case 0:
                 if (getIsClimberBot() && joystick.getRawButton(startAutoClimbButton)){
                     climberStage = 1;
                 }
                 break;
             case 1:
                 if (encoderClimberDistance >= topEncoderLimitValue || getIsClimberTop()){
                     climberStage = 2;
                 }
                 break;
             case 2:
                 if (getIsClimberBot()){
                     climberStage = 3;
                 }
                 break;
             default:
                 stopClimb();
         }
    }
    
    /**
     * Sets the parameters for switching into Manual Climb
     */
    public void switchClimbStates() {
        switch (climberState) {
            case kDriveState:
                if (drive.getDriveState() == Drive.kClimbState) {
                    climberState = kClimbState;
                    resetClimberEncoders(); //reset encoders when robot swaps to climb
                } else if (drive.getDriveState() != Drive.kClimbState) {
                    climberState = kDriveState;
                }
            case kClimbState:
                if (drive.getDriveState() != Drive.kClimbState){
                    climberState = kDriveState;
                } else if (drive.getDriveState() == Drive.kClimbState){
                    climberState = kClimbState;
                }
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
                shiftClimberGearLockJoystickInput();
                initEncoderSetting();
                setClimberEncodersDirection();
                //getJoystickInput(rightStick);
                getClimbJoystickInputWithHardLimit();
                setLinearClimbOutput();
                break;
        }
    }
     
    /**
     * Puts values for LimitSwitches on the SmartDashboard
     */
    public void runDiagnostics() {
        SmartDashboard.putBoolean("Bot LimitSwitch", getIsClimberBot());
        SmartDashboard.putBoolean("Top LimitSwitch", getIsClimberTop());
        SmartDashboard.putNumber("Climber Avg Dist", encoderClimberDistance);
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
    }
}

