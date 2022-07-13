/**
 * @(#)RobotStatePanel.java 1.1 2001/07/26
 */

package edu.wsu.KheperaSimulator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * The <code>RobotStatePanel</code> is a GUI component that shows the current
 * status of the robot gripper, arm, and the object presence sensor. It might be
 * easier to change the implementation of this class so that the images are
 * loaded into JLabels instead of using the graphics API.
 *
 * @author  Steve Perretta
 */
public class RobotStatePanel extends JPanel {
  private String panel;
  private CurrentRobotState rState;
  private Image current;
  private Image state1;
  private Image state2;
  private int lastState;  // arm, gripper
  private boolean objPresent = false;
  private int panelType;
  /** Arm status panel type. */
  public static int T_ARM  = 0;
  /** Gripper status panel type. */
  public static int T_GRIP = 1;
  /** Object sensor status panel type. */
  public static int T_OBJ  = 2;

  /**
   * Allocate a new <code>RobotStatePanel</code> that shows the status of the
   * specified type.  The possible values for type can be:
   *    T_ARM,
   *    T_GRIP or
   *    T_OBJ
   * @param type the type of status
   */
  public RobotStatePanel(String type) {
    super();
    panel = type;
    if(panel.equals("arm")) {
      panelType = T_ARM;
      lastState = KSGripperStates.ARM_UP;
    }
    else if(panel.equals("grip")) {
      panelType = T_GRIP;
      lastState = KSGripperStates.GRIP_OPEN;
    }
    else if(panel.equals("obj")) {
      panelType = T_OBJ;
    }
    initialize(panel);
  }

  /**
   * @see javax.swing.JComponent
   * @see java.awt.Graphics
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g); //paint background

    //Draw image at its natural size first.
    g.drawImage(current, 0, 0, this);
  }

  /**
   * Initialize this GUI component and draw it.
   * @param type the type of status
   */
  private void initialize(String type) {
    setPreferredSize(new Dimension(40, 30));
    setLayout(new BorderLayout());
    setBackground(Color.white);

    if(type.equalsIgnoreCase("arm")) {
      state1 = Toolkit.getDefaultToolkit().getImage("images/armUp03.gif");
      state2 = Toolkit.getDefaultToolkit().getImage("images/armDown03.gif");
    }

    else if(type.equalsIgnoreCase("grip")) {
      state1 = Toolkit.getDefaultToolkit().getImage("images/gOpen03.gif");
      state2 = Toolkit.getDefaultToolkit().getImage("images/gClosed03.gif");
    }

    else if(type.equalsIgnoreCase("obj")) {
      state1 = Toolkit.getDefaultToolkit().getImage("images/gNoObj03.gif");
      state2 = Toolkit.getDefaultToolkit().getImage("images/gObj03.gif");
    }
    current = state1;
    repaint();
  }

  protected void setCurrentState(CurrentRobotState rs) {
    rState = rs;
    if(panelType == T_ARM)
      lastState = KSGripperStates.ARM_UP;
    else if(panelType == T_GRIP)
      lastState = KSGripperStates.GRIP_OPEN;

    current = state1;
    repaint();
  }

  /**
   * Update the current image of this status component.
   */
  protected void updateImage() {
    int tempState;

    if(panelType == T_ARM) {
      tempState = rState.getArmState();
      if(tempState != lastState) {
        if(tempState == KSGripperStates.ARM_UP)
          current = state1;
        else
          current = state2;
        lastState = tempState;
        repaint();
        return;
      }
    }

    if(panelType == T_GRIP) {
      tempState = rState.getGripperState();
      if(tempState != lastState) {
        if(tempState == KSGripperStates.GRIP_OPEN)
          current = state1;
            else
              current = state2;
              lastState = tempState;
              repaint();
              return;
      }
    }

    if(panelType == T_OBJ) {
      boolean temp = rState.isObjectPresent();
      if(temp != objPresent) {
        if(!temp)
          current = state1;
        else
          current = state2;
        objPresent = temp;
        repaint();
      }
    }
  }
} // RobotStatePanel
