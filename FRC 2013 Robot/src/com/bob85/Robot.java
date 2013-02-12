package com.bob85;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {

    Servo dropServo = new Servo(FrisbeeLoader.kDROPSERVO_CHANNEL);
    Servo readyServo = new Servo(FrisbeeLoader.kREADYSERVO_CHANNEL);
    Victor hopperBelt = new Victor(FrisbeeLoader.kHOPPERBELTMOTOR_CHANNEL);
    F310Gamepad opPad = new F310Gamepad(3);
    FrisbeeLoader frisbeeLoader = new FrisbeeLoader(dropServo, readyServo, hopperBelt, opPad);
    
    public void robotInit() {

    }
    
    public void disabledInit() {
        
    }
    
    public void autonomousInit() {
        
    }
    
    public void teleopInit() {
        
    }
    
    public void testInit() {
        
    }
    
    public void disabledPeriodic() {
        
    }

    public void autonomousPeriodic() {

    }

    public void teleopPeriodic() {
        frisbeeLoader.runFrisbeeLoader();
    }
    
    public void testPeriodic() {
        frisbeeLoader.runFrisbeeLoader();
    }
    
}
