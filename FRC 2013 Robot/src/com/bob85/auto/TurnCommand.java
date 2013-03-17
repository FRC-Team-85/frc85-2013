package com.bob85.auto;

import com.bob85.Drive;

public class TurnCommand {
    Drive drive;
    private double angle;
    private boolean isClockwise;
    private double maxOutput = 0.675;
    
    /**
     * Constructs a TurnCommand with a Drive and desired angle
     * @param drive
     * @param angle 
     */
    public TurnCommand(Drive drive, double angle) {
        this.drive = drive;
        this.angle = angle;
        
        isClockwise = (angle >= 0) ? this.isClockwise = true : false;
    }
    /**
     * Resets the gyro angle to 0
     */
    public void initTurnCommand() {
        drive.resetGyro();
    }
    
    /**
     * Turns the robot for a set angle clockwise
     * @return 
     */
    public boolean turnCommand() {
        
        if (isClockwise) {
            if (drive.getAngle() < angle) {
                drive.setMotorOutputSetting(maxOutput, -maxOutput);
                drive.setLinearizedOutput();
            } else if (drive.getAngle() >= angle) {
                drive.setMotorOutputSetting(0, 0);
                drive.setLinearizedOutput();
                return true;
            } else {
            }
        } else {
            if (drive.getAngle() > angle) {
                drive.setMotorOutputSetting(-maxOutput, maxOutput);
                drive.setLinearizedOutput();
            } else if (drive.getAngle() <= angle) {
                drive.setMotorOutputSetting(0, 0);
                drive.setLinearizedOutput();
                return true;
            } else {
            }
        }
        return false;
    }
}
