package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
    
    public static final int SHOOTER_MOTOR_CHANNEL = 5;
    public static final int SHOOTER_BELT_MOTOR_CHANNEL = 6;
    public static final int SHOOTER_RPM_SENSOR_CHANNEL = 5;
    
    private static int shooterState; // 0 is standby, 1 is readying, 2 is shoot
    
    private double kOnTargetPercentTolerance = 0.1;
    
    private static final double kRPM_TO_PWM = (1/5310);
    
    private Victor shooterMotor;
    private Victor shooterBeltMotor;
    
    private double shooterBeltMotorOutput;
    
    private HallEffect shooterHalleffect;
    
    private F310Gamepad gamepad;
    
    private void initShooterSettings() {
        shooterHalleffect.setMaxPeriod(0.2);
    }
    
    public Shooter(Victor shooterMotor, Victor shooterBeltMotor, 
            HallEffect shooterHalleffect, 
            F310Gamepad joystick) {
        this.shooterMotor = shooterMotor;
        this.shooterBeltMotor = shooterBeltMotor;
        this.shooterHalleffect = shooterHalleffect;
        this.gamepad = joystick;
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
        pwmValue = RPM / kRPM_TO_PWM;
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
        rpmValue = (int) (PWM * (1/kRPM_TO_PWM));
        return rpmValue;
    }
    
    public void setShooterSpeed(double speed) {
        shooterMotor.set(speed);
    }
    
    public boolean onTarget() {
        if (Math.abs(convertRPMtoPWM(shooterHalleffect.getRPM()) -  shooterMotor.get()) <  kOnTargetPercentTolerance) {
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
        setShooterBeltSpeed(1);
        setShooterSpeed(1);
    }
    
    /**
     * Returns shooter state
     * @return 0 = 0 output, 1 = shooter not at correct RPM, 2 = shooter at correct RPM
     */
    public static int getShooterState() {
        return shooterState;
    }
    
    public void runDiagnostics() {
        SmartDashboard.putNumber("Shooter PWM Setting", shooterMotor.get());
        SmartDashboard.putNumber("Shooter RPM", shooterHalleffect.getRPM());
        SmartDashboard.putNumber("Shooter Belt PWM Setting", shooterBeltMotor.get());
        SmartDashboard.putBoolean("Shooter On Target", onTarget());
        SmartDashboard.putNumber("Shooter State", getShooterState());
    }
    
    private void switchShooterStates() {
        switch (shooterState) {
            case 0:
                shooterState = (gamepad.getButton(ButtonType.kRB)) ? 1 : 0;
                break;
            case 1:
                if (gamepad.getButton(ButtonType.kRB) && onTarget()) {
                    shooterState = 2;
                } else if (!gamepad.getButton(ButtonType.kRB)) {
                    shooterState = 0;
                } else if (gamepad.getButton(ButtonType.kRB) && !onTarget()) {
                    shooterState = 1;
                }
                break;
            case 2:
                if (!gamepad.getButton(ButtonType.kRB)) {
                    shooterState = 0;
                } else if (gamepad.getButton(ButtonType.kRB) && !onTarget()) {
                    shooterState = 1;
                } else if (gamepad.getButton(ButtonType.kRB) && onTarget()) {
                    shooterState = 2;
                }
        }
    }
    
    public void runShooterStates() {
        switch (shooterState) {
            case 0:
                setShooterToRest();
                break;
            case 1:
                setShooterMaxSpeed();
                break;
            case 2:
                setShooterMaxSpeed();
                break;
        }
    }
     
    /**
     * disables and reset shooter sensors
     */
    public void disableShooter() {
        shooterHalleffect.stop();
        shooterHalleffect.reset();
    }
    
    /**
     * runs control and diagnostics of the shooter
     */
    public void runShooter() {
        switchShooterStates();
        runShooterStates();
        runDiagnostics();
    }
    
    /**
     * resets and enables shooter sensors
     */
    public void initShooter() {
        shooterHalleffect.reset();
        shooterHalleffect.start();
    }
}
