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
public class Autonomous {

    Timer autoTimer;
    double waitTime;

    public void stageAutoDrive(double driveSpeed, SpeedController leftSpControl, SpeedController rightSpControl, Encoder leftDriveEnc, Encoder rightDriveEnc) {
        if (((leftDriveEnc.get() + rightDriveEnc.get()) / 2) < 200) {
            leftSpControl.set(driveSpeed);
            rightSpControl.set(driveSpeed);
        } else {
            leftSpControl.set(0.0);
            rightSpControl.set(0.0);
            autoTimer.start();
        }

        if (autoTimer.get() > waitTime && ((leftDriveEnc.get() + rightDriveEnc.get()) / 2) > 0) {
            leftSpControl.set(-driveSpeed);
            rightSpControl.set(-driveSpeed);
        } else {
            leftSpControl.set(0.0);
            rightSpControl.set(0.0);
        }
    }

    public void stageAutoShoot(double shooterSpeed, SpeedController shooterMotor) {
        if (autoTimer.get() > 0) {
            shooterMotor.set(shooterSpeed);
            //unfinished, write for disk loader mechanism


        } else {
            shooterMotor.set(0.0);
        }
    }

    /**
     * Skeleton for a test function
     */
    private static void testAutoMode() {
    }

    /**
     * Only public access to the class
     */
    public static void runAutonomous() {
        testAutoMode();
    }
}
