/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85.auto;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author Developer
 */
public class AutoTimer {
    Timer autotimer;
    
    AutoModeChooser m_autoChooser;
    
    double shooterTimer1;
    double shooterTimer2;
    
    int autoMode;
    
    public AutoTimer(AutoModeChooser autoChooser) {
        m_autoChooser = autoChooser;
        
    }
    
    private void getAutoMode() {
        autoMode = m_autoChooser.chooseAnalogMode();
        
    }
    
    private void setShooterTimers(double timer1Input, double timer2Input) {
        shooterTimer1 = timer1Input;
        shooterTimer2 = timer2Input;
    }
    
    public void setTimerDelays() {
        switch(autoMode) {
            case 0:{
                setShooterTimers(0.0,0.0);
                break;
            }
            case 1:{
                setShooterTimers(1.0,4.0);
                break;                
            }
            default:{
                setShooterTimers(0.0,0.0);
                break;
            }
    }
        
    }
    
    public double returnTimerDelay1() {
        return shooterTimer1;
    }
    
    public double returnTimerDelay2() {
        return shooterTimer2;
    }
}
