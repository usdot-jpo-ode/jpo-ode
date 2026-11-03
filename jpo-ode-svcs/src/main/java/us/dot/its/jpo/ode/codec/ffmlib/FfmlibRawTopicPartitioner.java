package us.dot.its.jpo.ode.codec.ffmlib;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

/** Balances null-key FFM raw records while preserving Kafka's keyed partition hash. */
public class FfmlibRawTopicPartitioner implements Partitioner {

  private final Map<String, AtomicInteger> nextPartitionByTopic = new ConcurrentHashMap<>();

  @Override
  public void configure(Map<String, ?> configs) {
    // No additional configuration is needed.
  }

  @Override
  public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes,
      Cluster cluster) {
    List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
    if (partitions == null || partitions.isEmpty()) {
      throw new IllegalStateException("No partitions available for FFM raw topic " + topic);
    }
    if (keyBytes != null) {
      return Utils.toPositive(Utils.murmur2(keyBytes)) % partitions.size();
    }

    List<PartitionInfo> available = partitions.stream()
        .filter(partition -> partition.leader() != null)
        .toList();
    List<PartitionInfo> candidates = available.isEmpty() ? partitions : available;
    int index = nextPartitionByTopic.computeIfAbsent(topic, ignored -> new AtomicInteger())
        .getAndIncrement();
    return candidates.get(Math.floorMod(index, candidates.size())).partition();
  }

  @Override
  public void close() {
    nextPartitionByTopic.clear();
  }
}
