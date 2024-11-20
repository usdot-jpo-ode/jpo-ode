package us.dot.its.jpo.ode.udp;

public class InvalidPayloadException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidPayloadException(String message) {
        super(message);
    }
}
