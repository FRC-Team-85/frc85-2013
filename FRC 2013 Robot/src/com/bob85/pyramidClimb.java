package com.bob85;

import edu.wpi.first.wpilibj.*;


public class pyramidClimb {
    
    
    public void elevatorHooks(Encoder elevEnc, Joystick auxStick, int upButton, int downButton, SpeedController leftDriveMotor, SpeedController rightDriveMotor, double elevDriveSpeed) {
     
        if (auxStick.getRawButton(upButton) == true) {
            //Drive Elev Up
            leftDriveMotor.set(elevDriveSpeed);
            rightDriveMotor.set(-elevDriveSpeed);
        } else if (auxStick.getRawButton(downButton) == true) {
            //Drive Elev Down
            leftDriveMotor.set(-elevDriveSpeed);
            rightDriveMotor.set(elevDriveSpeed);
        } else {
            //Stop Elev
            leftDriveMotor.set(0);
            rightDriveMotor.set(0);
        }

}
    
    public void driveElevShift(Joystick leftStick, int driveShiftButton, int elevShiftButton, Servo leftShiftServo, Servo rightShiftServo){
        
    }
    
}
