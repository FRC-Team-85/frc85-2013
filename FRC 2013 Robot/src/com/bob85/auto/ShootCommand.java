package com.bob85.auto;

import com.bob85.Hopper;
import com.bob85.Shooter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShootCommand {
    private Timer timer;
    private Shooter shooter;
    private Hopper hopper;
    public static final int frisbee_val = 3; //Amount of frisbees to fire
    private ShotTimer shotTimer; 
    public int shotNumber = 1; //current shot number
    private double shotTime = 0.5; //length of time to leave hopper on in shot cycle
    private double currentTime;
    
    /**
     * Constructs a ShootCommand
     * @param shooter Shooter object
     * @param shotTimer ShotTimer object
     * @param hopper Hopper object
     */
    public ShootCommand(Shooter shooter, ShotTimer shotTimer, Hopper hopper) {
        this.shooter = shooter;
        this.hopper = hopper;
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
        currentTime = timer.get();
        return currentTime;
    }
    
    /**
     * Runs the hopper motor
     */
    public void runHopper() {
            hopper.setMotorOutputSetting(-1);
            hopper.setLinearizedOutput();
    }
    
    public void disableHopper() {
            hopper.setMotorOutputSetting(0);
            hopper.setLinearizedOutput();
    }
    
    /**
     * Turns the hopper belt on for a set time to shoot a frisbee
     * @param shotTime shot time setting to turn on the hopper motor
     * @return 
     */
    public boolean shootFrisbee(double shotTime) {
        if (getTime() >= shotTime && getTime() <= (shotTime + this.shotTime)) {
            runHopper();
            return false;
        } else if (getTime() < shotTime) {
            disableHopper();
            return false;
        } else {
            disableHopper();
            return true;
        }
    }
    
    public void runDiagnostics() {
        SmartDashboard.putNumber("Shot Number", shotNumber);
        SmartDashboard.putNumber("Shot Time", getTime());
        SmartDashboard.putNumber("Shot Time 1", shotTimer.getShotTime(1));
        SmartDashboard.putNumber("Shot Time 2", shotTimer.getShotTime(2));
        SmartDashboard.putNumber("Shot Time 3", shotTimer.getShotTime(3));
    }
    /**
     * Resets the ShootCommand variables
     */
    public void initShootCommand() {
        shotNumber = 1;
        timer.reset();
        timer.start();
        
    }
    
    /**
     * Runs the ShootCommand
     * @return is shootCommand() complete
     */
    public boolean shootCommand() {
        hopper.unlockServo();
        runDiagnostics();
        switch (shotNumber) {
            case 1:
                runShooter();
                if (shootFrisbee(shotTimer.getShotTime(1))) {
                    shotNumber = 2;
                }
                break;
            case 2:
                runShooter();
                if (shootFrisbee(shotTimer.getShotTime(2))) {
                    shotNumber = 3;
                }
                break;
            case 3:
                runShooter();
                if (shootFrisbee(shotTimer.getShotTime(3))) {
                    shotNumber = 4;
                }
                break;
            case 4:
                runShooter();
                if (getTime() < 15) {
                    runHopper();
                } else {
                    shotNumber = 5;
                    disableHopper();
                }
                break;
            default:
                disableShooter();
                disableHopper();
                break;
        }
        
        if (shotNumber == 5) {
            disableShooter();
            return true;
        } else {
            return false;
        }
    }
}
