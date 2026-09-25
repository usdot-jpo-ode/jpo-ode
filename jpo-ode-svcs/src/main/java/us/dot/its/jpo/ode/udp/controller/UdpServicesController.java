package us.dot.its.jpo.ode.udp.controller;

import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.generic.GenericReceiver;
import us.dot.its.jpo.ode.udp.map.MapReceiver;
import us.dot.its.jpo.ode.udp.portmapped.PortMappedIngestConfigLoader;
import us.dot.its.jpo.ode.udp.psm.PsmReceiver;
import us.dot.its.jpo.ode.udp.rsm.RsmReceiver;
import us.dot.its.jpo.ode.udp.rtcm.RtcmReceiver;
import us.dot.its.jpo.ode.udp.sdsm.SdsmReceiver;
import us.dot.its.jpo.ode.udp.spat.SpatReceiver;
import us.dot.its.jpo.ode.udp.srm.SrmReceiver;
import us.dot.its.jpo.ode.udp.ssm.SsmReceiver;
import us.dot.its.jpo.ode.udp.tim.TimReceiver;

/**
 * Centralized UDP service dispatcher.
 */
@Controller
@Slf4j
public class UdpServicesController {

  private final List<ExecutorService> executors = new ArrayList<>();

  /**
   * Constructs a UdpServicesController to manage UDP receiver services for different message
   * types.
   *
   * @param udpProps             Properties containing configuration for each UDP receiver.
   * @param rawEncodedJsonTopics Topics to which raw messages are published when direct JSON is off.
   * @param ingestPublisher      Publisher that selects the raw topic or direct Ode JSON.
   * @param portMappedIngestConfigLoader Loader for extra port-mapped receivers.
   */
  @Autowired
  public UdpServicesController(UDPReceiverProperties udpProps,
                               RawEncodedJsonTopics rawEncodedJsonTopics,
                               UdpIngestPublisher ingestPublisher,
                               PortMappedIngestConfigLoader portMappedIngestConfigLoader) {

    log.debug("Starting UDP receiver services...");

    startReceiver(new BsmReceiver(udpProps.getBsm(), ingestPublisher, rawEncodedJsonTopics.getBsm()));
    startReceiver(new TimReceiver(udpProps.getTim(), ingestPublisher, rawEncodedJsonTopics.getTim()));
    startReceiver(new SsmReceiver(udpProps.getSsm(), ingestPublisher, rawEncodedJsonTopics.getSsm()));
    startReceiver(new SrmReceiver(udpProps.getSrm(), ingestPublisher, rawEncodedJsonTopics.getSrm()));
    startReceiver(new SpatReceiver(udpProps.getSpat(), ingestPublisher, rawEncodedJsonTopics.getSpat()));
    startReceiver(new MapReceiver(udpProps.getMap(), ingestPublisher, rawEncodedJsonTopics.getMap()));
    startReceiver(new PsmReceiver(udpProps.getPsm(), ingestPublisher, rawEncodedJsonTopics.getPsm()));
    startReceiver(new SdsmReceiver(udpProps.getSdsm(), ingestPublisher, rawEncodedJsonTopics.getSdsm()));
    startReceiver(new RtcmReceiver(udpProps.getRtcm(), ingestPublisher, rawEncodedJsonTopics.getRtcm()));
    startReceiver(new RsmReceiver(udpProps.getRsm(), ingestPublisher, rawEncodedJsonTopics.getRsm()));
    startReceiver(new GenericReceiver(udpProps.getGeneric(), ingestPublisher, rawEncodedJsonTopics));

    
    List<AbstractUdpReceiverPublisher> receivers = portMappedIngestConfigLoader
      .loadReceivers(udpProps, rawEncodedJsonTopics, ingestPublisher);
    for (AbstractUdpReceiverPublisher receiver : receivers) {
      startReceiver(receiver);
    }

    log.debug("UDP receiver services started.");
  }

  /**
   * Starts a receiver in its own executor service and manages its lifecycle.
   *
   * @param receiver The receiver to start
   */
  private void startReceiver(AbstractUdpReceiverPublisher receiver) {
    ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true); // Makes thread exit when main application exits
      return thread;
    });

    executors.add(executor);

    executor.submit(() -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          receiver.run();
        }
      } catch (Exception e) {
        log.error("Error in receiver {}: {}", receiver.getClass().getSimpleName(), e.getMessage(), e);
      }
    });
  }

  /**
   * Gracefully shuts down all executor services and closes all receivers.
   */
  @PreDestroy
  public void shutdown() {
    log.info("Shutting down UDP services...");

    // First, shutdown all executors
    executors.forEach(executor -> {
      try {
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
          executor.shutdownNow();
          if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            log.error("Executor did not terminate");
          }
        }
      } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    });

    log.info("UDP services shutdown completed.");
  }
}