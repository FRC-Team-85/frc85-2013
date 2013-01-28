package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    Victor leftDriveMotor = new Victor(1);
    Victor rightDriveMotor = new Victor(2);
    Joystick testDriveStick = new Joystick(1);
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, testDriveStick);

    public void robotInit() {

    }
    
    public void disabledInit() {
        
    }
    
    public void autonomousInit() {
        
    }
    
    public void teleopInit() {
        
    }
    
    public void testInit() {
        
    }
    
    public void disabledPeriodic() {
        
    }

    public void autonomousPeriodic() {

    }

    public void teleopPeriodic() {
        drive.joystickBasedTestDrive();
    }
    
    public void testPeriodic() {
    
    }
    
}
