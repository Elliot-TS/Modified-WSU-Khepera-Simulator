/*
 * stateUpdater.java
 *
 * Created on Aug 23, 2005, 6.56 PM
 * aarti
 */

package edu.wsu.KheperaSimulator;
import java.io.*;
import java.util.Date;

public class stateUpdater extends Thread
{

	//Attributes
	private String inc;
	private String message;
	private String writeCmd;
	private String input;
	private CurrentRobotState state;

	//Reader and Writer 
	private BufferedReader reader;
	private PrintWriter writer;

	//Message Handling purposes
	private MessageProcessor messProc;
	
	//Original 
    	stateUpdater(BufferedReader r,PrintWriter w,CurrentRobotState _state)
	{
		setDaemon(true);
		reader = r;
		writer = w;
		state = _state;
		processStart();
	}

	public void processStart()
	{
		message = read();
		checkMessage();
		if(state.sessionStatus == ClientConfiguration.RUNNING)
			return;
		else
			processStart();
	}
	
	public void run()
	{
		while(true)
		{
			if(state.pendingCmd == true)
			{
				writeCmd = state.cmd;
				write(writeCmd);
				do{
				message = read();
				}while(!checkLen());
				checkMessage();
				state.pendingCmd=false;
			}
			try			
			{
				sleep(5);
			}
		
			catch( Exception e )
			{
				System.out.println("Exception in overall sleep of stateUpdater");
			}
		}
	}
	
	public boolean checkLen()
	{
		if ((message.length() <= 0) || (message == null))
			return false;
		else
			return true;
	}

	public void checkMessage()
	{
		try{
			switch(message.charAt(0))
	    	{
	    		case '#': processStatusMessage();
				    break;
	        	case 'n': messProc = new MessageProcessor(state.getSensorValues());
				    state.postSensorValues(messProc.processSensorArray(message));
				    state.distTimeStamp = new Date().getTime();
	        	          break;
	        	case 'o': messProc = new MessageProcessor(state.getSensorValues());
				    state.postSensorValues(messProc.processSensorArray(message));
				    state.lightTimeStamp = new Date().getTime();
	        	          break;
	        	case 't': messProc = new MessageProcessor();
				    state.postObjectPresent(messProc.processObjPresent(message));
				    state.objectTimeStamp = new Date().getTime();
		     	          break;
	        	case 'f': state.postResistivity(messProc.processResistivity(message));
	        	          break;
	    	}
		}
		catch (StringIndexOutOfBoundsException e)
		{	
			return;
		}
	}

//Method to process the status message

	public void processStatusMessage()
	{
	   	if( message.startsWith("#TIMEOUT") )
		{
			state.sessionStatus = ClientConfiguration.TIMEOUT;
			stop();			
		}
		
		else if( message.startsWith("#START") )
		{
			state.waitTime = "now";
			state.sessionStatus = ClientConfiguration.RUNNING;
		}
		
		else if( message.startsWith("#WAIT") )
		{
			state.waitTime = "in " + message.substring(6);
			state.sessionStatus = ClientConfiguration.WAITING;
		}
		
		else if( message.startsWith("#STUCK") )
		{
			state.waitTime = "must wait until the Khepera is unstuck";
			state.sessionStatus = ClientConfiguration.STUCK;
		}
	}

//Methods to read from and write into the socket	

  	public void write( String m )
   	{
   		if( ClientConfiguration.DEBUG ) System.out.println("commander writes: " + m);
		writer.println(m);
		writer.flush();
   	}
	
   	public String read()
   	{
   		try
   		{
   			inc = reader.readLine();
		}
   		
   		catch(Exception e)
   		{
   			state.sessionStatus = ClientConfiguration.BROKEN;
   			System.out.println("\nConnection Lost");
   			System.exit(0); 
		}
		
		//System.out.println("returning string " + inc);
   		return inc;
		
    }
    

}





