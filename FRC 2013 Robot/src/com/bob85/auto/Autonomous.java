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
    ShootCommand shootCmd;
    TurnCommand turn180Cmd;
    DriveCommand driveStage2Cmd;
    DriveCommand driveStage1Cmd;
    
    /**
     * Instantiates the Commands
     */
    private void initCommands() {
        shootCmd = new ShootCommand(shooter, shotTimer, frisbeeLoader);
        turn180Cmd = new TurnCommand(drive, 180);
        driveStage2Cmd = new DriveCommand(drive);
        driveStage1Cmd = new DriveCommand(drive);
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
                    if (shootCmd.shootCommand()) {
                        autoStage = 1;
                    }
                } else {
                    autoStage = 1;
                }
                break;
            case 1:
                if (autoChooser.driveStage1) {
                    if (driveStage1Cmd.driveCommand(drive.getEncodersDistance(), 200)) {
                        autoStage = 2;
                    }
                } else {
                    autoStage = 2;
                }
            case 2:
                if (autoChooser.turnStage) {
                    if (turn180Cmd.turnCommand()) {
                        autoStage = 3;
                    }
                } else {
                    autoStage = 3;
                }
                break;
            case 3:
                if (autoChooser.driveStage2) {
                    if (driveStage2Cmd.driveCommand(drive.getEncodersDistance(), 500)) {
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
        shootCmd.initShootCommand();
        driveStage1Cmd.initDriveCommand();
        turn180Cmd.initTurnCommand();
        driveStage2Cmd.initDriveCommand();
    }
}
