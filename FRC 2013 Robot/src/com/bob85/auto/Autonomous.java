package com.bob85.auto;

import com.bob85.*;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;

public class Autonomous {

    private int autoStage = 0;
    
    Timer autoTimer;
    AutoModeChooser autoChooser;
    ShotTimer shotTimer;
    Drive drive;
    Shooter shooter;
    FrisbeeLoader frisbeeLoader;
    ShootCommand shoot3Command;
    TurnCommand turn180Command;
    DriveCommand drivetoCenterCommand;
    DriveCommand driveBackOffCommand;
    
    /**
     * Instantiates the Commands
     */
    private void initCommands() {
        shoot3Command = new ShootCommand(shooter, shotTimer, frisbeeLoader);
        turn180Command = new TurnCommand(drive, 180);
        drivetoCenterCommand = new DriveCommand(drive);
        driveBackOffCommand = new DriveCommand(drive);
    }

    /**
     * Constructs an Autonomous object
     * @param autoChooser Autonomous Mode Selector
     * @param shotTimer ShootCommand Shot Time Settings
     * @param drive Drive
     * @param shooter Shooter
     * @param frisbeeLoader Hopper
     */
    public Autonomous(AutoModeChooser autoChooser, ShotTimer shotTimer, Drive drive,
            Shooter shooter, FrisbeeLoader frisbeeLoader) {
        this.autoChooser = autoChooser;
        this.shotTimer = shotTimer;
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
                if (autoChooser.driveStage1) {
                    if (driveBackOffCommand.driveCommand(drive.getEncodersDistance(), 200)) {
                        autoStage = 2;
                    }
                } else {
                    autoStage = 2;
                }
            case 2:
                if (autoChooser.turnStage) {
                    if (turn180Command.turnCommand()) {
                        autoStage = 3;
                    }
                } else {
                    autoStage = 3;
                }
                break;
            case 3:
                if (autoChooser.driveStage2) {
                    if (drivetoCenterCommand.driveCommand(drive.getEncodersDistance(), 500)) {
                        autoStage = 4;
                    }
                } else {
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
     * Runs the autonomous program
     */
    public void runAutonomous() {
        runSequentialAutonomous();
    }

    /**
     * Resets Autonomous & Command variables and gets mode & shot time settings
     */
    public void initAutonomous() {
        autoStage = 0;
        autoChooser.runAutoModeChooser();
        shotTimer.runShotTimer();
        shoot3Command.initShootCommand();
        driveBackOffCommand.initDriveCommand();
        turn180Command.initTurnCommand();
        drivetoCenterCommand.initDriveCommand();
    }
}
