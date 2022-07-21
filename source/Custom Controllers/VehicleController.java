package edu.wsu.KheperaSimulator;
import edu.wsu.KheperaSimulator.*;
import java.io.*;


public class VehicleController extends RobotController{

    private double bounceAngle;
    private boolean bouncing;
    private boolean nearWall;
    private double[][] foodSources; // Array of {x, y, strength}
	public VehicleController() 
	{
        bouncing = false;
        nearWall = false;
        foodSources = new double[][]{ {300, 200, 50000} };
	}

    int[] getLeftWheelPos(int dist)
    {
        double angle = getRobotAngle();

        int x = getRobotXPosition();
        int y = getRobotYPosition();
        
        // Rotate (0, -1) by the angle and add the position
        int pos[] = {(int)( dist * Math.sin(angle)) + x,
                     (int)(-dist * Math.cos(angle)) + y };
        return pos;
    }
    int[] getRightWheelPos(int dist)
    {
        double angle = getRobotAngle();

        int x = getRobotXPosition();
        int y = getRobotYPosition();
        
        // Rotate (0, 0.5) by the angle and add the position
        int pos[] = {(int)(-dist * Math.sin(angle)) + x,
                     (int)( dist * Math.cos(angle)) + y };
        return pos;
    }

    // Get the distance to the light source from a given sensor
    public double getLightDistance(int index) throws Exception
    {
        // Light Value is
        // log_2(x/a) * 100 where x is the distance to the light, and a is a constant
        double a = 1; // An arbitrary constant
        return (a * Math.pow(2, getLightValue(index) / 100.0));
    }

    // Get the brightness of the light source at a given sensor
    public double getLightBrightness(int index) throws Exception
    {
        double a = 1000; // Brightness at distance 1
        return a / Math.pow(getLightDistance(index), 2);
    }

    // Get Strength of Smell from Food Source
    // side: 0 for right sensor, 1 for left sensor
    // source: index of food source to smell
    public double getFoodSmell(int side, int source)
    {
        int sensorPos[];
        int eye_dist = 5;
        if (side == 0) {
            sensorPos = getLeftWheelPos(eye_dist);
        }
        else {
            sensorPos = getRightWheelPos(eye_dist);
        }
        double foodPos[] = foodSources[source];

        double sqrDist = Math.pow(sensorPos[0]-foodPos[0], 2) +
                         Math.pow(sensorPos[1]-foodPos[1], 2);

        return foodPos[2] / sqrDist;
    }

    // Avoid Walls
    private boolean bounceOffWall()
    {
        double robotAngle = getRobotAngle();
        double dAngle = (robotAngle - bounceAngle);
        if (dAngle < 0 && -dAngle > bounceAngle)
            dAngle += 2 * Math.PI;

        if (dAngle < Math.PI - 0.01)
        {
            setMotorSpeeds(5,-5);
            return false;
        }
        else return true;
    }
    
    public int getTotalDistance()
    {
        return Math.max(
                getDistanceValue(0),
                Math.max(
                    getDistanceValue(1),
                    Math.max(
                        getDistanceValue(2),
                        Math.max(
                            getDistanceValue(3),
                            Math.max(
                                getDistanceValue(4),
                                Math.max(
                                    getDistanceValue(5),
                                    Math.max(
                                        getDistanceValue(6),
                                        getDistanceValue(7))
                ))))));
    }

    // Motor Activation Function
    public double motorActivation(double x) throws Exception
    {
        double scale = 90;
        double sigma = 4;
        double mu = 7;
        double denom = sigma * Math.sqrt(2 * Math.PI);
        double exp = -1.0/2.0 * Math.pow((x - mu) / sigma, 2);
        return scale * Math.pow(Math.E, exp) / denom; 
        //return x;
    }

    // Standard rules for the vehicle to follow
    public void runVehicle() throws Exception
	{
        double brightness1 = (getLightBrightness(7) +
                              getLightBrightness(0) +
                              getLightBrightness(1) +
                              getLightBrightness(2)) / 4.0;
        double brightness2 = (getLightBrightness(3) +
                              getLightBrightness(4) +
                              getLightBrightness(5) +
                              getLightBrightness(6)) / 4.0;

        brightness1 += getFoodSmell(0, 0);
        brightness2 += getFoodSmell(1, 0);

        brightness1 /= 3;
        brightness2 /= 3;

        double motor1Speed = motorActivation(brightness1);
        double motor2Speed = motorActivation(brightness2);

        // constrain motorSpeed to [-9, 9]
        motor1Speed = motor1Speed < -9.0 ? -9.0 : motor1Speed > 9.0 ? 9.0 : motor1Speed; 
        motor2Speed = motor2Speed < -9.0 ? -9.0 : motor2Speed > 9.0 ? 9.0 : motor2Speed; 

        //setMotorSpeeds((int)motor1Speed, (int)motor2Speed);
        setMotorSpeeds((int)motor2Speed, (int)motor1Speed);
	}

    public void doWork() throws Exception
	{
        double dist = getTotalDistance();
        int dist_limit = 700;

        if (dist > dist_limit&& !nearWall)
        {
            bounceAngle = getRobotAngle();
            nearWall = true;
        }

        if (nearWall)
            bouncing = !bounceOffWall();

        if (!bouncing)
            runVehicle();

        if (!bouncing && getTotalDistance() <= dist_limit)
            nearWall = false;
	}

	public void close() throws Exception
	{
	}
}
