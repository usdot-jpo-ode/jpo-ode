package us.dot.its.jpo.ode.udp.spat;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@Slf4j
public class SpatReceiver extends AbstractUdpReceiverPublisher {

  private final FfmlibDecodeService decodeService;

  public SpatReceiver(ReceiverProperties receiverProperties, FfmlibDecodeService decodeService) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
    this.decodeService = decodeService;
  }

  @Override
  public void run() {
    log.debug("SPAT UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP SPAT packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          decodeService.decode(packet, SupportedMessageType.SPAT);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding SPAT packet", e);
      } catch (Exception e) {
        log.error("Error receiving SPAT packet", e);
      }
    } while (!isStopped());
  }
}