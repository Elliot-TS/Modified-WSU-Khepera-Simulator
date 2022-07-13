/**
 * @(#)Controller.java 1.1 2002/10/12
 * modified 2003/2 to work with remote control
 * Copyright Wright State University. All Rights Reserved.
 */

package edu.wsu.KheperaSimulator;

/**
 * The <code>Controller</code> interface should be implemented by any class
 * whose instances are intended to be executed by a thread and perform
 * real-time manipulation of an object.
 *
 * @author    Brian Potchik
 * @version   1.0 10/12/02
 */
public interface Controller extends Runnable
{

  /**
   * When an object implementing the interface <code>Controller</code> is used
   * to create a thread, the starting thread causes the objects
   * <code>doWork</code> method to be called.  This method is called repeatedly
   * and may perform any task.  The time needed for <code>doWork</code> to
   * complete is not significant with respect to the next invocation of this
   * method.
   * @throws java.lang.Exception
   */
  public abstract void doWork() throws java.lang.Exception;

  /**
   * Indicates that the application has finished using the controller, and that
   * any resources being used may be released. The starting thread invokes the
   * object's <code>close</code> method only when <code>doWork</code> has
   * returned and is not scheuled to be called again.
   * @throws java.lang.Exception
   */
  public abstract void close() throws java.lang.Exception;
} // Controller
