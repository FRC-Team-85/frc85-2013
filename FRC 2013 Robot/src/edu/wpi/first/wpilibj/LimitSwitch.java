package edu.wpi.first.wpilibj;

public class LimitSwitch extends DigitalInput {
    
    private boolean isNC = false; //configuration if LimitSwitch is normally closed or normally open
    
        public LimitSwitch(int channel, boolean isNC) {
            super(channel);
            this.isNC = isNC;
        }
        public LimitSwitch(int slot, int channel, boolean isNC) {
            super(slot, channel);
            this.isNC = isNC;
        }
        
        /**
         * Returns the LimitSwitch's current state
         * @return is the LimitSwitch at an abnormal state
         */
        public boolean get() {
            if (isNC) {
                return !super.get();
            } else {
                return super.get();
            }
        }
}
