package us.dot.its.jpo.ode.udp.rsm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@Slf4j
public class RsmReceiver extends AbstractUdpReceiverPublisher {

  private final FfmlibDecodeService decodeService;

  public RsmReceiver(ReceiverProperties receiverProperties, FfmlibDecodeService decodeService) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
    this.decodeService = decodeService;
  }

  @Override
  public void run() {
    log.debug("RSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP RSM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          decodeService.decode(packet, SupportedMessageType.RSM);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding RSM packet", e);
      } catch (Exception e) {
        log.error("Error receiving RSM packet", e);
      }
    } while (!isStopped());
  }
}