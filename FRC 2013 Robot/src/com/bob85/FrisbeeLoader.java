/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class FrisbeeLoader {
    
    public static final int kDROPSERVO_CHANNEL = 8;
    public static final int kREADYSERVO_CHANNEL = 9;
    
    public static final int kHOPPERBELTMOTOR_CHANNEL = 7;
    
    private Servo dropServo;
    private Servo readyServo;
    
    private Victor hopperBeltMotor;
    
    private double hopperBeltSpeed;
    
    private Timer timer;
    private boolean timerReset;
    private double time;
    private double shiftTime = 0.3;
    private boolean isShiftDone;
    
    private double unlockedPosition = 1;
    private double lockedPosition = 0;
    
    public FrisbeeLoader(Servo dropServo, Servo readyServo, Victor hopperBeltMotor) {
        this.dropServo = dropServo;
        this.readyServo = readyServo;
        this.hopperBeltMotor = hopperBeltMotor;
        this.timer = new Timer();
        timer.reset();
    }
    
    private void unlockServo(Servo servo) {
        if (!(servo.get() == unlockedPosition)) {
            servo.set(unlockedPosition);
        }
    }
    
    private void lockServo(Servo servo) {
        if (!(servo.get() == lockedPosition)) {
            servo.set(lockedPosition);
        }
    }
    /**
     * Sets the Hopper Belt Motor TODO: add motor linearization
     * @param speed desired input
     */
    private void setHopperBeltMotor(double speed) {
        hopperBeltSpeed = speed;
        hopperBeltMotor.set(hopperBeltSpeed);
    }
    
    /**
     * Returns the Hopper Belt Motor assigned speed 
     * Meant to show original input after motor output is linearized
     * @return Original Hopper Belt Motor Input
     */
    private double getHopperBeltMotor() {
        return hopperBeltSpeed;
    }
    
    /**
     * Runs a timer to check if the servo shift surpassed the time required
     * to drop a Frisbee
     * 
     * @return boolean returning if the time limit is passed
     */
    private boolean getShiftDone() {
        if (!timerReset) {
            timer.reset();
            timerReset = true;
            timer.start();
        }
        
        time = timer.get();
        
        if (time < shiftTime) {
            isShiftDone = false;
        } else if (time >= shiftTime) {
            timer.stop();
            isShiftDone = true;
        }
        
        return isShiftDone;
    }
    
    /**
     * Resets the boolean condition to reset timer in getShiftDone()
     */
    private void resetGetShiftDoneTimer() {
        timerReset = false;
    }
    
    /**
     * Unlocks & locks a servo to drop a Frisbee onto the shooter belt
     * 
     * @param isEnabled buttonIsPressed() boolean
     */
    public void loadFrisbee(boolean isEnabled) {
        if (isEnabled) {
            if (!getShiftDone()) {
                unlockServo(dropServo);
                lockServo(readyServo);
            }
            else if (getShiftDone()) {
                lockServo(dropServo);
                unlockServo(readyServo);
            }
        } else {
            lockServo(dropServo);
            lockServo(readyServo);
            resetGetShiftDoneTimer();
        }
    }
    
    /**
     * dropServo is the servo used to drop the frisbees into the shooter
     * 
     * @param dropServo lower servo on the loader
     * @param auxStick operator controls
     * @param dropServoButton joystick button used for command
     */
    public void dropFrisbee(Servo dropServo, Joystick auxStick, int dropServoButton) {
        if(auxStick.getRawButton(dropServoButton)){
            dropServo.set(1);
        }
        else{
            dropServo.set(0);
        }
    }
    
    private void testFrisbeeLoader() {

    }
    
    public void runFrisbeeLoader() {
        testFrisbeeLoader();
    }
}
