package edu.wsu.KheperaSimulator;

/**
 * LinearVisionTurret.java 2006/04/18
 *
 */

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;

/**
 * A <code>LinearVisionTurret</code> class represents the Linear Vision Turret 
 * placed on top of the robot.This is a small camera that provides the vision 
 * functionality to the robot.It has a camera and a light intensity detector.
 * The camera renders 8 bit(0-255) grey scale values for 64 pixels(0-63).This
 * class models the LVT behavior.The light intensity detector has not been 
 * implemented since that only controls the frequency at which the image sensor
 * is polled w.r.t the light present in the environment.The images rendered are 
 * the same irrespective of the L.I.D.Assuming the lighting is the same at all 
 * times which is so in the lab,the image sensor is polled everytime the other
 * IR sensors values are updated.The LVT would return grey scale values for all
 * the 63 pixels.
 * 
 * @author  Aarti Raghavan
 */

public class LinearVisionTurret {
	
	    protected Vector stuckObjects;
	    protected Vector worldObjects;
	    protected Vector unblockedLightList;
	    private int[][] worldMap;
	    private CurrentRobotState rState;
	    private RobotCoordinates currentPos;
	    private MyFloatPoint[] trapPoints,newTrapPoints;
	    private WorldPanel world;
	    private Line2D.Float line;
	    
	    public LinearVisionTurret(int[][] map, CurrentRobotState crState,WorldPanel world) {
	        this.worldMap = map;
	        this.world = world;
	        this.rState = crState;
	        currentPos  = rState.getRobotCoordinates();
	        trapPoints = new MyFloatPoint[4];
	        worldObjects = world.getWorldObjects();
	        initializeTrap();
	    }
	    
	    private void initializeTrap()
	    {
	        trapPoints[0]= new MyFloatPoint(-1.6f,5f);
	        trapPoints[1]= new MyFloatPoint(1.6f,5f);
	        trapPoints[2]= new MyFloatPoint(-16.2f,50f);
	        trapPoints[3]= new MyFloatPoint(16.2f,50f);
	    }
	    
	    private float normRad(float theta) {
	        while(theta > Math.PI)  theta -= (float)(2*Math.PI);
	        while(theta < -Math.PI) theta += (float)(2*Math.PI);
	        return theta;
	    }
	    
	    protected void processLvt() {
	        float a,p1x,p1y,p2x,p2y,p3x,p3y,p4x,p4y,rx,ry,ex,ey,tx,ty;
	        ArrayList alist;
	        float i,j;
	        rx = currentPos.dx;
	        ry = currentPos.dy;
	        a =  currentPos.alpha;
	        	        
	        System.out.println("Current X is " + rx);
	        System.out.println("Current Y is " + ry);
	        System.out.println("Angle is " + a);
	        
	        computeLvtValues();
	        
	        /*if ( a >= 0 && a < 1.57){
	        	//Quadrant 1
	        	//Point 1
	        	p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos(a - 0.314));
		        p1y = (float)(ry + 5/Math.cos(0.314) * Math.sin(a - 0.314));
		        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
		        
		        //Point 2
	        	p2x = (float)(rx + 5/Math.cos(0.314) * Math.cos(a + 0.314));
		        p2y = (float)(ry + 5/Math.cos(0.314) * Math.sin(a + 0.314));
		        //System.out.println("New X,Y for point 2 is " + p2x + " " + p2y);
		        
		        //Point 3
	        	p3x = (float)(rx + 50/Math.cos(0.314) * Math.cos(a - 0.314));
		        p3y = (float)(ry + 50/Math.cos(0.314) * Math.sin(a - 0.314));
		        //System.out.println("New X,Y for point 3 is " + p3x + " " + p3y);
		        
		        //Point 4
	        	p4x = (float)(rx + 50/Math.cos(0.314) * Math.cos(a + 0.314));
		        p4y = (float)(ry + 50/Math.cos(0.314) * Math.sin(a + 0.314));
		        //System.out.println("New X,Y for point 4 is " + p4x + " " + p4y);
		        
		        alist = sortByDistance(1);
		        
		        //ListIterator l = alist.listIterator();
		        //while(l.hasNext()){
		        //	sortObject s = (sortObject)l.next();
		        //	System.out.println(s.windex);
		        //}
		        //findLineEquation(p1x,p1y,p4x,p4y,alist);
		        computeLvtValues(alist);
	        }
	       else if(a >= 1.57 && a < 3.14)
	       	{
	    	   //Quadrant 2
	        	//Point 1
	        	p1x = (float)(rx - 5/Math.cos(0.314) * Math.sin(a - 1.57 - 0.314));
		        p1y = (float)(ry + 5/Math.cos(0.314) * Math.cos(a - 1.57 - 0.314));
		        System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
		        
		        //Point 2
	        	p2x = (float)(rx - 5/Math.cos(0.314) * Math.cos(3.14 - a - 0.314));
		        p2y = (float)(ry + 5/Math.cos(0.314) * Math.sin(3.14 - a - 0.314));
		        System.out.println("New X,Y for point 2 is " + p2x + " " + p2y);
		        
		        //Point 3
		        p3x = (float)(rx - 50/Math.cos(0.314) * Math.sin(a - 1.57 - 0.314));
		        p3y = (float)(ry + 50/Math.cos(0.314) * Math.cos(a - 1.57 - 0.314));
		        System.out.println("New X,Y for point 3 is " + p3x + " " + p3y);
		        
		        //Point 4
		        p4x = (float)(rx - 50/Math.cos(0.314) * Math.cos(3.14 - a - 0.314));
		        p4y = (float)(ry + 50/Math.cos(0.314) * Math.sin(3.14 - a - 0.314));
		        System.out.println("New X,Y for point 4 is " + p4x + " " + p4y);
		        
		        alist = sortByDistance(2);
		        computeLvtValues(alist);
		        //findLineEquation(p1x,p1y,p4x,p4y,alist);
	        }
	        else if(a<0 && a >= -1.57)
	        	{
	        		//Quadrant 4
		        	//Point 1
		        	p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + 0.314));
    			    p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + 0.314));
			        System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
			        
			        //Point 2
    			    p2x = (float)(rx + 5/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) - 0.314));
			        p2y = (float)(ry - 5/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) - 0.314));
			        System.out.println("New X,Y for point 2 is " + p2x + " " + p2y);
			        
			        //Point 3
			        p3x = (float)(rx + 50/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + 0.314));
	    			p3y = (float)(ry - 50/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + 0.314));
			        System.out.println("New X,Y for point 3 is " + p3x + " " + p3y);
			        
			        //Point 4
			        p4x = (float)(rx + 50/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) - 0.314));
			        p4y = (float)(ry - 50/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) - 0.314));
			        System.out.println("New X,Y for point 4 is " + p4x + " " + p4y);
			        
			        alist = sortByDistance(4);
			     //   findLineEquation(p1x,p1y,p4x,p4y,alist);
			        
		        }
	        else{
	        	//Quadrant 3
	        	//Point 1
	        	p1x = (float)(rx - 5/Math.cos(0.314) * Math.cos(a - 3.14 - 0.314));
		        p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin(a - 3.14 - 0.314));
		        System.out.println("New X,Y for point 1 is " + p1x + " " +p1y);
		        
		        //Point 2
	        	p2x = (float)(rx - 5/Math.cos(0.314) * Math.cos(a - 3.14 + 0.314));
		        p2y = (float)(ry - 5/Math.cos(0.314) * Math.sin(a- 3.14 + 0.314));
		        System.out.println("New X,Y for point 2 is " + p2x + " " + p2y);
		        
		        //Point 3
		        p3x = (float)(rx - 50/Math.cos(0.314) * Math.cos(a - 3.14 - 0.314));
		        p3y = (float)(ry - 50/Math.cos(0.314) * Math.sin(a - 3.14 - 0.314));
		        System.out.println("New X,Y for point 3 is " + p3x + " " + p3y);
		        
		        //Point 4
		        p4x = (float)(rx - 50/Math.cos(0.314) * Math.cos(a - 3.14 + 0.314));
		        p4y = (float)(ry - 50/Math.cos(0.314) * Math.sin(a- 3.14 + 0.314));
		        System.out.println("New X,Y for point 4 is " + p4x + " " + p4y);
		        
		        alist = sortByDistance(3);
		       // findLineEquation(p1x,p1y,p4x,p4y,alist);
	        }*/
	        
	    }

	    
	   protected void computeLvtValues() 
	    {
	    	int quad=0;
	    	ArrayList alist;// = alist;
	    	float psi,a;
	    	float p1x,p1y,p2x,p2y,rx,ry,value;
	    	a =  currentPos.alpha;
	    	rx = currentPos.x;
	    	ry = currentPos.y;

	    	//Quadrant 1
	    	if ( a >= 0 && a < 1.57){
	    		quad = 1;
	    		alist = sortByDistance(quad);
	    	}
	    		
	    	//Quadrant 2
	    	else if(a >= 1.57 && a < 3.14){
	    		quad = 2;
	    		alist = sortByDistance(quad);
	    	}
	    	//Quadrant 4
	    	else if(a<0 && a >= -1.57){
	    		quad = 4;
	    		alist = sortByDistance(quad);
	    	}
	    	//Quadrant 3
	    	else {
	    		quad = 3;
	    		alist = sortByDistance(quad);
	    	}
	    	
	    	switch(quad){
	    	
	    	case 1:
	    	{//Quadrant 1
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS 
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			if(a < psi) //left spill over
	    			{
	    				p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos(psi - a));
	    				p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin(psi - a));
	    				
	    				p2x = (float)(rx + 50/Math.cos(0.314) * Math.cos(psi - a));
	    				p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin(psi - a));
	    				
	    				arrayIndex++;
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel " + arrayIndex + ": " + value);
	    				
	    			}
	    			else  //within the quadrant
	    			{
//	    				Quadrant 1
	    	        	//Point 1
	    	        	p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos(a - psi));
	    		        p1y = (float)(ry + 5/Math.cos(0.314) * Math.sin(a - psi));
	    		        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
	    		        
	    		        //Point 3
	    	        	p2x = (float)(rx + 50/Math.cos(0.314) * Math.cos(a - psi));
	    		        p2y = (float)(ry + 50/Math.cos(0.314) * Math.sin(a - psi));
	    		        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
	    		        
	    		        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel " + arrayIndex + ": " + value);
	    		     }
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			if((1.57 - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx - 5/Math.cos(0.314) * Math.sin((psi + a) - 1.57));
	    				p1y = (float)(ry + 5/Math.cos(0.314) * Math.cos((psi + a) - 1.57));
	    				
	    				p2x = (float)(rx - 50/Math.cos(0.314) * Math.sin((psi + a) - 1.57));
	    				p2y = (float)(ry + 50/Math.cos(0.314) * Math.cos((psi + a) - 1.57));
	    				
	    				arrayIndex++;
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else  //within the quadrant
	    			{
	    				//Quadrant 1
	    	        	//Point 1
	    	        	p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos(a - psi));
	    		        p1y = (float)(ry + 5/Math.cos(0.314) * Math.sin(a - psi));
	    		        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
	    		        
	    		        //Point 3
	    	        	p2x = (float)(rx + 50/Math.cos(0.314) * Math.cos(a - psi));
	    		        p2y = (float)(ry + 50/Math.cos(0.314) * Math.sin(a - psi));
	    		        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
	    		        
	    		        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    	}
	    	break;
	    	case 2:{//Quadrant 2
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			if((a - 1.57) < psi) //left spill over
	    			{
	    				p1x = (float)(rx + 5/Math.cos(0.314) * Math.sin(psi - (a - 1.57)));
	    				p1y = (float)(ry + 5/Math.cos(0.314) * Math.cos(psi - (a - 1.57)));
	    				
	    				p2x = (float)(rx + 50/Math.cos(0.314) * Math.sin(psi - (a - 1.57)));
	    				p2y = (float)(ry + 50/Math.cos(0.314) * Math.cos(psi - (a - 1.57)));
	    				
	    				arrayIndex++;
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else
	    			{
	    				p1x = (float)(rx - 5/Math.cos(0.314) * Math.sin(a - 1.57 - psi));
	    		        p1y = (float)(ry + 5/Math.cos(0.314) * Math.cos(a - 1.57 - psi));
	    		        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 50/Math.cos(0.314) * Math.sin(a - 1.57 - psi));
	    		        p2y = (float)(ry + 50/Math.cos(0.314) * Math.cos(a - 1.57 - psi));
	    		        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
	    				
	    		        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			if((3.14 - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx - 5/Math.cos(0.314) * Math.cos((psi + a) - 3.14));
	    				p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin((psi + a) - 3.14));
	    				
	    				p2x = (float)(rx - 50/Math.cos(0.314) * Math.cos((psi + a) - 3.14));
	    				p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin((psi + a) - 3.14));
	    				
	    				arrayIndex++;
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else
	    			{
	    				p1x = (float)(rx - 5/Math.cos(0.314) * Math.sin(a - 1.57 - psi));
	    		        p1y = (float)(ry + 5/Math.cos(0.314) * Math.cos(a - 1.57 - psi));
	    		        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 50/Math.cos(0.314) * Math.sin(a - 1.57 - psi));
	    		        p2y = (float)(ry + 50/Math.cos(0.314) * Math.cos(a - 1.57 - psi));
	    		        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
	    				
	    		        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    	}
	    	break;
	    	case 3:{//Quadrant 3
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			if((a - 3.14) < psi) //left spill over
	    			{
	    				p1x = (float)(rx - 5/Math.cos(0.314) * Math.cos(psi - (a - 3.14)));
	    				p1y = (float)(ry + 5/Math.cos(0.314) * Math.sin(psi - (a - 3.14)));
	    				
	    				p2x = (float)(rx - 50/Math.cos(0.314) * Math.cos(psi - (a - 3.14)));
	    				p2y = (float)(ry + 50/Math.cos(0.314) * Math.sin(psi - (a - 3.14)));
	    				
	    				arrayIndex++;
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else
	    			{
//	    				Quadrant 3
	    	        	//Point 1
	    	        	p1x = (float)(rx - 5/Math.cos(0.314) * Math.cos(a - 3.14 - psi));
	    		        p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin(a - 3.14 - psi));
	    		        //System.out.println("New X,Y for point 1 is " + p1x + " " +p1y);
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 50/Math.cos(0.314) * Math.cos(a - 3.14 - psi));
	    		        p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin(a - 3.14 - psi));
	    		        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
	    				
	    		        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			if((normRad((float)4.712) - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx + 5/Math.cos(0.314) * Math.sin((a + psi) - normRad((float)4.712)));
	    				p1y = (float)(ry - 5/Math.cos(0.314) * Math.cos((a + psi) - normRad((float)4.712)));
	    				
	    				p2x = (float)(rx + 50/Math.cos(0.314) * Math.sin((a + psi) - normRad((float)4.712)));
	    				p2y = (float)(ry - 50/Math.cos(0.314) * Math.cos((a + psi) - normRad((float)4.712)));
	    				
	    				arrayIndex++;
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else
	    			{
//	    				Quadrant 3
	    	        	//Point 1
	    	        	p1x = (float)(rx - 5/Math.cos(0.314) * Math.cos(a - 3.14 - psi));
	    		        p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin(a - 3.14 - psi));
	    		        //System.out.println("New X,Y for point 1 is " + p1x + " " +p1y);
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 50/Math.cos(0.314) * Math.cos(a - 3.14 - psi));
	    		        p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin(a - 3.14 - psi));
	    		        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
	    				
	    		        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    	}
	    	break;
	    	case 4:{//Quadrant 4
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			if((a - normRad((float)4.712)) < psi) //left spill over
	    			{
	    				p1x = (float)(rx - 5/Math.cos(0.314) * Math.sin(psi - (a - normRad((float)4.712))));
	    				p1y = (float)(ry - 5/Math.cos(0.314) * Math.cos(psi - (a - normRad((float)4.712))));
	    				
	    				p2x = (float)(rx - 50/Math.cos(0.314) * Math.sin(psi - (a - normRad((float)4.712))));
	    				p2y = (float)(ry - 50/Math.cos(0.314) * Math.cos(psi - (a - normRad((float)4.712))));
	    				
	    				arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else
	    			{
//	    				Quadrant 4
			        	//Point 1
			        	p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + psi));
	    			    p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + psi));
				        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
				        
				        //Point 3
				        p2x = (float)(rx + 50/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + psi));
		    			p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + psi));
				        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
				        
				        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			if((normRad((float)6.28) - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos((psi + a) - normRad((float)6.28)));
	    				p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin((psi + a) - normRad((float)6.28)));
	    				
	    				p2x = (float)(rx + 50/Math.cos(0.314) * Math.cos((psi + a) - normRad((float)6.28)));
	    				p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin((psi + a) - normRad((float)6.28)));
	    				
	    				arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    			else
	    			{
//	    				Quadrant 4
			        	//Point 1
			        	p1x = (float)(rx + 5/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + psi));
	    			    p1y = (float)(ry - 5/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + psi));
				        //System.out.println("New X,Y for point 1 is " + p1x + " " + p1y);
				        
				        //Point 3
				        p2x = (float)(rx + 50/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + psi));
		    			p2y = (float)(ry - 50/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + psi));
				        //System.out.println("New X,Y for point 3 is " + p2x + " " + p2y);
				        
				        arrayIndex++;
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				System.out.println("Pixel" + arrayIndex + ": " + value);
	    			}
	    		}
	    
	    	}
	    	break;
	    	default:
	    			
	    	}
	    }
	    
	   protected ArrayList sortByDistance(int q){
	    	
		   ArrayList list = new ArrayList();
	    	double rx,ry;
	    	int dist,quad,i=-1;
	    	rx = (double)currentPos.x;
	    	ry = (double)currentPos.y;
	    	quad = q;
	    		    	
	    	//Line2D.Float line = new Line2D.Float(p1x,p1y,p2x,p2y);
	    	//Point2D.Float pt = new Point2D.Float(currentPos.x,currentPos.y);
   
	    	Enumeration e = worldObjects.elements();
	        while(e.hasMoreElements()) 
	        {
	          Vertex v = (Vertex)e.nextElement();
	          i++;
	          	
	          	if (quad == 1)
	          	{
	          		if (v.xPos >= (int)rx)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			System.out.println(dist);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	          	else if (quad == 2)
	          	{
	          		if (v.yPos >= (int)ry)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	          	else if (quad == 3)
	          	{
	          		if (v.xPos <= (int)rx)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	          	else if (quad == 4)
	          	{
	          		if (v.yPos <= (int)ry)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	        }
	        
	        Collections.sort(list);
	        return list;
	    }

	   private float checkObjectPresent(float _p1x,float _p1y,float _p2x,float _p2y,ArrayList alist)
	   {
		   float p1x = _p1x, p1y = _p1y, p2x = _p2x, p2y = _p2y;
		   Line2D.Float line = new Line2D.Float(p1x,p1y,p2x,p2y);
		   ArrayList list;
		   list = alist;
	       
		   ListIterator l = alist.listIterator();
	        while(l.hasNext())
	        {
	        	sortObject s = (sortObject)l.next();
	          	Vertex v = (Vertex)worldObjects.get(s.windex);
	          	if (v.objType == WorldPanel.WALL)
	          	{
	        	 if(v.theta == 0.0f) 
	        	 {
	        		 if(line.intersects((double)v.xPos,(double)v.yPos,(double)v.xPos + 7,(double)v.yPos + 49))
	        		 //if(line.intersectsLine((double)v.xPos,(double)v.yPos,(double)v.xPos + 7,(double)v.yPos))
	        		 {
	        			 //System.out.println("1.Yes!");
	        			 return ((float)1.0);
	        		 }
	        		 /*else if(line.intersectsLine((double)v.xPos,(double)v.yPos,(double)v.xPos,(double)v.yPos + 49))
	        		 {
	        			 //System.out.println("2.Yes!");
	        			 return ((float)1.0);
	        		 }
	        		 else if(line.intersectsLine((double)v.xPos+7,(double)v.yPos,(double)v.xPos+7,(double)v.yPos + 49))
	        		 {
	        			 //System.out.println("3.Yes!");
	        			 return ((float)1.0);
	        		 }
	        		 else if(line.intersectsLine((double)v.xPos,(double)v.yPos+49,(double)v.xPos+7,(double)v.yPos + 49))
	        		 {
	        			 //System.out.println("4.Yes!");
	        			 return ((float)1.0);
	        		 }*/
	        	 }
	        	 else
	        	 {
	        		 //if(line.intersects((double)v.xPos,(double)v.yPos,(double)v.xPos + 49,(double)v.yPos + 7))
	        		 if(line.intersectsLine((double)v.xPos,(double)v.yPos,(double)v.xPos + 49,(double)v.yPos))
	        		 {
	        			 //System.out.println("1.Yes!");
	        			 return ((float)1.0);
	        		 }
	        		 else if(line.intersectsLine((double)v.xPos,(double)v.yPos,(double)v.xPos,(double)v.yPos - 7))
	        		 {
	        			 //System.out.println("2.Yes!");
	        			 return ((float)1.0);
	        		 }
	        		 else if(line.intersectsLine((double)v.xPos + 49,(double)v.yPos,(double)v.xPos + 49,(double)v.yPos - 7))
	        		 {
	        			 //System.out.println("3.Yes!");
	        			 return ((float)1.0);
	        		 }
	        		 else if(line.intersectsLine((double)v.xPos,(double)v.yPos-7,(double)v.xPos+49,(double)v.yPos - 7))
	        		 {
	        			// System.out.println("4.Yes!");
	        			 return ((float)1.0);
	        		 }
	        	 }
	        }
	        else if (v.objType == WorldPanel.CAP || v.objType == WorldPanel.BALL)
	        {
	        	if(line.intersects((double)v.xPos,(double)v.yPos,(double)v.xPos + 10,(double)v.yPos + 10))
       		 	{
	        		//System.out.println("1.Yes!");
	        		return ((float)0.5);
       		 	}
	        }
	        else
	        {
	        	continue;
	        }
	        }
	        return ((float)0.0);
		   
		   
	   }


}




