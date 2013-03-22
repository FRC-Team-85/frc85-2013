package com.bob85.auto;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPrefs {
    
    public static final String shotTime1Key = "Shot_Time_1";
    public static final String shotTime2Key = "Shot_Time_2";
    public static final String shotTime3Key = "Shot_Time_3";
    
    public static final String driveDist1Key = "Drive_Dist_1";
    public static final String turnAngleKey = "Turn_Angle";
    public static final String driveDist2Key = "Drive_Dist_2";

    private static final String prefKey = "_Pref";
    
    int frisbees_val;
    double[] shotTimes;
    double[] driveSettings;
    
    Preferences autoPref;
    
    public AutoPrefs(int frisbees) {
        frisbees_val = frisbees;
        shotTimes = new double[frisbees];
        driveSettings = new double[3];
        autoPref = Preferences.getInstance();
    }
    
    public void getPrefShotTimerData() {
        shotTimes[0] = autoPref.getDouble(shotTime1Key + prefKey, 5);
        shotTimes[1] = autoPref.getDouble(shotTime2Key + prefKey, 6);
        shotTimes[2] = autoPref.getDouble(shotTime3Key + prefKey, 7);
    }
    
    public void getPrefDriveSettingsData() {
        driveSettings[0] = autoPref.getDouble(driveDist1Key + prefKey, -12);
        driveSettings[1] = autoPref.getDouble(turnAngleKey + prefKey, 180);
        driveSettings[2] = autoPref.getDouble(driveDist2Key + prefKey, 12);
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
    public void initAutoPrefs() {
        autoPref.save();
    }
    
    /**
     * Run in autoInit() to retrieve shot time values at start of autonomous
     */
    public void runAutoPrefs() {
        getPrefShotTimerData();
        getPrefDriveSettingsData();
    }
}
