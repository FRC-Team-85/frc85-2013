package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class HallEffect extends Counter implements CounterBase, PIDSource, LiveWindowSendable{

    public HallEffect(int channel) {
        super(channel);
    }
    
    public int getRPM() {
        return (int) ((60/getPeriod()));
    }

    public double pidGet() {
        return getRPM();
    }
}
