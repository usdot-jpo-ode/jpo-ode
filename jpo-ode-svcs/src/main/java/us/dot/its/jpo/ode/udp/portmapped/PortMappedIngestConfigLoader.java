package us.dot.its.jpo.ode.udp.portmapped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;

@Slf4j
@Component
public class PortMappedIngestConfigLoader {

  private final PortMappedIngestConfig config;

  public PortMappedIngestConfigLoader(PortMappedIngestConfig config) {
    this.config = config;
  }

  public List<AbstractUdpReceiverPublisher> loadReceivers(UDPReceiverProperties udpProps,
      FfmlibDecodeService decodeService) {
    log.debug("Loading configurable UDP receivers from config...");

    if (config == null || config.getSources() == null || config.getSources().isEmpty()) {
      return Collections.emptyList();
    }

    List<AbstractUdpReceiverPublisher> receivers = new ArrayList<>();
    for (PortMappedIngestConfig.PortMappedIngestSource source : config.getSources()) {

      String type = normalizeType(source.getType());
      if (type == null) {
        log.warn("Skipping configurable UDP ingest source with missing type. Source: {}", source);
        continue;
      } else {
        source.setType(type);
      }

      PortMappedConfigurableReceiver receiver = new PortMappedConfigurableReceiver(
          buildReceiverProperties(udpProps, source),
          decodeService,
          source
      );
      receivers.add(receiver);
    }
    return receivers;
  }

  private String normalizeType(String type) {
    if (type == null || type.isBlank()) {
      return null;
    }
    return type.trim().toUpperCase(Locale.ROOT);
  }

  private ReceiverProperties buildReceiverProperties(UDPReceiverProperties udpProps,
      PortMappedIngestConfig.PortMappedIngestSource source) {
    ReceiverProperties baseProps = switch (source.getType()) {
      case "BSM" -> udpProps.getBsm();
      case "TIM" -> udpProps.getTim();
      case "SSM" -> udpProps.getSsm();
      case "SRM" -> udpProps.getSrm();
      case "SPAT" -> udpProps.getSpat();
      case "MAP" -> udpProps.getMap();
      case "PSM" -> udpProps.getPsm();
      case "SDSM" -> udpProps.getSdsm();
      case "RTCM" -> udpProps.getRtcm();
      case "RSM" -> udpProps.getRsm();
      case "GENERIC" -> udpProps.getGeneric();
      default -> null;
    };

    if (baseProps == null) {
      return null;
    }

    ReceiverProperties props = new ReceiverProperties();
    props.setBufferSize(baseProps.getBufferSize());
    props.setReceiverPort(source.getPort());
    return props;
  }
}
