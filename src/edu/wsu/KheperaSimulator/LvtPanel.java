package edu.wsu.KheperaSimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class LvtPanel extends JPanel{

  //private String panel;
  private CurrentRobotState rState;
  private JLabel[] labelarray;

  public LvtPanel() {
   setBackground(Color.white);
   setLayout(new FlowLayout(FlowLayout.LEADING,1,1));
   labelarray= new JLabel[64];
   
   for(int i=0;i<64;i++)
   {
	   labelarray[i] = new JLabel();
	   labelarray[i].setPreferredSize(new Dimension(5,20));
	   labelarray[i].setBackground(Color.darkGray);
	   labelarray[i].setOpaque(true);
	   this.add(labelarray[i]);
   }
  }
protected void updateLvtImage(){
	float tempLvtArray[] = rState.getLvtArray();
	for(int i = 0 ; i < 64 ; i++){
		if(tempLvtArray[i] == 10.0)
			labelarray[i].setBackground(Color.black);
		else if(tempLvtArray[i] == 100.0)
			labelarray[i].setBackground(Color.gray);
		else if(tempLvtArray[i] == 200.0)
			labelarray[i].setBackground(Color.lightGray);
		else if(tempLvtArray[i] == 255.0)
			labelarray[i].setBackground(Color.yellow);
		else if(tempLvtArray[i] == 230.0)
			labelarray[i].setBackground(Color.white);
	}
	repaint();
}
  /**
   * @see javax.swing.JComponent
   * @see java.awt.Graphics
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g); //paint background

    //Draw image at its natural size first.
    //g.drawImage(current, 0, 0, this);
    //g.setColor(Color.cyan);
   // label.setBackground(Color.green);
    //g.setColor(Color.green);
  }

  /**
   * Initialize this GUI component and draw it.
   * @param type the type of status
   */
 /* private void initialize(String type) {
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
  }*/

  protected void setCurrentState(CurrentRobotState rs) {
    rState = rs;
  }

  /**
   * Update the current image of this status component.
   */
 /* protected void updateImage() {
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
  }*/
} // RobotStatePanel
