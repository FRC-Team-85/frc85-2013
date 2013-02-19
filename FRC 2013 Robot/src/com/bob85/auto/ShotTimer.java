package com.bob85.auto;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShotTimer {
    
    public static final String shotTime1Key = "Shot Time 1";
    public static final String shotTime2Key = "Shot Time 2";
    public static final String shotTime3Key = "Shot Time 3";

    
    int frisbees_val;
    double[] shotTimes;
    
    public ShotTimer(int frisbees) {
        frisbees_val = frisbees;
        shotTimes = new double[frisbees];
    }
    
    /**
     * Sends NetworkTable objects to SmartDashboard to initiate them
     */
    public void initShotTimerData() {
        switch (frisbees_val) {
            case 0:
                break;
            case 1:
                SmartDashboard.putNumber(shotTime1Key, 0);
                break;
            case 2:
                SmartDashboard.putNumber(shotTime2Key, 0);
                break;
            case 3:
                SmartDashboard.putNumber(shotTime3Key, 0);
                break;
            default:
                break;
        }
    }
    
    /**
     * Gets the shot time values from SmartDashboard
     */
    public void getShotTimerData() {
        switch (frisbees_val) {
            case 0:
                break;
            case 1:
                shotTimes[0] = SmartDashboard.getNumber(shotTime1Key, 0);
                break;
            case 2:
                shotTimes[0] =SmartDashboard.getNumber(shotTime1Key, 0);
                shotTimes[1] =SmartDashboard.getNumber(shotTime2Key, 0);
                break;
            case 3:
                shotTimes[0] =SmartDashboard.getNumber(shotTime1Key, 0);                
                shotTimes[1] =SmartDashboard.getNumber(shotTime2Key, 0);                
                shotTimes[2] =SmartDashboard.getNumber(shotTime3Key, 0);
                break;
            default:
                break;
        }
    }
    
    /**
     * Returns value of shot time
     * @param shotTime Shot # (1 for 1, 2 for 2, 3 for 3)
     * @return time in seconds
     */
    public double getShotTime(int shotTime) {
        return shotTimes[shotTime-1];
    }
    
    /**
     * Run in robotInit() to send initial data over NetworkTable
     */
    public void initShotTimer() {
        initShotTimerData();
    }
    
    /**
     * Run in autoInit() to retrieve shot time values at start of autonomous
     */
    public void runShotTimer() {
        getShotTimerData();
    }
}
