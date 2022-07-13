/*
 * Sensor.java
 *
 * Created on June 18, 2001, 1:33 PM
 */

package edu.wsu.KheperaSimulator;

/**
 *
 * @author  steve
 * @version 
 */
public class Sensor  {
    private int   lightValue;
    private int   distValue;

    /** Creates new Sensor */
    public Sensor() {
        lightValue = 500;
        distValue  = 0;
    }
    
    public int getDistValue() { 
        return distValue;
    }
    
    public void setDistValue(int d) {
        distValue = d;
    }
    
    public int getLightValue() { 
        return lightValue;
    }
    
    public void setLightValue(int l) {
        lightValue = l;
    }
}
