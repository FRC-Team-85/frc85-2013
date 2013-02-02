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
    
    private double getTime() {
        if (!timerReset) {
            timer.reset();
            timerReset = true;
            timer.start();
        }
        
        time = timer.get();
        return time;
    }
    
    public void loadFrisbee() {
        
    }
}
