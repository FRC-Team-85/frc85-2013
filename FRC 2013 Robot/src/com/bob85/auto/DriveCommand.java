package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveCommand {

    private Drive drive;
    private boolean isResetEncoders;

    private String endDistanceOffset = "Drive Command Offset";
    
    /**
     * Constructs a DriveCommand with a reference to a Drive object
     * @param drive Drive object
     */
    public DriveCommand(Drive drive) {
        this.drive = drive;
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
     * @param desiredDist goal displacement of the robot in inches
     */
    public boolean driveCommand(double currentDist, double desiredDist) {
            boolean isForward = false;
            
            if (desiredDist >= 0) {
                isForward = true;
            }
        
            if (!isResetEncoders) {
                drive.resetEncoders();
                isResetEncoders = true;
            }
            
            if (isForward) {
                if ((currentDist) < (desiredDist - SmartDashboard.getNumber(endDistanceOffset))) {
                    drive.runRampUpTrapezoidalMotionProfile(0.75);
                    return false;
                } else {
                    drive.runRampDownTrapezoidalMotionProfile(0);
                    return true;
                }
            } else {
                if (currentDist > (desiredDist + SmartDashboard.getNumber(endDistanceOffset))) {
                    drive.runRampUpTrapezoidalMotionProfile(-0.75);
                    return false;
                } else {
                    drive.runRampDownTrapezoidalMotionProfile(0);
                    return true;
                }
            }
        
    }
}
