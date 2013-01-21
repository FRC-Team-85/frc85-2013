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
    public static int SHOOTER_MOTOR_CHANNEL = 2;
    
    public static int SHOOTER_RPM_SENSOR_SLOT = 2;
    public static int SHOOTER_RPM_SENSOR_CHANNEL = 1;
    
    private double _shooterPID_kP;
    private double _shooterPID_kI;
    private double _shooterPID_kD;
    private double _shooterPID_kF;
    private double _shooterPID_kMinInput;
    private double _shooterPID_kMaxInput;
    private double _shooterPID_kMinOutput;
    private double _shooterPID_kMaxOutput;
    
    private PIDController _shooterPID;
    
    private Victor _shooterMotor;
    
    public Shooter(Victor shooterMotor, PIDController shooterPIDController) {
        _shooterMotor = shooterMotor;
        _shooterPID = shooterPIDController;
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
            double measuredRPM, double minRPM, double maxRPM) {
        if (isEnabled) {
            if (((measuredRPM < minRPM) || (measuredRPM > maxRPM ))) {
                initPID();
            }
            else if (((measuredRPM > minRPM) && (measuredRPM < maxRPM ))) {
                _shooterMotor.set(setSpeed);
            }
            else {
                _shooterMotor.set(0);
            }
        }
    }
    
}
