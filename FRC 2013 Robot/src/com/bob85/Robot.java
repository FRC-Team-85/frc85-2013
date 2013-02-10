package com.bob85;


import edu.wpi.first.wpilibj.HallEffect;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    Victor shooterMotor = new Victor(Shooter.SHOOTER_MOTOR_CHANNEL);
    
    HallEffect shooterSensor = new HallEffect(Shooter.SHOOTER_RPM_SENSOR_CHANNEL);
    
    Joystick opStick = new Joystick(3);
    
    PIDController shooterPID = new PIDController(0,0,0,0, shooterSensor, shooterMotor);
    
    Shooter shooter = new Shooter(shooterMotor, shooterPID, shooterSensor);
    
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
        shooter.testSingleSpeedPID(opStick.getRawButton(8), 0.465, 2250, 3000);
        SmartDashboard.putNumber("Shooter Motor", shooterMotor.get());
        SmartDashboard.putNumber("Shooter RPM", shooterSensor.get());
    }
    
    public void testPeriodic() {
        
    }
    
}
