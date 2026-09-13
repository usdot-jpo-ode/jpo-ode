package us.dot.its.jpo.ode.udp.psm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@Slf4j
public class PsmReceiver extends AbstractUdpReceiverPublisher {

  private final FfmlibDecodeService decodeService;

  public PsmReceiver(ReceiverProperties receiverProperties, FfmlibDecodeService decodeService) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
    this.decodeService = decodeService;
  }

  @Override
  public void run() {
    log.debug("PSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP PSM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          decodeService.decode(packet, SupportedMessageType.PSM);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding PSM packet", e);
      } catch (Exception e) {
        log.error("Error receiving PSM packet", e);
      }
    } while (!isStopped());
  }
}