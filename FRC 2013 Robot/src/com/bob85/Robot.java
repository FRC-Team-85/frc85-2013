package com.bob85;

import com.bob85.auto.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    boolean isClimbMode;
    
    boolean isDrive;
    
    Joystick leftStick = new Joystick(1);
    Joystick rightStick = new Joystick(2);
    F310Gamepad opPad = new F310Gamepad(3);

    AutoModeChooser autoChooser = new AutoModeChooser();
    ShotTimer shotTimer = new ShotTimer(ShootCommand.frisbee_val);
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
    
    Autonomous auto = new Autonomous(autoChooser, shotTimer, gyro, drive, shooter, frisbeeLoader);
    
    public void robotInit() {
        drive.driveInit();
        shotTimer.initShotTimer();   
        shooter.initShooter();
        climber.initClimber();
    }
    
    public void disabledInit() {
        drive.disabledInit();
    }
    
    public void autonomousInit() {
        auto.initAutonomous();
    }
    
    public void teleopInit() {
        gyro.reset();
    }
    
    public void testInit() {
        
    }

    public void disabledPeriodic() {
        
    }

    public void autonomousPeriodic() {

    }
    
    public void teleopPeriodic() {
        if (leftStick.getRawButton(10)) {
            isDrive = true;
        } else if (leftStick.getRawButton(11)) {
            isDrive = false;
        }
        shooter.runShooter();
        if (isDrive) {
            drive.runDrive();
        } else if (!isDrive) {
            climber.runClimber();
        }
        frisbeeLoader.runFrisbeeLoader();
        SmartDashboard.putNumber("Gyro", gyro.getAngle());
    }
    public void testPeriodic() {

    }
}
