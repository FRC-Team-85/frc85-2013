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
    
    private SendableChooser autoChooser;
    private AnalogChannel m_analogAutoChooser;
    
    private String m_optionZeroName;
    private String m_optionOneName;
    private String m_optionTwoName;
    
    private Object m_choice;
    private int analogChoice;
    
    public boolean[] driverStationInputs;
    private int driverStationInputsAmount = 8;
    
    DriverStation driverStation = DriverStation.getInstance();
    
    /**
     * Adds the choices to the SmartDashboard
     * The objects are all strings
     */
    private void initChoices() {
        autoChooser.addDefault(m_optionZeroName, m_optionZeroName);
        autoChooser.addObject(m_optionOneName, m_optionOneName);
        autoChooser.addObject(m_optionTwoName, m_optionTwoName);
    }
    
    public AutoModeChooser(String optionZeroName, String optionOneName, String optionTwoName) {
        autoChooser = new SendableChooser();
        
        m_optionZeroName = optionZeroName;
        m_optionOneName = optionOneName;
        m_optionTwoName = optionTwoName;
        
        initChoices();
    }
      
    /**
     * Gets choice selected from SmartDashboard
     * 
     * @return mode Object chosen
     */
    private void returnChoice() {
        m_choice = autoChooser.getSelected();
    }
    
    /**
     * Returns the chosen mode object
     * @return Object returned
     */
    public Object chooseMode() {
        returnChoice();
        return m_choice;
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
    
    public AutoModeChooser() {
        
    }
    
    private void returnDriverStationInputs() {
        int i;
        
        if (driverStationInputs == null) {
            driverStationInputs = new boolean[driverStationInputsAmount];
        }
        for (i=0; i<driverStationInputsAmount; i++) {
            driverStationInputs[i] = driverStation.getDigitalIn(i);
        }
    }
    
    public boolean getDriverStationInput(int channel) {
        return driverStationInputs[channel];
    }
    
    private void sendDriverStationInputsSmartDashboard() {
        int i;
        String key = "DS DIO ";
        
        for (i=0; i< driverStationInputsAmount; i++) {
            SmartDashboard.putBoolean(key + (i++), driverStationInputs[i]);
        }
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
    
    public void testDriveStationInputs() {
        returnDriverStationInputs();
        sendDriveStationsInputsSDB();
    }
}
