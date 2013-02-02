/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class FrisbeeLoader {
    
    private Servo dropServo;
    private Servo readyServo;
    
    private Timer timer;
    private boolean timerReset;
    private double time;
    private double shiftTime = 0.3;
    private boolean isShiftDone;
    
    private double unlockedPosition = 1;
    private double lockedPosition = 0;
    
    public FrisbeeLoader(Servo dropServo, Servo readyServo) {
        this.dropServo = dropServo;
        this.readyServo = readyServo;
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
}
