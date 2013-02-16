/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Shooter {
    
    public static final int SHOOTER_MOTOR_CHANNEL = 5;
    public static final int SHOOTER_BELT_MOTOR_CHANNEL = 6;
    public static final int SHOOTER_RPM_SENSOR_CHANNEL = 5;
    
    private static int shooterState; // 0 is standby, 1 is readying, 2 is shoot
    
    private double kOnTargetPercentTolerance = 0.1;
    
    private double shooterPID_kP = 0.009;
    private double shooterPID_kI = 0.0001;
    private double shooterPID_kD = 0;
    private double shooterPID_kF = 0;
    private double shooterPID_kMinInput = 0;
    private double shooterPID_kMaxInput = 3000;
    private double shooterPID_kMinOutput = 0;
    private double shooterPID_kMaxOutput = 1;
    private double shooterPID_kSetPoint = 2800;
    private double shooterPID_kPercentTolerance = kOnTargetPercentTolerance * 100;
    
    private static double kRPM_TO_PWM;
    
    private PIDController shooterPID;
    
    private Victor shooterMotor;
    private Victor shooterBeltMotor;
    
    private double shooterBeltMotorSpeed;
    
    private HallEffect shooterSensor;
    
    private F310Gamepad gamepad;
    
    /**
     * Initializes Shooter PID Controller Settings
     */
    private void initPIDConstants() {
        shooterPID.setPID(shooterPID_kP, shooterPID_kI, shooterPID_kD, shooterPID_kF);
        shooterPID.setInputRange(shooterPID_kMinInput, shooterPID_kMaxInput);
        shooterPID.setOutputRange(shooterPID_kMinOutput, shooterPID_kMaxOutput);
        shooterPID.setPercentTolerance(shooterPID_kPercentTolerance);
    }
    
    public Shooter(Victor shooterMotor, PIDController shooterPID, HallEffect shooterSensor) {
        this.shooterMotor = shooterMotor;
        this.shooterPID = shooterPID;
        this.shooterSensor = shooterSensor;
        initPIDConstants();
    }
    
    public Shooter(Victor shooterMotor, PIDController shooterPID, HallEffect shooterSensor, 
            F310Gamepad joystick) {
        this.shooterMotor = shooterMotor;
        this.shooterPID = shooterPID;
        this.shooterSensor = shooterSensor;
        this.gamepad = joystick;
        initPIDConstants();
    }
    
    public Shooter(Victor shooterMotor, Victor shooterBeltMotor, 
            PIDController shooterPID, HallEffect shooterSensor, 
            F310Gamepad joystick) {
        this.shooterMotor = shooterMotor;
        this.shooterBeltMotor = shooterBeltMotor;
        this.shooterPID = shooterPID;
        this.shooterSensor = shooterSensor;
        this.gamepad = joystick;
        initPIDConstants();
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
    
    /**
     * Calculates a shooter RPM SetPoint based on distance away from target
     * @param distance distance from target in inches
     */
    private void calculateShooterSpeed(double distance) {
        shooterPID_kSetPoint = distance;
    }
    
    private void setShooterSpeed(double speed) {
        shooterMotor.set(speed);
    }
    
    /**
     * Enables PID Controller if it is not already enabled
     */
    private void initPID() {
        if (!shooterPID.isEnable()) {
            shooterPID.enable();
            shooterPID.setSetpoint(shooterPID_kSetPoint);
        }
    }
    
    private void disablePID() {
        if (shooterPID.isEnable()) {
            shooterPID.disable();
        }
    }
    
    private void changePIDSetpoint(boolean isEnabled, double setpoint) {
        if (isEnabled && shooterPID.getSetpoint() != setpoint) {
            shooterPID.setSetpoint(setpoint);
        }
    }
    
    public boolean onTarget() {
        if (shooterPID.isEnable()) {
            return shooterPID.onTarget();
        } else {
            boolean onTarget;
            
            if (Math.abs(convertRPMtoPWM(shooterSensor.getRPM()) -  shooterMotor.get()) <  kOnTargetPercentTolerance) {
                onTarget = true;
            } else {
                onTarget = false;
            }
            
            return onTarget;
        }
    }
    
    /**
     * Runs the PID Controller if outside of setSpeed RPM range, if inside
     * switches to a static speed to prevent oscillation
     * 
     * @param isEnabled Checks if the function is enabled
     * @param setSpeed Desired final shooter output
     * @param measuredRPM Shooter sensor's measured RPM
     * @param minRPM Minimum RPM to turn off PID Controller
     * @param maxRPM Maximum RPM to turn off PID Controller
     */
    private void testSingleSpeedPID(boolean isEnabled, double setSpeed, 
            double minRPM, double maxRPM) {
        
        int getRPM = shooterSensor.getRPM();
        
        if (isEnabled) {
            if (((getRPM < minRPM) || (getRPM > maxRPM ))) {
                initPID();
            }
            else if (((getRPM > minRPM) && (getRPM < maxRPM ))) {
                disablePID();
                shooterMotor.set(setSpeed);
            }
            else {
                shooterMotor.set(0);
            }
        }
        else {
            disablePID();
            shooterMotor.set(0);
        }
    }
    
    private void runAutomaticShooterPID(boolean isEnabled) {
        if (isEnabled) {
            initPID();            
        } else {
            disablePID();
        }
    }
    
    /**
     * Calculates shooter speed in RPM and use it in a PID Controller
     * @param isEnabled
     * @param distance 
     */
    private void testAutomaticShooterSpeedCalculation(boolean isEnabled, double distance) {
        if (isEnabled) {
            calculateShooterSpeed(distance);
            initPID();
        } else {
            disablePID();
        }
    }
    
    private void setShooterBeltSpeed(double speed) {
        shooterBeltMotorSpeed = speed;
        shooterBeltMotor.set(shooterBeltMotorSpeed);
    }
    
    private double getShooterBeltSpeed() {
        return shooterBeltMotorSpeed;
    }
    
    public static int getShooterState() {
        return shooterState;
    }
    
    private void runDiagnostics() {
        SmartDashboard.putNumber("Shooter PWM Setting", shooterMotor.get());
        SmartDashboard.putNumber("Shooter RPM", shooterSensor.getRPM());
        SmartDashboard.putNumber("Shooter Belt PWM Setting", shooterBeltMotor.get());
        SmartDashboard.putBoolean("Shooter On Target", onTarget());
        SmartDashboard.putNumber("Shooter State", getShooterState());
    }
    
    private void runCompetitionShooter() {
        setShooterBeltSpeed(0.5);
        runAutomaticShooterPID(gamepad.getRawButton(8));
        
    }
    
    /**
     * Runs a shooter feature needed to be tested
     * @param distance distance from target in inches
     */
    private void runAutomaticShooterSpeedTest(double distance) {
        testAutomaticShooterSpeedCalculation(gamepad.getTrigger(), distance);
    }
    
    private void runShooterPIDTest() {
        runAutomaticShooterPID(gamepad.getRawButton(8));
    }
    
    /**
     * Returns the Shooter State
     * @return 
     */
    private int getShooterStates() {
        return shooterState;
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
    
    private void runShooterStates() {
        switch (shooterState) {
            case 0:
                setShooterBeltSpeed(0);                
                setShooterSpeed(0);
                break;
            case 1:
                initPID();
                setShooterBeltSpeed(1);
                break;
            case 2:
                initPID();
                setShooterBeltSpeed(1);
                break;
            default:
                setShooterBeltSpeed(0);                
                setShooterSpeed(0);
                break;
        }
    }
    
    public void runShooter() {
        runShooterPIDTest();
        runDiagnostics();
    }
}
