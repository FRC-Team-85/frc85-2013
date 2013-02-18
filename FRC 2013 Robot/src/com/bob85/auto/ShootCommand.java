package com.bob85.auto;

import com.bob85.FrisbeeLoader;
import com.bob85.Shooter;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class ShootCommand {
    private Timer timer;
    private Shooter shooter;
    private FrisbeeLoader frisbeeLoader;
    public static final int frisbee_val = 3; //Amount of frisbees to fire
    private ShotTimer shotTimer;
    private int shotNumber = 1;
    private double shotTime = 0.15;
    private double currentTime;
    
    public ShootCommand(Shooter shooter, ShotTimer shotTimer, FrisbeeLoader frisbeeLoader) {
        this.shooter = shooter;
        this.frisbeeLoader = frisbeeLoader;
        this.shotTimer = shotTimer;
        timer = new Timer();
    }
    
    private void runShooter() {
        shooter.setShooterSpeed(1);
        shooter.setShooterBeltSpeed(1);
    }
    
    public double getTime() {
        timer.start();
        currentTime = timer.get() * MathUtils.pow(10, -6);
        return currentTime;
    }
    
    public boolean shootFrisbee(double shotTime) {
        if (getTime() >= shotTime && getTime() <= (shotTime + this.shotTime)) {
            frisbeeLoader.setHopperBeltMotor(1);
            return false;
        } else if (getTime() < shotTime) {
            frisbeeLoader.setHopperBeltMotor(0);
            return false;
        } else {
            return true;
        }
    }
    
    public boolean shootCommand() {
        runShooter();
        timer.start();
        SmartDashboard.putNumber("ShotCommand Timer", getTime());
        switch (shotNumber) {
            case 1:
                if (shootFrisbee(shotTimer.getShotTime(1))) {
                    shotNumber = 2;
                }
                break;
            case 2:
                if (shootFrisbee(shotTimer.getShotTime(2))) {
                    shotNumber = 3;
                }
                break;
            case 3:
                if (shootFrisbee(shotTimer.getShotTime(3))) {
                    shotNumber = 4;
                }
                break;
            default:
                break;
        }
        
        if (shotNumber == 4) {
            return true;
        } else {
            return false;
        }
    }
}
