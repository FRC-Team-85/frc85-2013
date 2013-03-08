package com.bob85.auto;

import com.bob85.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

    public static final int kShootStage = 1;
    public static final int kDrive1Stage = kShootStage + 1;
    public static final int kTurnStage = kDrive1Stage + 1;
    public static final int kDrive2Stage = kTurnStage + 1;
    private int autoStage = kShootStage;
    
    Timer autoTimer;
    AutoModeChooser autoChooser;
    ShotTimer shotTimer;
    Drive drive;
    Shooter shooter;
    Hopper frisbeeLoader;
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
        driveStage1Cmd = new DriveCommand(drive, -1);
        driveStage2Cmd = new DriveCommand(drive, 1);

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
            Shooter shooter, Hopper frisbeeLoader) {
        this.autoChooser = autoChooser;
        this.shotTimer = shotTimer;
        this.drive = drive;
        this.shooter = shooter;
        this.frisbeeLoader = frisbeeLoader;
        initCommands();
    }
    
    /**
     * Sends autonomous diagnostics to SmartDashboard
     */
    private void runDiagnostics() {
        SmartDashboard.putNumber("Gyro Angle", drive.getAngle());
        SmartDashboard.putNumber("Average Drive Dist", drive.getAverageEncodersDistance());
        SmartDashboard.putNumber("Auto Stage", autoStage);
    }

    private void runSequentialAutonomous() {
        switch (autoStage) {
            case kShootStage:
                if (autoChooser.shootStage) {
                    if (shootCmd.shootCommand()) {
                        autoStage = kDrive1Stage;
                    }
                } else {
                    autoStage = kDrive1Stage;
                }
                break;
            case kDrive1Stage:
                if (autoChooser.driveStage1) {
                    if (driveStage1Cmd.driveCommand()) {
                        autoStage = kTurnStage;
                    }
                } else {
                    autoStage = kTurnStage;
                }
                break;
            case kTurnStage:
                if (autoChooser.turnStage) {
                    if (turn180Cmd.turnCommand()) {
                        autoStage = kDrive2Stage;
                    }
                } else {
                    autoStage = kDrive2Stage;
                }
                break;
            case kDrive2Stage:
                if (autoChooser.driveStage2) {
                    if (driveStage2Cmd.driveCommand()) {
                        autoStage = kDrive2Stage + 1;
                    }
                } else {
                    autoStage = kDrive2Stage + 1;
                }
                break;
            default: //should only run when autonomous finishes
                finishAutonomous();
                break;
        }
    }
    
    /**
     *Disables actuators when autonomous is done 
     */
    public void finishAutonomous() {
        drive.disableDrive();
        shooter.disableShooter();
        frisbeeLoader.disableHopper();
    }
    
    /**
     * Runs the autonomous program
     */
    public void runAutonomous() {
        runSequentialAutonomous();
        runDiagnostics();
    }

    /**
     * Resets Autonomous & Command variables and gets mode & shot time settings
     */
    public void initAutonomous() {
        autoStage = kShootStage;
        autoChooser.runAutoModeChooser();
        shotTimer.runShotTimer();
        shootCmd.initShootCommand();
        driveStage1Cmd.initDriveCommand();
        turn180Cmd.initTurnCommand();
        driveStage2Cmd.initDriveCommand();
    }
}
