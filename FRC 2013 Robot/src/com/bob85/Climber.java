package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Climber {
    
    public static final int kBOTTOM_LIMITSWITCH_SLOT = 1;
    public static final int kBOTTOM_LIMITSWITCH_CHANNEL = 8;
    
    public static final int kTOP_LIMITSWITCH_SLOT = 1;
    public static final int kTOP_LIMITSWITCH_CHANNEL = 9;
    
    Victor leftClimberMotors;
    Victor rightClimberMotors;
    Encoder leftClimberEncoder;
    Encoder rightClimberEncoder;
    Joystick leftStick;
    Joystick rightStick;
    
    private double encoderCPR = 250;
    private double encoderDistanceRatio = ((1.9 * Math.PI) / encoderCPR); //Every encoder revolution is 5.969 linear inches moved on the climber
    private double calcEncDistance;
    
    private double linearClimberMotorOutputCoefficient = -0.058;
    private double linearClimberMotorOutputOffset = 1.5;
    boolean speedLimitReached = false;
    
    private double linearClimberDistance;
    
    private double climberMotorOutput;
    
    private int climberStage;
    
    public boolean inDriveMode;

    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    
    
    
    Drive drive = new Drive(leftClimberMotors, rightClimberMotors);
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
    }
    
    
    public Climber(Joystick leftStick, Joystick rightStick,
            Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder, 
            DigitalInput bottomClimberLimitSwitch, DigitalInput topClimberLimitSwitch) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        
        initEncoderSetting();
    }
    
    private void stopClimb() {
        climberMotorOutput = 0;
        setClimberMotors();
    }
    
    private void calcAvgEncDistance() {
        calcEncDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
    }
    
    private void getEncoderDistance() {
        calcAvgEncDistance();
        linearClimberDistance = calcEncDistance;
        
        if (bottomClimberLimitSwitch.get() == true) {
            linearClimberDistance = 0;
            rightClimberEncoder.reset();
            leftClimberEncoder.reset();
        }
    }
    
    private void scaleStage1LinearClimberMotorOutputUp() {
        climberMotorOutput = (linearClimberMotorOutputCoefficient * linearClimberDistance + linearClimberMotorOutputOffset);
    }
    
    private void scaleStage1LinearClimberMotorOutputDown(){
        
        double speedSwitchPoint = -0.8;
        
        if (climberMotorOutput > speedSwitchPoint && speedLimitReached != true) {
            //Speed increases
            climberMotorOutput = (-linearClimberMotorOutputCoefficient * linearClimberDistance - linearClimberMotorOutputOffset);
        } else if (climberMotorOutput <= speedSwitchPoint){
            speedLimitReached = true;
            //Speed decreases 
            climberMotorOutput = (linearClimberMotorOutputCoefficient * linearClimberDistance - 0.1);
        } else {
            stopClimb();
        }
    }
    
    private void stage2LinearClimb() {
        climberMotorOutput = -1;
    }
    
    private void setClimberMotors() {
        leftClimberMotors.set(-climberMotorOutput);
        rightClimberMotors.set(climberMotorOutput);
    }
    
    private void backDriveAtTop() {
        if (topClimberLimitSwitch.get() == true){
            stopClimb();
            
        }
    }
    
    public void runLinearClimber(boolean isEnabled) {
        
    }
    
    public void manualJoystickElevDrive(Joystick joyStick, double topEncoderLimitValue){
        getEncoderDistance();
        drive.setMotorOutputDeadbands();
                
        if (inDriveMode != true) {
            if (topEncoderLimitValue <= linearClimberDistance && joyStick.getY() > 0) {
                stopClimb();
            }
            if (bottomClimberLimitSwitch.get() == true && joyStick.getY() < 0) {
                stopClimb();
            }
            if (topClimberLimitSwitch.get() != true && linearClimberDistance < topEncoderLimitValue) {
                MotorLinearization.linearizeVictor884Output(leftClimberMotors, -joyStick.getY());
                MotorLinearization.linearizeVictor884Output(rightClimberMotors, joyStick.getY());
            } else {
                stopClimb();
            }
        }

    }
    
    public void setClimberToPresetHeight(Joystick joystick, int topButton, double presetHeight){
        if (joystick.getRawButton(topButton) == true){
            if (presetHeight > linearClimberDistance && topClimberLimitSwitch.get() != true){
                climberMotorOutput = -0.5;
                setClimberMotors();
            } else {
                stopClimb();
            }
        }
    }
    
     public void runAutoClimb(double topEncoderLimit) {
        
        switch(climberStage) {
            case 0:
                manualJoystickElevDrive(rightStick, topEncoderLimit);
                break;
            case 1:
                break;
            default:
                stopClimb();
        }
    }
    
     public void switchAutoClimb(int startAutoClimbButton){
         switch(climberStage){
             case 0:
                 if (bottomClimberLimitSwitch.get() && rightStick.getRawButton(startAutoClimbButton)){
                     climberStage = 1;
                 }
                 break;
             case 1:
                 break;
             default:
                 stopClimb();
         }
    }
}

