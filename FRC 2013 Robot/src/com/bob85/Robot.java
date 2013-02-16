package com.bob85;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.F310Gamepad.AxisType;
import edu.wpi.first.wpilibj.F310Gamepad.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

    Servo dropServo = new Servo(FrisbeeLoader.kDROPSERVO_CHANNEL);
    Victor hopperBelt = new Victor(FrisbeeLoader.kHOPPERBELTMOTOR_CHANNEL);
    F310Gamepad opPad = new F310Gamepad(3);
    
    
    Victor shooterMotor = new Victor(Shooter.SHOOTER_MOTOR_CHANNEL);
    Victor shooterBeltMotor = new Victor(Shooter.SHOOTER_BELT_MOTOR_CHANNEL);
    
    HallEffect shooterSensor = new HallEffect(Shooter.SHOOTER_RPM_SENSOR_CHANNEL);
    
    PIDController shooterPID = new PIDController(0,0,0,0, shooterSensor, shooterMotor);
    
    Shooter shooter = new Shooter(shooterMotor, shooterBeltMotor, shooterPID, shooterSensor, opPad);
    FrisbeeLoader frisbeeLoader = new FrisbeeLoader(dropServo, hopperBelt, opPad);
    
    public void robotInit() {
        SmartDashboard.putNumber("runIfNothingElseWorks", 0);
        shooter.initShooter();
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
    
    public void runIfNothingElseWorks() {
        frisbeeLoader.setHopperBeltMotor(opPad.getAxis(AxisType.kDPadY) * -0.4);
        shooter.setShooterSpeed(SmartDashboard.getNumber("runIfNothingElseWorks"));
        shooter.setShooterBeltSpeed(1);
        if (opPad.getButton(ButtonType.kRB)) {
            frisbeeLoader.unlockServo(dropServo);
        } else if (opPad.getButton(ButtonType.kLB)) {
            frisbeeLoader.lockServo(dropServo);
        }
    }
}
