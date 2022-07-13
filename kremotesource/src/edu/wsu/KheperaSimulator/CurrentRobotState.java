/*
 * CurrentRobotState.java
 *
 * Created on July 27, 2001, 11:09 AM
 */

package edu.wsu.KheperaSimulator;
import java.util.Date;
/**
 *
 * @author  aarti
 * @version 
 */

public class CurrentRobotState
{
    public Sensor[] sensors;
    private int armState, gripperState;
    private Motor motor;
    private int resistivity;
    private boolean objPresent;
    //public boolean obPresent;
    private boolean objHeld;
    //public int resis;
    public long distTimeStamp;
    public long lightTimeStamp;
    public long objectTimeStamp;

    //Commands to the khepera	
    private String motorCommand;
    private String armUp;
    private String armDown;
    private String gripOpen;
    private String gripClosed;
    public String cmd;
	
    //Commands for the sensors
    public String distSensors;
    public String lightSensors;
    public String objectSensors;

    //Check if there is some write operation is going on
    public boolean pendingCmd;

    public int sessionStatus;
    public String waitTime;
    
    private int lStuck;
    private int rStuck;
    private long lPos;
    private long rPos;

    private static final int STUCK_LIMIT = 20;
	
    CurrentRobotState()
	{
            sessionStatus = ClientConfiguration.WAITING;
            waitTime = "in ----";
	
            rStuck = 0;
            lStuck = 0;
            lPos = 0;
            rPos = 0;
       
            armState = KSGripperStates.ARM_UP;
            gripperState = KSGripperStates.GRIP_CLOSED;
            resistivity = 0;
            objPresent = false;
            //obPresent = false;
            objHeld = false;
            pendingCmd = false;
            motor = new Motor();
            sensors = new Sensor[8];
		
            // The khepera commands
            motorCommand = "D";				
            armUp = "T,1,E,180";
            armDown = "T,1,E,249";
            gripOpen = "T,1,D,0";
            gripClosed = "T,1,D,1";
		
            //Sensor commands
            distSensors = "N";
            lightSensors = "O";
            objectSensors = "T,1,G";
			
		distTimeStamp = 0;
		lightTimeStamp = 0;
		objectTimeStamp = 0;
  
            initSensors();
	}

    private void initSensors()
    {
        for(int i = 0;i < 8;i++)
            sensors[i] = new Sensor();
    }

	// =========================================================================
	// ===                          	========================================
	// ===		Mutator Methods			========================================
	// ===                          	========================================
	// =========================================================================
    
    
	public void setArmState(int arm)
        { 
		if( arm == KSGripperStates.ARM_UP )
		{
			//Am avoiding repetitions of commands if the current arm state and the new arm states are the same
			if ( arm != armState )
			{
				armState = arm;
				cmd = armUp;
				pendingCmd = true;
				while(pendingCmd==true){}
			}
		}
		
		else if( arm == KSGripperStates.ARM_DOWN && arm != armState )
		{
			//Am avoiding repetitions of commands if the current arm state and the new arm states are the same
			if ( arm != armState )
			{
				armState = arm;
				cmd = armDown;
				pendingCmd = true;			
				while(pendingCmd==true){}
			}
		}
		
		else
		{
			System.out.println("An illegal arm state was used.  Check the");
			System.out.println("controller source code to make sure you're");
			System.out.println("doing things properly.\n");
			
			System.out.println("Session ended.");
			
			System.exit(0);
		}
        }
      
    public void setGripperState(int grip) 
    { 
        if( grip == KSGripperStates.GRIP_OPEN )
        {
		if(grip != gripperState)
		{
			gripperState = grip;
			cmd = gripOpen;
			pendingCmd = true;
			while(pendingCmd==true){}
		}
	  }
        else if ( grip == KSGripperStates.GRIP_CLOSED )
	  {
		if(grip != gripperState)
		{
			gripperState = grip;
			cmd = gripClosed;
			pendingCmd = true;			
			while(pendingCmd==true){}
		}
	  }
	  else
	  {
		System.out.println("An illegal gripper state was used.  Check the");
		System.out.println("controller source code to make sure you're");
		System.out.println("doing things properly.\n");
		System.out.println("Session ended.");
		
		System.exit(0);	
	  }
    }


    public void setMotorSpeeds( int left, int right )
    {
    		motor.setMotorSpeeds( left, right );
		cmd = motorCommand + "," + getMotorState().leftSpeed + "," + getMotorState().rightSpeed;
		pendingCmd = true;
		while(pendingCmd==true){}		
	}
	
	
	public void setLeftMotorSpeed( int speed )
	{
		motor.setLeftMotorSpeed( speed );
		cmd = motorCommand + "," + getMotorState().leftSpeed + "," + getMotorState().rightSpeed;
		pendingCmd = true;
		while(pendingCmd==true){}
	}
	
	
	public void setRightMotorSpeed( int speed )
	{
		motor.setRightMotorSpeed( speed );
		cmd = motorCommand + "," + getMotorState().leftSpeed + "," + getMotorState().rightSpeed;
		pendingCmd = true;		
		while(pendingCmd==true){}
	}
    
    public void setMotorPositions( long left, long right )
    {
    	motor.setMotorPositions( left, right );
    }
    
    public void postSensorValues(Sensor[] s)
    {
    	sensors = s;
    }
  
    public void postResistivity(int rVal)
    {
    	resistivity = rVal;	
    }
    
    public void postObjectPresent(boolean objPresent)
    { 
        this.objPresent = objPresent;
    }

    public void distSensorRead()
    {
	cmd = distSensors;
	pendingCmd = true;		
	while(pendingCmd==true){}
    }

    public void lightSensorRead()
    {
	cmd = lightSensors;
	pendingCmd = true;		
	while(pendingCmd==true){}
    }
	
//	 ===	End of Mutator Methods		========================================
	
	// =========================================================================
	// ===                          	========================================
	// ===		Accessor Methods		========================================
	// ===                          	========================================
	// =========================================================================
    
    public long getLeftPosition()
    {
    	return motor.leftPosition;
    }
        
    public long getRightPosition()
    {
    	return motor.rightPosition;
    }
    
    public boolean isStuck()
    {
		if( motor.leftSpeed != 0 )
		{
			if( lPos == motor.leftPosition )
				lStuck++;
			else
				lStuck = 0;
		}

		if( motor.rightSpeed != 0 )
		{
			if( rPos == motor.rightPosition )
				rStuck++;
			else
				rStuck = 0;
		}


		lPos = motor.leftPosition;
		rPos = motor.rightPosition;

		if( rStuck > STUCK_LIMIT || lStuck > STUCK_LIMIT )
			return true;

		else
			return false;
    }


    public int getArmState() 
    {
    	return armState; 
    }
       
    
    public int getGripperState()
    {
    	return gripperState; 
    }
    
    
    public Motor getMotor()
    {
    	return motor; 
    }
    
    
    public Motor getMotorState()
    {
    	return motor; 
    }
    
    
    public Sensor[] getSensorValues()
    {
    	return sensors;
    }


	public int getResistivity()
    {
    	return resistivity;
    } 
   

 	public boolean isObjectPresent()
    {
	cmd = objectSensors;
	pendingCmd = true;		
	while(pendingCmd==true){}
	return objPresent;
    }
	public boolean checkObjectPresent()
	{
		return objPresent;
	}
   
	public boolean isObjectHeld()
    {
    	return objHeld;
    }
	
}
    
//	 ===	End of Accessor Methods		=============================
	
