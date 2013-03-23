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
    
    Servo leftShiftServo;
    Servo rightShiftServo;
    private int kLeftShifterDrive = 0;
    private int kLeftShifterClimb = 1;
    private int kRightShifterDrive = 1;
    private int kRightShifterClimb = 0;
    
    Gyro gyro;
    
    Timer timer;
    
    public void Triller(SpeedController leftDriveMotors, SpeedController rightDriveMotors, SpeedController tiltClimberMotor,
            F310Gamepad gamepad, Servo leftDriveShiftServo, Servo rightDriveShiftServo, Gyro gyro, Timer timer){
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        this.tiltClimberMotor = tiltClimberMotor;
        this.gamepad = gamepad;
        this.leftShiftServo = leftDriveShiftServo;
        this.rightShiftServo = rightDriveShiftServo;
        this.gyro = gyro;
        this.timer = timer;
    }
    private void test(){
        timer.start();
        leftShiftServo.set(kLeftShifterDrive);
        rightShiftServo.set(kRightShifterClimb);
        if (timer.get() <= 3){
        leftDriveMotors.set(0.5);
        rightDriveMotors.set(0.5);}
        else if(timer.get() > 3 && timer.get() < 6){
        leftShiftServo.set(-0.5);
        rightShiftServo.set(-0.5);
        }
        else{
            leftDriveMotors.set(0);
            rightDriveMotors.set(0);
            timer.stop();
            timer.reset();
        }
    }
}
