package us.dot.its.jpo.ode.udp.portmapped;

import java.net.DatagramPacket;
import java.net.InetAddress;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UnsupportedMessageTypeException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.udp.generic.GenericReceiver;

/**
 * Port-mapped configurable receiver that overrides the sender IP from configuration before
 * routing to the FFMLib decode path.
 */
@Slf4j
public class PortMappedConfigurableReceiver extends GenericReceiver {

  private final PortMappedIngestConfig.PortMappedIngestSource ingestConfig;

  /**
   * Constructs a port-mapped receiver with an overridden source address.
   *
   * @param props UDP receiver properties
   * @param decodeService FFMLib decode service
   * @param ingestConfig port-mapped ingest source configuration
   */
  public PortMappedConfigurableReceiver(ReceiverProperties props,
      FfmlibDecodeService decodeService,
      PortMappedIngestConfig.PortMappedIngestSource ingestConfig) {
    super(props, decodeService);
    log.info("Creating PortMappedConfigurableReceiver with port {} and buffer size {} and Remap IP {}",
        props.getReceiverPort(), props.getBufferSize(), ingestConfig.getOriginIp());
    this.ingestConfig = ingestConfig;
  }

  /** Receives and routes datagrams using the configured source address and message type. */
  @Override
  public void run() {
    log.debug("PortMappedConfigurableReceiver started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        packet.setData(buffer);
        packet.setLength(buffer.length);
        socket.receive(packet);
        if (packet.getLength() <= 0 || packet.getData() == null) {
          log.debug("Skipping empty payload");
          continue;
        }

        senderIp = this.ingestConfig.getOriginIp();
        InetAddress senderAddress = InetAddress.getByName(senderIp);
        packet.setAddress(senderAddress);
        senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        routeMessageByMessageType(this.ingestConfig.getType(), packet);

      } catch (UnsupportedMessageTypeException e) {
        log.error("Unsupported Message Type", e);
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
