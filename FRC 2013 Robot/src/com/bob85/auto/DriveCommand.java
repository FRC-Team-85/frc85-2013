package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveCommand {

    private Drive drive;
    private boolean isResetEncoders;
    boolean isForward;

    private String endDistanceOffset = "Drive Command Offset";
    
    private double dist;
    private double maxOutput = 0.75;
    
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
     * Creates NetworkTable key & value for constant sensitivity to stop the robot 
     * before reaching desired distance
     */
    public static void initSmartDashboardDefaultValues() {
        SmartDashboard.putNumber("Drive Command Offset", 5);
    }
    
    /**
     * Resets and starts the encoders for the DriveCommand
     */
    public void initDriveCommand() {
        drive.resetEncoders();
        drive.enableEncoders();
    }

    /**
     * Drives the robot for a desired distance. 
     *
     * @param currentDist current displacement robot is at in inches
     */
    public boolean driveCommand(double currentDist) {
        
            if (!isResetEncoders) {
                drive.resetEncoders();
                isResetEncoders = true;
            }
            
            if (isForward) {
                if ((currentDist) < (dist - SmartDashboard.getNumber(endDistanceOffset))) {
                    drive.runRampUpTrapezoidalMotionProfile(maxOutput);
                    return false;
                } else {
                    drive.runRampDownTrapezoidalMotionProfile(0);
                    return true;
                }
            } else {
                if (currentDist > (dist + SmartDashboard.getNumber(endDistanceOffset))) {
                    drive.runRampUpTrapezoidalMotionProfile(-maxOutput);
                    return false;
                } else {
                    drive.runRampDownTrapezoidalMotionProfile(0);
                    return true;
                }
            }
        
    }
}
