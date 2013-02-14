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
    
    public TurnCommand(Gyro gyro, Drive drive) {
        this.gyro = gyro;
        this.drive = drive;
    }
    
    public void turnCommand(double angle) {
        if (!gyroReset) {
            gyro.reset();
            gyroReset = true;
        }
    }
}
