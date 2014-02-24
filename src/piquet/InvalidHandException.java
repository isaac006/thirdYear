/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package piquet;

/**
 *
 * @author isaac006
 */
public class InvalidHandException extends Exception {

  /**
   * Creates a new instance of
   * <code>InvalidHandException</code> without detail message.
   */
  public InvalidHandException() {
    super();
  }

  /**
   * Constructs an instance of
   * <code>InvalidHandException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public InvalidHandException(String msg) {
    super(msg);
  }
}
