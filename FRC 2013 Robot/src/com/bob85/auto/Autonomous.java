\package com.bob85.auto;

import com.bob85.Drive;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;
//import src.com.bob85.FrisbeeLoader;

/*
 * @author Michael Chau <mchau95@gmail.com>
 */
public class Autonomous {

    private int autoStage = 0;
    
    Timer autoTimer;
    double waitTime = 6.5;//in seconds; this is an approximate
    double uWaitTime = (waitTime * MathUtils.pow(10.0, 6.0)); //in microSeconds (uSeconds)
    AutoModeChooser autoChooser;
    Gyro gyro;
    Drive drive;
    TurnCommand turn180Command;
    DriveCommand drivetoCenterCommand;
    
    private void initCommands() {
        turn180Command = new TurnCommand(gyro, drive, 180);
        drivetoCenterCommand = new DriveCommand(drive);
    }

    public Autonomous(AutoModeChooser autoChooser, Gyro gyro, Drive drive) {
        this.autoChooser = autoChooser;
        this.gyro = gyro;
        this.drive = drive;
        initCommands();
    }

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
       //Need to switch to stages
        autoTimer.start();
//Timer.get() is in microSeconds
        if (autoTimer.get() > uWaitTime && ((leftDriveEnc.get() + rightDriveEnc.get()) / 2) > -100) { //stage2-->4?
            leftFrontSpControl.set(-driveSpeed); 
            leftBackSpControl.set(-driveSpeed);
            rightFrontSpControl.set(-driveSpeed);
            rightBackSpControl.set(-driveSpeed);
        } else if (autoTimer.get() > uWaitTime && ((leftDriveEnc.get() + rightDriveEnc.get()) / 2) == -100) { //need to check this by testing
            leftFrontSpControl.set(1.0);
            leftBackSpControl.set(1.0);
            rightFrontSpControl.set(-1.0);
            rightBackSpControl.set(-1.0);
        } else if (autoTimer.get() == (uWaitTime * 1.5)) {
            leftFrontSpControl.set(0.0);
            leftBackSpControl.set(0.0);
            rightFrontSpControl.set(0.0);
            rightBackSpControl.set(0.0);
        } else {
            leftFrontSpControl.set(0.0);
            leftBackSpControl.set(0.0);
            rightFrontSpControl.set(0.0);
            rightBackSpControl.set(0.0);
        }
    }

    public void stageAutoShoot(double shooterSpeed, SpeedController shooterMotor, Servo dropServo, Servo readyServo) {
        if (autoTimer.get() > 0.0) { //stage1
            shooterMotor.set(shooterSpeed);
            //unlockServo(dropServo); 
            //unlockServo(readyServo);
            //~unfinished, write for disk loader mechanism
        } else if (autoTimer.get() >= uWaitTime) {
            shooterMotor.set(0.0);
            //lockServo(readyServo);
        } else {
            shooterMotor.set(0.0);
            //lockServo(dropServo);
            //lockServo(readyServo);
        }
    }

    private void runSequentialAutonomous() {
        switch (autoStage) {
            case 0:
                if (true) {
                    autoStage = 1;
                }
                break;
            case 1:
                
                if (turn180Command.turnCommand()) {
                    autoStage = 2;
                }
                break;
            case 2:
                if (drivetoCenterCommand.driveCommand(drive.getEncodersDistance(), 50)) {
                    autoStage = 3;
                }
                break;
            case 3:
                if (true) {
                    autoStage = 4;
                }
                break;
            case 4:
                if (true) {
                    autoStage = 5;
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Skeleton for a test function
     */
    private void testAutoMode() {
        
    }
    
    private void runCompetitionAutonomous() {
        runSequentialAutonomous();
    }

    /**
     * Only public access to the class
     */
    public void runAutonomous() {
        testAutoMode();
    }

    public void initAutonomous() {
        autoStage = 0;
        autoChooser.testDriveStationInputs();
    }
}
