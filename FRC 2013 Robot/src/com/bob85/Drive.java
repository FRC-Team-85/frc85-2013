/*
 * MAKE SURE EACH SIDE OF THE DRIVE HAVE THE SAME POLARITY WHEN USING Y SPLITTER
 */
package com.bob85;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Drive {
    
    private SpeedController m_leftDriveMotors; //class reference to left drive
    private SpeedController m_rightDriveMotors; //class reference to right drive
    
    Joystick m_leftDriveJoystick; //reference to left drive joystick
    Joystick m_rightDriveJoystick; //reference to right drive joystick
    
    private double leftMotorsOutput; //left drive motor output setting
    private double rightMotorsOutput; //right drive motor output setting
    
    private double deadband = 0.1; //Deadband for drive motor output
    
    /**
     * Constructs a Robot Drive with two PWM channels
     * 
     * @param leftDriveMotors left drive PWM channel
     * @param rightDriveMotors right drive PWM channel
     */
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors) {
        m_leftDriveMotors = leftDriveMotors;
        m_rightDriveMotors = rightDriveMotors;
    }
    
    /**
     * Constructs a Robot Drive with two PWM channels and joystick input
     * 
     * @param leftDriveMotors left drive PWM channel
     * @param rightDriveMotors right drive PWM channel
     * @param leftDriveJoystick left drive joystick
     * @param rightDriveJoystick right drive joystick
     */
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors,
            Joystick leftDriveJoystick, Joystick rightDriveJoystick) {
        m_leftDriveMotors = leftDriveMotors;
        m_rightDriveMotors = rightDriveMotors;
        m_leftDriveJoystick = leftDriveJoystick;
        m_rightDriveJoystick = rightDriveJoystick;
    }
    
    /**
     * Maps the motor outputs to the joysticks Y axis
     */
    private void getJoystickInput() {
        leftMotorsOutput = m_leftDriveJoystick.getY();
        rightMotorsOutput = m_rightDriveJoystick.getY();
    }
    
    /**
     * Sets motor output setting to zero if it falls under the deadband
     * @param motorOutput current motor output setting 
     */
    private void setDeadband(double motorOutput) {
        if (Math.abs(motorOutput) < deadband) {
            motorOutput = 0;
        }
    }
    
    /**
     * Sets motor output setting to a linearized desired output
     */
    private void setLinearizedOutput() {
        leftMotorsOutput = MotorLinearization.calculateLinearOutput(leftMotorsOutput);
        rightMotorsOutput = MotorLinearization.calculateLinearOutput(rightMotorsOutput);
    }
    
    /**
     * Sets the motors output with the motor output settings
     */
    private void setMotorsOutput() {
     m_leftDriveMotors.set(leftMotorsOutput);
     m_rightDriveMotors.set(rightMotorsOutput);
    }

    /**
     * Uses two joysticks in a tank drive setup to run the motors
     */
    public void joystickBasedTankDrive() {
        getJoystickInput();
        setLinearizedOutput();
        setDeadband(leftMotorsOutput);
        setDeadband(rightMotorsOutput);
        setMotorsOutput();
    }
}
