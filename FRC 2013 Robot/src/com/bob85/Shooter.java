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
public class Shooter {
    
    public static int SHOOTER_MOTOR_SLOT = 2;
    public static int SHOOTER_MOTOR_CHANNEL = 6;
    
    public static int SHOOTER_RPM_SENSOR_SLOT = 1;
    public static int SHOOTER_RPM_SENSOR_CHANNEL = 5;
    
    private double _shooterPID_kP = 0.009;
    private double _shooterPID_kI = 0.0001;
    private double _shooterPID_kD = 0;
    private double _shooterPID_kF = 0;
    private double _shooterPID_kMinInput = 0;
    private double _shooterPID_kMaxInput = 3000;
    private double _shooterPID_kMinOutput = 0;
    private double _shooterPID_kMaxOutput = 1;
    private double _shooterPID_kSetPoint = 2800;
    
    private PIDController _shooterPID;
    
    private Victor _shooterMotor;
    
    private HallEffect _shooterSensor;
    
    public Shooter(Victor shooterMotor, PIDController shooterPIDController, HallEffect shooterSensor) {
        _shooterMotor = shooterMotor;
        _shooterPID = shooterPIDController;
        _shooterSensor = shooterSensor;
        initPIDConstants();
    }
    
    /**
     * Initializes Shooter PID Controller Settings
     */
    private void initPIDConstants() {
        _shooterPID.setPID(_shooterPID_kP, _shooterPID_kI, _shooterPID_kD, _shooterPID_kF);
        _shooterPID.setInputRange(_shooterPID_kMinInput, _shooterPID_kMaxInput);
        _shooterPID.setOutputRange(_shooterPID_kMinOutput, _shooterPID_kMaxOutput);
    }
    
    /**
     * Enables PID Controller if it is not already enabled
     */
    private void initPID() {
        if (!_shooterPID.isEnable()) {
            _shooterPID.enable();
            _shooterPID.setSetpoint(_shooterPID_kSetPoint);
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
    public void testSingleSpeedPID(boolean isEnabled, double setSpeed, 
            double minRPM, double maxRPM) {
        
        int getRPM = _shooterSensor.returnRPM();
        
        if (isEnabled) {
            if (((getRPM < minRPM) || (getRPM > maxRPM ))) {
                initPID();
            }
            else if (((getRPM > minRPM) && (getRPM < maxRPM ))) {
                _shooterMotor.set(setSpeed);
            }
            else {
                _shooterMotor.set(0);
            }
        }
        else {
            _shooterPID.disable();
            _shooterMotor.set(0);
        }
    }
    
}
