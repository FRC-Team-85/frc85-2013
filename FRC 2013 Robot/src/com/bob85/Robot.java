package com.bob85;

import com.bob85.auto.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    AutoModeChooser autoChooser = new AutoModeChooser();
    ShotTimer shotTimer = new ShotTimer(ShootCommand.frisbee_val);
    Joystick js_Left = new Joystick(1);
    Joystick js_Right = new Joystick(2);
    F310Gamepad pad_Operator = new F310Gamepad(3);

    Gyro gyro_Drive_Heading = new Gyro(Drive.kANALOG_DRIVE_GYRO_HEADING);
    
    Victor vic_Drive_Left = new Victor(Drive.kPWM_DRIVE_VICTOR_LEFT);
    Victor vic_Drive_Right = new Victor(Drive.kPWM_DRIVE_VICTOR_RIGHT);
    Victor vic_Shooter_Wheel = new Victor(Shooter.kPWM_SHOOTER_VICTOR_WHEEL);
    Victor vic_Shooter_Belt = new Victor(Shooter.kPWM_SHOOTER_VICTOR_BELT);
    Victor vic_Hopper_Belt = new Victor(Hopper.kPWM_HOPPER_VICTOR_BELT);
    Victor vic_Climber_Tilt = new Victor(Climber.kPWM_CLIMBER_VICTOR_TILT);
    
    Servo servo_Drive_PTO_Left = new Servo(Drive.kPWM_DRIVE_SERVO_PTO_LEFT);
    Servo servo_Drive_PTO_Right = new Servo (Drive.kPWM_DRIVE_SERVO_PTO_RIGHT);
    Servo servo_Hopper_Pin_Frisbee_Lock = new Servo(Hopper.kPWM_HOPPER_SERVO_PIN_FRISBEE_LOCK);
    
    Encoder enc_Drive_Left = new Encoder(Drive.kDIO_DRIVE_ENCODER_LEFT_A, Drive.kDIO_DRIVE_ENCODER_LEFT_B);
    Encoder enc_Drive_Right = new Encoder(Drive.kDIO_DRIVE_ENCODER_RIGHT_A, Drive.kDIO_DRIVE_ENCODER_RIGHT_B);
    
    DigitalInput limit_Climber_Bottom = new DigitalInput(Climber.kDIO_CLIMBER_LIMITSWITCH_BOT);
    DigitalInput limit_Climber_Top = new DigitalInput(Climber.kDIO_CLIMBER_LIMITSWITCH_TOP);
    DigitalInput limit_Climber_Tilt_Rest = new DigitalInput(Climber.kDIO_CLIMBER_LIMITSWITCH_TILT_REST);
    DigitalInput limit_Climber_Tilt_Extent = new DigitalInput(Climber.kDIO_CLIMBER_LIMITSWITCH_TILT_EXTENT);
    
    HallEffect halle_Shooter_Wheel = new HallEffect(Shooter.kDIO_SHOOTER_HALLEFFECT_WHEEL);
    
    
    Drive drive = new Drive(vic_Drive_Left, vic_Drive_Right, servo_Drive_PTO_Left, servo_Drive_PTO_Right,
            enc_Drive_Left, enc_Drive_Right, gyro_Drive_Heading, js_Left, js_Right);
    Shooter shooter = new Shooter(vic_Shooter_Wheel, vic_Shooter_Belt, halle_Shooter_Wheel, pad_Operator);
    Climber climber = new Climber(drive, js_Left, js_Right,
            vic_Drive_Left, vic_Drive_Right, vic_Climber_Tilt,
            enc_Drive_Left, enc_Drive_Right,
            limit_Climber_Bottom, limit_Climber_Top, limit_Climber_Tilt_Rest, limit_Climber_Tilt_Extent);
  
    
    Hopper hopper = new Hopper(servo_Hopper_Pin_Frisbee_Lock, vic_Hopper_Belt, pad_Operator);
    
    Autonomous auto = new Autonomous(autoChooser, shotTimer, drive, shooter, hopper);
    public void robotInit() {
        drive.initDrive();
        shotTimer.initShotTimer();   
        shooter.initShooter();
        climber.initClimber();
        hopper.initHopper();
    }
    
    public void disabledInit() {
        drive.disableDrive();
        shooter.disableShooter();
        hopper.disableHopper();
    }
    
    public void autonomousInit() {
        drive.initDrive();
        shooter.initShooter();
        hopper.initHopper();
        auto.initAutonomous();
        climber.initClimber();
    }
    
    public void teleopInit() {
        drive.initDrive();
        shooter.initShooter();
        hopper.initHopper();
        climber.initClimber();
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
        hopper.runHopper();
    }
    public void testPeriodic() {

    }
}
