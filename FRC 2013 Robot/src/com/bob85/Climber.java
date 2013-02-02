package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Climber {

    private double encoderDistanceRatio = 0.150; //Every encoder revolution is 0.150 linear inches moved on the climber
    private double encoderCPR = 250;
    
    private double linearClimberMotorOutputCoefficient = -0.05;
    private double linearClimberMotorOutputOffset = 1.5;
    
    private double linearClimberDistance;
    
    private double linearClimberMotorOutput;
    
    private int climberStage;

    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    
    Victor tiltClimber;
    Victor linearClimber;
    Encoder linearClimberEncoder;
    
    private void initializeEncoderSetting() {
        linearClimberEncoder.setDistancePerPulse(encoderDistanceRatio/encoderCPR);
    }
    
    public Climber(Victor tiltClimber, Victor linearClimber, 
            Encoder linearClimberEncoder, DigitalInput bottomClimberLimitSwitch,
            DigitalInput topClimberLimitSwitch) {
        this.tiltClimber = tiltClimber;
        this.linearClimber = linearClimber;
        this.linearClimberEncoder = linearClimberEncoder;
        this.bottomClimberLimitSwitch = bottomClimberLimitSwitch;
        this.topClimberLimitSwitch = topClimberLimitSwitch;
        
        initializeEncoderSetting();
    }
    
    private void stopClimb() {
        linearClimberMotorOutput = 0;
    }
    
    private void getEncoderDistance() {
        linearClimberDistance = linearClimberEncoder.getDistance();
        
        if (bottomClimberLimitSwitch.get()) {
            linearClimberDistance = 0;
            linearClimberEncoder.reset();
        }
    }
    
    private void scaleStage1LinearClimberMotorOutput() {
        linearClimberMotorOutput = (linearClimberMotorOutputCoefficient*linearClimberDistance + linearClimberMotorOutputOffset);
    }
    
    private void stage2LinearClimb() {
        linearClimberMotorOutput = -1;
    }
    
    private void setLinearClimberMotor() {
        linearClimber.set(linearClimberMotorOutput);
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
                setLinearClimberMotor();
                break;
            case 2:
                
            default:
                stopClimb();
        }
    }
    
    public void runLinearClimber(boolean isEnabled) {
        
    }
    
    public void elevatorHooks(Encoder elevEnc, Joystick auxStick, int upButton, int downButton, SpeedController leftDriveMotor, SpeedController rightDriveMotor, double elevDriveSpeed, boolean inElevMode) {

        if (inElevMode == true && auxStick.getRawButton(upButton) == true) {
            //Drive Elev Up
            leftDriveMotor.set(elevDriveSpeed);
            rightDriveMotor.set(-elevDriveSpeed);
        } else if (inElevMode == true && auxStick.getRawButton(downButton) == true) {
            //Drive Elev Down
            leftDriveMotor.set(-elevDriveSpeed);
            rightDriveMotor.set(elevDriveSpeed);
        } else {
            //Stop Elev
            leftDriveMotor.set(0);
            rightDriveMotor.set(0);
        }

    }

    public void driveElevShift(Joystick leftStick, int driveShiftButton, int elevShiftButton, Servo leftShiftServo, Servo rightShiftServo, boolean inElevMode) {
        //driveModeButton
        if (leftStick.getRawButton(driveShiftButton) == true) {
            leftShiftServo.set(0.0);
            rightShiftServo.set(1.0);
            inElevMode = false;
        //elevModeButton    
        } else if (leftStick.getRawButton(elevShiftButton) == true) {
            leftShiftServo.set(1.0);
            rightShiftServo.set(0.0);
            inElevMode = true;
        }
    }
    
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
