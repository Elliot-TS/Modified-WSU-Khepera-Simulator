/**
 * @(#)UpdateSensors.java 1.1 2001/07/27
 *
 */

package edu.wsu.KheperaSimulator;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.swing.JLabel;

/**
 * An <code>UpdateSensors</code> class provides the ability to update the sensor
 * values of the robot based on the robot's environment, and its position/orientation
 * in that environment.
 */
public class UpdateSensors {
    private String[] labelValues;
    protected Vector stuckObjects;
    protected Vector unblockedLightList;
    private int[][] worldMap;
    private CurrentRobotState rState;
    private RobotCoordinates currentPos;
    private JLabel[] sLabels;
    private Sensor[] sensorArray;
    private Random randGen;
    private MyPoint gripSensor;
    private String display = "disable";
    private short[] sensorVectors;
    protected float[] sensorCoords;
    private static int sensorVal = 0;
    // Primary set of 15 points and sensor values
    private short[][] v;  // holds the following 5 arrays
    private short[]  v0 = {500,400,200,50,10, 350,175,40,5,0,
                           500,400,200,50,10};
    private short[]  v1 = {550,450,250,90,15, 400,225,70,15,0,
                           550,450,250,90,15};

    private short[]  v2 = {600,500,300,140,25, 450,275,100,20,5,
                           600,500,300,140,25};

    private short[]  v3 = {650,550,350,180,45, 500,325,150,70,10,
                           650,550,350,180,45};
    private short[]  v4 = {700,600,400,200,60, 550,375,200,90,20,
                           700,600,400,200,60};
    private MyPoint[][] testPoints; //stores screen coordinates for these x,y pairs
    private short[]  sx = {-2,-4,-5,-7,-9, 0, 0, 0, 0, 0, 2,4,5, 7, 9};
    private short[]  sy = { 2, 4, 8,13,20, 6,10,16,22,29, 2,4,8,13,20};

    // Secondary set of 15 points and sensor values
    private short[][] t;  // holds the following 5 arrays
    private short[]  t0 = {600,450,300,100,20, 450,250,50,20,5,
                           600,450,300,100,20};
    private short[]  t1 = {650,500,350,150,30, 500,300,100,30,5,
                           650,500,350,150,30};

    private short[]  t2 = {700,550,400,200,50, 550,350,150,50,15,
                           700,550,400,200,50};

    private short[]  t3 = {750,600,450,250,60, 600,400,200,100,25,
                           750,600,450,250,60};
    private short[]  t4 = {800,650,500,300,80, 650,450,250,150,40,
                           800,650,500,300,80};
    private MyPoint[][] midPoints;
    private short[]  stX = {-1,-3,-5,-6,-8, 0, 0, 0, 0, 0, 1,3,5, 6, 8};
    private short[]  stY = { 1, 3, 6,10,16, 4, 8,13,19,25, 1,3,6,10,16};

    private int distLevel = 2;
    private int lightLevel = 2;
    private WorldPanel world;

    // Collision
    private Rectangle rect;
    private int objectID = 0;
    private float rx, ry, ra; // robot x, y, & angle
    private float sina, cosa;
    protected Vertex heldObject = null;
    private Vector objList;
    private MyPoint[] armPoints;
    private MyPoint[] closedGPoints;

    // NEW 7/29/2003 - sp
    private boolean contact = false; // need to notify KSWriter of changes
    // END NEW

    /**
     * Listener for sensor label updates on the GUI; based on events generated
     * by a swing timer at regular intervals.
     */
    ActionListener sensorDisplayUpdate = new ActionListener (){
      public void actionPerformed (ActionEvent e){
        for(int i = 0; i < 8; i++) {
          if(display.equals("light")) {
            labelValues[i] = Integer.toString(sensorArray[i].getLightValue());
            sLabels[i].setText(labelValues[i]);
          }
          else {
            labelValues[i] = Integer.toString(sensorArray[i].getDistValue());
            sLabels[i].setText(labelValues[i]);
          }
        }
      }
    };
    javax.swing.Timer sensorDisplayTimer = new javax.swing.Timer(100, sensorDisplayUpdate);


    /**
     * Allocate a new <code>UpdateSensors</code> object.
     * @param map 500x500 object id matrix - see WorldrawManager.java for details
     * @param rState robot state information
     * @param sLabels sensor readings labels - see KSFrame.java for details
     * @param world reference to main panel
     */
    public UpdateSensors(int[][] map, CurrentRobotState rState, JLabel[] sLabels,
                         WorldPanel world) {
        this.worldMap = map;
        this.sLabels = sLabels;
        this.world = world;
        stuckObjects = new Vector();
        rect = new Rectangle();
        randGen = new Random();
        unblockedLightList = new Vector();
        sensorVectors = new short[3];
        sensorArray = new Sensor[8];
        testPoints = new MyPoint[8][15];
        midPoints  = new MyPoint[8][15];
        armPoints  = new MyPoint[5];
        closedGPoints = new MyPoint[3];
        gripSensor = new MyPoint(17,0); // was 18,0
        labelValues = new String[8];
        sensorCoords = new float[32];
        initLabelValues();
        initSensorPositions();
        initTestPoints();
        initCollisionPoints();
        initSensorValues();
        objList = world.getWorldObjects();
        this.rState = rState;
        currentPos  = rState.getRobotCoordinates();
        reInitialize();
    }

    /**
     * Reset this object back to the default values.
     */
    protected void reInitialize() {
      // NEW 7/30/2003 - sp
      initSensorPositions();
      rState.postSensorValues(sensorArray); // ???
        //for (int i = 0; i < sensorArray.length; i++) {
         //   sensorArray[i].reset();
        //}

        /* What follows are suggestions to fix reInit bug 7/28/2003-sp */
      stuckObjects.clear(); // added 7/28/2003 - sp
      objList = world.getWorldObjects(); // added 7/30/2003 - sp
      // need to ensure new map matrix gets set
    }

    /**
     * Get the sensor display mode (distance, light, or none) for sensor
     * label updates on the GUI.
     * @return "distance", "light", or "disable"
     */
    protected String getDisplayMode() {
        return display;
    }

   /**
    * Set the sensor display mode (distance, light, or disable) for sensor
    * label updates on the GUI.
    * @param mode "distance", "light", or "disable"
    */
    protected void setDisplayMode(String mode) {
      display = mode;
      if(display.equals("disable")) {
        sensorDisplayTimer.stop();
      }
      else if(display.equals("light") || display.equals("distance") ){
      //  if(display.equals("light") || display.equals("distance") ){
        sensorDisplayTimer.start();
      }
      //else
    	//  sensorDisplayTimer.stop();
    }

   /**
    * Initialize the sensor label Strings.
    */
    private void initLabelValues() {
        for (int i = 0;i < 8;i++)
            labelValues[i] = new String();
    }

   /**
    * Setup sensor value arrays.
    */
    private void initSensorValues() {
        v = new short[5][15];
        t = new short[5][15];

        v[0] = v0;
        v[1] = v1;
        v[2] = v2;
        v[3] = v3;
        v[4] = v4;

        t[0] = t0;
        t[1] = t1;
        t[2] = t2;
        t[3] = t3;
        t[4] = t4;
    }

   /**
    * Initialize the coordinates and orientation of each sensor on the robot
    * based on a robot-local coordinate system.
    */
    private void initSensorPositions() {
        sensorArray[0] = new Sensor(3.5f, 10.5f, (float)(Math.PI/2.0));
        sensorArray[1] = new Sensor(7.5f, 9.0f, (float)(Math.PI/4.0));
        sensorArray[2] = new Sensor(10.5f, 4.0f, 0.0f); // y was 4.0
        sensorArray[3] = new Sensor(10.5f, -4.0f, 0.0f); // y was -4.0
        sensorArray[4] = new Sensor(7.5f, -9.0f, (float)(-Math.PI/4.0));
        sensorArray[5] = new Sensor(3.5f, -10.5f, (float)(-Math.PI/2.0));

        sensorArray[6] = new Sensor(-9.5f, -5.5f, (float)Math.PI); //....
        sensorArray[7] = new Sensor(-9.5f, 5.5f, (float)Math.PI); //....
    }

   /**
    * Initialize some of the test point coordinates on the robot used for
    * collision detection.
    */
    private void initTestPoints() {
        int i,j,k,ex,ey,xc,yc;
        float alpha, cosa, sina, cosaR, sinaR, x,y;
        cosaR = (float)Math.cos(0.0);
        sinaR = (float)Math.sin(0.0);

        for(i = 0;i < 8;i++) {
            x = sensorArray[i].x;
            y = sensorArray[i].y;
            alpha = sensorArray[i].theta;
            xc = (int)(0.0f + y*sinaR + x*cosaR);
            yc = (int)(0.0f - y*cosaR + x*sinaR);
            sina = (float)Math.sin(alpha);
            cosa = (float)Math.cos(alpha);
            if((i >= 0) && (i <= 5)) k = 5 - i;
            else if(i == 6) k = 7;
            else k = 6;
            for(j = 0;j < 15;j++) {
                ex = xc + (int)(sy[j] * cosa + sx[j] * sina);
                ey = yc + (int)(sx[j] * cosa - sy[j] * sina);
                testPoints[k][j] = new MyPoint(ex,ey);
                ex = xc + (int)(stY[j] * cosa + stX[j] * sina);
                ey = yc + (int)(stX[j] * cosa - stY[j] * sina);
                midPoints[k][j] = new MyPoint(ex,ey);
            }
        }
    }

   /**
    * Initialize the set of collision detection test point coordinates used
    * when the gripper-arm is down.
    */
    private void initCollisionPoints() {
        // left arm
        armPoints[0] = new MyPoint(10, 10);
        armPoints[1] = new MyPoint(20, 10);
        // front center of body
        armPoints[2] = new MyPoint(13,0);
        // right arm
        armPoints[3] = new MyPoint(10, -10);
        armPoints[4] = new MyPoint(20, -10);

        closedGPoints[0] = new MyPoint(10, 10);
        closedGPoints[1] = new MyPoint(20, 0);
        closedGPoints[2] = new MyPoint(10, -10);
    }

   /**
    * Set the distance sensing parameter. This value is used to tweak sensor
    * sensitivity from the GUI controls.
    * @param dp new distance sensing parameter
    */
    protected void setDistParam(int dp) {
        distLevel = dp;
    }

    /**
     * Set the light sensing parameter. This value is used to tweak sensor
     * sensitivity from the GUI controls.
     * @param lp new light sensing parameter
     */
    protected void setLightParam(int lp) {
        lightLevel = lp;
    }

   /**
    * Normalize value in radians.
    * @param theta value of robot's angle in radians
    * @return normalized value of input parameter
    */
    private float normRad(float theta) {
        while(theta > Math.PI)  theta -= (float)(2*Math.PI);
        while(theta < -Math.PI) theta += (float)(2*Math.PI);
        return theta;
    }

    /**
     * Returns a reference to the current <code>stuckObject</code> vector.
     * <code>stuckObjects</code> is a collection of ball/cap objects that are currently
     * "stuck" against some static object in the current world configuration,
     * hence they cannot move unless grabbed by the gripper.
     * @return (see above)
     */
    protected Vector getStuckObjects() {
        return stuckObjects;
    }

   /**
    * Computes the current distance value reading for a single sensor.
    * @param x sensor x coordinate
    * @param y sensor y coordinate
    * @param a sensor angle in radians
    * @param sNum sensor id number (0 - 7)
    * @return sensor distance value
    */
    private int computeDistValues(float x, float y, float a, int sNum) {
        int    i,j,k,ex,ey,tx,ty,value;
        float cosa, sina;
	for(int q = 0;q < 3;q++)
        sensorVectors[q] = 0;
        cosa = (float)Math.cos(a);
        sina = (float)Math.sin(a);
        objectID = -1;
        for(i=0;i<3;i++) {
            for(j=0;j<5;j++) {
                k = i*5+j;
                ex=(int)(x+testPoints[sNum][k].y*sina+testPoints[sNum][k].x*cosa);
                ey=(int)(y-testPoints[sNum][k].y*cosa+testPoints[sNum][k].x*sina);
                
                if(ey >= 500) ey = 499;
                if(ey < 0) ey = 0;
                if(ex >= 500) ex = 499;
                if(ex < 0) ex = 0;

                if(worldMap[ey][ex] != 0) {
               		/*if(k==5 || k==6 ||k==7 || k==8 ||k==9){
               			if(sensorVal == 32){
               				sensorVal =0;}
               			rState.setSensorCoord(ex,sensorVal);
               			rState.setSensorCoord(ey,sensorVal + 1);
               			sensorVal = sensorVal + 2;
               		}*/
                	tx=(int)(x+midPoints[sNum][k].y*sina+midPoints[sNum][k].x*cosa);
                    ty=(int)(y-midPoints[sNum][k].y*cosa+midPoints[sNum][k].x*sina);
                    if(worldMap[ty][tx] != 0) {
                    	if(k==5 || k==6 ||k==7 || k==8 ||k==9){
                   			if(sensorVal == 32){
                   				sensorVal =0;}
                   			rState.setSensorCoord(tx,sensorVal);
                   			rState.setSensorCoord(ty,sensorVal + 1);
                   			sensorVal = sensorVal + 2;
                   		}
                        sensorVectors[i]=t[distLevel][k];
                        if(worldMap[ty][tx] > 4){
                            objectID = worldMap[ty][tx];}
                        break;
                    }
                  else {
                	  if(k==5 || k==6 ||k==7 || k==8 ||k==9){
                 			if(sensorVal == 32){
                 				sensorVal =0;}
                 			rState.setSensorCoord(ex,sensorVal);
                 			rState.setSensorCoord(ey,sensorVal + 1);
                 			sensorVal = sensorVal + 2;
                 		}
                        sensorVectors[i]=v[distLevel][k];
                        if(worldMap[ey][ex] > 4)
                            objectID = worldMap[ey][ex];
                        break;
                    }
                }
            	if(k==9){
            		if(sensorVal == 32){
                 		sensorVal =0;}
            	rState.setSensorCoord(ex,sensorVal);
            	rState.setSensorCoord(ey,sensorVal + 1);
            	//rState.setSensorCoord(testPoints[sNum][9].x,sensorVal);
            	//rState.setSensorCoord(testPoints[sNum][9].y,sensorVal + 1);
            	sensorVal = sensorVal + 2;
            	}
            	}
      }
        value = sensorVectors[0]+sensorVectors[1]+sensorVectors[2];
        if (value < 7)         value = randGen.nextInt(7);
        else if (value > 1022) value = 1023;
        else {
            value = (int)(value * (0.90f + (float)(randGen.nextInt(2000)/10000.0f)));
            if (value > 1023) value = 1023;
        }
        return(value);
    }

   /**
    * Computes the current light value reading for a single sensor.
    * @param x sensor x coordinate
    * @param y sensor y coordinate
    * @param alpha sensor angle in radians
    * @return sensor light value
    */
    private int computeLightValues(int x, int y, float alpha) {
        float  angle,d,dx,dy;
        int    value = 500; // dark

        Enumeration e = unblockedLightList.elements();
	while(e.hasMoreElements()) {
		Vertex light = (Vertex)e.nextElement();
            // need to see if "light" is in the obstructed list -
            //   if so, then continue

            dx = (float)((light.xPos+7) - x);   // - x
            dy = (float)((light.yPos+7) - y);  // - y
            d = (float)(250 - Math.sqrt(dx*dx + dy*dy))/250.0f;
            if (d>0) {
                //angle = Math.atan2(-dy,dx) - alpha;           // o
                angle = (float)(Math.atan2(dy,dx) - alpha);     // m
                angle = normRad(angle);
                if ((angle > -Math.PI/3)&&(angle < Math.PI/3)) {
                    //value -= (int)(Math.cos(angle * 1.5)*d*450); //50 = maximal lightning
                    value -= (int)(Math.cos(angle * 1.5f)*d*(350+lightLevel*10));
                }
            }
        }
        if (value < 50) value = 50;
        value -= (value*(randGen.nextInt(200)-100))/2000; /* noise = +-5% */
        return(value);
    }

   /**
    * Determines if an object in the world lies between the robot and a light
    * source by looking for objects intersecting a line drawn between the robot
    * and the light source.
    * @param x0 x coordinate of robot
    * @param y0 y coordinate of robot
    * @param x1 x coordinate of light
    * @param y1 y coordinate of light
    * @return <tt>true</tt> if light is obstructed, <tt>false</tt> otherwise.
    */
    private boolean isLightObstructed(int x0, int y0, int x1, int y1)
    {
        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepx, stepy;

        if (dy < 0) {
            dy = -dy;
            stepy = -1;
        } else {
            stepy = 1;
        }
        if (dx < 0) {
            dx = -dx;
            stepx = -1;
        } else {
            stepx = 1;
        }
        dy <<= 1;
        dx <<= 1;

        //y0 *= raster.width;
        //y1 *= raster.width;
        //raster.pixel[x0+y0] = pix;
        if (dx > dy) {
            int fraction = dy - (dx >> 1);
            while (x0 != x1) {
                if (fraction >= 0) {
                    y0 += stepy;
                    fraction -= dx;
                }
                x0 += stepx;
                fraction += dy;
                int objectID = worldMap[y0][x0];
                if(objectID != 0 && objectID != 2) // was != 1 (?)
                    return true;
            }
        } else {
            int fraction = dx - (dy >> 1);
            while (y0 != y1) {
                if (fraction >= 0) {
                    x0 += stepx;
                    fraction -= dy;
                }
                y0 += stepy;
                fraction += dx;
                int objectID = worldMap[y0][x0];
                if(objectID != 0 && objectID != 2) // was != 1 (?)
                    return true;
            }
        }
        return false;
    }

    // need to test for other objects too.
   /**
    * Determine if an object has collided with an unmovable object when dropped
    * or pushed. This method ensures that objects will not land on walls or
    * outside of the outer walls.
    * @param obj ball or cap object
    * @return <tt>true</tt> if no collision detected, <tt>false</tt> otherwise
    */
    private boolean testObjectCollision(Vertex obj) {
        int x, y;
        int ox, oy;
        boolean clear = false;
        ox = obj.xPos;
        oy = obj.yPos;

        // test top corners first
        if(worldMap[oy][ox] == 1 || worldMap[oy][ox] == 2) {
            for(y = oy;y < (oy+8);y++) {
                if(worldMap[y][ox] == 0) {
                    oy = y;
                    obj.yPos = oy;
                    clear = true;
                    break;
                }
            }
            if(!clear) oy += 8;
            if(worldMap[oy][ox] == 1 || worldMap[oy][ox] == 2) {
                for(x = ox;x < (ox+8);x++) {
                    if(worldMap[oy][x] == 0) {
                        ox = x;
                        obj.xPos = ox;
                        obj.yPos = oy;
                        return true;
                    }
                }
            }
        }
        if(worldMap[oy][ox+8] == 1 || worldMap[oy][ox+8] == 2) {
            for(x = ox+8;x > ox;x--) {
                if(worldMap[oy][x] == 0) {
                    ox -= (ox+8)-x;
                    obj.xPos = ox;
                    obj.yPos = oy;
                    return true;
                }
            }
        }
        // now test bottom corners -> essentially reverse the above
        if(worldMap[oy+8][ox] == 1 || worldMap[oy+8][ox] == 1) {
            for(y = oy+8;y > oy;y--) {
                if(worldMap[y][ox] == 0) {
                    oy -= (oy+8)-y;
                    obj.yPos = oy;
                    clear = true;
                    break;
                }
            }
            if(!clear) oy -= 8;
            if(worldMap[oy+8][ox] == 1 || worldMap[oy+8][ox] == 2) {
                for(x = ox;x < (ox+8);x++) {
                    if(worldMap[oy+8][x] == 0) {
                        ox = x;
                        obj.xPos = ox;
                        obj.yPos = oy;
                        return true;
                    }
                }
            }
        }
        if(worldMap[oy+8][ox+8] == 1 || worldMap[oy+8][ox+8] == 2) {
            for(x = ox+8;x > ox;x--) {
                if(worldMap[oy+8][x] == 0) {
                    ox -= (ox+8)-x;
                    obj.xPos = ox;
                    obj.yPos = oy;
                    return true;
                }
            }
        }

        if(clear) return true;
        else      return false;
    }

   /**
    * Indirectly computes the light and distance readings for all sensors based
    * on the robot's current state, coordinates and orientation.
    */
    protected void processSensors() {
        int i, arm, grip;
        boolean objectPresent;
        float  zx, zy, za; // sensor x, y, & angle
        boolean dark             = true;
        // NEW 7/29/2003 - sp
        contact = false;

        /* setup robot coordinate values */
        rx = currentPos.dx + 13.0f;
        ry = currentPos.dy + 13.0f;
        ra = currentPos.alpha;
        sina = (float)Math.sin(ra);
        cosa = (float)Math.cos(ra);
        
        arm =  rState.getArmState();
        grip = rState.getGripperState();
        objectPresent = rState.isObjectPresent();

        /* setup light sensor testing:
         *   - if there are lights, find out which ones are obstructed
         */
        unblockedLightList.clear();
        if(!world.lightObjects.isEmpty()) {
            Enumeration e = world.lightObjects.elements();
            while(e.hasMoreElements()) {
                Vertex light = (Vertex)e.nextElement();
                if(!isLightObstructed((int)rx,(int)ry,light.xPos,light.yPos)) {
                    unblockedLightList.add(light);
                    dark = false;
                }
            }
        }
        if(heldObject != null && grip == KSGripperStates.GRIP_OPEN)
            dropObject(arm);

        if(heldObject == null && grip == KSGripperStates.GRIP_OPEN &&
           arm == KSGripperStates.ARM_UP)
            rState.postObjectPresent(false);

        // NEW 8/6/03 - sp -- sometimes objects are picked up too fast
        //if(heldObject == null && grip == KSGripperStates.GRIP_CLOSED &&
         //arm == KSGripperStates.ARM_UP && objectPresent)
          //rState.postObjectPresent(false);

        //rState.sensorDone = false;
        for(i = 0;i < 8;i++) {
            float alphaR = normRad(ra);
            /* when arm is down, front & side sensors will be blocked
             */
            if(arm == KSGripperStates.ARM_DOWN) {
                int temp;
                if(i >= 0 && i<= 5) {
                    sensorArray[i].setDistValue(1015+(randGen.nextInt(8)));
                    // still need to look for close balls/caps
                	float zx1 = sensorArray[i].x;
                    float zy1 = sensorArray[i].y;
                    float xc1 = (rx + (zy1*sina) + (zx1*cosa));
                    float yc1 = (ry - (zy1*cosa) + (zx1*sina));

                 	if(sensorVal == 32){
                 		sensorVal =0;}
                	rState.setSensorCoord(xc1,sensorVal);
                	rState.setSensorCoord(yc1,sensorVal + 1);
                 	sensorVal = sensorVal + 2;
                    temp = computeDistValues(rx, ry, alphaR, i);
                }
                // back sensors still need normal processing
                else {
                	float zx1 = sensorArray[i].x;
                    float zy1 = sensorArray[i].y;
                    float xc1 = (rx + (zy1*sina) + (zx1*cosa));
                    float yc1 = (ry - (zy1*cosa) + (zx1*sina));

                 	if(sensorVal == 32){
                 		sensorVal =0;}
                	rState.setSensorCoord(xc1,sensorVal);
                	rState.setSensorCoord(yc1,sensorVal + 1);
                 	sensorVal = sensorVal + 2;
                    temp = computeDistValues(rx, ry, alphaR, i);
                    sensorArray[i].setDistValue(temp);
                    //array[i] = objID;
                }
                if((i != 2) && (i != 3)) {
                    if(objectID != -1 && temp > 1000)
                        computeNormalCollision(i);
                }
                else {
                    if(objectID != -1 && temp > 600)
                      computeArmCollision(i, grip, objectPresent);
                }
            }
            /* when arm is up, compute all readings
             */
            else {
            	float zx1 = sensorArray[i].x;
                float zy1 = sensorArray[i].y;
                float xc1 = (rx + (zy1*sina) + (zx1*cosa));
                float yc1 = (ry - (zy1*cosa) + (zx1*sina));

             	if(sensorVal == 32){
             		sensorVal =0;}
            	rState.setSensorCoord(xc1,sensorVal);
            	rState.setSensorCoord(yc1,sensorVal + 1);
             	sensorVal = sensorVal + 2;
                int temp = computeDistValues(rx,ry,alphaR,i);

                sensorArray[i].setDistValue(temp);
                if(objectID != -1 && temp > 1000)
                        computeNormalCollision(i);
            }

            /* if there are no lights, don't bother computing anything
             */
            if(dark) {
                int value = 500;
                //value -= (value*(randGen.nextInt(200)-100))/2000;
                value += randGen.nextInt(11);
                sensorArray[i].setLightValue(value);
            }
            else {
                zx = sensorArray[i].x;
                zy = sensorArray[i].y;
                za = sensorArray[i].theta;
                int xc = (int)(rx + zy*sina + zx*cosa);
                int yc = (int)(ry - zy*cosa + zx*sina);
                float alphaS = normRad(ra + za);
                int lVal = computeLightValues(xc, yc, alphaS);
                int l = i;
                if(i == 0) l = 5;
                else if(i == 1) l = 4;
                else if(i == 4) l = 1;
                else if(i == 5) l = 0;
                sensorArray[l].setLightValue(lVal);
            }
        }
        // NEW 7/29/2003 - sp
        if(contact)
          world.setWorldChange(true);
    }

    // Info: objectHeld == true, gripper is open
   /**
    * This method is called when the robot has dropped an object.
    * @param armState current arm state (KSGripperStates.ARM_UP or
    * KSGripperStates.ARM_DOWN)
    */
    private void dropObject(int armState) {
        int i, j, dropX, dropY;
        boolean objStuck = false;

        if(armState == KSGripperStates.ARM_UP) {
            dropX = (int)(rx + 0.0f*sina + 18.0f*cosa);
            dropY = (int)(ry - 0.0f*cosa + 18.0f*sina);
        }
        else {
            dropX = (int)(rx + 0.0f*sina + 16.0f*cosa);
            dropY = (int)(ry - 0.0f*cosa + 16.0f*sina);
        }
        dropX -= 4;
        dropY -= 4;

        rState.setObjectHeld(false);
        rState.postObjectPresent(false);
        heldObject.setVertexCoordinates(dropX,dropY,0.0f);
        objStuck = testObjectCollision(heldObject);
        if(objStuck) {
            if(!stuckObjects.contains(heldObject))
                stuckObjects.add(heldObject);
        }
        for(i = dropY;i < (dropY+9);i++) {
            for(j = dropX;j < (dropX+9);j++) {
                if(worldMap[i][j] == 0)
                    worldMap[i][j] = heldObject.id;
            }
        }
        heldObject = null;
        world.setHeldId(0);
        // NEW 7/29/2003 - sp
        contact = true;
    }

    // info: objectID, sensor #, arm is up
   /**
    * Determines if the robot has made contact with an object. If this is so,
    * then the object's position is updated based on very primitive collision
    * physics. This method assumes that the arm is up.
    * @param sNum id of the sensor that detected an object close by
    */
    private void computeNormalCollision(int sNum) {
        int i, j;
        boolean objStuck = false;
        boolean aHit = false;
        Vertex closeObject = null;

        Enumeration e = objList.elements();
	while(e.hasMoreElements()) {
	    closeObject = (Vertex)e.nextElement();
            if(closeObject.id == objectID) {
              break;
            }
	}
        if (closeObject != null) {
            rect.setBounds(closeObject.xPos,closeObject.yPos,8,8);
        }

        if(sNum == 2 || sNum == 3) {
            // first check point between front sensors
            int tipX = (int)(rx + 0.0f*sina + 10.0f*cosa);
            int tipY = (int)(ry - 0.0f*cosa + 10.0f*sina);
            if(rect.contains(tipX, tipY)) {
                aHit = true;
                // NEW 7/29/2003 - sp
                contact = true;
                // erase map segment
                for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                    for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                        if(worldMap[i][j] == closeObject.id)
                            worldMap[i][j] = 0;
                    }
                }
                // the 2.0 up the y is an offset
                // these x,y's should be taken as the centriod
                closeObject.xPos = (int)(rx + 0.0f*sina + 16.0f*cosa);
                closeObject.yPos = (int)(ry - 0.0f*cosa + 16.0f*sina);
                // now adjust to upper-left point
                closeObject.xPos -= 4;
                closeObject.yPos -= 4;
                objStuck = testObjectCollision(closeObject);
                if(objStuck) {
                    if(!stuckObjects.contains(closeObject))
                        stuckObjects.add(closeObject);
                }
                // add new location to map
                for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                    for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                        if(worldMap[i][j] == 0)
                            worldMap[i][j] = closeObject.id;
                    }
                }
            }
        }
        if(!aHit && (sNum == 6 || sNum == 7)) {
            // first check point between rear sensors
            int tipX = (int)(rx + 0.0f*sina + -10.0f*cosa);
            int tipY = (int)(ry - 0.0f*cosa + -10.0f*sina);
            if(rect.contains(tipX, tipY)) {
                aHit = true;
                // NEW 7/29/2003 - sp
                contact = true;
                // erase map segment
                for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                    for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                        if(worldMap[i][j] == closeObject.id)
                            worldMap[i][j] = 0;
                    }
                }
                // the 2.0 up the y is an offset
                // these x,y's should be taken as the centriod
                closeObject.xPos = (int)(rx + 0.0f*sina + -16.0f*cosa);
                closeObject.yPos = (int)(ry - 0.0f*cosa + -16.0f*sina);
                // now adjust to upper-left point
                closeObject.xPos -= 4;
                closeObject.yPos -= 4;
                objStuck = testObjectCollision(closeObject);
                if(objStuck) {
                    if(!stuckObjects.contains(closeObject))
                        stuckObjects.add(closeObject);
                }
                // add new location to map
                for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                    for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                        if(worldMap[i][j] == 0)
                            worldMap[i][j] = closeObject.id;
                    }
                }
            }
        }
        if(!aHit) {
          // NEW 7/30/2003 - sp -- why did this break????
          int aX = 0, aY = 0;
          boolean computeRect = true;
          //System.out.println("Sensor " + sNum + " is hot");
          switch(sNum) {
            // 3.5f, 10.5f
            case 0: aX = (int)(rx + 10.0f*sina + 4.0f*cosa);
                    aY = (int)(ry - 10.0f*cosa + 4.0f*sina);
                    break;
            // 7.5f, 9.0f
            case 1: aX = (int)(rx + 9.0f*sina + 7.0f*cosa);
                    aY = (int)(ry - 9.0f*cosa + 7.0f*sina);
                    break;
            // 10.5f, 4.0f
            case 2: aX = (int)(rx + 4.0f*sina + 10.0f*cosa);
                    aY = (int)(ry - 4.0f*cosa + 10.0f*sina);
                    break;
            // 10.5f, -4.0f
            case 3: aX = (int)(rx + -4.0f*sina + 10.0f*cosa);
                    aY = (int)(ry - -4.0f*cosa + 10.0f*sina);
                    break;
            // 7.5f, -9.0f
            case 4: aX = (int)(rx + -9.0f*sina + 7.0f*cosa);
                    aY = (int)(ry - -9.0f*cosa + 7.0f*sina);
                    break;
            // 3.5f, -10.5f
            case 5: aX = (int)(rx + -10.0f*sina + 3.0f*cosa);
                    aY = (int)(ry - -10.0f*cosa + 3.0f*sina);
                    break;
            // -9.5f, -5.5f
            case 6: aX = (int)(rx + -5.0f*sina + -9.0f*cosa);
                    aY = (int)(ry - -5.0f*cosa + -9.0f*sina);
                    break;
            // -9.5f, 5.5f
            case 7: aX = (int)(rx + 5.0f*sina + -9.0f*cosa);
                    aY = (int)(ry - 5.0f*cosa + -9.0f*sina);
                    break;
            default: computeRect = false;
          }
          // END NEW
         /* This code was replaced by the above NEW code 7/30/2003 - sp
          System.out.println("Sensor " + sNum + " is hot");
            int aX = (int)(rx + sensorArray[sNum].y*sina +
                                sensorArray[sNum].x*cosa);
            int aY = (int)(ry - sensorArray[sNum].y*cosa +
                                sensorArray[sNum].x*sina);
           */
          if(computeRect) {
            if (rect.contains(aX, aY)) {
              aHit = true;
              // NEW 7/29/2003 - sp
              contact = true;
              //erase map segment
              for (i = closeObject.yPos; i < (closeObject.yPos + 9); i++) {
                for (j = closeObject.xPos; j < (closeObject.xPos + 9); j++) {
                  if (worldMap[i][j] == closeObject.id)
                    worldMap[i][j] = 0;
                }
              }
              switch (sNum) { //                           y           x
                case 0:
                  closeObject.xPos = (int) (rx + 16.0f * sina + 3.0f * cosa);
                  closeObject.yPos = (int) (ry - 16.0f * cosa + 3.0f * sina);
                  break;
                case 1:
                  closeObject.xPos = (int) (rx + 14.0f * sina + 8.0f * cosa);
                  closeObject.yPos = (int) (ry - 14.0f * cosa + 8.0f * sina);
                  break;
                case 2:
                  closeObject.xPos = (int) (rx + 8.5f * sina + 10.0f * cosa);
                  closeObject.yPos = (int) (ry - 8.5f * cosa + 10.0f * sina);
                  break;
                case 3:
                  closeObject.xPos = (int) (rx + -8.5f * sina + 10.0f * cosa);
                  closeObject.yPos = (int) (ry - -8.5f * cosa + 10.0f * sina);
                  break;
                case 4:
                  closeObject.xPos = (int) (rx + -14.0f * sina + 8.0f * cosa);
                  closeObject.yPos = (int) (ry - -14.0f * cosa + 8.0f * sina);
                  break;
                case 5:
                  closeObject.xPos = (int) (rx + -16.0f * sina + 3.0f * cosa);
                  closeObject.yPos = (int) (ry - -16.0f * cosa + 3.0f * sina);
                  break;
                case 6:
                  closeObject.xPos = (int) (rx + -10.0f * sina + -7.0f * cosa);
                  closeObject.yPos = (int) (ry - -10.0f * cosa + -7.0f * sina);
                  break;
                case 7:
                  closeObject.xPos = (int) (rx + 10.0f * sina + -7.0f * cosa);
                  closeObject.yPos = (int) (ry - 10.0f * cosa + -7.0f * sina);
                  break;

              } // end switch
              closeObject.xPos -= 4;
              closeObject.yPos -= 4;
              // need to check if another object occupies this space
              // also ensure object is in bounds
              objStuck = testObjectCollision(closeObject);
              if (objStuck) {
                if (!stuckObjects.contains(closeObject))
                  stuckObjects.add(closeObject);
              }
              for (i = closeObject.yPos; i < (closeObject.yPos + 9); i++) {
                for (j = closeObject.xPos; j < (closeObject.xPos + 9); j++) {
                  if (worldMap[i][j] == 0)
                    worldMap[i][j] = closeObject.id;
                }
              }
            } // end if(contains)
          }
        } // end if(!aHit)
    }

    // Info: objectID, sNum == 2 || 3, arm is down
   /**
    * Determines if the robot arm has made contact with an object. If this is so,
    * then the object's position is updated based on very primitive collision
    * physics. This method assumes that the arm is down.
    * @param sNum id of the sensor that detected an object close by
    * @param gripState current gripper state (KSGripperStates.GRIP_OPEN or
    * KSGripperStates.GRIP_CLOSED)
    * @param obj object present in gripper
    */
    private void computeArmCollision(int sNum, int gripState, boolean obj) {
        int i, j, t;
        boolean isFound = false;
        boolean objectHeld = false;
        boolean aHit = false;
        boolean objStuck = false;
        Vertex closeObject = null;

        Enumeration e = objList.elements();
	while(e.hasMoreElements()) {
	    closeObject = (Vertex)e.nextElement();
            if(closeObject.id == objectID)
                break;
	}
        rect.setBounds(closeObject.xPos,closeObject.yPos,8,8);

        if(gripState == KSGripperStates.GRIP_OPEN) {
            int gX = (int)(rx + gripSensor.y*sina + gripSensor.x*cosa);
            int gY = (int)(ry - gripSensor.y*cosa + gripSensor.x*sina);
            if(rect.contains(gX, gY)) {
                isFound = true;
                rState.postObjectPresent(true);
            }
            else {
                rState.postObjectPresent(false);
            }
            if(!isFound) {
                for(t = 0;t < 5;t++) {
                    int aX = (int)(rx + armPoints[t].y*sina + armPoints[t].x*cosa);
                    int aY = (int)(ry - armPoints[t].y*cosa + armPoints[t].x*sina);
                    if(rect.contains(aX, aY)) {
                        aHit = true;
                        break;
                    }
                }
                if(aHit) {
                  // NEW 7/29/2003 - sp
                  contact = true;
                    //erase map segment
                    for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                        for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                            if(worldMap[i][j] == closeObject.id)
                                worldMap[i][j] = 0;
                        }
                    }
                    switch(t) { //                              y           x
                        case 0: closeObject.xPos = (int)(rx + 18.0f*sina + 9.0f*cosa);
                                closeObject.yPos = (int)(ry - 18.0f*cosa + 9.0f*sina);
                                break;
                        case 1: closeObject.xPos = (int)(rx + 18.0f*sina + 24.0f*cosa);
                                closeObject.yPos = (int)(ry - 18.0f*cosa + 24.0f*sina);
                                break;
                        case 2: closeObject.xPos = (int)(rx + 0.0f*sina + 19.0f*cosa);
                                closeObject.yPos = (int)(ry - 0.0f*cosa + 19.0f*sina);
                                break;
                        case 3: closeObject.xPos = (int)(rx + -18.0f*sina + 9.0f*cosa);
                                closeObject.yPos = (int)(ry - -18.0f*cosa + 9.0f*sina);
                                break;
                        case 4: closeObject.xPos = (int)(rx + -18.0f*sina + 24.0f*cosa);
                                closeObject.yPos = (int)(ry - -18.0f*cosa + 24.0f*sina);
                                break;
                    }
                    closeObject.xPos -= 4;
                    closeObject.yPos -= 4;
                    objStuck = testObjectCollision(closeObject);
                    if(objStuck) {
                        if(!stuckObjects.contains(closeObject))
                            stuckObjects.add(closeObject);
                    }
                    for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                        for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                            if(worldMap[i][j] == 0)
                                worldMap[i][j] = closeObject.id;
                        }
                    }
                }// end if(aHit)
            }// end not isFound
        }// end if(GRIP_OPEN)
        /* Gripper is closed */
        else {
            if(obj && heldObject == null) { // object detected
                rState.setObjectHeld(true);
                heldObject = closeObject;
                // NEW 7/29/2003 - sp
                contact = true;
                // -erase from map
                for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                    for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                        if(worldMap[i][j] == closeObject.id)
                            worldMap[i][j] = 0;
                    }
                }
                if(stuckObjects.contains(closeObject))
                    stuckObjects.removeElement(closeObject);
                objectHeld = true;
                world.setHeldId(heldObject.id);
            }
            else {
                for(t = 0;t < 3;t++) {
                    int aX = (int)(rx + closedGPoints[t].y*sina + closedGPoints[t].x*cosa);
                    int aY = (int)(ry - closedGPoints[t].y*cosa + closedGPoints[t].x*sina);
                    if(rect.contains(aX, aY)) {
                        aHit = true;
                        // NEW 7/29/2003 - sp
                        contact = true;
                        break;
                    }
                }
                if(aHit) {
                    //erase map segment
                    for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                        for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                            if(worldMap[i][j] == closeObject.id)
                                worldMap[i][j] = 0;
                        }
                    }
                    switch(t) { //                              y           x
                        case 0: closeObject.xPos = (int)(rx + 18.0f*sina + 9.0f*cosa);
                                closeObject.yPos = (int)(ry - 18.0f*cosa + 9.0f*sina);
                                break;
                        case 1: closeObject.xPos = (int)(rx + 0.0f*sina + 28.0f*cosa);
                                closeObject.yPos = (int)(ry - 0.0f*cosa + 28.0f*sina);
                                break;
                        case 2: closeObject.xPos = (int)(rx + -18.0f*sina + 9.0f*cosa);
                                closeObject.yPos = (int)(ry - -18.0f*cosa + 9.0f*sina);
                                break;
                    }
                    closeObject.xPos -= 4;
                    closeObject.yPos -= 4;
                    objStuck = testObjectCollision(closeObject);
                    if(objStuck) {
                        if(!stuckObjects.contains(closeObject))
                            stuckObjects.add(closeObject);
                    }
                    for(i = closeObject.yPos;i < (closeObject.yPos+9);i++) {
                        for(j = closeObject.xPos;j < (closeObject.xPos+9);j++) {
                            if(worldMap[i][j] == 0)
                                worldMap[i][j] = closeObject.id;
                        }
                    }
                }// end if(aHit)
            }// else (!obj)
        }
    }
}
