/**
 * @(#)KSMain.java 1.1 2002/10/17
 *
 * Copyright Wright State University. All Rights Reserved.
 *
 */

package edu.wsu.KheperaSimulator;

import javax.swing.UIManager;
import java.awt.*;

/**
 * Contains the main method.  The application environment is setup and the
 * simulator is constructed and shown.
 *
 * @author    Brian Potchik
 * @version   1.1 2002/10/17
 */
public class KSMain {
  private boolean packFrame = true;
  private KSFrame frame = null;

  /**
   * Allocates a new <code>KSMain</code> that will contruct the WSU Khepera
   * Simulator and show it.
   */
  public KSMain() {
    KSFrame frame = new KSFrame("WSU Khepera Robot Simulator v7.3");

    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }

    //Center the application
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  } // KSMain

  /**
   * Set the look and feel and create a new <code>KSMain</code>.
   */
  public static void main(String args[]) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch(Exception e) {
      e.printStackTrace();
    }
    new KSMain();
  } // main
} // KSMain
