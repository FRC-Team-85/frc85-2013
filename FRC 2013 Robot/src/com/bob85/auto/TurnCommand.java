package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.Gyro;

public class TurnCommand {
    Drive drive;
    private double angle;
    
    public TurnCommand(Drive drive, double angle) {
        this.drive = drive;
        this.angle = angle;
    }
    
    public void initTurnCommand() {
        drive.resetGyro();
    }
    
    public boolean turnCommand() {
        
        if (drive.getAngle() < angle) {
            drive.setMotorOutputSetting(0.5, -0.5);
            drive.setLinearizedOutput();
            return false;
        } else if (drive.getAngle() > angle) {
            drive.setMotorOutputSetting(0, 0);
            drive.setLinearizedOutput();
            return true;
        } else {
            return false;
        }
    }
}
