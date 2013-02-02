/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;
//import src.com.bob85.FrisbeeLoader;

/*
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Autonomous {

    Timer autoTimer;
    double waitTime = 6.5;//in seconds; this is an approximate
    double uWaitTime = (waitTime * MathUtils.pow(10.0, 6.0)); //in microSeconds (uSeconds)

    public void stageAutoDrive(double driveSpeed, SpeedController leftFrontSpControl, SpeedController leftBackSpControl, SpeedController rightFrontSpControl, SpeedController rightBackSpControl, Encoder leftDriveEnc, Encoder rightDriveEnc) {
        /*if (((leftDriveEnc.get() + rightDriveEnc.get()) / 2) < 200) { //twelveInches~200encCounts
         leftFrontSpControl.set(driveSpeed);
         leftBackSpControl.set(driveSpeed);
         rightFrontSpControl.set(driveSpeed);
         leftBackSpControl.set(driveSpeed);
         } else {
         leftFrontSpControl.set(0.0);
         leftBackSpControl.set(0.0);
         rightFrontSpControl.set(0.0);
         rightBackSpControl.set(0.0);
         autoTimer.start();
         }*/
        autoTimer.start();

        if (autoTimer.get() > uWaitTime && ((leftDriveEnc.get() + rightDriveEnc.get()) / 2) > -100) {
            leftFrontSpControl.set(-driveSpeed);
            leftBackSpControl.set(-driveSpeed);
            rightFrontSpControl.set(-driveSpeed);
            rightBackSpControl.set(-driveSpeed);
        } else if (autoTimer.get() > uWaitTime && ((leftDriveEnc.get() + rightDriveEnc.get()) / 2) == -100) {
            leftFrontSpControl.set(1.0);
            leftBackSpControl.set(1.0);
            rightFrontSpControl.set(-1.0);
            rightBackSpControl.set(-1.0);
        } else {
            leftFrontSpControl.set(0.0);
            leftBackSpControl.set(0.0);
            rightFrontSpControl.set(0.0);
            rightBackSpControl.set(0.0);
        }
    }

    public void stageAutoShoot(double shooterSpeed, SpeedController shooterMotor, Servo dropServo, Servo readyServo) {
        if (autoTimer.get() > 0.0) { //Timer.get() is in microSecinds
            shooterMotor.set(shooterSpeed);
            //unlockServo(dropServo); 
            //unlockServo(readyServo);
            //unfinished, write for disk loader mechanism
        } else if (autoTimer.get() >= uWaitTime) {
            shooterMotor.set(0.0);
            //lockServo(readyServo);
        } else {
            shooterMotor.set(0.0);
            //lockServo(dropServo);
            //lockServo(readyServo);
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
