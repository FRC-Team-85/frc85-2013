/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class HallEffect extends Counter implements CounterBase, PIDSource, LiveWindowSendable{

    public HallEffect(int channel) {
        super(channel);
    }
    
    public int getRPM() {
        return (int) ((get()/getPeriod()) * 60);
    }

    public double pidGet() {
        return getRPM();
    }
}
