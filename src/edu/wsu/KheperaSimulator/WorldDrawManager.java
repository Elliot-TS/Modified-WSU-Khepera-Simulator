/**
 * @(#)WorldDrawManager.java 1.1 2002/10/19
 *
 */

package edu.wsu.KheperaSimulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A <code>WorldDrawManager</code> class provides the main thread to run the
 * simulator engine. This thread manages drawing routines, controller threads,
 * various book keeping and shared data.
 */
public class WorldDrawManager implements Runnable {

  private Thread thread = null;
  private Vector stuckObjects;
  private DrawableRobot robotUpdate = null;
  protected CurrentRobotState rState;
  private UpdateSensors readSensors = null;
  private UpdateRobotImage robotImage;
  protected int[][] wImage;
  private boolean active = false;
  private boolean holding = false;
  private Collision contact;
  private int distLevel, lightLevel, speedLevel;
  private int rx,ry;
  private javax.swing.Timer paintTimer;
  private ActionListener paintListener;
  private boolean streamFromClient = false;
  private KSFrame frame = null;
  private LinearVisionTurretCopy lvt;
  private String displayMode = "disable";
  // NEW 7/30/2003 - sp
  private boolean playbackState = false;
  private boolean lvtOn;
  
  /**
   * Listener for check Box on the GUI; based on events generated
   * by a swing timer at regular intervals.
   */
  ActionListener lvtUpdate = new ActionListener (){
    public void actionPerformed (ActionEvent e){
      frame.lvtPanel.updateLvtImage();
    }
  };
  javax.swing.Timer lvtUpdateTimer = new javax.swing.Timer(50, lvtUpdate);  
  
  
  

  /**
   * Allocate a new <code>WorldDrawManager</code>.
   * @param parent a reference to the parent container
   */
  public WorldDrawManager(KSFrame parent) {
    this.frame = parent;
    wImage = new int[500][500];
    rState = new CurrentRobotState();
    robotUpdate = new DrawableRobot(rState);
    robotImage = new UpdateRobotImage(rState);
    readSensors = new UpdateSensors(wImage, rState, frame.sLabels, frame.worldPanel);
    readSensors.setDistParam(distLevel);
    readSensors.setLightParam(lightLevel);
    frame.worldPanel.setRobotObject(robotImage);
    lvt = new LinearVisionTurretCopy(rState, frame.worldPanel);

    // Set-up world drawing routine
    ActionListener paintListener = new ActionListener (){
      public void actionPerformed (ActionEvent e){
        	frame.worldPanel.repaint();
      }};
    paintTimer = new javax.swing.Timer(50, paintListener); // was 100
    paintTimer.setRepeats(true);
  } // WorldDrawManager

  /**
   * Access the value for the current sensor label display mode for GUI sensor
   * value updates. Legal display modes are "distance", "light", and "disable".
   * @return current display mode
   */
  protected String getDisplayMode() {
    return readSensors.getDisplayMode();
  }

  /**
   * Change the value for the current sensor label display mode for GUI sensor
   * value updates.
   * @param mode display mode ("distance", "light", or "disable")
   */
  protected void setDisplayMode(String mode) {
	  displayMode = mode;
	  frame.worldPanel.repaint();
  	  readSensors.setDisplayMode(mode);
  }
  
  protected void setLvtMode(boolean mode){
	  lvtOn = mode;
	  if(lvtOn)
	  {
		  robotImage.lvt = true;
	  }
	  else
	  {
		  robotImage.lvt = false;
	  }
  }

  protected void setSensorMode(boolean mode){
	  if(mode)
	  {
		  robotImage.sensor = true;
		  
	  }
	  else
	  {
		  robotImage.sensor = false;
	  }
  }
  /**
   * Sets all values in the 500 x 500 world image matrix to zeros.
   */
  protected void clearWorldImage() {
    for(int i = 0; i < 500; i++)
      for(int j = 0; j < 500; j++)
        wImage[i][j] = 0;
  }

  /**
   * Sets values in the 500 x 500 world image matrix to appropriate values based
   * on the presence and position of user placed objects (and the static outer
   * walls). The matrix is simply another representation of the robot arena that
   * uses object id numbers at pixel locations where objects are present.
   */
  protected void createWorldImage() {
    int i,j,x,y;
    Vector wObj = frame.worldPanel.worldObjects;
    Vertex robObj = frame.worldPanel.setRobot;

    /* Init outer walls */
    // top section
    for(i = 0;i < 19;i++)
      for(j = 0;j < 500;j++)
        wImage[i][j] = 1;
    // bottom section
    for(i = 484;i < 500;i++)   // i = 480
      for(j = 0;j < 500;j++)
        wImage[i][j] = 1;
    // left section
    for(i = 18;i < 485;i++)   // i = 19
      for(j = 0;j < 19;j++)
        wImage[i][j] = 1;
    // right section
    for(i = 19;i < 480;i++)
      for(j = 482;j < 500;j++)
        wImage[i][j] = 1;
    //this algorithm with the objects can be used for the movable objects
    Enumeration e = wObj.elements();
    while(e.hasMoreElements()) {
      Vertex v = (Vertex)e.nextElement();
      x = v.xPos;
      y = v.yPos;

      if(v.objType == WorldPanel.WALL) {
        if(v.theta == 0.0f) {
          for(i = y;i < (y+50);i++) {
            for(j= x;j < (x+8);j++) {
              if(i < 0 || i > 499) break;
              if(j < 0 || j > 499) break;
              wImage[i][j] = 1;
            }
          }
        }
        // 90 degrees
        else {
          for(i = (y-8);i < y;i++) {
            for(j= x;j < (x+50);j++) {
              if(i < 0 || i > 499) break;
              if(j < 0 || j > 499) break;
              wImage[i][j] = 1;
            }
          }
        }
      }
      // change this to trim edges
      else if(v.objType == WorldPanel.LIGHT) {
        for(i = y;i < (y+16);i++) {
          for(j = x;j < (x+16);j++) {
            if(i < 0 || i > 499) break;
            if(j < 0 || j > 499) break;
            wImage[i][j] = 2;
          }
        }
      }

      // Moveable objects
      else if(v.objType == WorldPanel.CAP) {
        for(i = y;i < (y+9);i++) {
          for(j = x;j < (x+9);j++) {
            if(i < 0 || i > 499) break;
            if(j < 0 || j > 499) break;
            wImage[i][j] = v.id;
          }
        }
      }
      // Light
      else if(v.objType == WorldPanel.BALL) {
        for(i = y;i < (y+9);i++) {
          for(j = x;j < (x+9);j++) {
            if(i < 0 || i > 499) break;
            if(j < 0 || j > 499) break;
            wImage[i][j] = v.id;
          }
        }
      }
    }
    // robot placed with set button
    if(robObj != null) {
      rState.setRobotCoordinates(robObj.xPos-3,robObj.yPos-3,robObj.theta);
    }
  } // createWorldImage

  /**
   * Resets appropriate data before starting the <code>WorldDrawManager</code>
   * thread.
   */
  protected void reInitialize() {
    rState.reInitialize();
    readSensors.reInitialize();
  }

  /**
   * Starts routine used when user plays a previously recorded file.
   */
  protected void startPlayback() {
    frame.worldPanel.setRunState(true);
    // NEW 7/30/2003 - sp
    playbackState = true;
    frame.lvtPanel.setCurrentState(this.rState);
    paintTimer.start();
    lvtUpdateTimer.start();
  }

  /**
   * Stops playing a previously recorded file.
   */
  protected void stopPlayback() {
    paintTimer.stop();
    frame.worldPanel.setRunState(false);
    // NEW 7/30/2003 - sp
    playbackState = false;
    lvtUpdateTimer.stop();
    // frame.worldPanel.clearObjects(); // why?? -sp
    // uncommented 7/28/2003 - sp - fixes object detection on 2nd+ runs
    //frame.worldPanel.clearObjects(); 8/6/03 -sp - fixed  above and commented out again
    clearWorldImage();
    reInitialize();
  }

  /**
   * Starts the main simulation thread, which runs the robot based on the currently
   * loaded controller and generated sensor values.
   */
  protected void startSimulator() {
    active = true;
    // NEW 7/30/2003 - sp attempting to fix obj prob w/ loaded maps
    readSensors.reInitialize();
    readSensors.setDistParam(distLevel);
    readSensors.setLightParam(lightLevel);

    stuckObjects = readSensors.getStuckObjects(); // ?????
    contact = new Collision(rState, wImage, stuckObjects); // object creation bad

    frame.armPanel.setCurrentState(rState);
    frame.gripPanel.setCurrentState(rState);
    frame.objPresPanel.setCurrentState(rState);
    
    //frame.lvtPanel.setCurrentState(rState);
    
    createWorldImage(); // create world image and get robots position
    robotUpdate.reInitialize(); // set robots position
    robotUpdate.setSpeedParam(speedLevel);
    startPlayback();

    // create new thread and start sim
    thread = new Thread(this, "WorldDrawManager");
    thread.setPriority(thread.getPriority() - 2);
    thread.start();
  } // startRobot

  /**
   * Terminates the main simulation thread, halting the controller execution.
   */
  protected void stopSimulator() {
    active = false;
    stopPlayback();
  } // stopRobot


  /**
   * Sets the varaiable parameter associated with distance sensor sensitivity
   * to the value passed.
   * @param level new distance sensor sensitivity level
   */
  protected void setDistLevel(int level) {
    distLevel = level;
    if(readSensors != null)
      readSensors.setDistParam(distLevel);
  }

  /**
   * Sets the varaiable parameter associated with light sensor sensitivity
   * to the value passed.
   * @param level new light sensor sensitivity level
   */
  protected void setLightLevel(int level) {
    lightLevel = level;
    if(readSensors != null)
      readSensors.setLightParam(lightLevel);
  }

  /**
   * Sets the varaiable parameter associated with calculating motor speeds
   * to the value passed.
   * @param level new speed adjustment level
   */
  protected void setSpeedLevel(int level) {
    speedLevel = level;
    if(robotUpdate != null)
      robotUpdate.setSpeedParam(speedLevel);
  }

  /**
   * Sets distance, light and speed adjustment parameters to those passed.
   * @param dl new distance sensor sensitivity level
   * @param ll new light sensor sensitivity level
   * @param sl new speed adjustment level
   */
  protected void setAllLevels(int dl, int ll, int sl) {
    distLevel = dl;
    lightLevel = ll;
    speedLevel = sl;
  }

   /**
   * @see java.lang.Runnable
   */
  public void run() {
    int hit   = 0;
    while (active) {
      if( !active ) break;
      hit = contact.testCollision(); // hmmm still works when this is gone
      if( !active ) break;
      robotUpdate.updateCoordinates(hit);
      if( !active ) break;
      readSensors.processSensors();
      lvt.processLvt();
      frame.armPanel.updateImage();
      frame.gripPanel.updateImage();
      frame.objPresPanel.updateImage();
      //frame.lvtPanel.updateLvtImage();

      // essential for effeciency
      try {
        thread.sleep(5);
    	 //Thread.sleep(5);
        } catch (Exception e) {}
    }
  } // run
} // WorldDrawManager
