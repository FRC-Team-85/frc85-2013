/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.*;


public class Thriller {
    
    SpeedController leftDriveMotors;
    SpeedController rightDriveMotors;
    SpeedController tiltClimberMotor;
    
    F310Gamepad gamepad;
    
    Encoder leftDriveEncoder;
    Encoder rightDriveEncoder;
    
    DigitalInput upperClimberLimitSwitch;
    DigitalInput lowerClimberLimitSwitch;
    DigitalInput restTiltClimberLimitSwitch;
    DigitalInput extendTiltClimberLimitSwitch;
    
    Servo leftShiftServo;
    Servo rightShiftServo;
    private int kLeftShiftDrive = 0;
    private int kLeftShiftClimb = 1;
    private int kRightShiftDrive = 1;
    private int kRightShiftClimb = 0;
    
    Gyro gyro;
    
    Timer timer;
    
    public void Triller(SpeedController leftDriveMotors, SpeedController rightDriveMotors, SpeedController tiltClimberMotor,
            F310Gamepad gamepad, Servo leftDriveShiftServo, Servo rightDriveShiftServo, Gyro gyro, Timer timer,
            DigitalInput upperClimberLimitSwitch, DigitalInput lowerClimberLimitSwitch,
            DigitalInput restTiltClimberLimitSwitch, DigitalInput extendTiltClimberLimitSwitch,
            Encoder leftDriveEncoder, Encoder rightDriveEncoder){
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        this.tiltClimberMotor = tiltClimberMotor;
        this.gamepad = gamepad;
        this.leftShiftServo = leftDriveShiftServo;
        this.rightShiftServo = rightDriveShiftServo;
        this.gyro = gyro;
        this.timer = timer;
        this.upperClimberLimitSwitch = upperClimberLimitSwitch;
        this.lowerClimberLimitSwitch = lowerClimberLimitSwitch;
        this.restTiltClimberLimitSwitch = restTiltClimberLimitSwitch;
        this.extendTiltClimberLimitSwitch = extendTiltClimberLimitSwitch;
    }

    private void setDriveMotors(double speed){
        leftDriveMotors.set(speed);
        rightDriveMotors.set(-speed);
    }
    
    /**
     * Set PTO's into Drive
     */
    private void setDriveMode(){
        leftShiftServo.set(kLeftShiftDrive);
        rightShiftServo.set(kRightShiftDrive);
    }
    
    /**
     * Set PTO's into Climb
     */
    private void setClimbMode(){
        leftShiftServo.set(kLeftShiftClimb);
        rightShiftServo.set(kRightShiftClimb);
    }
    
    /**
     * Toggle method for what mode the PTO's are in
     * 
     * @param leftInDrive 
     * @param rightInDrive 
     */
    private void setMixedPTOMode(boolean leftInDrive, boolean rightInDrive){
        if (leftInDrive){
            leftShiftServo.set(kLeftShiftDrive);
        } else {
            leftShiftServo.set(kRightShiftClimb);
        }
        
        if (rightInDrive){
            rightShiftServo.set(kRightShiftDrive);
        } else {
            rightShiftServo.set(kRightShiftClimb);
        }
    }
    
    /**
     * reset Encoders when lowerLimit is hit
     */
    private void resetEncoders(){
        if (lowerClimberLimitSwitch.get()){
            leftDriveEncoder.reset();
            rightDriveEncoder.reset();
        }
    }
    
    /**
     * Set climber tilt limits
     * 
     * @param speed desired speed for tilt
     */
    private void setClimberTiltLimits(double speed){
        if (restTiltClimberLimitSwitch.get() && tiltClimberMotor.get() < 0){
            tiltClimberMotor.set(0);
        } else if (extendTiltClimberLimitSwitch.get() && tiltClimberMotor.get() > 0){
            tiltClimberMotor.set(0);
        } else {
            tiltClimberMotor.set(speed);
        }
    }
    
    private void setClimberHookLimits(){
        if (upperClimberLimitSwitch.get()){
            if ((leftDriveMotors.get() > 0) || (rightDriveMotors.get() > 0)){
                setDriveMotors(0);
            }
        }
        
        if (lowerClimberLimitSwitch.get()){
            if ((leftDriveMotors.get() < 0) || (rightDriveMotors.get() < 0)){
                setDriveMotors(0);
            }
        }
    }
    
    
    
    
    
    private void test() {
        timer.start();
        leftShiftServo.set(kLeftShiftDrive);
        rightShiftServo.set(kRightShiftClimb);
        if (timer.get() <= 3) {
            leftDriveMotors.set(0.5);
            rightDriveMotors.set(0.5);
        } else if (timer.get() > 3 && timer.get() < 6) {
            leftShiftServo.set(-0.5);
            rightShiftServo.set(-0.5);
        } else {
            leftDriveMotors.set(0);
            rightDriveMotors.set(0);
            timer.stop();
            timer.reset();
        }
    }
    
    
}
