package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.Timer;

public class DriveCommand {

    private Drive drive;
    private Timer timer;
    private boolean isCommandStarted; //checks if command initialization run has occured
    boolean isForward;
    private double commandTimeOut = -1; //time out setting for DriveCommand, -1 defaults to no time out
    private double endDistanceOffset = 1;
    
    private double dist;
    private double currentDist;
    private double maxOutput = 0.35;
    
    /**
     * Constructs a DriveCommand with a reference to a Drive object
     * @param drive Drive object
     * @param dist Desired distance in inches
     */
    public DriveCommand(Drive drive, double dist) {
        this.drive = drive;
        this.dist = dist;
        isForward = (dist >= 0) ? true : false;
    }
    
    public DriveCommand(Drive drive, double dist, double timeOut) {
        this(drive, dist);
        commandTimeOut = timeOut;
        timer = new Timer();
    }
    
    public void changeDesiredDistance(double dist) {
        this.dist = dist;
        isForward = (dist >= 0) ? true : false;
    }
    
    /**
     * Resets and starts the encoders for the DriveCommand
     */
    public void initDriveCommand() {
        drive.resetEncoders();
        drive.enableEncoders();
        isCommandStarted = false;
    }

    /**
     * Drives the robot for a desired distance. 
     *
     * @param currentDist current displacement robot is at in inches
     */
    public boolean driveCommand() {
            
            if (!isCommandStarted) {
                drive.resetEncoders();
                timer.start();
                isCommandStarted = true;
            }
            
            if (commandTimeOut != -1 && timer.get() > commandTimeOut) {
                return true;
            }
            
            currentDist = drive.getAverageEncodersDistance();
            
            if (isForward) {
                if ((currentDist) < (dist)) {
                    //drive.runRampUpTrapezoidalMotionProfile(maxOutput);
                    drive.setMotorOutputSetting(maxOutput, maxOutput);
                                drive.setNonlinearizedOutput();
                } else {
                    //drive.runRampDownTrapezoidalMotionProfile(0);
                    drive.setMotorOutputSetting(0 , 0);
                    drive.setNonlinearizedOutput();
                    return true;
                }
            } else {
                if (currentDist > (dist)) {
                    //drive.runRampUpTrapezoidalMotionProfile(-maxOutput);
                    drive.setMotorOutputSetting(-maxOutput, -maxOutput);
                    drive.setNonlinearizedOutput();
                } else {
                    //drive.runRampDownTrapezoidalMotionProfile(0);
                    drive.setMotorOutputSetting(0 , 0);
                    drive.setNonlinearizedOutput();
                    return true;
                }
            }
        return false;
    }
}
