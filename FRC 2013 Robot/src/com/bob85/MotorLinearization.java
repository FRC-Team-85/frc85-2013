/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class MotorLinearization {
    
    /**
     * Linearize actual Victor 884 voltage output to match desired output
     * Function is based off of FRC 1114's 2012 Robot Code
     * 
     * @param victorMotor Motor output to linearize
     * @param x Desired Motor Output
     */
    public static void linearizeVictor884Output(Victor victorMotor, double x) {
        double linearOutput = 3.1199*(x*x*x*x) - 4.4664*(x*x*x) + 2.2378*(x*x) + 0.122*x;
        victorMotor.set(linearOutput);
    }
    
}
