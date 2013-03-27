package edu.wpi.first.wpilibj;

public class LimitSwitch extends DigitalInput {
    
    private boolean isNC = false; //configuration if LimitSwitch is normally closed or normally open
    
    /**
     * Constructs a LimitSwitch and defaults module number to 1
     * @param channel Digital I/O Channel the LimitSwitch is in
     * @param isNC is the LimitSwitch normally closed
     */
        public LimitSwitch(int channel, boolean isNC) {
            super(channel);
            this.isNC = isNC;
        }
        
        /**
         * Constructs a LimitSwitch
         * @param slot module #1 or 2
         * @param channel Digital I/O Channel
         * @param isNC is the LimitSwitch normally closed
         */
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
