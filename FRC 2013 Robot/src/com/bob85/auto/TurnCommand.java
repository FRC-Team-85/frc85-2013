package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.Timer;

public class TurnCommand {
    Drive drive;
    Timer timer;
    private double angle;
    private boolean isClockwise;
    private boolean isCommandStarted = false;
    private double maxOutput = 0.675; //drive motor output
    private double commandTimeOut = -1;
    
    /**
     * Constructs a TurnCommand with a Drive and desired angle
     * @param drive Drive Subsystem
     * @param angle Desired Angle in degrees
     */
    public TurnCommand(Drive drive, double angle) {
        this.drive = drive;
        this.angle = angle;
        isClockwise = (angle >= 0) ? this.isClockwise = true : false;
        timer = new Timer();
    }
    
    /**
     * Initializes a TurnCommand with a timeOut setting
     * @param drive Drive Subsystem
     * @param angle desired angle in degrees
     * @param timeOut timeout setting in seconds
     */
    public TurnCommand(Drive drive, double angle, double timeOut) {
        this(drive, angle);
        commandTimeOut = timeOut;
    }
    
    /**
     * Resets the gyro angle to 0
     */
    public void initTurnCommand() {
        drive.resetGyro();
        timer.reset();
        isCommandStarted = false;
    }
    
    /**
     * Turns the robot for a set angle clockwise
     * @return 
     */
    public boolean turnCommand() {
        if (!isCommandStarted) {
            timer.start();
            drive.resetGyro();
            isCommandStarted = true;
        }
        
        if (commandTimeOut != -1 && timer.get() > commandTimeOut) {
            return true;
        }
        
        if (isClockwise) {
            if (drive.getAngle() < angle) {
                drive.setMotorOutputSetting(maxOutput, -maxOutput);
                drive.setLinearizedOutput();
            } else if (drive.getAngle() >= angle) {
                drive.setMotorOutputSetting(0, 0);
                drive.setLinearizedOutput();
                timer.stop();
                return true;
            }
        } else {
            if (drive.getAngle() > angle) {
                drive.setMotorOutputSetting(-maxOutput, maxOutput);
                drive.setLinearizedOutput();
            } else if (drive.getAngle() <= angle) {
                drive.setMotorOutputSetting(0, 0);
                drive.setLinearizedOutput();
                timer.stop();
                return true;
            }
        }
        return false;
    }
}
