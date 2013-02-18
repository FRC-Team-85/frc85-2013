package com.bob85.auto;

import com.bob85.Shooter;

/**
 *
 * @author Michael Chau <mchau95@gmail.com>
 */
public class ShootCommand {
    private Shooter shooter;
    private int frisbees; //Amount of frisbees to fire
    private AutoTimer shotTimer;
    private int shotNumber = 1;
    
    public ShootCommand(Shooter shooter, int frisbees, AutoTimer shotTimer) {
        this.frisbees = frisbees;
        this.shooter = shooter;
        this.shotTimer = shotTimer;
    }
    
    public void shootCommand() {
        switch (shotNumber) {
            case 1:
                
        }
    }
}
