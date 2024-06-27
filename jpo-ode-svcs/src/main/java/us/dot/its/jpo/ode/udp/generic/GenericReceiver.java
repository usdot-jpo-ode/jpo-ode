package us.dot.its.jpo.ode.udp.generic;

import java.net.DatagramPacket;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.map.MapReceiver;
import us.dot.its.jpo.ode.udp.psm.PsmReceiver;
import us.dot.its.jpo.ode.udp.spat.SpatReceiver;
import us.dot.its.jpo.ode.udp.srm.SrmReceiver;
import us.dot.its.jpo.ode.udp.ssm.SsmReceiver;
import us.dot.its.jpo.ode.udp.tim.TimReceiver;
import us.dot.its.jpo.ode.uper.UperUtil;

public class GenericReceiver extends AbstractUdpReceiverPublisher {

    private static Logger logger = LoggerFactory.getLogger(GenericReceiver.class);

    private StringPublisher publisher;



    @Autowired
    public GenericReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getGenericReceiverPort(), odeProps.getGenericBufferSize());

        this.publisher = new StringPublisher(odeProps);
    }

    public GenericReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);

        this.publisher = new StringPublisher(odeProps);

    }

    @Override
    public void run() {

        logger.debug("Generic UDP Receiver Service started.");

        byte[] buffer = new byte[bufferSize];

       

        do {

            // packet should be recreated on each loop to prevent latent data in buffer
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                logger.debug("Waiting for Generic UDP packets...");
                socket.receive(packet);
                if (packet.getLength() > 0) {
                    senderIp = packet.getAddress().getHostAddress();
                    senderPort = packet.getPort();
                    logger.debug("Packet received from {}:{}", senderIp, senderPort);

                    byte[] payload = packet.getData();
                    if (payload == null){
                        logger.debug("Skipping Null Payload");
                        continue;
                    }
                    String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
                    String messageType = UperUtil.determineHexPacketType(payloadHexString);

                    logger.debug("Detected Message Type {}", messageType);

                    if (messageType == "MAP") {
                        String mapJson = MapReceiver.buildJsonMapFromPacket(packet);
                        if(mapJson != null){
                            publisher.publish(mapJson, publisher.getOdeProperties().getKafkaTopicOdeRawEncodedMAPJson());
                        }
                    } else if(messageType == "SPAT") {
                        String spatJson = SpatReceiver.buildJsonSpatFromPacket(packet);
                        if(spatJson != null){
                            publisher.publish(spatJson, publisher.getOdeProperties().getKafkaTopicOdeRawEncodedSPATJson());
                        }
                    } else if (messageType == "TIM") {
                        String timJson = TimReceiver.buildJsonTimFromPacket(packet);
                        if(timJson != null){
                            publisher.publish(timJson, publisher.getOdeProperties().getKafkaTopicOdeRawEncodedTIMJson());
                        }
                    } else if (messageType == "BSM") {
                        String bsmJson = BsmReceiver.buildJsonBsmFromPacket(packet);
                        if(bsmJson!=null){
                            publisher.publish(bsmJson, this.odeProperties.getKafkaTopicOdeRawEncodedBSMJson());
                        }
                    } else if (messageType == "SSM") {
                        String ssmJson = SsmReceiver.buildJsonSsmFromPacket(packet);
                        if(ssmJson!=null){
                            publisher.publish(ssmJson, this.odeProperties.getKafkaTopicOdeRawEncodedSSMJson());
                        }
                    } else if (messageType == "SRM") {
                        String srmJson = SrmReceiver.buildJsonSrmFromPacket(packet);
                        if(srmJson!=null){
                            publisher.publish(srmJson, this.odeProperties.getKafkaTopicOdeRawEncodedSRMJson());
                        }
                    } else if (messageType == "PSM") {
                        String psmJson = PsmReceiver.buildJsonPsmFromPacket(packet);
                        if(psmJson!=null){
                            publisher.publish(psmJson, this.odeProperties.getKafkaTopicOdeRawEncodedPSMJson());
                        }
                    }else{
                        logger.debug("Unknown Message Type");
                    }
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
   }
}
