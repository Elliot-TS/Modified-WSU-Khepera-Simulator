# Modified-WSU-Khepera-Simulator
A slightly modified version of the WSU Khepera Robot Simulator with custom controllers that implement the first four vehicles in Braitenburg's Vehicles.

# Installation and Running

In order to run this program, you must [install Java 18|https://www.codejava.net/java-se/install-oracle-jdk-18-on-windows].  Then download this repository, and run the ´run.sh´ script to start the program.

# Differences between Khepera Robot and Braitenburg Vehicles 
The Khepera Robot has eight light and distance sensors arranged around the robot.  Two look forward, two sideways, two backward, and two diagonally forward.  In order to simulate the two omnidirectional light sensors in the Braitenburg Vehicles, I average the light value from each of the sensors on each side of the robot.

As a result, whereas in the Braitenburg vehicles, both sensors will be activated even if the light source is on one side of the robot, in this implementation of the vehicles, only the sensors on one side will be activated.  In addition, the robot has a blind spot diagonally behind it that can cause the vehicle to slow down at unexpected times (this is most evident in vehicle 1).

In the WSU Khepera Robot Simulator, whenever the robot crashes into an object, the simulation stops.  As a result, to prevent the simulation from constantly crashing, each vehicle is programmed to overwrite its default behavior if it comes too close to a wall and to turn 180 degrees, though it sometimes overshoots the 180 degrees slightly.  This doesn't fix everything as the robot can still get stuck or accidentally run into walls sometimes, but it generally does a decent job at keeping the robot from crashing so that its behaviors can be observed for a longer amount of time.

# Explanation of Each Vehicle

##Vehicle 1

This vehicle has a single light sensor connected to both motors.  The brighter the light, the faster the robot goes.

##Vehicle 2a

This vehicle has two light sensors parallel-connected to each motor -- the left sensor to the left motor, and the right sensor to the right motor.

##Vehicle 2b

This vehicle is similar to 2a, but the sensors are cross-connected to each motor -- the left sensor to the right motor, and vice versa.

##Vehicle 3a

Similar to 2a, but the speed of the motors is inversely proportional to the brightness of the light.

##Vehicle 3b

Similar to 3a, but the wires are cross-connected.

##Vehicle 3c

Similar to 3a, but with added distance sensors on each side cross-connected to the motors.  The activation of the light sensors is added to the activation of the distance sensors.

##Vehicle 3d

Similar to 3c, but the light sensors are also cross-connected.

## Vehicle 4a

Similar to 3a, but now the activation of the sensors goes through an activation function to determine the speed of the motors.  In this case, the activation function is a bell curve with μ = 7, σ = 4, and then scaled by a factor of 90.  Thus, when the sensor activation is 0, the motor speed is about 2; when the activation is 7, the motor reaches its maximum speed of about 9, and wen the activation is 20, the motor speed is about 0.

## Vehicle 4b

Similar to 4a, but with cross connections.

## Vehicle 4c

Similar to 4a, but with cross-connected distance sensors in addition to the light sensors.

## Vehicle 4d

Similar to 4b, but with cross-connected distance sensors in addition to the light sensors.

