package us.dot.its.jpo.ode.archive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.archive.ArchiveRecordConverter.ConvertedRecord;
import us.dot.its.jpo.ode.archive.ArchiveRecordConverter.LineFormat;
import us.dot.its.jpo.ode.archive.ArchiveTopicResolver.TopicTarget;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

/**
 * Imports S3 archive files written by the jpo-utils S3 archive connectors.
 *
 * <p>Each file holds newline delimited JSON records of one Kafka topic, optionally gzip
 * compressed. The topic is read from the archive object name,
 * {@code <topic>+<partition>+<startOffset>.json.gz}:
 * <ul>
 *   <li>Raw encoded records are published unchanged to their raw encoded topic, where the ODE
 *       decodes them.</li>
 *   <li>Compact records ({@code odeReceivedAt}, {@code originIp}, {@code asn1}) are converted into
 *       raw encoded records and published to the raw encoded topic of their message type.</li>
 *   <li>Full decoded records are published unchanged to their decoded topic, or decoded again
 *       when {@link ArchiveImporterProperties#isRedecodeFullRecords()} is set.</li>
 * </ul>
 *
 * <p>The archived {@code odeReceivedAt} and {@code originIp} are kept in all cases.
 */
@Slf4j
@Component
public class ArchiveImportProcessor {

  private static final int MAX_LOGGED_SKIPS = 10;

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ArchiveTopicResolver topicResolver;
  private final ObjectMapper objectMapper;
  private final MeterRegistry meterRegistry;
  private final ArchiveImporterProperties properties;
  private final Set<String> disabledTopics;

  /**
   * Creates the processor.
   *
   * @param kafkaTemplate      the template used to publish the records
   * @param topicResolver      maps archived topics to their import targets
   * @param objectMapper       parses the archived lines
   * @param meterRegistry      records the import counters
   * @param properties         the archive importer settings
   * @param odeKafkaProperties provides the disabled topics, which are never published to
   */
  public ArchiveImportProcessor(KafkaTemplate<String, String> kafkaTemplate,
      ArchiveTopicResolver topicResolver, ObjectMapper objectMapper, MeterRegistry meterRegistry,
      ArchiveImporterProperties properties, OdeKafkaProperties odeKafkaProperties) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicResolver = topicResolver;
    this.objectMapper = objectMapper;
    this.meterRegistry = meterRegistry;
    this.properties = properties;
    this.disabledTopics = odeKafkaProperties.getDisabledTopics() == null ? Set.of()
        : odeKafkaProperties.getDisabledTopics();
  }

  /**
   * The outcome of importing one file.
   *
   * @param published the number of records published to Kafka
   * @param skipped   the number of lines that could not be imported
   */
  public record ImportResult(long published, long skipped) {
  }

  private record Outgoing(String topic, String value, String format) {
  }

  /**
   * Imports every ready file in the inbox and its subdirectories, in file name order, so that the
   * records of a topic partition are published in offset order. Imported files are moved to the
   * backup directory and files that fail are moved to the failed directory, keeping their path
   * relative to the inbox.
   *
   * @param inbox     the inbox directory
   * @param backupDir the directory for imported files
   * @param failedDir the directory for files that could not be imported
   * @return the number of files imported
   */
  public int processDirectory(Path inbox, Path backupDir, Path failedDir) {
    List<Path> files;
    try (Stream<Path> walk = Files.walk(inbox)) {
      files = walk.filter(Files::isRegularFile).filter(this::isReady).sorted().toList();
    } catch (IOException e) {
      log.error("Unable to list the archive inbox {}", inbox, e);
      return 0;
    }

    int imported = 0;
    for (Path file : files) {
      Path relative = inbox.relativize(file);
      try {
        ImportResult result = importFile(file);
        log.info("Imported archive file {}: {} records published, {} lines skipped", relative,
            result.published(), result.skipped());
        moveFile(file, backupDir.resolve(relative));
        imported++;
      } catch (ArchiveImportException | IOException e) {
        log.error("Failed to import archive file {}: {}", relative, e.getMessage());
        try {
          moveFile(file, failedDir.resolve(relative));
        } catch (IOException moveException) {
          log.error("Unable to move archive file {} to {}", file, failedDir, moveException);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.warn("Archive import interrupted, {} is retried on the next scan", relative);
        break;
      }
    }

    removeEmptyDirectories(inbox);
    return imported;
  }

  /**
   * Imports one archive file.
   *
   * @param file the archive file
   * @return the number of published records and skipped lines
   * @throws ArchiveImportException if the topic is not supported, no line could be imported, or
   *                                Kafka did not acknowledge the records
   * @throws IOException            if the file cannot be read
   * @throws InterruptedException   if interrupted while waiting for Kafka
   */
  public ImportResult importFile(Path file)
      throws ArchiveImportException, IOException, InterruptedException {
    Optional<ArchiveFileName> fileName = ArchiveFileName.parse(file.getFileName().toString());
    TopicTarget target = null;
    if (fileName.isPresent()) {
      String topic = fileName.get().topic();
      target = topicResolver.resolve(topic)
          .orElseThrow(() -> new ArchiveImportException("Unsupported topic " + topic));
    }
    String sourceTopic = target == null ? "unknown" : target.topic();

    long published = 0;
    long skipped = 0;
    List<CompletableFuture<?>> pending = new ArrayList<>();
    try (BufferedReader reader = openReader(file)) {
      String line;
      long lineNumber = 0;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        if (line.isBlank()) {
          continue;
        }
        Outgoing outgoing;
        try {
          outgoing = route(line, target);
        } catch (ArchiveImportException | JsonProcessingException e) {
          skipped++;
          count(sourceTopic, "invalid", "skipped");
          if (skipped <= MAX_LOGGED_SKIPS) {
            log.warn("Skipping line {} of {}: {}", lineNumber, file.getFileName(),
                e.getMessage());
          }
          continue;
        }
        if (disabledTopics.contains(outgoing.topic())) {
          throw new ArchiveImportException("Topic " + outgoing.topic() + " is disabled");
        }
        pending.add(kafkaTemplate.send(outgoing.topic(), outgoing.value()));
        count(outgoing.topic(), outgoing.format(), "published");
        published++;
        if (pending.size() >= properties.getSendBatchSize()) {
          awaitAcknowledgements(pending);
        }
      }
    }
    awaitAcknowledgements(pending);

    if (published == 0 && skipped > 0) {
      throw new ArchiveImportException("None of the " + skipped + " lines could be imported");
    }
    return new ImportResult(published, skipped);
  }

  private Outgoing route(String line, TopicTarget target)
      throws ArchiveImportException, JsonProcessingException {
    JsonNode node = objectMapper.readTree(line);
    LineFormat format = ArchiveRecordConverter.detect(node);

    if (target == null) {
      if (format != LineFormat.COMPACT) {
        throw new ArchiveImportException(
            "Only compact records can be imported from a file without the archive file name");
      }
      return toRawTopic(ArchiveRecordConverter.fromCompact(node, null), "compact");
    }

    return switch (target.kind()) {
      case RAW_ENCODED -> {
        if (format != LineFormat.FULL) {
          throw new ArchiveImportException("Expected a raw encoded record");
        }
        yield new Outgoing(target.topic(), line, "raw");
      }
      case DECODED_MESSAGE -> switch (format) {
        case COMPACT -> toRawTopic(
            ArchiveRecordConverter.fromCompact(node, target.messageType()), "compact");
        case FULL -> properties.isRedecodeFullRecords()
            ? toRawTopic(ArchiveRecordConverter.fromDecoded(node, target.messageType()),
                "redecoded")
            : new Outgoing(target.topic(), line, "decoded");
        case UNKNOWN -> throw new ArchiveImportException("Unrecognized record");
      };
      case DECODED_OTHER -> {
        if (format != LineFormat.FULL) {
          throw new ArchiveImportException("Expected a full record");
        }
        yield new Outgoing(target.topic(), line, "decoded");
      }
    };
  }

  private Outgoing toRawTopic(ConvertedRecord converted, String format) {
    return new Outgoing(topicResolver.rawEncodedTopic(converted.messageType()), converted.json(),
        format);
  }

  private void awaitAcknowledgements(List<CompletableFuture<?>> pending)
      throws ArchiveImportException, InterruptedException {
    if (pending.isEmpty()) {
      return;
    }
    try {
      CompletableFuture.allOf(pending.toArray(CompletableFuture[]::new))
          .get(properties.getSendTimeoutSeconds(), TimeUnit.SECONDS);
    } catch (ExecutionException e) {
      throw new ArchiveImportException("Kafka rejected a record: " + e.getCause().getMessage(), e);
    } catch (TimeoutException e) {
      throw new ArchiveImportException("Kafka did not acknowledge the records within "
          + properties.getSendTimeoutSeconds() + " seconds", e);
    }
    pending.clear();
  }

  private void count(String topic, String format, String result) {
    Counter.builder("ode.archive.import.records")
        .description("Records read from imported S3 archive files")
        .tags("topic", topic, "format", format, "result", result)
        .register(meterRegistry)
        .increment();
  }

  private static BufferedReader openReader(Path file) throws IOException {
    InputStream in = new BufferedInputStream(Files.newInputStream(file));
    try {
      in.mark(2);
      int first = in.read();
      int second = in.read();
      in.reset();
      if (first == 0x1f && second == 0x8b) {
        in = new GZIPInputStream(in, 65536);
      }
      return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    } catch (IOException e) {
      in.close();
      throw e;
    }
  }

  private boolean isReady(Path file) {
    String name = file.getFileName().toString();
    if (name.startsWith(".") || name.endsWith(".part") || name.endsWith(".tmp")) {
      return false;
    }
    return isOlderThanMinAge(file);
  }

  private boolean isOlderThanMinAge(Path path) {
    try {
      Instant modified = Files.getLastModifiedTime(path).toInstant();
      return !modified.isAfter(
          Instant.now().minus(Duration.ofSeconds(properties.getMinFileAgeSeconds())));
    } catch (IOException e) {
      return false;
    }
  }

  private void removeEmptyDirectories(Path inbox) {
    try (Stream<Path> walk = Files.walk(inbox)) {
      walk.sorted(Comparator.reverseOrder())
          .filter(dir -> !dir.equals(inbox) && Files.isDirectory(dir) && isOlderThanMinAge(dir))
          .forEach(dir -> {
            try (Stream<Path> entries = Files.list(dir)) {
              if (entries.findAny().isEmpty()) {
                Files.delete(dir);
              }
            } catch (IOException e) {
              log.debug("Unable to remove directory {}", dir, e);
            }
          });
    } catch (IOException e) {
      log.debug("Unable to clean up the archive inbox {}", inbox, e);
    }
  }

  private static void moveFile(Path file, Path target) throws IOException {
    Files.createDirectories(target.getParent());
    Path destination = target;
    if (Files.exists(destination)) {
      destination = target.resolveSibling(
          System.currentTimeMillis() + "-" + target.getFileName().toString());
    }
    Files.move(file, destination);
  }
}
