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
    private static final double kRPM_TO_PWM = (1/5310);
    
    private Victor shooterMotor;
    private Victor shooterBeltMotor;
    
    private double shooterBeltMotorSpeed;
    
    private HallEffect shooterSensor;
    
    private F310Gamepad gamepad;
    
    public Shooter(Victor shooterMotor, HallEffect shooterSensor) {
        this.shooterMotor = shooterMotor;
        this.shooterSensor = shooterSensor;
    }
    
    public Shooter(Victor shooterMotor, HallEffect shooterSensor, 
            F310Gamepad joystick) {
        this.shooterMotor = shooterMotor;
        this.shooterSensor = shooterSensor;
        this.gamepad = joystick;
    }
    
    public Shooter(Victor shooterMotor, Victor shooterBeltMotor, 
            HallEffect shooterSensor, 
            F310Gamepad joystick) {
        this.shooterMotor = shooterMotor;
        this.shooterBeltMotor = shooterBeltMotor;
        this.shooterSensor = shooterSensor;
        this.gamepad = joystick;
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
        boolean onTarget;

        if (Math.abs(convertRPMtoPWM(shooterSensor.getRPM()) -  shooterMotor.get()) <  kOnTargetPercentTolerance) {
            onTarget = true;
        } else {
            onTarget = false;
        }

        return onTarget;
    }
    
    public void setShooterBeltSpeed(double speed) {
        shooterBeltMotorSpeed = speed;
        shooterBeltMotor.set(-shooterBeltMotorSpeed);
    }
    
    public double getShooterBeltSpeed() {
        return shooterBeltMotorSpeed;
    }
    
    public static int getShooterState() {
        return shooterState;
    }
    
    public void runDiagnostics() {
        SmartDashboard.putNumber("Shooter PWM Setting", shooterMotor.get());
        SmartDashboard.putNumber("Shooter RPM", shooterSensor.getRPM());
        SmartDashboard.putNumber("Shooter Belt PWM Setting", shooterBeltMotor.get());
        SmartDashboard.putBoolean("Shooter On Target", onTarget());
        SmartDashboard.putNumber("Shooter State", getShooterState());
    }
    
    private void switchShooterStates() {
        switch (shooterState) {
            case 0:
                if (gamepad.getButton(ButtonType.kRB)) {
                    shooterState = 1;
                }
                break;
            case 1:
                if (gamepad.getButton(ButtonType.kRB) && onTarget()) {
                    shooterState = 2;
                }
                break;
            case 2:
                if (!gamepad.getButton(ButtonType.kRB)) {
                    shooterState = 0;
                } else if (gamepad.getButton(ButtonType.kRB) && !onTarget()) {
                    shooterState = 1;
                }
        }
    }
    
    public void runShooterStates() {
        switch (shooterState) {
            case 0:
                setShooterBeltSpeed(0);                
                setShooterSpeed(0);
                break;
            case 1:
                setShooterSpeed(1);
                setShooterBeltSpeed(1);
                break;
            case 2:
                setShooterSpeed(1);
                setShooterBeltSpeed(1);
                break;
            default:
                setShooterBeltSpeed(0);                
                setShooterSpeed(0);
                break;
        }
    }
    
    public void runAlexShooterSetup() {
        if (gamepad.getButton(ButtonType.kRB)) {
            setShooterSpeed(1);
            setShooterBeltSpeed(1);
        } else {
            setShooterSpeed(0);
            setShooterBeltSpeed(0);
        }
    }
    
    public void runShooter() {
        runAlexShooterSetup();
        runDiagnostics();
    }
    
    public void initShooter() {
        shooterSensor.start();
        shooterSensor.setMaxPeriod(0.2);
    }
}
