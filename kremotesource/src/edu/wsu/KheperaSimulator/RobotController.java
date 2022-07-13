/*
 *		RobotController.java
 *
 *		Created by Steve Perretta, Modified by Duane Bolick on 08 JAN 2005
 *
 */		

package edu.wsu.KheperaSimulator;
import java.io.*;
import java.util.Date;


public abstract class RobotController implements Controller
{

	private long waitTime = 5;
	private boolean finished = false;
	private boolean running = false;
	private Thread thread = null;
	private String name = null;
	
	public BufferedReader reader;
	public PrintWriter writer;

	private CurrentRobotState state;

	private stateUpdater sUpdate;
	private Thread updateThread;

	private Sensor[] sensors = null;

	public RobotController()
	{
	}

	protected void initialize(String _name, long timeout, BufferedReader r, PrintWriter w, CurrentRobotState st)
	{
		name = _name;
		reader = r;
		writer = w;
		state = st;
		waitTime = timeout;

		sUpdate = new stateUpdater(reader,writer,state);
		updateThread = new Thread(sUpdate);
		//REMOVE LATER
		updateThread.start();				
	} 
	
	protected void simStart()
	{
		thread = new Thread( this, name );
		thread.start();
	}

	protected void setWaitTime(long waitTime)
	{
		this.waitTime = waitTime;
	}

	protected void setFinished(boolean finished)
	{
		this.finished = finished;
	}

	protected boolean isRunning()
	{
		return running;
	}


	public void run() 
	{
		running = true;
		try
		{
			//while (!finished)
			while(state.sessionStatus == ClientConfiguration.RUNNING)
			{
				doWork();
				sleep(waitTime);
			}

			close();
		} 

		catch (Exception e)
		{
			System.out.println("\nThere was a problem with the controller.");
			System.out.println("Here is the controller thread's stack at");
			System.out.println("the time of the error:\n\n");

			System.out.println("=== Stack Trace ======================\n");
			e.printStackTrace();
			System.out.println("\n=== End of the stack trace ===========");

			System.out.println("\nSession ended\n");
			System.exit(0);
		}

		running = false;
	} 
  
	public void sleep(long timeout)
	{
		try 
		{
			//this.sleep(timeout);
			Thread.sleep(timeout);
		}
	
		catch (Exception e) 
		{
                    System.out.println("Exception in sleep in Robot Ctrlr");
		}
	}
  
  
	// =========================================================================
	// ===                          	========================================
	// ===		Mutator Methods	========================================
	// ===                          	========================================
	// =========================================================================
    

	public void setMotorSpeeds(int left, int right)
	{
		state.setMotorSpeeds(left, right);
	}


	public void setLeftMotorSpeed(int speed)
	{
		state.setLeftMotorSpeed(speed);
	}


	public void setRightMotorSpeed(int speed)
	{
		state.setRightMotorSpeed(speed);
	}

	
	public void setArmState(int armState)
	{
		state.setArmState(armState);			
	}


	public void setGripperState(int gripState)
	{
		state.setGripperState(gripState);
	}



	
	// =========================================================================
	// ===                          	========================================
	// ===		Accessor Methods		========================================
	// ===                          	========================================
	// =========================================================================
    
		
	public int getArmState()
	{
		return state.getArmState();
	}

	
	public int getGripperState()
	{
		return state.getGripperState();
	}

		
	public boolean isObjectPresent()
	{
		if((new Date().getTime() - state.objectTimeStamp) >= 200)
		{
			return state.isObjectPresent();
		}
		else
			return state.checkObjectPresent();
		/*long temp;
		if((temp = new Date().getTime() - state.objectTimeStamp) >= 200)
		{
			System.out.println("objtemp is " + temp);	
			return state.isObjectPresent();
		}
		else
			return state.checkObjectPresent();*/
	}

	
	public boolean isObjectHeld()
	{
		return state.isObjectHeld();
	}

	
	public int getLightValue(int sensorID)
	{
		if((new Date().getTime() - state.lightTimeStamp) >= 200)
		{
			state.lightSensorRead();
			return state.sensors[sensorID].getLightValue();

		}
		else
			return state.sensors[sensorID].getLightValue();
		/*long temp;
		if((temp = new Date().getTime() - state.lightTimeStamp) >= 200)
		{
			System.out.println("lighttemp is " + temp);
			state.lightSensorRead();
			return state.sensors[sensorID].getLightValue();

		}
		else
			return state.sensors[sensorID].getLightValue();*/
	}

	
	public int getDistanceValue(int sensorID)
	{
		if((new Date().getTime() - state.distTimeStamp) >= 200)
		{
			state.distSensorRead();
			return state.sensors[sensorID].getDistValue();
		}
		else
			return state.sensors[sensorID].getDistValue();
		/*long temp;
		if((temp = new Date().getTime() - state.distTimeStamp) >= 200)
		{
			System.out.println("disttemp is " + temp);	
			state.distSensorRead();
			return state.sensors[sensorID].getDistValue();
		}
		else
			return state.sensors[sensorID].getDistValue();*/
	}

	
	public int getResistivity()
	{
		return state.getResistivity();
	}

	
	public long getRightWheelPosition()
	{
		return state.getRightPosition();
	}

	
	public long getLeftWheelPosition()
	{
		return state.getLeftPosition();
	}


} // RobotController
