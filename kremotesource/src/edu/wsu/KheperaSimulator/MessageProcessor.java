package edu.wsu.KheperaSimulator;
import java.util.*;
/*
 * MessageProcessor.java
 *
 * Created on July 20, 2001, 2:00 PM
 */

/**
 *
 * @author  steve
 * @version 
 */

public class MessageProcessor 
{
    
    private Sensor[] sensors;
    private Motor  motors;
    private String message;
    private boolean messageValid;
    
   
    public MessageProcessor(Sensor[] s)
    {
     	//sensors = new Sensor[8];
	//initSensors();
	sensors = s;
        
    }

    public MessageProcessor()
    {
        
    }

    private void initSensors()
    {
        for(int i = 0;i < 8;i++)
            sensors[i] = new Sensor();
    }


    public Sensor[] processSensorArray(String _message)
    {
        message = new String(_message);
   	  if ( ClientConfiguration.DEBUG ) System.out.println("posted sensor array: " + message);
        int len = message.length();
        char type = message.charAt(0);
        //String clip = message.substring(0, len-1); I have changed this coz substring needs len
        String clip = message.substring(0, len);
        StringTokenizer tokens = new StringTokenizer(clip, ",");
        
        int i = 0;
        while(tokens.hasMoreTokens())
		{
            String next = new String(tokens.nextToken());
            if(next.equals("n") || next.equals("o"))
                continue;
            try {
                if(type == 'n'){
                    sensors[i].setDistValue(Integer.parseInt(next));}
                else
                    sensors[i].setLightValue(Integer.parseInt(next));
            } catch (NumberFormatException e) {
                //System.err.println("String->Int: at []=="+ i);
			} catch (NullPointerException e1) {
                //System.err.println("String->Int: at []=="+ i);
				System.out.println("MPSA Null pointer exception");
            }

            i++;
        }
		return sensors;
    }

     
    public boolean processObjPresent(String _message)
    {
        message = new String(_message);
    	if ( ClientConfiguration.DEBUG ) System.out.println("posted object present: " + message);
    	
        int len = message.length();

		if( len < 6 )
			return false;

        char temp = message.charAt(6);

        if(temp == '0')
			return false;
        else
			return true;
    }
    
    
    public int processResistivity(String _message)
    {
    	if ( ClientConfiguration.DEBUG ) System.out.println("posted resistivity: " + message);
    	
        int len = message.length();
        Integer tempIntVal = new Integer(0);
        
        tempIntVal = Integer.valueOf(message.substring(2,len-1));
        return tempIntVal.intValue();
    }
    
}
