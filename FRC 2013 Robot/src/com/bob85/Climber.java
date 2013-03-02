package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
    public static final int kREST_LIMITSWITCH = 6;
    public static final int kEXTEND_LIMITSWITCH = 7;
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
    
    public boolean inDriveMode;
    
    private double speedSwitchPoint = -0.8;
    private double topEncoderLimitValue;

    DigitalInput restClimberLimitSwitch;
    DigitalInput extendClimberLimitSwitch;
    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    
    Drive drive;
    
    private int climberState;
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
    }
    
    /**
     * Constructs a Climber using: 2 joysticks, 2 encoders, and 4 limitSwitches.
     * 
     * @param drive
     * @param leftStick left Joystick
     * @param rightStick right Joystick
     * @param leftClimberMotors inverted driveMotors
     * @param rightClimberMotors inverted driveMotors
     * @param leftClimberEncoder left DriveEncoder
     * @param rightClimberEncoder right DriveEncoder
     * @param restClimberLimitSwitch switch to show ClimberArm is ready for Shooting
     * @param extendClimberLimitSwitch switch to show ClimberArm is ready for Climb
     * @param bottomClimberLimitSwitch bottom HookLimitSwitches
     * @param topClimberLimitSwitch top HookLimitSwitches
     * @param lockClimberServo pinLockGear Servo 
     */
    public Climber(Drive drive, Joystick leftStick, Joystick rightStick,
            Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder,
            DigitalInput restClimberLimitSwitch, DigitalInput extendClimberLimitSwitch,
            DigitalInput bottomClimberLimitSwitch, DigitalInput topClimberLimitSwitch,
            Servo lockClimberServo) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.restClimberLimitSwitch = restClimberLimitSwitch;
        this.extendClimberLimitSwitch = extendClimberLimitSwitch;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        this.lockClimberServo = lockClimberServo;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        this.drive = drive;
        initEncoderSetting();
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
     * 
     * @param inClimb boolean to show if the robot is in Climber Mode
     */
    private void reverseClimberEncoderReads(boolean inClimb){
        if (inClimb == true){
            leftClimberEncoder.setReverseDirection(true);
            rightClimberEncoder.setReverseDirection(false);
        } else if (inClimb == false){
            leftClimberEncoder.setReverseDirection(false);
            rightClimberEncoder.setReverseDirection(true);
        }
    }
    
    /**
     * Method used to Shift PTO servos using Buttons 2 & 3 on a Joystick
     * 
     * @param joystick Joystick Input 
     */
    private void joystickBasedShiftClimberLock() {
        if (rightStick.getRawButton(3)) {
            lockClimberServo.set(1);
        } else if (rightStick.getRawButton(2)) {
            lockClimberServo.set(0);
        }
    }
    
    /**
     * Method used to Shift One Side of the PTO
     * Button 4 shifts leftSide into Climber
     * Button 5 shifts rightSide into Climber
     * 
     * @param joystick 
     */
    private void joystickBasedShiftOneSide(Joystick joystick) {
        if (joystick.getRawButton(4)) {
            drive.setleftServoClimbPosition();
        }
        
        if (joystick.getRawButton(5)) {
            drive.setRightServoClimbPosition();
        }
    }
    
    /**
     * Get the Boolean from the restingArmSwitch on the climberMount
     * 
     * @return 
     */
    private boolean getIsRest() {
        return restClimberLimitSwitch.get();
    }
    
    /**
     * Get the Boolean from the extendedArmSwitch on the climberMount
     * 
     * @return 
     */
    private boolean getIsExtend() {
        return extendClimberLimitSwitch.get();
    }
    
    /**
     * Get the Boolean value from the bottomHook limitSwitch
     * 
     * @return 
     */
    private boolean getIsBot() {
        return bottomClimberLimitSwitch.get();
    }
    
    /**
     * Get the Boolean value from the topHook limitSwitch
     * 
     * @return 
     */
    private boolean getIsTop() {
        return topClimberLimitSwitch.get();
    }
    
    /**
     * Get the Joystick outputs in the climberMode
     * 
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
     * Changes the Motor output into a linear value correlating to the Joysticks
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
    private void calcAvgEncDistance() {
        encoderClimberDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
    }
    
    /**
     * Computes the average EncoderCounts and resets the values when the bottomLimitSwitch is hit 
     */
    private void getEncoderDistance() {
        calcAvgEncDistance();
        
        if (bottomClimberLimitSwitch.get() == true) {
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
        
            if (topClimberLimitSwitch.get() && -joyStick.getY() < 0) {
                stopClimb();
            } else if (bottomClimberLimitSwitch.get() && -joyStick.getY() > 0) {
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
        
        if (topClimberLimitSwitch.get() != true || encoderClimberDistance < topEncoderLimitValue){
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

        if (!bottomClimberLimitSwitch.get()) {
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
            if (presetHeight > encoderClimberDistance && topClimberLimitSwitch.get() != true){
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
                 if (bottomClimberLimitSwitch.get() && joystick.getRawButton(startAutoClimbButton)){
                     climberStage = 1;
                 }
                 break;
             case 1:
                 if (encoderClimberDistance >= topEncoderLimitValue || topClimberLimitSwitch.get() == true){
                     climberStage = 2;
                 }
                 break;
             case 2:
                 if (bottomClimberLimitSwitch.get()){
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
            case 0:
                if (drive.getDriveState() == Drive.kClimbState) {
                    climberState = 1;
                } else if (drive.getDriveState() != Drive.kClimbState) {
                    climberState = 0;
                }
            case 1:
                if (drive.getDriveState() != Drive.kClimbState){
                    climberState = 0;
                } else if (drive.getDriveState() == Drive.kClimbState){
                    climberState = 1;
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
            case 0:
                //resetClimberEncoders();
                reverseClimberEncoderReads(false);
                break;
            case 1:
                joystickBasedShiftClimberLock();
                //resetClimberEncoders();
                initEncoderSetting();
                reverseClimberEncoderReads(true);
                getJoystickInput(rightStick);
                setLinearClimbOutput();
                break;
        }
    }
     
    /**
     * Puts values for LimitSwitches on the SmartDashboard
     */
    public void runDiagnostics() {
        SmartDashboard.putBoolean("Rest LimitSwitch", getIsRest());
        SmartDashboard.putBoolean("Extend LimitSwitch", getIsExtend());
        SmartDashboard.putBoolean("Bot LimitSwitch", getIsBot());
        SmartDashboard.putBoolean("Top LimitSwitch", getIsTop());
    } 
    
    /**
     * Set values when starting the Robot
     */
    public void initClimber() {
        
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

