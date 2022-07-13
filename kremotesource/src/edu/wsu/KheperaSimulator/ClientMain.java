/*
 *	ClientMain.java
 *
 *	Created on 04 JAN 2005
 *	by Duane Bolick
 *
 *	ver 1.0
 *
 */


package edu.wsu.KheperaSimulator;


public class ClientMain 
{

    public ClientMain() 
    {
    }
   
    public static void main (String args[]) 
    {
        RemoteClient rc = new RemoteClient();
        rc.go();
		System.exit(0);
    }
}
