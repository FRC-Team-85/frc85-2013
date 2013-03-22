package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
    
    public static final String key_shooterRPMCheck = "Shooter On Target";
    
    
    
    public static final int kPWM_SHOOTER_VICTOR_WHEEL = 5;
    public static final int kPWM_SHOOTER_VICTOR_BELT = 6;
    public static final int kDIO_SHOOTER_HALLEFFECT_WHEEL = 5;
    
    private final int kStopShooterState = 0;
    private final int kRunShooterNotAtTargetRPMState = 1;
    private final int kRunShooterAtTargetRPMState = 2;
    private static int shooterState = 0; // 0 is standby, 1 is readying, 2 is shoot
    private static int shooterSetpointState = 1;
    
    private double kOnTargetRPMTolerance = -50;
    
    private static final double kPWM_TO_RPM = 4350;
    private static final double kRPM_TO_PWM = (1/4350);
    
    public static final int kSHOOTER_RPM_MAX_SPEED_SETPOINT = 4350;
    public static final int kSHOOTER_RPM_PYRAMID_BACK_SETPOINT = 4000;
 
    public static int shooterSetpoint = kSHOOTER_RPM_MAX_SPEED_SETPOINT;
    private Victor shooterMotor;
    private Victor shooterBeltMotor;
    
    private double shooterBeltMotorOutput;
    
    private HallEffect shooterHalleffect;
    
    private F310Gamepad gamepad;
    
    public static final ButtonType kBUTTON_SHOOT_FULL_SPEED = ButtonType.kRB;
    public static final ButtonType kBUTTON_SHOOT_PYRAMID_SPEED = ButtonType.kLB;
    
    private PIDController shooterPID;
    
    private void initShooterSettings() {
        
        shooterHalleffect.setMaxPeriod(0.2);
        if (shooterPID != null) {
            shooterPID.setSetpoint(kSHOOTER_RPM_PYRAMID_BACK_SETPOINT);
            shooterPID.setPercentTolerance(2);
            shooterPID.setInputRange(0, kPWM_TO_RPM);
            shooterPID.setOutputRange(0, 1);
        }
    }
    
    public Shooter(Victor shooterMotor, Victor shooterBeltMotor, 
            HallEffect shooterHalleffect, 
            F310Gamepad joystick) {
        this.shooterMotor = shooterMotor;
        this.shooterBeltMotor = shooterBeltMotor;
        this.shooterHalleffect = shooterHalleffect;
        this.gamepad = joystick;
        shooterPID = new PIDController(0.000232, 0, 0, 0.5, shooterHalleffect, shooterMotor, 0.02);
        initShooterSettings();
    }
    
    /**
     * Returns an RPM's equivalence in a 0 to 1 PWM scale
     * 
     * @param RPM Revolutions per minute
     * @return 
     */
    public static double convertRPMtoPWM(int RPM) {
        double pwmValue ;
        pwmValue = RPM * kRPM_TO_PWM;
        pwmValue = (pwmValue > 1) ? 1 : pwmValue;
        return pwmValue;
    }
    
    /**
     * Returns an PWM's equivalence as an RPM
     * @param PWM -1.0 to 1.0 PWM signal
     * @return 
     */
    public static int convertPWMtoRPM(double PWM) {
        int rpmValue;
        rpmValue = (int) (PWM * kPWM_TO_RPM);
        return rpmValue;
    }
    
    public void setShooterSpeed(double speed) {
        shooterMotor.set(speed);
    }
    
    public boolean onTarget() {
        if ((shooterHalleffect.getRPM() -  kSHOOTER_RPM_MAX_SPEED_SETPOINT) >  kOnTargetRPMTolerance) {
            return true;
        } else {
            return false;
        }
    }
    
    public void setShooterBeltSpeed(double speed) {
        shooterBeltMotorOutput = speed;
        shooterBeltMotor.set(-shooterBeltMotorOutput);
    }
    
    public double getShooterBeltSpeed() {
        return shooterBeltMotorOutput;
    }
    
    public void runShooterBelt() {
        setShooterBeltSpeed(1);
    }
    
    /**
     * set shooter and belt with 0 output
     */
    public void setShooterToRest() {
        setShooterBeltSpeed(0);
        setShooterSpeed(0);
    }
    
    /**
     * sets shooter and belt with 100% output
     */
    public void setShooterMaxSpeed() {
        setShooterSpeed(1);
    }
    
    /**
     * Runs a rudimentary closed feedback loop shooter control
     * @param setpointRPM target RPM
     */
    public void runBangBangSpeedControl(int setpointRPM) {
        double output;
        output = (shooterHalleffect.getRPM() > setpointRPM) ? 0 : 1;
        setShooterSpeed(output);
        setShooterBeltSpeed(1);
    }
    
    /**
     * Change the Shooter PID Setpoint 
     * @param setpoint desired RPM
     */
    public void setPIDShooterSetpoint(int setpoint) {
        if (shooterPID.getSetpoint() != setpoint) {
            shooterPID.setSetpoint(setpoint);
        }
    }
    
    public void disablePIDShooter() {
       if (shooterPID.isEnable()) {
           shooterPID.disable();
       }
       setShooterBeltSpeed(0);
    }
    
    public void runPIDShooter() {
        if (!shooterPID.isEnable()) {
            shooterPID.enable();
        }
        setShooterBeltSpeed(1);
    }
    
    /**
     * Returns shooter state
     * @return 0 = 0 output, 1 = shooter not at correct RPM, 2 = shooter at correct RPM
     */
    public static int getShooterState() {
        return shooterState;
    }
    
    public void runDiagnostics() {
        SmartDashboard.putNumber("Shooter RPM", shooterHalleffect.getRPM());
        SmartDashboard.putBoolean(key_shooterRPMCheck, onTarget());
        //SmartDashboard.putNumber("Shooter State", getShooterState());
    }
    
    private void switchShooterSetpointStates() {
        switch (shooterSetpointState) {
            case 0:
                shooterSetpointState = (gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED)) ? 1 : 0;
                break;
            case 1:
                shooterSetpointState = (gamepad.getButton(kBUTTON_SHOOT_PYRAMID_SPEED)) ? 0 : 1;
                break;
        }
    }
    
    /**
     * Changes Shooter Setpoint based on current Setpoint State
     */
    private void runShooterSetpointStates() {
        switch (shooterSetpointState) {
            default:
                shooterSetpoint = kSHOOTER_RPM_MAX_SPEED_SETPOINT;
                break;
        }
        setPIDShooterSetpoint(shooterSetpoint);
    }
    
    private void switchShooterStates() {
        switch (shooterState) {
            case kStopShooterState:
                shooterState = (gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED)) ? kRunShooterNotAtTargetRPMState : kStopShooterState;
                break;
            case kRunShooterNotAtTargetRPMState:
                if (gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED) && onTarget()) {
                    shooterState = kRunShooterAtTargetRPMState;
                } else if (!gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED)) {
                    shooterState = kStopShooterState;
                } else if (gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED) && !onTarget()) {
                    shooterState = kRunShooterNotAtTargetRPMState;
                }
                break;
            case kRunShooterAtTargetRPMState:
                if (!gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED)) {
                    shooterState = kStopShooterState;
                } else if (gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED) && !onTarget()) {
                    shooterState = kRunShooterNotAtTargetRPMState;
                } else if (gamepad.getButton(kBUTTON_SHOOT_FULL_SPEED) && onTarget()) {
                    shooterState = kRunShooterAtTargetRPMState;
                }
                break;
        }
    }
    
    public void runShooterStates() {
        switch (shooterState) {
            case kStopShooterState:
                setShooterToRest();
                break;
            case kRunShooterNotAtTargetRPMState:
                setShooterMaxSpeed();
                if (gamepad.getButton(ButtonType.kY)) {
                    setShooterBeltSpeed(1);
                } else {
                    setShooterBeltSpeed(0);
                }
                break;
            case kRunShooterAtTargetRPMState:
                setShooterMaxSpeed();
                if (gamepad.getButton(ButtonType.kY)) {
                    setShooterBeltSpeed(1);
                } else {
                    setShooterBeltSpeed(0);
                }
                break;
        }
    }
     
    /**
     * Run shooter control 
     */
    public void runBangBangShooterStates() {
        switch (shooterState) {
            case kStopShooterState:
                setShooterToRest();
                break;
            case kRunShooterNotAtTargetRPMState:
                runBangBangSpeedControl(shooterSetpoint);
                break;
            case kRunShooterAtTargetRPMState:
                runBangBangSpeedControl(shooterSetpoint);
                break;
        }
    }
    
    /**
     * Run shooter control depending on current shooter state
     */
    public void runPIDShooterStates() {
        switch (shooterState) {
            case kStopShooterState:
                disablePIDShooter();
                break;
            case kRunShooterNotAtTargetRPMState:
                runPIDShooter();
                break;
            case kRunShooterAtTargetRPMState:
                runPIDShooter();
                break;
        }
    }
    
    /**
     * disables and reset shooter sensors
     */
    public void disableShooter() {
        shooterHalleffect.stop();
        shooterHalleffect.reset();
        setShooterSpeed(0);
        setShooterBeltSpeed(0);
    }
    
    /**
     * runs control and diagnostics of the shooter
     */
    public void runShooter() {
        switchShooterStates();
        switchShooterSetpointStates();
        runShooterSetpointStates();
        runShooterStates();
        runDiagnostics();
    }
    
    /**
     * resets and enables shooter sensors
     */
    public void initShooter() {
        shooterHalleffect.reset();
        shooterHalleffect.start();
        shooterState = kStopShooterState;
    }
}
