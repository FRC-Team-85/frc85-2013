/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class DriveCommand {

    private RobotDrive m_drive;
    private double initialDist;
    private boolean isInitialDistSet;

    public DriveCommand(RobotDrive drive) {
        m_drive = drive;
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
                initialDist = currentDist;
                isInitialDistSet = true;
            }
            if ((currentDist - initialDist) < desiredDist) {
                m_drive.drive(1, 0);
            } else {
                m_drive.drive(0, 0);
                isInitialDistSet = false;
            }
        }
    }
}
