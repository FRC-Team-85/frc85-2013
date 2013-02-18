package com.bob85;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    Victor leftDriveMotor = new Victor(Drive.kLEFTDRIVE_VICTORS);
    Victor rightDriveMotor = new Victor(Drive.kRIGHTDRIVE_VICTORS);
    Servo leftDriveServo = new Servo(Drive.kLEFTDRIVE_SERVO);
    Servo rightDriveServo = new Servo (Drive.kRIGHTDRIVE_SERVO);
    Joystick leftStick = new Joystick(1);
    Joystick rightStick = new Joystick(2);
    Joystick opPad = new Joystick(3);
    Encoder leftDriveEncoder = new Encoder(Drive.kLEFTDRIVE_ENCODER_A, Drive.kLEFTDRIVE_ENCODER_B);
    Encoder rightDriveEncoder = new Encoder(Drive.kRIGHTDRIVE_ENCODER_A, Drive.kRIGHTDRIVE_ENCODER_B);
    DigitalInput bottomClimberLimitSwitch = new DigitalInput(Climber.kBOTTOM_LIMITSWITCH_CHANNEL,Climber.kBOTTOM_LIMITSWITCH_SLOT);
    DigitalInput topClimberLimitSwitch = new DigitalInput(Climber.kTOP_LIMITSWITCH_CHANNEL,Climber.kTOP_LIMITSWITCH_SLOT);
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, leftDriveServo, rightDriveServo,
            leftDriveEncoder, rightDriveEncoder, leftStick, rightStick);
    Climber climber = new Climber(leftStick, rightStick,
            leftDriveMotor, rightDriveMotor, 
            leftDriveEncoder, rightDriveEncoder, 
            bottomClimberLimitSwitch, topClimberLimitSwitch);
    
    Victor hopperBelt = new Victor(7);

    public void robotInit() {
        drive.driveInit();
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
        //drive.encoderTestDrive();
        //climber.manualJoystickElevDrive(rightDriveStick, 500); no limitswitches implemented yet
        
        SmartDashboard.putNumber("leftSideEncoderDist", leftDriveEncoder.getDistance());
        SmartDashboard.putNumber("rightSideEncoderDist", rightDriveEncoder.getDistance());
        SmartDashboard.putNumber("leftEncRaw", leftDriveEncoder.get());
        SmartDashboard.putNumber("rightEncRaw", rightDriveEncoder.get());
        
        leftDriveMotor.set(MotorLinearization.calculateLinearOutput(rightStick.getY()));
        rightDriveMotor.set(MotorLinearization.calculateLinearOutput(-rightStick.getY()));
        
        if (opPad.getRawButton(8)){
            MotorLinearization.linearizeVictor884Output(hopperBelt, 1.0);
        } else {
            hopperBelt.set(0);
        }
        
    }
    
    public void testPeriodic() {
    
    }
    
}
