package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class HallEffect extends Counter implements CounterBase, PIDSource, LiveWindowSendable{

    private int IIRFilterCount;
    private final double kIIRFilter = 1;
    
    public HallEffect(int channel) {
        super(channel);
    }
    
    public int getRPM() {
        return (int) ((60/getPeriod()));
    }
    
    public int getIIRFilterRPM() {
        IIRFilterCount = (int)(kIIRFilter * IIRFilterCount + (1-kIIRFilter)*get());
        reset();
        return (int) (IIRFilterCount / 0.02 * 60);
    }

    public double pidGet() {
        return getRPM();
    }
}
