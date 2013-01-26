package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Climber {

    public void elevatorHooks(Encoder elevEnc, Joystick auxStick, int upButton, int downButton, SpeedController leftDriveMotor, SpeedController rightDriveMotor, double elevDriveSpeed, boolean inElevMode) {

        if (inElevMode == true && auxStick.getRawButton(upButton) == true) {
            //Drive Elev Up
            leftDriveMotor.set(elevDriveSpeed);
            rightDriveMotor.set(-elevDriveSpeed);
        } else if (inElevMode == true && auxStick.getRawButton(downButton) == true) {
            //Drive Elev Down
            leftDriveMotor.set(-elevDriveSpeed);
            rightDriveMotor.set(elevDriveSpeed);
        } else {
            //Stop Elev
            leftDriveMotor.set(0);
            rightDriveMotor.set(0);
        }

    }

    public void driveElevShift(Joystick leftStick, int driveShiftButton, int elevShiftButton, Servo leftShiftServo, Servo rightShiftServo, boolean inElevMode) {
        //driveModeButton
        if (leftStick.getRawButton(driveShiftButton) == true) {
            leftShiftServo.set(0.0);
            rightShiftServo.set(1.0);
            inElevMode = false;
        //elevModeButton    
        } else if (leftStick.getRawButton(elevShiftButton) == true) {
            leftShiftServo.set(1.0);
            rightShiftServo.set(0.0);
            inElevMode = true;
        }
    }
    
    public void diskIntake(Joystick auxStick, SpeedController intakeRoller, int feedInButton, int feedOutButton){
        if (auxStick.getRawButton(feedInButton)){
            intakeRoller.set(0.6);
        }
        else if (auxStick.getRawButton(feedOutButton)){
            intakeRoller.set(-0.6);
        }
        else {
            intakeRoller.set(0);
        }
    }
}
