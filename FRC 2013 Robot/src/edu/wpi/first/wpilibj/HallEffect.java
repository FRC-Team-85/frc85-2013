package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class HallEffect extends Counter implements CounterBase, PIDSource, LiveWindowSendable{

    private int IIRFilterCount;
    private final double kIIRFilter = 0.5;
    
    public HallEffect(int channel) {
        super(channel);
    }
    
    /**
     * Returns RPM based on WPIlib getPeriod()
     * @return 
     */
    public int getRPM() {
        return (int) ((60/getPeriod()));
    }
    
    /**
     * Returns RPM based on an IIR Filtered Halleffect counts
     * @return 
     */
    public int getIIRFilterRPM() {
        IIRFilterCount = (int)(kIIRFilter * IIRFilterCount + (1-kIIRFilter)*get());
        reset();
        return (int) (IIRFilterCount / 0.02 * 60); // 0.02 is periodic loop time
    }

    /**
     * PIDSource returns RPM
     * @return RPM
     */
    public double pidGet() {
        return getRPM();
    }
}
