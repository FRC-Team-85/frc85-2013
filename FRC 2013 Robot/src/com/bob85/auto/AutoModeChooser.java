/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class AutoModeChooser {
    
    private SendableChooser autoChooser;
    
    private String m_optionZeroName;
    private String m_optionOneName;
    private String m_optionTwoName;
    
    private Object m_choice;
    
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
}
