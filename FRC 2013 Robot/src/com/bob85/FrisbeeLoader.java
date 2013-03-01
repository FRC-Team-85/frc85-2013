package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.AxisType;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FrisbeeLoader {
    
    public static final int kDROPSERVO_CHANNEL = 8; //Hopper Locking Pin Servo PWM Channel
    
    public static final int kHOPPERBELTMOTOR_CHANNEL = 7; //Hopper Belt Motor PWM Channel
    
    private Servo dropServo; //reference to hopper locking pin servo
    
    private Victor hopperBeltMotor; //reference to hopper belt motor
    
    private F310Gamepad gamepad; //reference to F310 Gamepad 
    
    private double hopperBeltMotorOutput; //hopper belt motor desired output setting
    
    private Timer timer; //hopper deploy frisbee timer
    private boolean timerReset; //hopper deploy frisbee reset
    private double time; //hopper deploy frisbee current time
    private double shiftTime = 0.3; //time to leave servo unlocked
    private boolean isShiftDone; //is servo shift for shot done
    
    private double unlockedPosition = 1;
    private double lockedPosition = 0;
    
    private final int kLockedServoState = 0;
    private final int kUnlockedServoState = 1;
    private static int hopperState;
    private double beltIntakeSpeed = 1;
    private double dropSpeed = 1;
    
    /**
     * Constructs Hopper subsystem object
     * @param dropServo Hopper Frisbee Lock Pin Servo
     * @param hopperBeltMotor Hopper Belt Motor
     * @param opPad F310 Gamepad
     */
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
    
    /**
     * Sets linearized hopper belt motor output
     */
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
         
    public void switchHopperStates(){
        switch(hopperState){
            case kLockedServoState:                
                hopperState = (gamepad.getButton(ButtonType.kRB)) ? kUnlockedServoState : kLockedServoState;
                break;
            case kUnlockedServoState:
                hopperState = (!gamepad.getButton(ButtonType.kRB)) ? kLockedServoState : kUnlockedServoState;
        }
    }
    
    public void runHopperStates() {
        switch (hopperState) {
            case kLockedServoState:
                lockServo();
                getGamepadDPadYAxis(1);
                setLinearizedOutput();
                break;                
            case kUnlockedServoState:
                unlockServo();
                getGamepadDPadYAxis(1);
                setLinearizedOutput();
                break;
        }
    }
    
    /**
     * Return hopper state
     * @return hopper state 0 for locked servo 1 for unlocked
     */
    public static int getHopperState(){
        return hopperState;
    }
    
    /**
     * Sends diagnostics of hopper to SmartDashboard
     */
    public void runDiagnostics() {
        SmartDashboard.putNumber("Hopper State", getHopperState());
        SmartDashboard.putBoolean("Servo Lock", getLockServo());
        SmartDashboard.putBoolean("Servo Unlock", getUnlockServo());
        SmartDashboard.putNumber("servo Position", dropServo.get());
    }
    
    /**
     * Runs operator control of the Hopper
     */
    public void runFrisbeeLoader() {
        switchHopperStates();
        runHopperStates();
        runDiagnostics();
    }
}
