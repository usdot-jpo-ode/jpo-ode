package us.dot.its.jpo.ode.udp;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.DatagramPacket;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibOutputPublisher;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.udp.UdpHexDecoder.UdpDecodeInput;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Publishes a UDP packet to its raw encoded topic, or decodes it in process in FFM mode.
 *
 * <p>While {@code ode.asn1.codec-mode=ffm}, the socket thread enqueues stripped UPER bytes. Worker
 * threads decode and publish Ode JSON without waiting for the producer acknowledgement. A full
 * queue blocks the caller, which stops {@code socket.receive} for that receiver. External mode
 * still publishes the raw encoded topic.
 */
@Component
@Slf4j
public class UdpIngestPublisher {

  private static final int QUEUE_CAPACITY = 1024;
  private static final Map<SupportedMessageType, Profile> PROFILES = profiles();

  private final KafkaTemplate<String, String> rawKafka;
  private final boolean directJsonActive;
  private final int workerCount;
  private final ArrayBlockingQueue<UdpDecodeInput> queue;
  private final FfmlibDecodeService decoder;
  private final FfmlibOutputPublisher output;
  private final Counter directFailures;
  private final AtomicBoolean closed = new AtomicBoolean();
  private final AtomicBoolean workersStarted = new AtomicBoolean();

  /**
   * Creates the ingest publisher used by every UDP receiver.
   *
   * @param rawKafka producer for raw encoded JSON when the codec mode is external
   * @param mode codec mode selection
   * @param decoder native decode service, present in FFM mode
   * @param output JSON producer, present in FFM mode
   * @param meters failure counter registry
   * @param workerCount decode workers
   */
  @Autowired
  public UdpIngestPublisher(
      @Qualifier("kafkaTemplate") KafkaTemplate<String, String> rawKafka,
      Asn1CodecModeProperties mode,
      ObjectProvider<FfmlibDecodeService> decoder,
      ObjectProvider<FfmlibOutputPublisher> output,
      MeterRegistry meters,
      @Value("${ode.ffmlib.listener-concurrency:4}") int workerCount) {
    this(rawKafka, mode.isFfm(), workerCount, QUEUE_CAPACITY, decoder.getIfAvailable(),
        output.getIfAvailable(), meters);
  }

  /**
   * Publishes through the raw topic only. Receiver tests use this so they keep the injected
   * template.
   *
   * @param rawKafka raw encoded JSON producer
   * @return a publisher that never decodes in process
   */
  public static UdpIngestPublisher rawOnly(KafkaTemplate<String, String> rawKafka) {
    return new UdpIngestPublisher(rawKafka, false, 1, 1, null, null, null);
  }

  UdpIngestPublisher(KafkaTemplate<String, String> rawKafka, boolean ffmActive, int workerCount,
      int queueCapacity, FfmlibDecodeService decoder, FfmlibOutputPublisher output,
      MeterRegistry meters) {
    this.rawKafka = rawKafka;
    this.directJsonActive = ffmActive && decoder != null && output != null;
    this.workerCount = Math.max(1, workerCount);
    this.queue = new ArrayBlockingQueue<>(Math.max(1, queueCapacity));
    this.decoder = decoder;
    this.output = output;
    this.directFailures = meters == null ? null
        : meters.counter("ode.ffmlib.direct.decode.failures");
    if (ffmActive && !directJsonActive) {
      log.warn("FFM codec mode is set but decode is not active; "
          + "UDP will keep publishing raw topics");
    }
  }

  /** Starts decode workers after FFM beans exist. */
  @PostConstruct
  public void start() {
    if (directJsonActive) {
      startWorkers();
    }
  }

  /** Stops workers and unblocks a receiver waiting on a full queue. */
  @PreDestroy
  public void close() {
    closed.set(true);
  }

  /**
   * Publishes one UDP packet.
   *
   * @param packet received datagram
   * @param type message type used to strip headers and choose the raw topic contract
   * @param rawTopic raw encoded topic used when the codec mode is external
   * @throws InvalidPayloadException when the packet does not contain the message start flag
   * @throws InterruptedException when the caller is interrupted while the decode queue is full
   */
  public void publish(DatagramPacket packet, SupportedMessageType type, String rawTopic)
      throws InvalidPayloadException, InterruptedException {
    Profile profile = PROFILES.get(type);
    UdpDecodeInput input = UdpHexDecoder.prepareDecodeInput(packet, type, profile.recordType(),
        profile.source(), profile.generatedBy(), profile.includeDetails());
    if (!directJsonActive) {
      OdeAsn1Data data = new OdeAsn1Data(input.metadata(), new OdeAsn1Payload(input.uperBytes()));
      String json = JsonUtils.toJson(data, false);
      if (json != null) {
        rawKafka.send(rawTopic, json);
      }
      return;
    }
    enqueue(input);
  }

  /**
   * Blocks until the decode queue accepts the input, or until the publisher closes.
   *
   * @param input stripped UPER bytes and metadata
   * @throws InterruptedException when the waiting thread is interrupted
   */
  void enqueue(UdpDecodeInput input) throws InterruptedException {
    while (!closed.get()) {
      if (queue.offer(input, 50, TimeUnit.MILLISECONDS)) {
        return;
      }
    }
  }

  boolean isDirectJsonActive() {
    return directJsonActive;
  }

  private void startWorkers() {
    if (!workersStarted.compareAndSet(false, true)) {
      return;
    }
    for (int index = 0; index < workerCount; index++) {
      Thread worker = new Thread(this::decodeLoop, "ffm-direct-decode-" + index);
      worker.setDaemon(true);
      worker.start();
    }
    log.info("FFM direct JSON decode started with {} workers", workerCount);
  }

  private void decodeLoop() {
    while (!closed.get() && !Thread.currentThread().isInterrupted()) {
      try {
        UdpDecodeInput input = queue.poll(50, TimeUnit.MILLISECONDS);
        if (input == null) {
          continue;
        }
        output.publish(decoder.prepareRaw(input.metadata(), input.uperBytes(), null));
      } catch (InterruptedException error) {
        Thread.currentThread().interrupt();
        return;
      } catch (RuntimeException error) {
        if (directFailures != null) {
          directFailures.increment();
        }
        log.error("Direct FFM decode failed for UDP input", error);
      }
    }
  }

  private static Map<SupportedMessageType, Profile> profiles() {
    Map<SupportedMessageType, Profile> profiles = new EnumMap<>(SupportedMessageType.class);
    profiles.put(SupportedMessageType.BSM,
        new Profile(RecordType.bsmTx, Source.EV, GeneratedBy.OBU, true));
    profiles.put(SupportedMessageType.TIM,
        new Profile(RecordType.timMsg, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.MAP,
        new Profile(RecordType.mapTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.SPAT,
        new Profile(RecordType.spatTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.PSM,
        new Profile(RecordType.psmTx, Source.RSU, GeneratedBy.UNKNOWN, false));
    profiles.put(SupportedMessageType.SRM,
        new Profile(RecordType.srmTx, Source.RSU, GeneratedBy.OBU, false));
    profiles.put(SupportedMessageType.SSM,
        new Profile(RecordType.ssmTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.SDSM,
        new Profile(RecordType.sdsmTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.RTCM,
        new Profile(RecordType.rtcmTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.RSM,
        new Profile(RecordType.rsmTx, Source.RSU, GeneratedBy.RSU, false));
    return profiles;
  }

  private record Profile(RecordType recordType, Source source, GeneratedBy generatedBy,
      boolean includeDetails) {
  }
}
