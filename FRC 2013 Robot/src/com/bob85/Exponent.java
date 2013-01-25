/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bob85;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */

public class Exponent {
    
    /**
     * Raises a number to desired power
     * 
     * @param x base
     * @param exp exponent 
     */
    public static double pow(double x, double exp) {
        double output = x;
        double result = 1;
        for (int i=0; i<exp; i++) {
            result *= x;
        }
        return result;
    }
}
