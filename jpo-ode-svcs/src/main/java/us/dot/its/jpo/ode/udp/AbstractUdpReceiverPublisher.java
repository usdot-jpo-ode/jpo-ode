package us.dot.its.jpo.ode.udp;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.uper.UperUtil;

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

   public OdeAsn1Payload getPayloadHexString(DatagramPacket packet, UperUtil.SupportedMessageTypes msgType) {
      String startFlag = UperUtil.getStartFlag(msgType);
      // extract the actual packet from the buffer
      byte[] payload = packet.getData();
      if (payload == null)
         return null;
      // convert bytes to hex string and verify identity
      String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
      if (payloadHexString.indexOf(startFlag) == -1)
         return null;
      
      logger.debug("Full {} packet: {}", msgType, payloadHexString);
      payloadHexString = UperUtil.stripDot3Header(payloadHexString, startFlag);
      logger.debug("Stripped {} packet: {}", msgType, payloadHexString);

      OdeAsn1Payload timPayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
      
      return timPayload;
   }

}