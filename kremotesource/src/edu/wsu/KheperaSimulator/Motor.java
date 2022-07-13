/*
 * Motor.java
 *
 * Created on July 22, 2001, 11:42 AM
 */

package edu.wsu.KheperaSimulator;

/**
 *
 * @author  steve
 * @version 
 */
public class Motor {
    public int  leftSpeed, rightSpeed;
    public long leftPosition, rightPosition;

    /** Creates new Motor */
    public Motor() {
        leftSpeed  = 0;
        rightSpeed = 0;
        leftPosition  = 0L;
        rightPosition = 0L;
    }
    
    public Motor(int lSpeed, int rSpeed, long lPos, long rPos) {
        leftSpeed  = lSpeed;
        rightSpeed = rSpeed;
        leftPosition  = lPos;
        rightPosition = rPos;
    }
    
    public long getLeftPosition() {
        return leftPosition;
    }
    
    public long getRightPosition() {
        return rightPosition;
    }
    
    public synchronized void setMotorSpeeds(int lSpeed, int rSpeed) {
        if(lSpeed < 10 && lSpeed > -10) // was ||
            leftSpeed = lSpeed;
        if(rSpeed < 10 && rSpeed > -10) // was ||
            rightSpeed = rSpeed;
    }
    
    //the following two functions were added by Tom on 3-10-03 for compatibiltiy with simulator update
    public void setLeftMotorSpeed(int speed) {
        this.setMotorSpeeds(speed,11);
    }
    
    public void setRightMotorSpeed(int speed) {
        this.setMotorSpeeds(11,speed);
    }
    
    public void setMotorPositions(long lPos, long rPos) {
        if(lPos > Long.MAX_VALUE || lPos < Long.MIN_VALUE) 
            leftPosition = 0L;
        else
            leftPosition = lPos;
        if(rPos > Long.MAX_VALUE || rPos < Long.MIN_VALUE)
            rightPosition = 0L;
        else
            rightPosition = rPos;
    }
}
