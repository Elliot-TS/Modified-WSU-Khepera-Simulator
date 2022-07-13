/**
 * @(#)StatusBar.java 1.1 2002/10/30
 *
 */

package edu.wsu.KheperaSimulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Representation of a generic status bar for an application.  This is a static
 * class.  In this case it provides the ability to call the methods from any
 * where in the application.  For example, StatusBar.setCenterStatus(...);
 *
 * @author    Brian Potchik
 * @version   1.1 2002/10/05
 */
public class StatusBar extends JPanel {

  private static JLabel leftStatus = new JLabel();
  private static JLabel centerStatus = new JLabel();
  private static JLabel rightStatus = new JLabel();

  /**
   * Allocates a new <code>StatusBar<code> object.
   */
  public StatusBar() {
    this.setLayout(new BorderLayout());
    leftStatus.setBorder(BorderFactory.createLoweredBevelBorder());
    centerStatus.setBorder(BorderFactory.createLoweredBevelBorder());
    rightStatus.setBorder(BorderFactory.createLoweredBevelBorder());
    leftStatus.setMinimumSize(new Dimension(350, 20));
    leftStatus.setPreferredSize(new Dimension(350, 20));
    centerStatus.setMinimumSize(new Dimension(50, 20));
    centerStatus.setPreferredSize(new Dimension(50, 20));
    rightStatus.setMinimumSize(new Dimension(200, 20));
    rightStatus.setPreferredSize(new Dimension(200, 20));
    this.add(leftStatus, BorderLayout.WEST);
    this.add(centerStatus,BorderLayout.CENTER);
    this.add(rightStatus, BorderLayout.EAST);
  } // StatusBar

  /**
   * Resets the status of all notification areas in this status bar.
   */
  public static synchronized void clearStatus() {
    leftStatus.setText(null);
    centerStatus.setText(null);
    rightStatus.setText(null);
  }

  /**
   * Set the left status of this status bar
   * @param s a <tt>String</tt> representation of the status
   */
  public static synchronized void setLeftStatus(String s) {
    leftStatus.setText(s);
  }

  /**
   * Set the center status of this status bar
   * @param s a <tt>String</tt> representation of the status
   */
  public static synchronized void setCenterStatus(String s) {
    centerStatus.setText(s);
  }

  /**
   * Set the right status of this status bar
   * @param s a <tt>String</tt> representation of the status
   */
  public static synchronized void setRightStatus(String s) {
    rightStatus.setText(s);
  }
} // StatusBar
