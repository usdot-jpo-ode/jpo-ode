package us.dot.its.jpo.ode.udp.sdsm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@Slf4j
public class SdsmReceiver extends AbstractUdpReceiverPublisher {

  private final FfmlibDecodeService decodeService;

  public SdsmReceiver(ReceiverProperties receiverProperties, FfmlibDecodeService decodeService) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
    this.decodeService = decodeService;
  }

  @Override
  public void run() {
    log.debug("SDSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP SDSM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          decodeService.decode(packet, SupportedMessageType.SDSM);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding SDSM packet", e);
      } catch (Exception e) {
        log.error("Error receiving SDSM packet", e);
      }
    } while (!isStopped());
  }
}