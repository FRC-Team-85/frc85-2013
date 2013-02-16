package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Climber {

    private double encoderDistanceRatio = (1/1.9) * Math.PI; //Every encoder revolution is 1 linear inches moved on the climber
    private double encoderCPR = 250;
    private double calcEncDistance;
    private double leftMotorOutput;
    private double rightMotorOutput;
    
    private double linearClimberMotorOutputCoefficient = -0.058;
    private double linearClimberMotorOutputOffset = 1.5;
    boolean speedLimitReached = false;
    
    private double linearClimberDistance;
    
    private double climberMotorOutput;
    
    private int climberStage;
    
    public boolean inDriveMode;

    DigitalInput bottomClimberLimitSwitch;
    DigitalInput topClimberLimitSwitch;
    
    Victor leftClimberMotors;
    Victor rightClimberMotors;
    Encoder leftClimberEncoder;
    Encoder rightClimberEncoder;
    Servo leftShiftServo;
    Servo rightShiftServo;
    
    Drive drive = new Drive(leftClimberMotors, rightClimberMotors);
    
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
    
    public Climber(Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder, 
            Servo leftShiftServo, Servo rightShiftServo) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        this.leftShiftServo = leftShiftServo;
        this.rightShiftServo = rightShiftServo;
        
        initEncoderSetting();
    }
    
    public Climber(Victor leftClimberMotors, Victor rightClimberMotors, 
            Encoder leftClimberEncoder, Encoder rightClimberEncoder) {
        this.leftClimberMotors = leftClimberMotors;
        this.rightClimberMotors = rightClimberMotors;
        this.leftClimberEncoder = leftClimberEncoder;
        this.rightClimberEncoder = rightClimberEncoder;
        
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
                /** 1) Goes up at full speed then scales down to the top then stops at limit
                 *  2) Goes downward at a scaling speed increasing then decreases when speed > -0.8
                 *  3) Stops when Climber hits bottom limit
                 */ 
                if (topClimberLimitSwitch.get() != true) {
                    scaleStage1LinearClimberMotorOutputUp();
                    setClimberMotors();
                } else if (topClimberLimitSwitch.get() == true && bottomClimberLimitSwitch.get() != true) {
                    stopClimb();
                    scaleStage1LinearClimberMotorOutputDown();
                    setClimberMotors();
                } else if (bottomClimberLimitSwitch.get() == true){
                    stopClimb();
                }
                break;
                
            case 2:
                
            default:
                stopClimb();
        }
    }
    
    public void runLinearClimber(boolean isEnabled) {
        
    }
    
    public void manualJoystickElevDrive(Joystick joyStick, double topEncoderLimitValue){
        getEncoderDistance();
        climberMotorOutput = MotorLinearization.calculateLinearOutput(-joyStick.getY());
        drive.setMotorOutputDeadbands();
        
        if (inDriveMode != true) {
            if (topEncoderLimitValue <= linearClimberDistance && climberMotorOutput > 0) {
                stopClimb();
            }
            if (bottomClimberLimitSwitch.get() == true && climberMotorOutput < 0) {
                stopClimb();
            }
            if (topClimberLimitSwitch.get() != true && linearClimberDistance < topEncoderLimitValue) {
                setClimberMotors();
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
    
    /**
     *
     * @param auxStick Joystick
     * @param upButton Button to go up
     * @param downButton Button to go down
     * @param inDriveMode Boolean Check
     */
    public void manualButtonElevDrive(Joystick auxStick, double climbSpeed, int upButton, int downButton) {
        if (inDriveMode == false) {
            if (auxStick.getRawButton(upButton) == true && topClimberLimitSwitch.get() != true) {
                //Drive Elev Up
                climberMotorOutput = climbSpeed;
                setClimberMotors();
            } else if (auxStick.getRawButton(downButton) == true && bottomClimberLimitSwitch.get() != true) {
                //Drive Elev Down
                climberMotorOutput = -climbSpeed;
                setClimberMotors();
            } else {
                //Stop Elev
                stopClimb();
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
}
