package com.bob85.auto;

import com.bob85.Hopper;
import com.bob85.Shooter;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShootCommand {
    private Timer timer;
    private Shooter shooter;
    private Hopper frisbeeLoader;
    public static final int frisbee_val = 3; //Amount of frisbees to fire
    private ShotTimer shotTimer; 
    public int shotNumber = 1; //current shot number
    private double shotTime = 0.15; //length of time to leave hopper on in shot cycle
    private double currentTime;
    
    /**
     * Constructs a ShootCommand
     * @param shooter Shooter object
     * @param shotTimer ShotTimer object
     * @param frisbeeLoader Hopper object
     */
    public ShootCommand(Shooter shooter, ShotTimer shotTimer, Hopper frisbeeLoader) {
        this.shooter = shooter;
        this.frisbeeLoader = frisbeeLoader;
        this.shotTimer = shotTimer;
        timer = new Timer();
    }
    
    /**
     * Turns the shooter and shooter belt on
     */
    private void runShooter() {
        shooter.setShooterSpeed(1);
        shooter.setShooterBeltSpeed(1);
    }
    
    /**
     * Turns the shooter and shooter belt to zero output
     */
    private void disableShooter() {
        shooter.setShooterSpeed(0);
        shooter.setShooterBeltSpeed(0);
    }
    
    /**
     * Gets time in seconds
     * @return 
     */
    public double getTime() {
        timer.start();
        currentTime = timer.get() * MathUtils.pow(10, -6);
        return currentTime;
    }
    
    /**
     * Turns the hopper belt on for a set time to shoot a frisbee
     * @param shotTime shot time setting to turn on the hopper motor
     * @return 
     */
    public boolean shootFrisbee(double shotTime) {
        if (getTime() >= shotTime && getTime() <= (shotTime + this.shotTime)) {
            frisbeeLoader.setMotorOutputSetting(-1);
            frisbeeLoader.setLinearizedOutput();
            return false;
        } else if (getTime() < shotTime) {
            frisbeeLoader.setMotorOutputSetting(0);
            frisbeeLoader.setLinearizedOutput();
            return false;
        } else {
            frisbeeLoader.setMotorOutputSetting(0);
            frisbeeLoader.setLinearizedOutput();
            return true;
        }
    }
    
    /**
     * Resets the ShootCommand variables
     */
    public void initShootCommand() {
        shotNumber = 1;
        timer.reset();
    }
    
    /**
     * Runs the ShootCommand
     * @return is shootCommand() complete
     */
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
                disableShooter();
                break;
        }
        
        if (shotNumber == 4) {
            disableShooter();
            return true;
        } else {
            return false;
        }
    }
}
