package com.bob85.auto;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShotTimer {
    
    public static final String shotTime1Key = "ST1";
    public static final String shotTime2Key = "ST2";
    public static final String shotTime3Key = "ST3";

    private static final String shotTimePrefKey = "P";
    
    int frisbees_val;
    double[] shotTimes;
    
    Preferences shotPref;
    
    public ShotTimer(int frisbees) {
        frisbees_val = frisbees;
        shotTimes = new double[frisbees];
        shotPref = Preferences.getInstance();
    }
    
    /**
     * Sends NetworkTable objects to SmartDashboard to initiate them
     */
    public void initShotTimerData() {
        switch (frisbees_val) {
            case 0:
                break;
            case 1:
                SmartDashboard.putNumber(shotTime1Key, shotPref.getDouble(shotTime1Key + shotTimePrefKey, 0));
                break;
            case 2:
                SmartDashboard.putNumber(shotTime2Key, shotPref.getDouble(shotTime2Key + shotTimePrefKey, 0));
                break;
            case 3:
                SmartDashboard.putNumber(shotTime3Key, shotPref.getDouble(shotTime3Key + shotTimePrefKey, 0));
                break;
            default:
                break;
        }
    }
    
    /**
     * Gets the shot time values from SmartDashboard
     */
    public void getSmartDashboardShotTimerData() {
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
    
    public void getPrefShotTimerData() {
        shotTimes[0] = shotPref.getDouble(shotTime1Key + shotTimePrefKey, 5);
        shotTimes[1] = shotPref.getDouble(shotTime2Key + shotTimePrefKey, 6);
        shotTimes[2] = shotPref.getDouble(shotTime3Key + shotTimePrefKey, 7);
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
        shotPref.save();
    }
    
    /**
     * Run in autoInit() to retrieve shot time values at start of autonomous
     */
    public void runShotTimer() {
        getPrefShotTimerData();
    }
}
