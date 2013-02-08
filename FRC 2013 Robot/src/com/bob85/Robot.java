package com.bob85;

import com.bob85.auto.AutoModeChooser;
import com.bob85.auto.Autonomous;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    Victor leftDriveMotor = new Victor(1);
    Victor rightDriveMotor = new Victor(2);
    Joystick testDriveStick = new Joystick(1);
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, testDriveStick);
    
    AutoModeChooser testChooser = new AutoModeChooser();
    Autonomous auto = new Autonomous(testChooser);

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
        auto.initAutonomous();
    }

    public void teleopPeriodic() {
        drive.joystickBasedTestDrive();
    }
    
    public void testPeriodic() {
    
    }
    
}
