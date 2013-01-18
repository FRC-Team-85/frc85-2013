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
    
    public static int SHOOTER_MOTOR_CHANNEL = 2;
    public static int SHOOTER_MOTOR_SLOT = 2;
    
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
    
    private void initTestMode() {
        _shooterPID.startLiveWindowMode();
    }
    
}
