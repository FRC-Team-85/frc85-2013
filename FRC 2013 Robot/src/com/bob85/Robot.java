package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {

    Servo dropServo = new Servo(FrisbeeLoader.kDROPSERVO_CHANNEL);
    Victor hopperBelt = new Victor(FrisbeeLoader.kHOPPERBELTMOTOR_CHANNEL);
    F310Gamepad opPad = new F310Gamepad(3);
    
    
    Victor shooterMotor = new Victor(Shooter.SHOOTER_MOTOR_CHANNEL);
    Victor shooterBeltMotor = new Victor(Shooter.SHOOTER_BELT_MOTOR_CHANNEL);
    
    HallEffect shooterSensor = new HallEffect(Shooter.SHOOTER_RPM_SENSOR_CHANNEL);
    
    PIDController shooterPID = new PIDController(0,0,0,0, shooterSensor, shooterMotor);
    
    Shooter shooter = new Shooter(shooterMotor, shooterBeltMotor, shooterPID, shooterSensor, opPad);
    FrisbeeLoader frisbeeLoader = new FrisbeeLoader(dropServo, shooterSensor, hopperBelt, 
            shooterMotor, shooterPID, opPad);
    
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
        frisbeeLoader.runFrisbeeLoader();
        shooter.runShooter();
    }
    
    public void testPeriodic() {
        frisbeeLoader.runFrisbeeLoader();
        shooter.runShooter();
    }
    
}
