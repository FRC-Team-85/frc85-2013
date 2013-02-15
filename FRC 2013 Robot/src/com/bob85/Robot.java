package com.bob85;

import com.bob85.auto.AutoModeChooser;
import com.bob85.auto.AutoTimer;
import com.bob85.auto.Autonomous;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
   
    AutoModeChooser testChooser = new AutoModeChooser();
    AutoTimer autoTimer = new AutoTimer();
    Gyro gyro = new Gyro(1);
    
    Victor leftDriveMotor = new Victor(Drive.kLEFTDRIVE_VICTORS);
    Victor rightDriveMotor = new Victor(Drive.kRIGHTDRIVE_VICTORS);
    Servo leftDriveServo = new Servo(Drive.kLEFTDRIVE_SERVO);
    Servo rightDriveServo = new Servo (Drive.kRIGHTDRIVE_SERVO);
    Joystick leftDriveStick = new Joystick(1);
    Joystick rightDriveStick = new Joystick(2);
    Encoder leftDriveEncoder = new Encoder(Drive.kLEFTDRIVE_ENCODER_A, Drive.kLEFTDRIVE_ENCODER_B);
    Encoder rightDriveEncoder = new Encoder(Drive.kRIGHTDRIVE_ENCODER_A, Drive.kRIGHTDRIVE_ENCODER_B);
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, leftDriveServo, rightDriveServo,
            leftDriveEncoder, rightDriveEncoder, leftDriveStick, rightDriveStick);
    Autonomous auto = new Autonomous(testChooser, gyro, drive);


    Victor shooterMotor = new Victor(Shooter.SHOOTER_MOTOR_CHANNEL);
    Victor shooterBeltMotor = new Victor(Shooter.SHOOTER_BELT_MOTOR_CHANNEL);
    
    HallEffect shooterSensor = new HallEffect(Shooter.SHOOTER_RPM_SENSOR_CHANNEL);
    
    Joystick opStick = new Joystick(3);
    
    PIDController shooterPID = new PIDController(0,0,0,0, shooterSensor, shooterMotor);
    
    Shooter shooter = new Shooter(shooterMotor, shooterBeltMotor, shooterPID, shooterSensor, opStick);
    
    public void robotInit() {
        drive.driveInit();
        autoTimer.initAutoTimer();
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
        autoTimer.runAutoTimer();
    }

    public void teleopPeriodic() {
        shooter.runShooter();
        drive.runDrive();
        if (leftDriveStick.getTrigger()) {
            leftDriveServo.set(1);
            rightDriveServo.set(0);
        }
        if (rightDriveStick.getTrigger()) {
            leftDriveServo.set(0);
            rightDriveServo.set(1);
        }
    }
    
    public void testPeriodic() {
    
    }
    
}
