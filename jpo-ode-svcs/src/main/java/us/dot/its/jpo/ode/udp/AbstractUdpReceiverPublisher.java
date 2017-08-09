package us.dot.its.jpo.ode.udp;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;

public abstract class AbstractUdpReceiverPublisher implements Runnable {

   public class UdpReceiverException extends Exception {
      private static final long serialVersionUID = 1L;

      public UdpReceiverException(String string, Exception e) {
         super(string, e);
      }
   }

   private static Logger logger = LoggerFactory.getLogger(AbstractUdpReceiverPublisher.class);
   private static Coder coder = J2735.getPERUnalignedCoder();

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

   @Autowired
   public AbstractUdpReceiverPublisher(OdeProperties odeProps, int port, int bufferSize) {
      this.odeProperties = odeProps;
      this.port = port;
      this.bufferSize = bufferSize;

      try {
         socket = new DatagramSocket(this.port);
         logger.info("Created UDP socket bound to port {}", this.port);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + this.port, e);
      }
   }

   protected AbstractData decodeData(byte[] msg) throws UdpReceiverException {
      AbstractData decoded = null;
      try {
         decoded = J2735Util.decode(coder, msg);
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         throw new UdpReceiverException("Unable to decode UDP message", e);
      }
      return decoded;
   }
}