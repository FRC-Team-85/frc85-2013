package com.bob85.auto;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Developer
 */
public class AutoTimer {
    Timer autoTimer = new Timer();
    
    AutoModeChooser m_autoChooser;
    
    double shooterTimer1;
    double shooterTimer2;
    double shooterTimer3;
    
    public static final String shooterTimer1Key = "Shooter Timer 1";
    public static final String shooterTimer2Key = "Shooter Timer 2";
    public static final String shooterTimer3Key = "Shooter Timer 3";
    
    
    int autoMode;
    
    public AutoTimer(AutoModeChooser autoChooser) {
        m_autoChooser = autoChooser;
        
    }
    
    public AutoTimer() {
        
    }
    
    private void getAutoMode() {
        autoMode = m_autoChooser.chooseAnalogMode();
        
    }
    
    private void setShooterTimers(double timer1Input, double timer2Input) {
        shooterTimer1 = timer1Input;
        shooterTimer2 = timer2Input;
    }
    
    public void initSmartDashboardTimers() {
        SmartDashboard.putNumber(shooterTimer1Key, 0);
        SmartDashboard.putNumber(shooterTimer2Key, 0);
        SmartDashboard.putNumber(shooterTimer3Key, 0);
        
    }
    
    public void setSmartDashboardTimers() {
        shooterTimer1 = SmartDashboard.getNumber(shooterTimer1Key);
        shooterTimer2 = SmartDashboard.getNumber(shooterTimer2Key);
        shooterTimer3 = SmartDashboard.getNumber(shooterTimer3Key);
        
    }
    
    public void setAnalogTimerDelays() {
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
    
    public double returnTimerDelay3() {
        return shooterTimer3;
    }
    
    private void runDiagnostics() {
        SmartDashboard.putNumber(shooterTimer1Key + "Diagnostics", shooterTimer1);
        SmartDashboard.putNumber(shooterTimer2Key + "Diagnostics", shooterTimer2);
        SmartDashboard.putNumber(shooterTimer3Key + "Diagnostics", shooterTimer3);

    }
    
    public void initAutoTimer() {
        initSmartDashboardTimers();
        autoTimer.start();
    }
    
    public void runAutoTimer() {
        setSmartDashboardTimers();
        runDiagnostics();
    }
}
