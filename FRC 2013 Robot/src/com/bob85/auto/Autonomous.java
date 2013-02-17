/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import com.bob85.*;
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
    ShotTimer shotTimer;
    Gyro gyro;
    Drive drive;
    Shooter shooter;
    FrisbeeLoader frisbeeLoader;
    ShootCommand shoot3Command;
    TurnCommand turn180Command;
    DriveCommand drivetoCenterCommand;
    
    private void initCommands() {
        shoot3Command = new ShootCommand(shooter, shotTimer, frisbeeLoader);
        turn180Command = new TurnCommand(gyro, drive, 180);
        drivetoCenterCommand = new DriveCommand(drive);
    }

    public Autonomous(AutoModeChooser autoChooser, ShotTimer shotTimer, Gyro gyro, Drive drive,
            Shooter shooter, FrisbeeLoader frisbeeLoader) {
        this.autoChooser = autoChooser;
        this.shotTimer = shotTimer;
        this.gyro = gyro;
        this.drive = drive;
        this.shooter = shooter;
        this.frisbeeLoader = frisbeeLoader;
        initCommands();
    }

    private void runSequentialAutonomous() {
        switch (autoStage) {
            case 0:
                if (autoChooser.shootStage) {
                    if (shoot3Command.shootCommand()) {
                        autoStage = 1;
                    }
                } else {
                    autoStage = 1;
                }
                break;
            case 1:
                if (autoChooser.turnStage) {
                    if (turn180Command.turnCommand()) {
                        autoStage = 2;
                    }
                } else {
                    autoStage = 2;
                }
                break;
            case 2:
                if (autoChooser.driveStage) {
                    if (drivetoCenterCommand.driveCommand(drive.getEncodersDistance(), 500)) {
                        autoStage = 3;
                    }
                } else {
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
        shotTimer.runShotTimer();
        runSequentialAutonomous();
    }

    public void initAutonomous() {
        autoStage = 0;
        autoChooser.runAutoModeChooser();
        shotTimer.initShotTimer();
    }
}
