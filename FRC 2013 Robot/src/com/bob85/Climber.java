package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
    public static final int kREST_LIMITSWITCH = 6;
    public static final int kEXTEND_LIMITSWITCH = 7;
    public static final int kBOTTOM_LIMITSWITCH_CHANNEL = 8;  
    public static final int kTOP_LIMITSWITCH_CHANNEL = 9;
    
    Victor leftClimberMotors;
    Victor rightClimberMotors;
    Encoder leftClimberEncoder;
    Encoder rightClimberEncoder;
    Joystick leftStick;
    Joystick rightStick;
    
    private double encoderCPR = 250;
    private double encoderDistanceRatio = ((2 * Math.PI) / encoderCPR); //Every encoder revolution is 5.969 linear inches moved on the climber
    
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
    
    private void initEncoderSetting() {
        leftClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
        rightClimberEncoder.setDistancePerPulse(encoderDistanceRatio);
    }
    
    
    public Climber(Drive drive, Joystick leftStick, Joystick rightStick,
            Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder,
            DigitalInput restClimberLimitSwitch, DigitalInput extendClimberLimitSwitch,
            DigitalInput bottomClimberLimitSwitch, DigitalInput topClimberLimitSwitch) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.restClimberLimitSwitch = restClimberLimitSwitch;
        this.extendClimberLimitSwitch = extendClimberLimitSwitch;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        this.leftStick = leftStick;
        this.rightStick = rightStick;
        this.drive = drive;
        initEncoderSetting();
    }
    
    private boolean getIsRest() {
        return restClimberLimitSwitch.get();
    }
    
    private boolean getIsExtend() {
        return extendClimberLimitSwitch.get();
    }
    
    private boolean getIsBot() {
        return bottomClimberLimitSwitch.get();
    }
    
    private boolean getIsTop() {
        return topClimberLimitSwitch.get();
    }
    
    private void getJoystickInput(Joystick joystick) {
        climberMotorOutput = -joystick.getY();
    }
    
    private void stopClimb() {
        climberMotorOutput = 0;
        setClimberMotors();
    }
    
    private void setLinearClimbOutput() {
        leftClimberMotors.set(-MotorLinearization.calculateLinearOutput(climberMotorOutput));
        rightClimberMotors.set(MotorLinearization.calculateLinearOutput(climberMotorOutput));
    }
    
    private void setClimberMotors() {
        leftClimberMotors.set(-climberMotorOutput);
        rightClimberMotors.set(climberMotorOutput);
    }
    
    private void calcAvgEncDistance() {
        encoderClimberDistance = ((rightClimberEncoder.getDistance() + leftClimberEncoder.getDistance()) / 2 );
    }
    
    private void getEncoderDistance() {
        calcAvgEncDistance();
        
        if (bottomClimberLimitSwitch.get() == true) {
            encoderClimberDistance = 0;
            rightClimberEncoder.reset();
            leftClimberEncoder.reset();
        }
    }
    
    public void manualJoystickElevDrive(Joystick joyStick){
        getEncoderDistance();
        drive.setMotorOutputDeadbands();
                
        if (drive.getIsClimb()) {
            if (topClimberLimitSwitch.get() && -joyStick.getY() > 0) {
                stopClimb();
            } else if (bottomClimberLimitSwitch.get() && -joyStick.getY() < 0) {
                stopClimb();
            } else {
                getJoystickInput(joyStick);
                setLinearClimbOutput();
            }
        }
    }
    
    private void scaleStage1LinearClimberMotorOutputUp() {
        getEncoderDistance();
        
        if (topClimberLimitSwitch.get() != true || encoderClimberDistance < topEncoderLimitValue){
        climberMotorOutput = (linearClimberMotorOutputCoefficient * encoderClimberDistance + linearClimberMotorOutputOffset);
        } else {
            stopClimb();
        }
    }
    
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
     
    public void runDiagnostics() {
        SmartDashboard.putBoolean("Rest LimitSwitch", getIsRest());
        SmartDashboard.putBoolean("Extend LimitSwitch", getIsExtend());
        SmartDashboard.putBoolean("Bot LimitSwitch", getIsBot());
        SmartDashboard.putBoolean("Top LimitSwitch", getIsTop());
    } 
     
    public void initClimber() {
        
    } 
     
    public void runClimber() {
        runDiagnostics();
        drive.setJoystickBasedPTOShift();
        if (drive.getIsClimb()) {
            getJoystickInput(rightStick);
            setLinearClimbOutput();
        }
    }
}

