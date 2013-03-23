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
    AutoPrefs autoPrefs;
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
        shootCmd = new ShootCommand(shooter, autoPrefs, frisbeeLoader);
        turn180Cmd = new TurnCommand(drive, 180, 5);
        driveStage1Cmd = new DriveCommand(drive, -12, 2);
        driveStage2Cmd = new DriveCommand(drive, 12, 1);

    }

    /**
     * Constructs an Autonomous object
     * @param autoChooser Autonomous Mode Selector
     * @param autoPrefs ShootCommand Shot Time Settings
     * @param drive Drive
     * @param shooter Shooter
     * @param frisbeeLoader Hopper
     */
    public Autonomous(AutoModeChooser autoChooser, AutoPrefs autoPrefs, Drive drive,
            Shooter shooter, Hopper frisbeeLoader) {
        this.autoChooser = autoChooser;
        this.autoPrefs = autoPrefs;
        this.drive = drive;
        this.shooter = shooter;
        this.frisbeeLoader = frisbeeLoader;
        initCommands();
    }
    
    /**
     * Assigns the saved preferences to the autonomous command settings
     */
    private void setAutonomousSettings() {
        driveStage1Cmd.changeDesiredDistance(autoPrefs.driveSettings[0]);
        turn180Cmd.changeDesiredAngle(autoPrefs.driveSettings[1]);
        driveStage2Cmd.changeDesiredDistance(autoPrefs.driveSettings[2]);
    }
    
    /**
     * Sends autonomous diagnostics to SmartDashboard
     */
    private void runDiagnostics() {
        SmartDashboard.putNumber(Drive.key_Gyro_Angle, drive.getAngle());
        SmartDashboard.putNumber(Drive.key_Encoder_Average_Dist, drive.getAverageEncodersDistance());
        //SmartDashboard.putNumber("Auto Stage", autoStage);
        SmartDashboard.putBoolean(Shooter.key_shooterRPMCheck, shooter.onTarget());
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
        //runDiagnostics();
    }

    /**
     * Resets Autonomous & Command variables and gets mode & shot time settings
     */
    public void initAutonomous() {
        finishAutonomous();
        autoStage = kShootStage;
        autoChooser.runAutoModeChooser();
        autoPrefs.runAutoPrefs();
        setAutonomousSettings();
        shootCmd.initShootCommand();
        driveStage1Cmd.initDriveCommand();
        turn180Cmd.initTurnCommand();
        driveStage2Cmd.initDriveCommand();
    }
}
