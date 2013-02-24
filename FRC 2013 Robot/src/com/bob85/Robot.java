package com.bob85;

import com.bob85.auto.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    boolean isClimbMode;
    
    boolean isDrive;
    
    AutoModeChooser autoChooser = new AutoModeChooser();
    ShotTimer shotTimer = new ShotTimer(ShootCommand.frisbee_val);
    Joystick leftStick = new Joystick(1);
    Joystick rightStick = new Joystick(2);
    F310Gamepad opPad = new F310Gamepad(3);

    Gyro gyro = new Gyro(Drive.kGYRO);
    
    Victor leftDriveMotor = new Victor(Drive.kLEFTDRIVE_VICTORS);
    Victor rightDriveMotor = new Victor(Drive.kRIGHTDRIVE_VICTORS);
    Victor shooterMotor = new Victor(Shooter.SHOOTER_MOTOR_CHANNEL);
    Victor shooterBeltMotor = new Victor(Shooter.SHOOTER_BELT_MOTOR_CHANNEL);
    Victor hopperBelt = new Victor(FrisbeeLoader.kHOPPERBELTMOTOR_CHANNEL);
    
    Servo leftDriveServo = new Servo(Drive.kLEFTDRIVE_SERVO);
    Servo rightDriveServo = new Servo (Drive.kRIGHTDRIVE_SERVO);
    Servo dropServo = new Servo(FrisbeeLoader.kDROPSERVO_CHANNEL);
    Servo lockClimberServo = new Servo(Climber.kCLIMBERLOCK_SERVO);
    
    Encoder leftDriveEncoder = new Encoder(Drive.kLEFTDRIVE_ENCODER_A, Drive.kLEFTDRIVE_ENCODER_B);
    Encoder rightDriveEncoder = new Encoder(Drive.kRIGHTDRIVE_ENCODER_A, Drive.kRIGHTDRIVE_ENCODER_B);
    
    DigitalInput bottomClimberLimitSwitch = new DigitalInput(Climber.kBOTTOM_LIMITSWITCH_CHANNEL);
    DigitalInput topClimberLimitSwitch = new DigitalInput(Climber.kTOP_LIMITSWITCH_CHANNEL);
    DigitalInput restClimberLimitSwitch = new DigitalInput(Climber.kREST_LIMITSWITCH);
    DigitalInput extendClimberLimitSwitch = new DigitalInput(Climber.kEXTEND_LIMITSWITCH);
    
    HallEffect shooterSensor = new HallEffect(Shooter.SHOOTER_RPM_SENSOR_CHANNEL);
    
    
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, leftDriveServo, rightDriveServo,
            leftDriveEncoder, rightDriveEncoder, gyro, leftStick, rightStick);
    Shooter shooter = new Shooter(shooterMotor, shooterBeltMotor, shooterSensor, opPad);
    Climber climber = new Climber(drive, leftStick, rightStick,
            leftDriveMotor, rightDriveMotor,
            leftDriveEncoder, rightDriveEncoder,restClimberLimitSwitch, topClimberLimitSwitch,
            bottomClimberLimitSwitch, topClimberLimitSwitch, lockClimberServo);
  
    
    FrisbeeLoader frisbeeLoader = new FrisbeeLoader(dropServo, hopperBelt, opPad);
    
    Autonomous auto = new Autonomous(autoChooser, shotTimer, drive, shooter, frisbeeLoader);
    
    public void robotInit() {
        drive.initDrive();
        shotTimer.initShotTimer();   
        shooter.initShooter();
        climber.initClimber();
    }
    
    public void disabledInit() {
        drive.disableDrive();
    }
    
    public void autonomousInit() {
        drive.initDrive();
        auto.initAutonomous();
    }
    
    public void teleopInit() {
        drive.initDrive();
    }
    
    public void testInit() {
        
    }

    public void disabledPeriodic() {
        
    }

    public void autonomousPeriodic() {
        auto.runAutonomous();
    }
    
    public void teleopPeriodic() {
        shooter.runShooter();
        drive.runDrive();
        climber.runClimber();
        frisbeeLoader.runFrisbeeLoader();
        SmartDashboard.putNumber("Gyro", gyro.getAngle());
        SmartDashboard.putNumber("Left Drive Encoder",  leftDriveEncoder.get());
        SmartDashboard.putNumber("Right Drive Encoder", rightDriveEncoder.get());
    }
    public void testPeriodic() {

    }
}
