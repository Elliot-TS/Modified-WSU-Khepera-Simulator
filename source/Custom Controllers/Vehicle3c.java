import edu.wsu.KheperaSimulator.VehicleController;
import java.io.*;


public class Vehicle3c extends VehicleController{

	public Vehicle3c () 
	{
	}

    // Motor Activation Function
    public double motorActivation(double x) throws Exception
    {
        return 9.0 / x;
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

        double distance1 = (getDistanceValue(7) +
                            getDistanceValue(0) +
                            getDistanceValue(1) +
                            getDistanceValue(2)) / 4.0;
        double distance2 = (getDistanceValue(3) +
                            getDistanceValue(4) +
                            getDistanceValue(5) +
                            getDistanceValue(6)) / 4.0;


        double motor1Speed = motorActivation(brightness1 / 10.0 + distance2);
        double motor2Speed = motorActivation(brightness2 / 10.0 + distance1);

        // constrain motorSpeed to [-9, 9]
        motor1Speed = motor1Speed < -9.0 ? -9.0 : motor1Speed > 9.0 ? 9.0 : motor1Speed; 
        motor2Speed = motor2Speed < -9.0 ? -9.0 : motor2Speed > 9.0 ? 9.0 : motor2Speed; 

        //setMotorSpeeds((int)motor1Speed, (int)motor2Speed);
        setMotorSpeeds((int)motor1Speed, (int)motor2Speed);
	}

	public void close() throws Exception
	{
	}
}
