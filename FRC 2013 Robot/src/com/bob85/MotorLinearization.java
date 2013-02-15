/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

import com.sun.squawk.util.MathUtils;
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
        double linearOutput = 3.1199*MathUtils.pow(x, 4) - 4.4664*MathUtils.pow(x, 3) + 2.2378*MathUtils.pow(x, 2) + 0.122*x;
        victorMotor.set(linearOutput);
    }
    
}
