package com.bob85;

import com.bob85.auto.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    boolean isClimbMode;
    
    Joystick leftDriveStick = new Joystick(1);
    Joystick rightDriveStick = new Joystick(2);
    F310Gamepad opPad = new F310Gamepad(3);

    AutoModeChooser autoChooser = new AutoModeChooser();
    ShotTimer shotTimer = new ShotTimer(ShootCommand.frisbee_val);
    Gyro gyro = new Gyro(Drive.kGYRO);

    Servo dropServo = new Servo(FrisbeeLoader.kDROPSERVO_CHANNEL);
    Victor hopperBelt = new Victor(FrisbeeLoader.kHOPPERBELTMOTOR_CHANNEL);
    
    Victor leftDriveMotor = new Victor(Drive.kLEFTDRIVE_VICTORS);
    Victor rightDriveMotor = new Victor(Drive.kRIGHTDRIVE_VICTORS);
    Servo leftDriveServo = new Servo(Drive.kLEFTDRIVE_SERVO);
    Servo rightDriveServo = new Servo (Drive.kRIGHTDRIVE_SERVO);

    Encoder leftDriveEncoder = new Encoder(Drive.kLEFTDRIVE_ENCODER_A, Drive.kLEFTDRIVE_ENCODER_B);
    Encoder rightDriveEncoder = new Encoder(Drive.kRIGHTDRIVE_ENCODER_A, Drive.kRIGHTDRIVE_ENCODER_B);

    Victor shooterMotor = new Victor(Shooter.SHOOTER_MOTOR_CHANNEL);
    Victor shooterBeltMotor = new Victor(Shooter.SHOOTER_BELT_MOTOR_CHANNEL);
    
    HallEffect shooterSensor = new HallEffect(Shooter.SHOOTER_RPM_SENSOR_CHANNEL);
    PIDController shooterPID = new PIDController(0,0,0,0, shooterSensor, shooterMotor);
    
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, leftDriveServo, rightDriveServo,
            leftDriveEncoder, rightDriveEncoder, gyro, leftDriveStick, rightDriveStick);
    Shooter shooter = new Shooter(shooterMotor, shooterBeltMotor, shooterSensor, opPad);
    FrisbeeLoader frisbeeLoader = new FrisbeeLoader(dropServo, hopperBelt, opPad);
    Autonomous auto = new Autonomous(autoChooser, shotTimer, gyro, drive, shooter, frisbeeLoader);
    
    public void robotInit() {
        drive.driveInit();
        shotTimer.initShotTimer();   
        shooter.initShooter();
    }
    
    public void disabledInit() {
        drive.disabledInit();
    }
    
    public void autonomousInit() {
        auto.initAutonomous();
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
        shooter.runShooter();
        drive.runDrive();
        frisbeeLoader.runFrisbeeLoader();
        SmartDashboard.putNumber("Hall Effect", shooterSensor.getRPM());
    }
    public void testPeriodic() {

    }
}
