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
        double output = x;
        
        if (output < 0) {
            output *= -1;
            output = (-3.1199*MathUtils.pow(output, 4) + 4.4664*MathUtils.pow(output, 3) - 
                2.2378*MathUtils.pow(output, 2) - 0.122*output);
        } else {
            output = (3.1199*MathUtils.pow(output, 4) - 4.4664*MathUtils.pow(output, 3) + 
                    2.2378*MathUtils.pow(output, 2) + 0.122*output);
        }
        
        victorMotor.set(output);
    }
    
    public static double calculateLinearOutput(double x) {
        double output = x;
        
        if (output < 0) {
            output *= -1;
            output = (-3.1199*MathUtils.pow(output, 4) + 4.4664*MathUtils.pow(output, 3) - 
                2.2378*MathUtils.pow(output, 2) - 0.122*output);
        } else {
            output = (3.1199*MathUtils.pow(output, 4) - 4.4664*MathUtils.pow(output, 3) + 
                    2.2378*MathUtils.pow(output, 2) + 0.122*output);
        }
        
        return output;
    }
    
}
