package us.dot.its.jpo.ode.udp;

/** Raised when a generic UDP packet does not contain a supported J2735 message type. */
public class UnsupportedMessageTypeException extends Exception {

  private static final long serialVersionUID = 1L;

  public UnsupportedMessageTypeException(String message) {
    super(message);
  }
}
