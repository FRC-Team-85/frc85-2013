package edu.wpi.first.wpilibj;

public class F310Gamepad extends Joystick{
    
    public static class ButtonType {
        
        public final int value;
        
        static final int kX_val = 1;
        static final int kA_val  = 2;
        static final int kB_val  = 3;
        static final int kY_val  = 4;
        static final int kLF_val  = 5;
        static final int kRF_val  = 6;
        static final int kLB_val  = 7;
        static final int kRB_val  = 8;
        static final int kBack_val  = 9;
        static final int kStart_val  = 10;
        static final int kLStick_val  = 11;
        static final int kRStick_val  = 12;
        
        public static final ButtonType kX = new ButtonType(kX_val);
        public static final ButtonType kA = new ButtonType(kA_val);
        public static final ButtonType kB = new ButtonType(kB_val);
        public static final ButtonType kY = new ButtonType(kY_val);
        public static final ButtonType kLF = new ButtonType(kLF_val);
        public static final ButtonType kRF = new ButtonType(kRF_val);
        public static final ButtonType kLB = new ButtonType(kLB_val);
        public static final ButtonType kRB = new ButtonType(kRB_val);
        public static final ButtonType kBack = new ButtonType(kBack_val);
        public static final ButtonType kStart = new ButtonType(kStart_val);
        public static final ButtonType kLStick = new ButtonType(kLStick_val);
        public static final ButtonType kRStick = new ButtonType(kRStick_val);
      
        
        private ButtonType(int value) {
            this.value = value;
        }
    }
    
        
        
    public static class AxisType {
        public final int value;
        static final int kLeftX_val = 1;
        static final int kLeftY_val = 2;
        static final int kRightX_val = 3;
        static final int kRightY_val = 4;
        static final int kDPadX_val = 5;
        static final int kDPadY_val = 6;
        
        public static final AxisType kLeftX = new AxisType(kLeftX_val);
        public static final AxisType kLeftY = new AxisType(kLeftY_val);
        public static final AxisType kRightX = new AxisType(kRightX_val);
        public static final AxisType kRightY = new AxisType(kRightY_val);
        public static final AxisType kDPadX = new AxisType(kDPadX_val);
        public static final AxisType kDPadY = new AxisType(kDPadY_val);

        private AxisType(int value) {
            this.value = value;
        }
    }
    
    public F310Gamepad(int port) {
        super(port, 6, 12);
    }
    
    public boolean getButton(ButtonType button) {
        return getRawButton(button.value);
    }
    
    public double getAxis(AxisType axis) {
        return getRawAxis(axis.value);
    }
}
