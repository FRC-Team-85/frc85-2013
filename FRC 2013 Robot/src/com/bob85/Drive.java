/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Drive {
    
    private SpeedController m_leftDriveMotors;
    private SpeedController m_rightDriveMotors;
    
    Joystick m_leftDriveJoystick;
    Joystick m_rightDriveJoystick;
    
    private double leftMotorsOutput;
    private double rightMotorsOutput;
    
    private double deadbandMin = 0.1;
    
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors) {
        m_leftDriveMotors = leftDriveMotors;
        m_rightDriveMotors = rightDriveMotors;
    }
    
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors,
            Joystick leftDriveJoystick, Joystick rightDriveJoystick) {
        m_leftDriveMotors = leftDriveMotors;
        m_rightDriveMotors = rightDriveMotors;
        m_leftDriveJoystick = leftDriveJoystick;
        m_rightDriveJoystick = rightDriveJoystick;
    }
    
    private void getJoystickInput() {
        leftMotorsOutput = m_leftDriveJoystick.getY();
        rightMotorsOutput = m_rightDriveJoystick.getY();
    }
    
    private void setDeadband(double motorOutput) {
        if (Math.abs(motorOutput) < deadbandMin) {
            motorOutput = 0;
        }
    }
    
    private void setLinearizedOutput() {
        leftMotorsOutput = MotorLinearization.calculateLinearOutput(leftMotorsOutput);
        rightMotorsOutput = MotorLinearization.calculateLinearOutput(rightMotorsOutput);
    }
    
    private void setMotorsOutput() {
     m_leftDriveMotors.set(leftMotorsOutput);
     m_rightDriveMotors.set(rightMotorsOutput);
    }

    public void joystickBasedTankDrive() {
        getJoystickInput();
        setLinearizedOutput();
        setDeadband(leftMotorsOutput);
        setDeadband(rightMotorsOutput);
        setMotorsOutput();
    }
}
