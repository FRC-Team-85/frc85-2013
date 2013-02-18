/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class AutoModeChooser {
    
    private AnalogChannel m_analogAutoChooser;
    
    private int analogChoice;
    
    public boolean shootStage;
    public boolean driveStage1;
    public boolean turnStage;
    public boolean driveStage2;
    
    DriverStation driverStation;
    
    public AutoModeChooser() {
        driverStation = DriverStation.getInstance();
    }
    
    /**
     * Constructs an AutoModeChooser using an analog selector
     * 
     * @param analogModeChooser the analog selector
     */
    public AutoModeChooser(AnalogChannel analogModeChooser) {
        m_analogAutoChooser = analogModeChooser;
    }
    
    /**
     * Gets voltage and calculate choice selected
     */
    private void returnAnalogChoice() {
        analogChoice = (int) ((m_analogAutoChooser.getVoltage() + 0.45) * (12/5));    
    }
    
    /**
     * Returns the chosen mode integer
     * 
     * @return Gets chosen mode number
     */
    public int chooseAnalogMode() {
        returnAnalogChoice();
        return analogChoice;
    }
    
    private void getDriverStationInputs() {
        shootStage = driverStation.getDigitalIn(1);
        driveStage1 = driverStation.getDigitalIn(2);
        turnStage = driverStation.getDigitalIn(3);
        driveStage2 = driverStation.getDigitalIn(4);
    }
    
    private void sendDriveStationsInputsSDB() {
        SmartDashboard.putBoolean("DI 1", driverStation.getDigitalIn(1));
        SmartDashboard.putBoolean("DI 2", driverStation.getDigitalIn(2));
        SmartDashboard.putBoolean("DI 3", driverStation.getDigitalIn(3));
        SmartDashboard.putBoolean("DI 4", driverStation.getDigitalIn(4));
        SmartDashboard.putBoolean("DI 5", driverStation.getDigitalIn(5));
        SmartDashboard.putBoolean("DI 6", driverStation.getDigitalIn(6));
        SmartDashboard.putBoolean("DI 7", driverStation.getDigitalIn(7));
        SmartDashboard.putBoolean("DI 8", driverStation.getDigitalIn(8));
    }
  
    public void runAutoModeChooser() {
        getDriverStationInputs();
    }
}
