package us.dot.its.jpo.ode.udp;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.OdeProperties;

import java.net.DatagramSocket;
import java.net.SocketException;

@Slf4j
public abstract class AbstractUdpReceiverPublisher implements Runnable {

    public class UdpReceiverException extends Exception {
        private static final long serialVersionUID = 1L;

        public UdpReceiverException(String string, Exception e) {
            super(string, e);
        }
    }

    protected DatagramSocket socket;

    protected String senderIp;
    protected int senderPort;

    protected OdeProperties odeProperties;
    protected int port;
    protected int bufferSize;

    private boolean stopped = false;

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    protected AbstractUdpReceiverPublisher(int port, int bufferSize) {
        this.port = port;
        this.bufferSize = bufferSize;

        try {
            this.socket = new DatagramSocket(this.port);
            log.info("Created UDP socket bound to port {}", this.port);
        } catch (SocketException e) {
            log.error("Error creating socket with port {}", this.port, e);
        }
    }

}