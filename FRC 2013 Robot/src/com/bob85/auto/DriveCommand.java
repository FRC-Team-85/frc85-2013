package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveCommand {

    private Drive drive;
    private double initialDist;
    private boolean isInitialDistSet;

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
     * Resets the encoders for the DriveCommand
     */
    public void initDriveCommand() {
        drive.resetEncoders();
    }

    /**
     * Drives the robot for a desired distance. 
     *
     * @param currentDist current displacement robot is at in inches
     * @param desiredDist goal displacement of the robot in inches
     */
    public boolean driveCommand(double currentDist, double desiredDist) {
            if (!isInitialDistSet) {
                drive.resetEncoders();
                initialDist = currentDist;
                isInitialDistSet = true;
            }
            if ((currentDist - initialDist) < (desiredDist - SmartDashboard.getNumber(endDistanceOffset))) {
                drive.runRampUpTrapezoidalMotionProfile(0.75);
                drive.setLinearizedOutput();
                return false;
            } else {
                drive.runRampDownTrapezoidalMotionProfile(0);
                drive.setLinearizedOutput();
                return true;
            }
        
    }
}
