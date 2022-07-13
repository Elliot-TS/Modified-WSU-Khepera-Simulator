/**
 * @(#)Sensor.java 1.1 2001/06/18
 *
 */

package edu.wsu.KheperaSimulator;

/**
 * A <code>Sensor</code> class represents one infared sensor of the Khepera
 * robot.
 *
 * @author  Steve Perretta
 */
public class Sensor implements java.io.Serializable {
  protected float x;
  protected float y;
  protected float theta;
  private int lightValue;
  private int distValue;

  /**
   * Allocate a new <code>Sensor</code> object.
   * @param x the x position
   * @param y the y position
   * @param a the directional location
   */
  public Sensor(float x, float y, float a) {
    this.x = x;
    this.y = y;
    theta = a;
    lightValue = 500;
    distValue  = 0;
  }

  /**
   * Reset this sensor back to the default sensor values.
   */
  protected void reset() {
    // Should not reset coordinates -- see UpdateSensor class
    //this.x = 0;
    //this.y = 0;
    //this.theta = 0;
    lightValue = 500;
    distValue  = 0;
  }

  /**
   * get the distance value.
   * @return the distance value
   */
  protected int getDistValue() {
    return distValue;
  }

  /**
   * Set the distance value.
   * @param d the distance value
   */
  protected void setDistValue(int d) {
    distValue = d;
  }

  /**
   * Get the light value.
   * @return the light value
   */
  protected int getLightValue() {
    return lightValue;
  }

  /**
   * Set the light value.
   * @param l the light value
   */
  protected void setLightValue(int l) {
    lightValue = l;
  }
} // Sensor
