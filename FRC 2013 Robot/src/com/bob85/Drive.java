/*
 * MAKE SURE EACH SIDE OF THE DRIVE HAVE THE SAME POLARITY WHEN USING Y SPLITTER
 */
package com.bob85;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Drive {
    
    private SpeedController leftDriveMotors; //class reference to left drive
    private SpeedController rightDriveMotors; //class reference to right drive
    
    Joystick leftDriveJoystick; //reference to left drive joystick
    Joystick rightDriveJoystick; //reference to right drive joystick
    Joystick m_testDriveJoystick;
    
    private double leftMotorsOutput; //left drive motor output setting
    private double rightMotorsOutput; //right drive motor output setting
   
    private double leftOldOutput;
    private double rightOldOutput;
            
    private double deadband = 0.05; //Deadband for drive motor output
    
    private double encoderDistanceRatio = 1.22; //Each encoder pulse = 1.22inches traveled
    
    /**
     * Constructs a Robot Drive with two PWM channels
     * 
     * @param leftDriveMotors left drive PWM channel
     * @param rightDriveMotors right drive PWM channel
     */
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
    }
    
    public Drive(SpeedController leftDriveMotors, SpeedController rightDriveMotors,
            Joystick testDriveJoystick) {
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        m_testDriveJoystick = testDriveJoystick;
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
        this.leftDriveMotors = leftDriveMotors;
        this.rightDriveMotors = rightDriveMotors;
        this.leftDriveJoystick = leftDriveJoystick;
        this.rightDriveJoystick = rightDriveJoystick;
    }
    
    /**
     * Maps the motor outputs to the joysticks Y axis
     */
    private void getTankDriveJoystickInput() {
        leftMotorsOutput = leftDriveJoystick.getY();
        rightMotorsOutput = rightDriveJoystick.getY();
    }
    
    private void getTestDriveJoystickInput() {
        leftMotorsOutput = m_testDriveJoystick.getRawAxis(2);
        rightMotorsOutput = m_testDriveJoystick.getRawAxis(4);
    }
    
    /**
     * Sets motor output setting to zero if it falls under the deadband
     */    
    private void setMotorOutputDeadbands() {
        if (Math.abs(leftMotorsOutput) < deadband) {
            leftMotorsOutput = 0;
        }
        
        if (Math.abs(rightMotorsOutput) < deadband) {
            rightMotorsOutput = 0;
        }
    }
    
    /**
     * Sets motor output setting to a linearized desired output
     */
    private void setLinearizedOutput() {
        leftMotorsOutput = MotorLinearization.calculateLinearOutput(leftMotorsOutput);
        rightMotorsOutput = MotorLinearization.calculateLinearOutput(rightMotorsOutput);
    }
    
    private void motorsChangeLimit() {
        
        if (Math.abs(leftMotorsOutput-leftOldOutput)>0.5) {     //checks if the change is above 0.5
            if (leftMotorsOutput>0) {                           //checks if positive and rewrites
                leftOldOutput +=0.5;                            //oldoutput to change by +0.5
                }
            else if (leftMotorsOutput<0) {                      //check if negative and rewrites
                leftOldOutput -=0.5;                            //oldoutput to change by -0.5
                }                                           
        }
        else {                                                  //if change is acceptable
            leftOldOutput = leftMotorsOutput;                   //set change to oldoutput
        }
        
        if (Math.abs(rightMotorsOutput-rightOldOutput)>0.5) {   //same for right
            if (rightMotorsOutput>0) {
                rightOldOutput += 0.5;
                }
            else if (rightMotorsOutput<0) {
                rightOldOutput -= 0.5;
                }
        }
        else {
            rightOldOutput = rightMotorsOutput;
        }
        
        leftMotorsOutput = leftOldOutput;                       //sets changes to motors
        rightMotorsOutput = rightOldOutput;
            
    }
    
    /**
     * Sets the motors output with the motor output settings
     */
    private void setMotorsOutput() {
     leftDriveMotors.set(leftMotorsOutput);
     rightDriveMotors.set(rightMotorsOutput);
    }
    
    /**
     * Sends input and output of joystickBasedTestDrive()
     */
    private void sendTestDriveDiagnosticsSDB() {
        SmartDashboard.putNumber("Left Drive Input", m_testDriveJoystick.getRawAxis(2));
        SmartDashboard.putNumber("Right Drive Input", m_testDriveJoystick.getRawAxis(4));
        SmartDashboard.putNumber("Left Drive Output", leftDriveMotors.get());
        SmartDashboard.putNumber("Right Drive Output", rightDriveMotors.get());
    }

    /**
     * Uses two joysticks in a tank drive setup to run the motors
     */
    public void joystickBasedTankDrive() {
        getTankDriveJoystickInput();
        setLinearizedOutput();
        setMotorOutputDeadbands();
        setMotorsOutput();
    }
    
    public void joystickBasedTestDrive() {
        getTestDriveJoystickInput();
        setLinearizedOutput();
        setMotorOutputDeadbands();
        setMotorsOutput();
        sendTestDriveDiagnosticsSDB();
    }
    
    public void autoBasedDrive(double leftMotorOutput, double rightMotorOutput) {
        leftMotorsOutput = leftMotorOutput;
        rightMotorsOutput = rightMotorOutput;
        setLinearizedOutput();
        setMotorOutputDeadbands();
        setMotorsOutput();
    }
}
