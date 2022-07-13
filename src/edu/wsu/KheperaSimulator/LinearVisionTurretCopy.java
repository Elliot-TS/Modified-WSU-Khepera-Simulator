package edu.wsu.KheperaSimulator;

/**
 * LinearVisionTurret.java 2006/04/18
 *
 */

import java.awt.Color;
import java.awt.Graphics2D;
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

public class LinearVisionTurretCopy {
	
	    private CurrentRobotState rState;
	    private Vector worldObjects;
	    private RobotCoordinates currentPos;
	    private WorldPanel world;
	    
	    public LinearVisionTurretCopy(CurrentRobotState crState,WorldPanel world) {
	        this.world = world;
	        this.rState = crState;
	        currentPos  = rState.getRobotCoordinates();
	    }
	    
	    private float normRad(float theta) {
	        while(theta > Math.PI)  theta -= (float)(2*Math.PI);
	        while(theta < -Math.PI) theta += (float)(2*Math.PI);
	        return theta;
	    }
	    
	    protected void processLvt() {
	        worldObjects = world.getWorldObjects();
	        computeLvtValues();             
	    }

	    
/* This does the main processing for the LVT Panel.It gets the current robot coordinates,determines
 *  which quadrant it is in and then calls the sortByDistance method.This method sorts the object in front 
 *  of the LVT camera.64 lines for 64 pixels are drawn according to the 36 degrees and values for the 64 
 *  pixels is then calculated by determining if any of these lines intersect with any of the objects that
 *  have been previously sorted.
 */
	    protected void computeLvtValues() 
	    {
	    	int quad=0;
	    	ArrayList alist;
	    	float psi,a;
	    	float p1x,p1y,p2x,p2y,rx,ry,value;
	    	a =  currentPos.alpha;
	    	rx = currentPos.dx + 13.0f;
	    	ry = currentPos.dy + 13.0f;

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
	    	else if(a < 0 && a >= -1.5707963){
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
	    			arrayIndex++;
	    			if(a < psi) //left spill over
	    			{
	    				p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.cos(psi - a));
	    				p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.sin(psi - a));
	    				
	    				p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.cos(psi - a));
	    				p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.sin(psi - a));
	    				
	    				if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    				rState.setLvtValue(value,arrayIndex);
	    			}
	    				else  //within the quadrant
	    			{
//	    				Quadrant 1
	    	        	//Point 1
		    	        p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.cos(a - psi));
		    		    p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.sin(a - psi));
	    		        
	    		        //Point 3
		    		    p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.cos(a - psi));
		    		    p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.sin(a - psi));
	    		        
	    				if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    		        
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    		     }
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
    				arrayIndex++;
	    			if((1.57 - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.sin((psi + a) - 1.57));
	    				p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.cos((psi + a) - 1.57));
	    				
	    				p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.sin((psi + a) - 1.57));
	    				p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.cos((psi + a) - 1.57));
	    				
	    				if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    				
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    			else  //within the quadrant
	    			{
	    				//Quadrant 1
	    	        	//Point 1
	    				p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.cos(a + psi));
	    		        p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.sin(a + psi));
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.cos(a + psi));
	    		        p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.sin(a + psi));
	    		        
	    				if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    		        
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    		}
	    	}
	    	break;
	    	case 2:{//Quadrant 2
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			arrayIndex++;
	    			if((a - 1.57) < psi) //left spill over
	    			{
	    				p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.sin(psi - (a - 1.57)));
	    				p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.cos(psi - (a - 1.57)));
	    				
	    				p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.sin(psi - (a - 1.57)));
	    				p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.cos(psi - (a - 1.57)));
	    				
	    				if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    				
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    			else
	    			{
	    				p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.sin(a - 1.57 - psi));
	    		        p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.cos(a - 1.57 - psi));
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.sin(a - 1.57 - psi));
	    		        p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.cos(a - 1.57 - psi));
    		        
	    		        if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    		        
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    				//System.out.println("Pixel " + arrayIndex + ": " + value);
	    			}
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			arrayIndex++;
	    			if((3.14 - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.cos((psi + a) - 3.14));
	    				p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.sin((psi + a) - 3.14));
	    				
	    				p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.cos((psi + a) - 3.14));
	    				p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.sin((psi + a) - 3.14));
	    				
	    				if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    				
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    			else
	    			{
	    	        	p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.cos(3.14 - a - psi));
	    		        p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.sin(3.14 - a - psi));
	    		        
	    		        //Point 3
	    	        	p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.cos(3.14 - a - psi));
	    		        p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.sin(3.14 - a - psi));
	    		        
	    		    	if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    		    	
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
				    }
	    		}
	    	}
	    	break;
	    	case 3:{//Quadrant 3
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			arrayIndex++;
	    			if((a - 3.14) < psi) //left spill over
	    			{
	    				p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.cos(psi - (a - 3.14)));
	    				p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.sin(psi - (a - 3.14)));
	    				
	    				p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.cos(psi - (a - 3.14)));
	    				p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.sin(psi - (a - 3.14)));
	    				
	    				if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    				
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);

	    			}
	    			else
	    			{
//	    				Quadrant 3
	    	        	//Point 1
	    	        	p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.cos(a - 3.14 - psi));
	    		        p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.sin(a - 3.14 - psi));
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.cos(a - 3.14 - psi));
	    		        p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.sin(a - 3.14 - psi));

	    		        if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    		        
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			arrayIndex++;
	    			if((normRad((float)4.712) - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.sin((a + psi) - normRad((float)4.712)));
	    				p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.cos((a + psi) - normRad((float)4.712)));
	    				
	    				p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.sin((a + psi) - normRad((float)4.712)));
	    				p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.cos((a + psi) - normRad((float)4.712)));
	    				
	    		    	if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    				
	    				value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    			else
	    			{
//	    				Quadrant 3
	    	        	//Point 1
	    	        	p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.cos(a - 3.14 + psi));
	    		        p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.sin(a - 3.14 + psi));
	    		        
	    		        //Point 3
	    		        p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.cos(a - 3.14 + psi));
	    		        p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.sin(a - 3.14 + psi));
	    		        
	    		    	if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    				
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    		}
	    	}
	    	break;
	    	case 4:{//Quadrant 4
	    		int arrayIndex = -1;
	    		//LEFT SIDE PIXELS
	    		for(psi = (float)0.314 ; psi > 0 ; psi-=(0.314/32))
	    		{
	    			arrayIndex++;
	    			if((a - normRad((float)4.712)) < psi) //left spill over
	    			{
	    				p1x = (float)(rx - 20.85/Math.cos(0.314) * Math.sin(psi - (a - normRad((float)4.712))));
	    				p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.cos(psi - (a - normRad((float)4.712))));
	    				
	    				p2x = (float)(rx - 208.5/Math.cos(0.314) * Math.sin(psi - (a - normRad((float)4.712))));
	    				p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.cos(psi - (a - normRad((float)4.712))));
	    				
	    		        if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
	    				
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    			else
	    			{
//	    				Quadrant 4
			        	//Point 1
			        	p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + psi));
	    			    p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + psi));
				        
				        //Point 3
				        p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) + psi));
		    			p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) + psi));
		    			
	    		        if(arrayIndex==0){
	    					currentPos.setLvtCoordinates(p1x,p1y,0);
	    					currentPos.setLvtCoordinates(p2x,p2y,2);
	    				}
				        
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    		}
	    		
	    		//RIGHT SIDE PIXELS
	    		for(psi = (float)0 ; psi < (float)0.314 ; psi+=(0.314/32))
	    		{
	    			arrayIndex++;
	    			if((normRad((float)6.28) - a) < psi) //right spill over
	    			{
	    				p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.cos((psi + a) - normRad((float)6.28)));
	    				p1y = (float)(ry + 20.85/Math.cos(0.314) * Math.sin((psi + a) - normRad((float)6.28)));
	    				
	    				p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.cos((psi + a) - normRad((float)6.28)));
	    				p2y = (float)(ry + 208.5/Math.cos(0.314) * Math.sin((psi + a) - normRad((float)6.28)));
	    				
	    		    	if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
	    				
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    			else
	    			{
//	    				Quadrant 4
			        	//Point 1
			        	p1x = (float)(rx + 20.85/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) - psi));
	    			    p1y = (float)(ry - 20.85/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) - psi));
				        
				        //Point 3
				        p2x = (float)(rx + 208.5/Math.cos(0.314) * Math.cos((normRad((float)6.28) - a) - psi));
		    			p2y = (float)(ry - 208.5/Math.cos(0.314) * Math.sin((normRad((float)6.28) - a) - psi));
		    			
	    		    	if(arrayIndex==63){
	    					currentPos.setLvtCoordinates(p1x,p1y,1);
	    					currentPos.setLvtCoordinates(p2x,p2y,3);
	    				}
				        
	    		        value = checkObjectPresent(p1x,p1y,p2x,p2y,alist);
	    		        rState.setLvtValue(value,arrayIndex);
	    			}
	    		}
	    
	    	}
	    	break;
	    	default:
	    		break;
	    			
	    	}
	    }
	    /*This method sorts the object in front of the camera with respect to distance.
	     * 
	     */
	   protected ArrayList sortByDistance(int q){
	    	
		   	ArrayList list = new ArrayList();
	    	double rx,ry;
	    	int dist,quad,i=-1;
	    	rx = (double)currentPos.x + 13.0f;
	    	ry = (double)currentPos.y + 13.0f;
	    	quad = q;
	    		    	
	    	Enumeration e = worldObjects.elements();
	        while(e.hasMoreElements()) 
	        {
	          Vertex v = (Vertex)e.nextElement();
	          i++;
	          	
	          	if (quad == 1)
	          	{
	          		if (v.xPos >= (int)rx || v.yPos >= (int)ry)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			//System.out.println(dist);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	          	else if (quad == 2)
	          	{
	          		if (v.yPos >= (int)ry || v.xPos <= (int)rx)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	          	else if (quad == 3)
	          	{
	          		if (v.xPos <= (int)rx || v.yPos <= (int)ry)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	          	else if (quad == 4)
	          	{
	          		if (v.yPos <= (int)ry || v.xPos >= (int)rx)
	          		{
	          			dist = (int)Point2D.Float.distance(rx,ry,(double)v.xPos,(double)v.yPos);
	          			list.add(new sortObject(new Integer(dist),i));
	          		}
	          	}
	          	
	        }
	        
	        Collections.sort(list);
	        return list;
	    }

	   /*This method is used to determine if any of the 64 lines intersect with any objects in front of it.
	    * 
	    */
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
	        		 if(line.intersects((double)v.xPos,(double)v.yPos,8,50))
	        		 {
	        			 return ((float)10.0);
	        		 }
	        	 }
	        	 else
	        	 {
	        		 if(line.intersects((double)v.xPos,(double)(v.yPos - 8),50,8))
	        		 {
	        			 return ((float)10.0);
	        		 }
	        	 }
	        }
	        else if (v.objType == WorldPanel.CAP)
	        {
	        	if(line.intersects((double)v.xPos,(double)v.yPos,7,7))
       		 	{
	        		return ((float)100.0);
       		 	}
	        }
	        else if (v.objType == WorldPanel.BALL)
	        {
	        	if(line.intersects((double)v.xPos,(double)v.yPos,7,7))
       		 	{
	        		return ((float)200.0);
       		 	}
	        }
	        else if (v.objType == WorldPanel.LIGHT)
	        {
	        	if(line.intersects((double)v.xPos,(double)v.yPos,15,15))
       		 	{
	        		return ((float)255.0);
       		 	}
	        }
	        else if(p2x >= 480 || p2y >=480 || p2x <= 11 || p2y <= 11)
	        {
	        	return ((float)10.0);
	        }
	        else
	        {
	        	continue;
	        }
	      }
	        if(p2x >= 480 || p2y >=480 || p2x <= 11 || p2y <=11)
	        {
	        	return ((float)10.0);
	        }
	        else
	        	return ((float)230.0);
		   
		   
	   }
	   
}




