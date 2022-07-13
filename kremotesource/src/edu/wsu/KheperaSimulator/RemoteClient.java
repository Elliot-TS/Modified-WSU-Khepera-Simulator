/*
 *		RemoteClient.java
 *
 *		Created on 04 JAN 2005 by Duane Bolick
 *		ver 1.0
 *
 *		This is the class that does everything.  The main method (located in
 *		ClientMain.java) creates a RemoteClient object, and then calls its
 *		go() method, which is where all the magic happens.  This app uses
 *		multiple threads of execution to do its job.
 */

package edu.wsu.KheperaSimulator;
import java.io.*;
import java.net.*;


public class RemoteClient
{
	// Networking stuff
	private String ip;
	private int port;
	private Socket socket;
	public BufferedReader reader;
	public PrintWriter writer;

	// Utility stuff
    private RobotControllerLoader loader;
    private String name;
    private String webcamURL;
    private ClientConfiguration config;

  	private RobotController controller;
  
  	// Threads for the runnables
	private Thread controllerThread;
	
	// Intervals for the threads
	private long controllerThreadTimeout;
	private long waitThreadTimeout = 5;
	
	private CurrentRobotState state;
	private Thread stateThread;


	// =========================================================================
	// ===                          ============================================
	// ===		Public Methods		============================================
	// ===                          ============================================
	// =========================================================================
    

	public RemoteClient()
	{
		config = new ClientConfiguration();
		
		ip = config.IP;
		port = config.PORT;	
		webcamURL = config.WEBCAM_URL;
		
		controllerThreadTimeout = config.CONTROLLER_TIMEOUT;
	
		loader = new RobotControllerLoader( config.PATH );
	
		controller = null;

		state = new CurrentRobotState();//sets the default values in the CRS
	}

   
   	public void go()
	{
		printIntro();
		
		while( controller == null )
		{
			name = getControllerName();
			controller = loader.getController(name);
		}
		
		connectToServer();
		//REMOVE LATER	
		//waitForTurn();

		// Start the controller thread
		controller.initialize( name, controllerThreadTimeout, reader, writer, state);
		controllerThread = new Thread( controller );
		controllerThread.start();

		//REMOVE LATER	
		waitForTurn();
			
		while( state.sessionStatus == ClientConfiguration.RUNNING )
		{
			try
			{
				Thread.sleep(1000);
			}
			
			catch(Exception e)
			{
		
			}
		}

		cleanup();

		return;
	}
   
	// =========================================================================
	// ===                          ============================================
	// ===		Private Methods		============================================
	// ===                          ============================================
	// =========================================================================
    
    
    private void cleanup()
    {
    	if( state.sessionStatus == ClientConfiguration.TIMEOUT )
		{
			System.out.println("Your session time limit is up.");
		}
		
		else if( state.sessionStatus == ClientConfiguration.BROKEN )
		{
			System.out.println("The connection was lost.");
		}
		
		else if( state.sessionStatus == ClientConfiguration.STUCK )
		{
			System.out.println("The Khepera is stuck.");
			System.out.println("Ending run.");
		}
    }
    
    private void printIntro() 
    {
    	System.out.println("WSU Khepera Remote Interface\nPress CTRL-C to exit\n");
    	//System.out.println("Webcam URL: " + webcamURL + "\n");
    }
        
   private void waitForTurn()
    {
    	int lastWaitTimeLength = 0;
		    	
    	System.out.print("Your turn ");
    	
    	while( state.sessionStatus == ClientConfiguration.WAITING || state.sessionStatus == ClientConfiguration.STUCK ) 
		{	
			// need to wait for a bit here so state can update
			pause(waitThreadTimeout*4L);
			
			for( int i = 0 ; i < lastWaitTimeLength ; i++ )
			{
				System.out.print("\b");
				System.out.print(" ");
				System.out.print("\b");
			}
			
			System.out.print(state.waitTime);
			lastWaitTimeLength = state.waitTime.length();
		}
		
		System.out.println();
    }
    
    private void connectToServer()
	{
		try
		{
			socket = new Socket(ip, port);
			InputStreamReader iStream  = new InputStreamReader(socket.getInputStream());
			OutputStreamWriter oStream  = new OutputStreamWriter(socket.getOutputStream());;
			
			reader = new BufferedReader(iStream);
			writer = new PrintWriter(oStream);
		}
		
		catch(IOException ex)
		{
			System.out.println("Server Unavailable\n");
			System.exit(0);
		}
	}
       
 
	private String getControllerName()
    {
    	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		String input = null;
		String cName = null;
		String[] controllerNames;
		
        boolean done = false;
        int length;
        
	  	Integer i = new Integer(-9);
	  	
	  	
	  	controllerNames = loader.getControllerNames();
    	length = controllerNames.length - 1;
	  
	  	System.out.println("Please type the number for the Controller you wish to run");
	  
	  	for(int x = 0; x < controllerNames.length; x++)
	  	{
			System.out.println(x+"  "+controllerNames[x]);
	  	}
	  
	  	while(!done)
	  	{
	   		try
	   		{
                input = in.readLine();
		    	try
		    	{
					i = new Integer(input);
				}
		    
		    	catch (NumberFormatException e)
		    	{
                	System.out.println("Input was not a number");
                }
		    
		    	if((i.intValue() < controllerNames.length) && (i.intValue() >= 0))
		    	{
					cName = controllerNames[i.intValue()];
					done = true;
		    	}
		    
		    	else
					System.out.println("Please choose a number between 0 and "+length);
            }
            
            catch (IOException e) 
            {
            	System.out.println("Please choose a number between 0 and "+length);
		    	input = null;
            }	
            
    	}//end of while
	  
	  	
	  	return cName;
	}
	  
	  
	private void pause( long timeout )
	{
		try
		{
			Thread.sleep( timeout );
		}
		
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

  
}
