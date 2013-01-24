/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class TwoMotorPIDOutput implements PIDOutput{
    
    SpeedController m_motor1;
    SpeedController m_motor2;
    
    public TwoMotorPIDOutput(SpeedController motor1, SpeedController motor2) {
        m_motor1 = motor1;
        m_motor2 = motor2;
    }
    
    public void pidWrite(double speed) {
        m_motor1.set(speed);
        m_motor2.set(speed);
    }
}
