/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import com.bob85.Drive;
import edu.wpi.first.wpilibj.Gyro;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class TurnCommand {
    Gyro gyro;
    Drive drive;
    private boolean gyroReset;
    private double angle;
    
    public TurnCommand(Gyro gyro, Drive drive, double angle) {
        this.gyro = gyro;
        this.drive = drive;
        this.angle = angle;
    }
    
    public boolean turnCommand() {
        if (!gyroReset) {
            gyro.reset();
            gyroReset = true;
        }
        
        if (gyro.getAngle() < angle) {
            drive.setMotorOutputSetting(0.5, 0.5);
            drive.setLinearizedOutput();
            return false;
        } else if (gyro.getAngle() > angle) {
            drive.setMotorOutputSetting(0, 0);
            drive.setLinearizedOutput();
            return true;
        } else {
            return false;
        }
    }
}
