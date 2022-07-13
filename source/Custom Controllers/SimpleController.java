import edu.wsu.KheperaSimulator.RobotController;
import edu.wsu.KheperaSimulator.KSGripperStates;


public class SimpleController extends RobotController{

    private int lastLightDist = 10000;
    private int dir = 1;

	public SimpleController() 
	{
        setWaitTime(5);
	}
	

	public void doWork() throws Exception
	{
        //int lightDistance1 = getLightValue(1) / 100;
        //int lightDistance2 = getLightValue(2) / 100;
        //int brightness1;
        //int brightness2;
        //if (lightDistance1 == 0)
            //brightness1 = 9;
        //else
           //brightness1 = 9 / (lightDistance1);

        //if (lightDistance2 == 0)
            //brightness2 = 9;
        //else
            //brightness2 = 9 / (lightDistance2);

        //if (brightness1 < 1)
            //setLeftMotorSpeed(1);
        //else if (brightness1 < 9)
            //setLeftMotorSpeed(brightness1);
        //else
            //setLeftMotorSpeed(9);

        //if (brightness2 < 1)
            //setRightMotorSpeed(1);
        //else if (brightness2 < 9)
            //setRightMotorSpeed(brightness2);
        //else
            //setRightMotorSpeed(9);
	}


	public void close() throws Exception
	{
	}
}
