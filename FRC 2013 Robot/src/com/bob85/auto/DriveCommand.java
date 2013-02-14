/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class DriveCommand {

    private Drive drive;
    private double initialDist;
    private boolean isInitialDistSet;

    private String endDistanceOffset = "Drive Command Offset";
    
    public DriveCommand(Drive drive) {
        this.drive = drive;
    }
    
    public static void initSmartDashboardDefaultValues() {
        SmartDashboard.putNumber("Drive Command Offset", 5);
    }

    /**
     * Drives the robot for a desired distance. Currently waiting on a proper
     * closed loop autonomous drive method
     *
     * @param currentDist current displacement robot is at
     * @param desiredDist goal displacement of the robot
     * @param isEnabled is the method enabled
     */
    public void driveCommand(boolean isEnabled, double currentDist, double desiredDist) {
        if (isEnabled) {
            if (!isInitialDistSet) {
                drive.resetEncoders();
                initialDist = currentDist;
                isInitialDistSet = true;
            }
            if ((currentDist - initialDist) < (desiredDist - SmartDashboard.getNumber(endDistanceOffset))) {
                drive.runRampUpTrapezoidalMotionProfile(0.75);
            } else {
                drive.runRampDownTrapezoidalMotionProfile(0);
                isInitialDistSet = false;
     
            }
            drive.setLinearizedOutput();
        }
    }
}
