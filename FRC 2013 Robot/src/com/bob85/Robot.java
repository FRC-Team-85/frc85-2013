package com.bob85;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    
    Victor leftDriveMotor = new Victor(Drive.kLEFTDRIVE_VICTORS);
    Victor rightDriveMotor = new Victor(Drive.kRIGHTDRIVE_VICTORS);
    Servo leftDriveServo = new Servo(Drive.kLEFTDRIVE_SERVO);
    Servo rightDriveServo = new Servo (Drive.kRIGHTDRIVE_SERVO);
    Joystick leftDriveStick = new Joystick(1);
    Joystick rightDriveStick = new Joystick(2);
    Joystick opPad = new Joystick(3);
    Encoder leftDriveEncoder = new Encoder(Drive.kLEFTDRIVE_ENCODER_A, Drive.kLEFTDRIVE_ENCODER_B);
    Encoder rightDriveEncoder = new Encoder(Drive.kRIGHTDRIVE_ENCODER_A, Drive.kRIGHTDRIVE_ENCODER_B);
    Drive drive = new Drive(leftDriveMotor, rightDriveMotor, leftDriveServo, rightDriveServo,
            leftDriveEncoder, rightDriveEncoder, leftDriveStick, rightDriveStick);
    Climber climber = new Climber(leftDriveMotor,  rightDriveMotor, leftDriveEncoder, rightDriveEncoder);
    
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
        
        leftDriveMotor.set(MotorLinearization.calculateLinearOutput(rightDriveStick.getY()));
        rightDriveMotor.set(MotorLinearization.calculateLinearOutput(-rightDriveStick.getY()));
        
        if (opPad.getRawButton(8)){
            MotorLinearization.linearizeVictor884Output(hopperBelt, 1.0);
        } else {
            hopperBelt.set(0);
        }
        
        if (leftDriveStick.getTrigger()) {
            leftDriveServo.set(1);
            rightDriveServo.set(0);
            climber.inDriveMode = true;
        }
        if (rightDriveStick.getTrigger()) {
            leftDriveServo.set(0);
            rightDriveServo.set(1);
            climber.inDriveMode = false;
        }
        
    }
    
    public void testPeriodic() {
    
    }
    
}
