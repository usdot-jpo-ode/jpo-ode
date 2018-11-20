package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.util.XmlUtils;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

   private static final String BSM_START_FLAG = "0014"; // these bytes indicate
                                                        // start of BSM payload
   private static final int HEADER_MINIMUM_SIZE = 20; // WSMP headers are at
                                                      // least 20 bytes long

//ODE-581   private OssJ2735Coder j2735coder;

//ODE-581   private OdeDataPublisher publisher;
   protected SerialId serialId;
   protected StringPublisher publisher;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   @Autowired
   public BsmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
   }

   public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);
//ODE-581      this.j2735coder = new OssJ2735Coder();

//ODE-581      this.publisher = new OdeDataPublisher(odeProperties, OdeBsmSerializer.class.getName());
      this.serialId = new SerialId();
      this.publisher = new StringPublisher(odeProperties);
   }

   @Override
   public void run() {

      logger.debug("UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP packets...");
            socket.receive(packet);
            if (packet.getLength() > 0) {
               senderIp = packet.getAddress().getHostAddress();
               senderPort = packet.getPort();
               logger.debug("Packet received from {}:{}", senderIp, senderPort);

               // extract the actualPacket from the buffer
               byte[] payload = removeHeader(packet.getData());
               String payloadHexString = HexUtils.toHexString(payload);
               logger.debug("Packet: {}", payloadHexString);

               //ODE-581 removed decoding and replaced with sending the ASN encoding to asn1_codec
//               // try decoding as a message frame
//               J2735Bsm decodedBsm = null;
//               J2735MessageFrame decodedMf = (J2735MessageFrame) j2735coder.decodeUPERMessageFrameBytes(payload);
//               if (decodedMf != null) {
//                  decodedBsm = decodedMf.getValue();
//               } else {
//                  // if that failed, try decoding as a bsm
//                  decodedBsm = (J2735Bsm) j2735coder.decodeUPERBsmBytes(payload);
//               }
//
//               // if that failed, throw an io exception
//               if (decodedBsm == null) {
//                  throw new IOException("Failed to decode message received via UDP.");
//               }

               publish(payload);
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   /**
    * Attempts to strip WSMP header bytes. If message starts with "0014",
    * message is raw BSM. Otherwise, headers are >= 20 bytes, so look past that
    * for start of payload BSM.
    * 
    * @param packet
    */
   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);

      // logger.debug("BSM packet length: {}, start index: {}",
      // hexPacket.length(), startIndex);

      int startIndex = hexPacket.indexOf(BSM_START_FLAG);
      if (startIndex == 0) {
         logger.info("Message is raw BSM with no headers.");
      } else if (startIndex == -1) {
         logger.error("Message contains no BSM start flag.");
      } else {
         // We likely found a message with a header, look past the first 20
         // bytes for the start of the BSM
         int trueStartIndex = HEADER_MINIMUM_SIZE
               + hexPacket.substring(HEADER_MINIMUM_SIZE, hexPacket.length()).indexOf(BSM_START_FLAG);
         hexPacket = hexPacket.substring(trueStartIndex, hexPacket.length());
      }

      return HexUtils.fromHexString(hexPacket);
   }
   
   public void publish(byte[] payloadBytes) throws Exception {
     OdeAsn1Payload payload = new OdeAsn1Payload(payloadBytes);
     
     OdeLogMetadata msgMetadata = new OdeLogMetadata(payload);
     msgMetadata.setSerialId(serialId);

     Asn1Encoding msgEncoding = new Asn1Encoding("root", "MessageFrame", EncodingRule.UPER);
     msgMetadata.addEncoding(msgEncoding);
     OdeAsn1Data asn1Data = new OdeAsn1Data(msgMetadata, payload);

     // publisher.publish(asn1Data.toJson(false),
     // publisher.getOdeProperties().getKafkaTopicAsn1EncodedBsm());
     publisher.publish(XmlUtils.toXmlS(asn1Data), publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
     serialId.increment();
  }

}