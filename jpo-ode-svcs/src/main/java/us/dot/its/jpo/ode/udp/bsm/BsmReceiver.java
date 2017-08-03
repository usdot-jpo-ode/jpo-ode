package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.BsmStreamDecoderPublisher;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

    private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

    protected BsmStreamDecoderPublisher bsmDecoderPublisher;
    
    @Autowired
    public BsmReceiver(OdeProperties odeProps) {
        this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());
    }

    public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
        super(odeProps, port, bufferSize);
        bsmDecoderPublisher = new BsmStreamDecoderPublisher(odeProps, new SerialId(), null);
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
                    bsmDecoderPublisher.decodeBytesAndPublish(payload);
                }
            } catch (Exception e) {
                logger.error("Error receiving packet", e);
            }
        } while (!isStopped());
    }
    
    /**
     * Attempts to strip WSMP header bytes
     * 
     * @param packet
     */
    public byte[] removeHeader(byte[] packet) {
       String hexPacket = HexUtils.toHexString(packet);
       int startIndex = hexPacket.indexOf("0014");
       logger.debug("BSM packet length: {}, start index: {}", hexPacket.length(), startIndex);
       if (startIndex >= 20) {
          hexPacket = hexPacket.substring(startIndex, hexPacket.length());
       }
       return HexUtils.fromHexString(hexPacket);
    }
}
