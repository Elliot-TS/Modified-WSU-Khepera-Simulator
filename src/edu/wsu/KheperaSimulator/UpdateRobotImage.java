/**
 * @(#)UpdateBufferedWorld.java 1.1 2001/09/15
 *
 */

package edu.wsu.KheperaSimulator;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * An <code>UpdateRobotImage</code> class provides the functionality to
 * update the robots image.
 */
public class UpdateRobotImage {
  private CurrentRobotState rState;
  private RobotCoordinates rCoords;
  private Image robotAU, robotAD, robotADGO, robotADGC;
  protected boolean lvt = false;
  protected boolean sensor = false;

  /**
   * Allocates a new <code>UpdateRobotImage</code> to provide the ablility to
   * update the robots image.
   * @param rState the current robot state
   */
  public UpdateRobotImage(CurrentRobotState rState) {
    robotAU = Toolkit.getDefaultToolkit().getImage("images/robot_au.gif");
    robotAD = Toolkit.getDefaultToolkit().getImage("images/robot_ad.gif");
    robotADGO = Toolkit.getDefaultToolkit().getImage("images/robot_ad-go.gif");
    robotADGC = Toolkit.getDefaultToolkit().getImage("images/robot_ad-gc.gif");
    this.rState = rState;
    this.rCoords = rState.getRobotCoordinates();
  }

  /**
   * Update the robot image based on the current status of the robot.
   * @param g2 graphic context to draw in
   * @see java.awt.Graphics2D
   */
  public void updateRobotImage(Graphics2D g2) {
	  if(lvt)
	  {
		  g2.setColor(Color.black);
		  g2.draw(new Line2D.Float(rCoords.lvtPoints[0].x,rCoords.lvtPoints[0].y,rCoords.lvtPoints[1].x,rCoords.lvtPoints[1].y));
		  g2.draw(new Line2D.Float(rCoords.lvtPoints[1].x,rCoords.lvtPoints[1].y,rCoords.lvtPoints[3].x,rCoords.lvtPoints[3].y));
		  g2.draw(new Line2D.Float(rCoords.lvtPoints[3].x,rCoords.lvtPoints[3].y,rCoords.lvtPoints[2].x,rCoords.lvtPoints[2].y));
		  g2.draw(new Line2D.Float(rCoords.lvtPoints[2].x,rCoords.lvtPoints[2].y,rCoords.lvtPoints[0].x,rCoords.lvtPoints[0].y));
	  }
	  if(sensor)
	  {
		  g2.setColor(Color.cyan);
		  /* g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(0),rState.getSensorCoord(1)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(2),rState.getSensorCoord(3)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(4),rState.getSensorCoord(5)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(6),rState.getSensorCoord(7)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(8),rState.getSensorCoord(9)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(10),rState.getSensorCoord(11)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(12),rState.getSensorCoord(13)));
		   g2.draw(new Line2D.Float(rState.getRobotCoordinates().x + 13,rState.getRobotCoordinates().y + 13,rState.getSensorCoord(14),rState.getSensorCoord(15)));*/
		  g2.draw(new Line2D.Float(rState.getSensorCoord(0),rState.getSensorCoord(1),rState.getSensorCoord(2),rState.getSensorCoord(3)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(4),rState.getSensorCoord(5),rState.getSensorCoord(6),rState.getSensorCoord(7)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(8),rState.getSensorCoord(9),rState.getSensorCoord(10),rState.getSensorCoord(11)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(12),rState.getSensorCoord(13),rState.getSensorCoord(14),rState.getSensorCoord(15)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(16),rState.getSensorCoord(17),rState.getSensorCoord(18),rState.getSensorCoord(19)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(20),rState.getSensorCoord(21),rState.getSensorCoord(22),rState.getSensorCoord(23)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(24),rState.getSensorCoord(25),rState.getSensorCoord(26),rState.getSensorCoord(27)));
		  g2.draw(new Line2D.Float(rState.getSensorCoord(28),rState.getSensorCoord(29),rState.getSensorCoord(30),rState.getSensorCoord(31)));
	  }
	  g2.rotate(rCoords.alpha, rCoords.dx+13.0, rCoords.dy+13.0);
	  g2.drawImage(robotAU, rCoords.x, rCoords.y, null);
	  if(rState.getArmState() == KSGripperStates.ARM_DOWN) {
		  g2.drawImage(robotAD, rCoords.x, rCoords.y, null);
      if(rState.getGripperState() == KSGripperStates.GRIP_CLOSED) {
    	  g2.drawImage(robotADGC, rCoords.x, rCoords.y,null);
      }
      else if(rState.getGripperState() == KSGripperStates.GRIP_OPEN) {
        g2.drawImage(robotADGO, rCoords.x, rCoords.y,null);
      }
    }
	  g2.setTransform(new AffineTransform());
  } // updateRobotImage
} // UpdateBufferedWorld
