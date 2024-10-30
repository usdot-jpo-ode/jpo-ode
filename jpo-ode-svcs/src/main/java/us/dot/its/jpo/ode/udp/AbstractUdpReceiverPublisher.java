package us.dot.its.jpo.ode.udp;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;

public abstract class AbstractUdpReceiverPublisher implements Runnable {

   public class UdpReceiverException extends Exception {
      private static final long serialVersionUID = 1L;

      public UdpReceiverException(String string, Exception e) {
         super(string, e);
      }
   }

   private static Logger logger = LoggerFactory.getLogger(AbstractUdpReceiverPublisher.class);
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

   protected AbstractUdpReceiverPublisher(OdeProperties odeProps, int port, int bufferSize) {
      this.odeProperties = odeProps;
      this.port = port;
      this.bufferSize = bufferSize;

      try {
         this.socket = new DatagramSocket(this.port);
         logger.info("Created UDP socket bound to port {}", this.port);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + this.port, e);
      }
   }

}