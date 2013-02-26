package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.AxisType;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FrisbeeLoader {
    
    public static final int kDROPSERVO_CHANNEL = 8;
    
    public static final int kHOPPERBELTMOTOR_CHANNEL = 7;
    
    private Servo dropServo;
    
    private Victor hopperBeltMotor;
    
    private F310Gamepad gamepad;
    
    private double hopperBeltMotorOutput;
    
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
        this.gamepad = opPad;
        this.timer = new Timer();
        timer.reset();
    }
    
    /**
     * Sets motor output setting to D Pad Y Axis value
     * @param scaleFactor multiplication factor on output
     */
    public void getGamepadDPadYAxis(double scaleFactor) {
        hopperBeltMotorOutput = -gamepad.getAxis(AxisType.kDPadY);
        hopperBeltMotorOutput *= scaleFactor;
    }
    
    /**
     * Get is servo pin unlocked
     * @return 
     */
    public boolean getUnlockServo() {
        if (dropServo.get() == unlockedPosition) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Get is servo pin locked
     * @return 
     */
    public boolean getLockServo() {
        if (dropServo.get() == lockedPosition) {
            return true;
        } else {
            return false;
        }
    }
      
    /**
     * Tell servo to pull pin out of hopper area
     */
    public void unlockServo() {
        if (!(dropServo.get() == unlockedPosition)) {
            dropServo.set(unlockedPosition);
        }
    }
    
    /**
     * Tell servo to push pin into hopper area
     */
    public void lockServo() {
        if (!(dropServo.get() == lockedPosition)) {
            dropServo.set(lockedPosition);
        }
    }
    
    /**
     * Sets the Hopper Belt Motor Input Settings
     * @param speed negative is down and positive is up
     */
    public void setMotorOutputSetting(double speed) {
        hopperBeltMotorOutput = speed;
    }
    
    public void setLinearizedOutput() {
        hopperBeltMotor.set(MotorLinearization.calculateLinearOutput(hopperBeltMotorOutput));
    }
    
    /**
     * Returns the Hopper Belt Motor assigned speed 
     * Meant to show original input after motor output is linearized
     * @return Original Hopper Belt Motor Input
     */
    private double getHopperBeltSpeed() {
        return hopperBeltMotorOutput;
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
                unlockServo();
            }
            else if (getShiftDone()) {
                lockServo();
            }
        } else {
            lockServo();
            resetGetShiftDoneTimer();
        }
    }
    
    public void runAlexHopperSetup() {
        getGamepadDPadYAxis(1);
        setLinearizedOutput();
        if (gamepad.getButton(ButtonType.kRB)) {
            unlockServo();
        } else {
            lockServo();
        }
    }
    
    public void runHopperStates() {
        switch (hopperState) {
            case 0:
                lockServo();
                getGamepadDPadYAxis(1);
                break;                
            case 1:
                unlockServo();
                getGamepadDPadYAxis(1);
                break;
        }
    }
    
    public void switchHopperStates(){
        switch(hopperState){
            case 0:                
                hopperState = (gamepad.getButton(ButtonType.kRB)) ? 1 : 0;
                break;
            case 1:
                hopperState = (!gamepad.getButton(ButtonType.kRB)) ? 0 : 1;
        }
    }
    
    public static int getHopperState(){
        return hopperState;
    }
    
    public void runDiagnostics() {
        SmartDashboard.putNumber("Hopper State", getHopperState());
        SmartDashboard.putBoolean("Servo Lock", getLockServo());
        SmartDashboard.putBoolean("Servo Unlock", getUnlockServo());
        SmartDashboard.putNumber("servo Position", dropServo.get());
    }
    
    public void runFrisbeeLoader() {
        switchHopperStates();
        runHopperStates();
        runDiagnostics();
    }
}
