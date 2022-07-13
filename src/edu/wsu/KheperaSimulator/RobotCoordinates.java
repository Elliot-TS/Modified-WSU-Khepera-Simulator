/**
 * @(#)RobotCoordinates.java 1.1 2001/09/09
 */

package edu.wsu.KheperaSimulator;

/**
 * The <code>RobotCoordinates</code> class represents the current coordinates
 * and direction of the robot. The information stored in this class is used for
 * drawing the robot and generating sensor data, and should not be used by the
 * controller.
 *
 * @author  Steve Perretta
 */
public class RobotCoordinates implements java.io.Serializable{
  protected int x;
  protected int y;
  protected float alpha, dx, dy;
  protected MyFloatPoint[] lvtPoints;

  /**
   * Allocate a new <code>RobotCoordinates</code> object and initialize it
   * with default values.
   */
  public RobotCoordinates() {
    reInitialize();
  }

  /**
   * Allocate a new <code>RobotCoordinates</code> object and initialize it
   * with default values.
   * @param x initial x-coordinate of the robot
   * @param y initial y-coordinate of the robot
   * @param a initial direction of the robot
   * @param dx floating point representation of x
   * @param dy floating point representation of y
   */
  public RobotCoordinates(int x, int y, float a, float dx, float dy) {
    this.x = x;
    this.y = y;
    this.alpha = a;
    this.dx = dx;
    this.dy = dy;
    for(int i=0;i<4;i++){
    	this.lvtPoints[i].x = dx + 13f;
    	this.lvtPoints[i].y = dy + 13f;
    	//this.lvtPoints[i].x = (float)0;
    	//this.lvtPoints[i].y = (float)0;
    }
  }

  /**
   * Reset the robots position back to the default values.
   */
  protected void reInitialize() {
	lvtPoints = new MyFloatPoint[4];
    x = 250;
    y = 250;
    alpha = 0.0f;
    dx = (float)x;
    dy = (float)y;
    for(int i=0;i<4;i++){
    	lvtPoints[i] = new MyFloatPoint(dx + 13f,dy + 13f);
    	//lvtPoints[i] = new MyFloatPoint(263,0);
    	//lvtPoints[i].y = 250;
    }
  }

  /**
   * Set the current robot coordinates.
   * @param x initial x-coordinate of the robot
   * @param y initial y-coordinate of the robot
   * @param a initial direction of the robot
   * @param dx floating point representation of x
   * @param dy floating point representation of y
   */
  protected synchronized void setCoordinates(int x, int y, float a, float dx,
                                            float dy) {
    this.x = x;
    this.y = y;
    this.alpha = a;
    this.dx = dx;
    this.dy = dy;
  }
  protected synchronized void setLvtCoordinates(float x, float y, int num) {
	  this.lvtPoints[num].x = x;
	  this.lvtPoints[num].y = y;
  }
} // RobotCoordinates
