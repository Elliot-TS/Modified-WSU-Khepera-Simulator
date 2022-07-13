import edu.wsu.KheperaSimulator.VehicleController;
import java.io.*;


public class Vehicle1 extends VehicleController{

	public Vehicle1 () 
	{
	}

    // Motor Activation Function
    public double motorActivation(double x) throws Exception
    {
        return x;
    }

    // Standard rules for the vehicle to follow
    public void runVehicle() throws Exception
	{
        double brightness = (getLightBrightness(7) +
                             getLightBrightness(0) +
                             getLightBrightness(1) +
                             getLightBrightness(2) +
                             getLightBrightness(3) +
                             getLightBrightness(4) +
                             getLightBrightness(5) +
                             getLightBrightness(6)) / 8.0;

        double motorSpeed = motorActivation(brightness);

        // constrain motorSpeed to [-9, 9]
        motorSpeed = motorSpeed < -9.0 ? -9.0 : motorSpeed > 9.0 ? 9.0 : motorSpeed; 

        //setMotorSpeeds((int)motor1Speed, (int)motor2Speed);
        setMotorSpeeds((int)motorSpeed, (int)motorSpeed);
	}

	public void close() throws Exception
	{
	}
}
