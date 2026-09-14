package us.dot.its.jpo.ode.udp.map;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@Slf4j
public class MapReceiver extends AbstractUdpReceiverPublisher {

  private final FfmlibDecodeService decodeService;

  public MapReceiver(ReceiverProperties receiverProperties, FfmlibDecodeService decodeService) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
    this.decodeService = decodeService;
  }

  @Override
  public void run() {
    log.debug("MAP UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP MAP packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          decodeService.decode(packet, SupportedMessageType.MAP);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding MAP packet", e);
      } catch (Exception e) {
        log.error("Error receiving MAP packet", e);
      }
    } while (!isStopped());
  }
}