package us.dot.its.jpo.ode.archive;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The parts of an S3 archive object name, {@code <topic>+<partition>+<startOffset>.json[.gz]}, as
 * written by the Confluent S3 sink connector.
 *
 * @param topic       the Kafka topic the records were archived from
 * @param partition   the Kafka partition
 * @param startOffset the offset of the first record in the object
 */
public record ArchiveFileName(String topic, int partition, long startOffset) {

  private static final Pattern PATTERN =
      Pattern.compile("^(?<topic>[^+/\\\\]+)\\+(?<partition>\\d+)\\+(?<offset>\\d+)\\.json(\\.gz)?$");

  /**
   * Parses an archive object name.
   *
   * @param fileName the file name, without any directory
   * @return the parsed name, or empty if the name does not follow the archive naming
   */
  public static Optional<ArchiveFileName> parse(String fileName) {
    if (fileName == null) {
      return Optional.empty();
    }
    Matcher matcher = PATTERN.matcher(fileName);
    if (!matcher.matches()) {
      return Optional.empty();
    }
    try {
      return Optional.of(new ArchiveFileName(matcher.group("topic"),
          Integer.parseInt(matcher.group("partition")),
          Long.parseLong(matcher.group("offset"))));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
