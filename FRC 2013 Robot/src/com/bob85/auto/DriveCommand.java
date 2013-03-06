package com.bob85.auto;

import com.bob85.Drive;

public class DriveCommand {

    private Drive drive;
    private boolean isResetEncoders;
    boolean isForward;

    private double endDistanceOffset = 1;
    
    private double dist;
    private double currentDist;
    private double maxOutput = 0.5;
    
    /**
     * Constructs a DriveCommand with a reference to a Drive object
     * @param drive Drive object
     * @param dist Desired distance in inches
     */
    public DriveCommand(Drive drive, double dist) {
        this.drive = drive;
        this.dist = dist;
        isForward = (dist >= 0) ? true : false;
    }
    
    /**
     * Resets and starts the encoders for the DriveCommand
     */
    public void initDriveCommand() {
        drive.resetEncoders();
        drive.enableEncoders();
        isResetEncoders = false;
    }

    /**
     * Drives the robot for a desired distance. 
     *
     * @param currentDist current displacement robot is at in inches
     */
    public boolean driveCommand() {
            
            if (!isResetEncoders) {
                drive.resetEncoders();
                isResetEncoders = true;
            }
            
            currentDist = drive.getAverageEncodersDistance();
            
            if (isForward) {
                if ((currentDist) < (dist - endDistanceOffset)) {
                    drive.runRampUpTrapezoidalMotionProfile(maxOutput);
                    return false;
                } else {
                    drive.runRampDownTrapezoidalMotionProfile(0);
                    return true;
                }
            } else {
                if (currentDist > (dist + endDistanceOffset)) {
                    drive.runRampUpTrapezoidalMotionProfile(-maxOutput);
                    return false;
                } else {
                    drive.runRampDownTrapezoidalMotionProfile(0);
                    return true;
                }
            }
        
    }
}
