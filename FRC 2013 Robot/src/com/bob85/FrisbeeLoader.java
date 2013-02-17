/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.AxisType;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class FrisbeeLoader {
    
    public static final int kDROPSERVO_CHANNEL = 8;
    
    public static final int kHOPPERBELTMOTOR_CHANNEL = 7;
    
    private Servo dropServo;
    
    private Victor hopperBeltMotor;
    
    private F310Gamepad opPad;
    
    private double hopperBeltSpeed;
    
    private Timer timer;
    private boolean timerReset;
    private double time;
    private double shiftTime = 0.3;
    private boolean isShiftDone;
    
    private double unlockedPosition = 1;
    private double lockedPosition = 0;
    
    private static int hopperState;
    private double beltIntakeSpeed = 1;
    private double dropSpeed = 1;
    
    public FrisbeeLoader(Servo dropServo, Victor hopperBeltMotor, 
            F310Gamepad opPad) {
        this.dropServo = dropServo;
        this.hopperBeltMotor = hopperBeltMotor;
        this.opPad = opPad;
        this.timer = new Timer();
        timer.reset();
    }
    
    public boolean getUnlockServo() {
        if (dropServo.get() == unlockedPosition) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean getLockServo() {
        if (dropServo.get() == lockedPosition) {
            return true;
        } else {
            return false;
        }
    }
      
    /**
     * Tell servo to pull pin out of hopper area
     * @param servo 
     */
    public void unlockServo(Servo servo) {
        if (!(servo.get() == unlockedPosition)) {
            servo.set(unlockedPosition);
        }
    }
    
    /**
     * Tell servo to push pin into hopper area
     * @param servo 
     */
    public void lockServo(Servo servo) {
        if (!(servo.get() == lockedPosition)) {
            servo.set(lockedPosition);
        }
    }
    /**
     * Sets the Hopper Belt Motor TODO: add motor linearization
     * @param speed desired input
     */
    public void setHopperBeltMotor(double speed) {
        hopperBeltSpeed = speed;
        hopperBeltMotor.set(-MotorLinearization.calculateLinearOutput(hopperBeltSpeed));
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
     * Servo positions to allow a frisbee to be locked for dropping to shooter
     * @param isEnabled 
     */
    private void readyFrisbeeServoPositions(boolean isEnabled) {
        if (isEnabled) {
            lockServo(dropServo);
        }
    }
    
    /**
     * Servo positions to allow locked frisbee to fall to shooter
     * @param isEnabled 
     */
    private void dropFrisbeeServoPositions(boolean isEnabled) {
        if (isEnabled) {
            unlockServo(dropServo);
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
            timerReset = false;
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
            }
            else if (getShiftDone()) {
                lockServo(dropServo);
            }
        } else {
            lockServo(dropServo);
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
    
    public void runAlexHopperSetup() {
        setHopperBeltMotor(opPad.getAxis(AxisType.kDPadY));
    }
    
    public void runFrisbeeLoader() {
        runAlexHopperSetup();
    }
    
    public void runHopperStates() {
        switch (hopperState) {
            case 0:
                lockServo(dropServo);
                if (opPad.getAxis(AxisType.kDPadY) == -1){
                    setHopperBeltMotor(beltIntakeSpeed);
                } else if (opPad.getAxis(AxisType.kDPadY) == 1) {
                    setHopperBeltMotor(-beltIntakeSpeed);
                }
                break;
                
            case 1:
                unlockServo(dropServo);
                break;
                
            case 2:
                unlockServo(dropServo);
                
                if (opPad.getAxis(AxisType.kDPadY) == -1){
                    setHopperBeltMotor(beltIntakeSpeed);
                } else if (opPad.getAxis(AxisType.kDPadY) == 1) {
                    setHopperBeltMotor(-beltIntakeSpeed);
                }
                break;
                
        }
    }
    
    public void switchHopperStates(){
        switch(hopperState){
            case 0: 
                
                if (opPad.getButton(ButtonType.kRB)){
                    hopperState = 1;
                }    
                break;
            case 1:
                if (Shooter.getShooterState() == 1){
                    hopperState = 2;
                }    
                break;
            case 2:
                if (!opPad.getButton(ButtonType.kRB) && Shooter.getShooterState() == 0) {
                    hopperState = 0;
                } else if (opPad.getButton(ButtonType.kRB) && !(Shooter.getShooterState() == 0)) {
                    hopperState = 1;
                
                }  
                break;
            default:
                lockServo(dropServo);
                setHopperBeltMotor(0);
        }
    }
    
    public static int getHopperState(){
        return hopperState;
    }
    
    public void sendDiagnosticsSDB() {
        SmartDashboard.putNumber("Hopper Belt", -hopperBeltMotor.get());
        SmartDashboard.putNumber("Hopper State", getHopperState());
        SmartDashboard.putBoolean("Servo Lock", getLockServo());
        SmartDashboard.putBoolean("Servo Unlock", getUnlockServo());
    }
}
