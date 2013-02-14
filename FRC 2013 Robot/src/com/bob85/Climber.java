package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Climber {

    private double encoderDistanceRatio = 0.150; //Every encoder revolution is 0.150 linear inches moved on the climber
    private double encoderCPR = 250;
    private double calcEncDistance;
    
    private double linearClimberMotorOutputCoefficient = -0.05;
    private double linearClimberMotorOutputOffset = 1.5;
    
    private double linearClimberDistance;
    
    private double climberMotorOutput;
    
    private int climberStage;
    
    private boolean inDriveMode = true;

    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    
    Victor leftClimberMotors;
    Victor rightClimberMotors;
    Encoder leftClimberEncoder;
    Encoder rightClimberEncoder;
    Servo leftShiftServo;
    Servo rightShiftServo;
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
    }
    
    
    public Climber(Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder, 
            DigitalInput bottomClimberLimitSwitch, DigitalInput topClimberLimitSwitch,
            Servo leftShiftServo, Servo rightShiftServo) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        this.leftShiftServo = leftShiftServo;
        this.rightShiftServo = rightShiftServo;
        
        initEncoderSetting();
    }
    
    private void stopClimb() {
        climberMotorOutput = 0;
    }
    
    private void calcAvgEncDistance() {
        calcEncDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
    }
    
    private void getEncoderDistance() {
        calcAvgEncDistance();
        linearClimberDistance = calcEncDistance;
        
        if (bottomClimberLimitSwitch.get()) {
            linearClimberDistance = 0;
            rightClimberEncoder.reset();
            leftClimberEncoder.reset();
        }
    }
    
    private void scaleStage1LinearClimberMotorOutput() {
        climberMotorOutput = (linearClimberMotorOutputCoefficient*linearClimberDistance + linearClimberMotorOutputOffset);
    }
    
    private void stage2LinearClimb() {
        climberMotorOutput = -1;
    }
    
    private void setClimberMotors() {
        leftClimberMotors.set(climberMotorOutput);
        rightClimberMotors.set(climberMotorOutput);
    }
    
    private void backDriveAtTop() {
        if (topClimberLimitSwitch.get() == true){
            stopClimb();
            
        }
    }
    
    public void setClimberStage(boolean isStage1, boolean isStage2, boolean isStage3) {
        if (isStage1 && climberStage < 2) {
            climberStage = 1;
        }
        else if (isStage2 && (climberStage == 1)) {
            climberStage = 2;
        }
        else if (isStage3 && (climberStage == 2)) {
            climberStage = 3;
        }
    }
    
    public void driveLinearClimber() {
        getEncoderDistance();
        
        switch(climberStage) {
            case 1:
                scaleStage1LinearClimberMotorOutput();
                setClimberMotors();
                break;
            case 2:
                
            default:
                stopClimb();
        }
    }
    
    public void runLinearClimber(boolean isEnabled) {
        
    }
    
    /**
     *
     * @param auxStick Joystick
     * @param upButton Button to go up
     * @param downButton Button to go down
     * @param inDriveMode Boolean Check
     */
    public void setManualElevDrive(Joystick auxStick, double climbSpeed, int upButton, int downButton) {
        if (inDriveMode == false) {
            if (auxStick.getRawButton(upButton) == true) {
                //Drive Elev Up
                climberMotorOutput = climbSpeed;
                setClimberMotors();
            } else if (auxStick.getRawButton(downButton) == true) {
                //Drive Elev Down
                climberMotorOutput = -climbSpeed;
                setClimberMotors();
            } else {
                //Stop Elev
                stopClimb();
                setClimberMotors();
            }

        }
    }
    /**
     * 
     * @param leftStick Joystick
     * @param driveShiftButton Button to go into driveMode
     * @param elevShiftButton Button to go into elevMode
     * @param leftShiftServo leftServo
     * @param rightShiftServo rightServo
     */
    public void driveElevShift(Joystick leftStick, int driveShiftButton, int elevShiftButton) {
        //elevModeButton
        if (leftStick.getRawButton(driveShiftButton) == true) {
            leftShiftServo.set(1.0);
            rightShiftServo.set(0.0);
            inDriveMode = false;
        }
        //driveModeButton  
        if (leftStick.getRawButton(elevShiftButton) == true) {
            leftShiftServo.set(0.0);
            rightShiftServo.set(1.0);
            inDriveMode = true;
        }
    }
    
    /**
     * 
     * @param auxStick Joystick
     * @param intakeRoller Motor for hopper
     * @param feedInButton Intake Button
     * @param feedOutButton Out Button
     */
    public void diskIntake(Joystick auxStick, SpeedController intakeRoller, int feedInButton, int feedOutButton){
        if (auxStick.getRawButton(feedInButton)){
            intakeRoller.set(0.6);
        }
        else if (auxStick.getRawButton(feedOutButton)){
            intakeRoller.set(-0.6);
        }
        else {
            intakeRoller.set(0);
        }
    }
}
