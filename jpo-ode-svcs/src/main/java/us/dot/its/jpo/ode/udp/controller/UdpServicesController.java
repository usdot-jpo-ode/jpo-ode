package us.dot.its.jpo.ode.udp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
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
 * Centralized UDP service dispatcher. Starts one thread per message type receiver; all receivers
 * decode J2735 UPER payloads in-process via {@link FfmlibDecodeService} and publish directly to
 * the appropriate {@code topic.Ode{Type}Json} topic without an intermediate raw-encoded topic.
 */
@Controller
@Slf4j
public class UdpServicesController {

  private final List<ExecutorService> executors = new ArrayList<>();

  @Autowired
  public UdpServicesController(UDPReceiverProperties udpProps,
      FfmlibDecodeService decodeService,
      PortMappedIngestConfigLoader portMappedIngestConfigLoader) {

    log.debug("Starting UDP receiver services...");

    startReceiver(new BsmReceiver(udpProps.getBsm(), decodeService));
    startReceiver(new TimReceiver(udpProps.getTim(), decodeService));
    startReceiver(new SsmReceiver(udpProps.getSsm(), decodeService));
    startReceiver(new SrmReceiver(udpProps.getSrm(), decodeService));
    startReceiver(new SpatReceiver(udpProps.getSpat(), decodeService));
    startReceiver(new MapReceiver(udpProps.getMap(), decodeService));
    startReceiver(new PsmReceiver(udpProps.getPsm(), decodeService));
    startReceiver(new SdsmReceiver(udpProps.getSdsm(), decodeService));
    startReceiver(new RtcmReceiver(udpProps.getRtcm(), decodeService));
    startReceiver(new RsmReceiver(udpProps.getRsm(), decodeService));
    startReceiver(new GenericReceiver(udpProps.getGeneric(), decodeService));

    List<AbstractUdpReceiverPublisher> receivers =
        portMappedIngestConfigLoader.loadReceivers(udpProps, decodeService);
    for (AbstractUdpReceiverPublisher receiver : receivers) {
      startReceiver(receiver);
    }

    log.debug("UDP receiver services started.");
  }

  private void startReceiver(AbstractUdpReceiverPublisher receiver) {
    ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
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

  @PreDestroy
  public void shutdown() {
    log.info("Shutting down UDP services...");

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
