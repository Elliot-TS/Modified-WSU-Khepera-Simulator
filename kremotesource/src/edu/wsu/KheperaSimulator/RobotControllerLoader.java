/*
 *		RobotControllerLoader
 *
 *		Modified from RobotControllerDirector class (which was created by
 *		Steve Perretta) by Duane Bolick, 07 JAN 2005
 *
 *
 *		This is used to get a RobotController.  When it's created, This 
 *		object locates all available controller class files within the 
 *		directory specified	in its constructor and creates a list of names
 *		of all the .class files in the given directory.
 *
 *		Once that's done, you can get the list of controller names using
 *		getControllerNames, which will return a String array containing the
 *		names.
 *
 *		Once you've decided which controller you want, you can use the
 *		getController method, which will return a reference to a RobotController
 *		object for your own personal use.
 *
 *		You can even change directories with the changePath method - just give 
 *		it a new path, and the RobotControllerLoader will look there, instead.
 *		
 */

package edu.wsu.KheperaSimulator;

import java.io.*;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;


public class RobotControllerLoader 
{

	private String path;
  	private String[] controllerNames;


	public RobotControllerLoader(String _path) 
	{
            path = _path;
            findControllers();
  	}


	public String[] getControllerNames()
	{
            findControllers();
            return controllerNames;
  	}


	public RobotController getController(String name)
  	{
  		System.out.println("Loading " + name + "...");
		
    	try
    	{
      		Class c = Class.forName(name, true, new DirectoryClassLoader(path));
      		return (RobotController)c.newInstance();
	   	}
    	
    	catch (java.lang.ClassNotFoundException ex) 
    	{
      		JOptionPane.showMessageDialog(null,
                                    "The module class could not be found",
                                    "Class not Found",
                                    JOptionPane.ERROR_MESSAGE);
    	}
    	
    	catch (Exception e) 
    	{
      		e.printStackTrace();
    	}
    
    	return null;
    }

	
	public void changePath(String _path)
	{
		path = _path;
		findControllers();
	}
	
	
	
	
	// =========================================================================
	// ===							============================================
	// ===		Private Methods		============================================
	// ===							============================================
	// =========================================================================
	
	
	private void findControllers() 
	{
    	try
    	{
      		File directory = new File(path);
      		controllerNames = null;
      		controllerNames = directory.list(new ClassFileFilter());
      		
      		if( controllerNames == null || controllerNames.length == 0 )
      		{
      			System.out.println("No controllers were found.");
      			System.out.println("They should be located in " + path + "\n");
      			System.exit(0);
      		}
      		
      		for(int i = 0; i < controllerNames.length; i++) 
      		{
        		controllerNames[i] = (new StringTokenizer(controllerNames[i], ".", false)).nextToken();
        	}
    	}
    	
    	catch (Exception e) 
    	{	
    		e.printStackTrace();
    	}
	}



} // RobotControllerDirector



/**
 * A <code>FilenameFilter</code> class used to filter a directory to only see
 * class files.
 */
class ClassFileFilter implements FilenameFilter 
{
  public boolean accept(File dir, String name) 
  {
    if ( name.indexOf('$') > -1 )
  		return false;
    
    StringTokenizer tokenizer = new StringTokenizer(name, ".", false);
    String ext = null;
    
    while (tokenizer.hasMoreTokens())
    {
      ext = tokenizer.nextToken();
    }
    if (ext.equals("class")) {
      return true;
    }
    return false;
  }
} // ClassFileFilter
