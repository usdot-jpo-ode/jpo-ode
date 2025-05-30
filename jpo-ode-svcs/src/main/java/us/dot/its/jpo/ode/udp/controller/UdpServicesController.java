package us.dot.its.jpo.ode.udp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.generic.GenericReceiver;
import us.dot.its.jpo.ode.udp.map.MapReceiver;
import us.dot.its.jpo.ode.udp.psm.PsmReceiver;
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
   * @param rawEncodedJsonTopics Topics to which the decoded messages will be published via Kafka.
   * @param kafkaTemplate        Template to facilitate sending messages to Kafka topics.
   */
  @Autowired
  public UdpServicesController(UDPReceiverProperties udpProps,
                               RawEncodedJsonTopics rawEncodedJsonTopics,
                               KafkaTemplate<String, String> kafkaTemplate) {

    log.debug("Starting UDP receiver services...");

    startReceiver(new BsmReceiver(udpProps.getBsm(), kafkaTemplate, rawEncodedJsonTopics.getBsm()));
    startReceiver(new TimReceiver(udpProps.getTim(), kafkaTemplate, rawEncodedJsonTopics.getTim()));
    startReceiver(new SsmReceiver(udpProps.getSsm(), kafkaTemplate, rawEncodedJsonTopics.getSsm()));
    startReceiver(new SrmReceiver(udpProps.getSrm(), kafkaTemplate, rawEncodedJsonTopics.getSrm()));
    startReceiver(new SpatReceiver(udpProps.getSpat(), kafkaTemplate, rawEncodedJsonTopics.getSpat()));
    startReceiver(new MapReceiver(udpProps.getMap(), kafkaTemplate, rawEncodedJsonTopics.getMap()));
    startReceiver(new PsmReceiver(udpProps.getPsm(), kafkaTemplate, rawEncodedJsonTopics.getPsm()));
    startReceiver(new SdsmReceiver(udpProps.getSdsm(), kafkaTemplate, rawEncodedJsonTopics.getSdsm()));
    startReceiver(new GenericReceiver(udpProps.getGeneric(), kafkaTemplate, rawEncodedJsonTopics));

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