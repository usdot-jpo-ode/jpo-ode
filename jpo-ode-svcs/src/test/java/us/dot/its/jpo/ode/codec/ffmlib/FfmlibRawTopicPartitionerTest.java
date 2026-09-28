package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.junit.jupiter.api.Test;

class FfmlibRawTopicPartitionerTest {

  @Test
  void roundRobinsNullKeysPerTopicAndUsesCurrentMetadata() {
    FfmlibRawTopicPartitioner partitioner = new FfmlibRawTopicPartitioner();
    Cluster cluster = cluster("raw", 4, Set.of());

    assertEquals(List.of(0, 1, 2, 3, 0), List.of(
        partition(partitioner, "raw", null, cluster),
        partition(partitioner, "raw", null, cluster),
        partition(partitioner, "raw", null, cluster),
        partition(partitioner, "raw", null, cluster),
        partition(partitioner, "raw", null, cluster)));
    assertEquals(0, partition(partitioner, "other", null, cluster("other", 4, Set.of())));
    assertTrue(Set.of(0, 2).contains(partition(partitioner, "raw", null,
        cluster("raw", 4, Set.of(1, 3)))));
  }

  @Test
  void preservesKeyedHashPartitionAndBalancesConcurrentNullKeys() throws Exception {
    FfmlibRawTopicPartitioner partitioner = new FfmlibRawTopicPartitioner();
    Cluster cluster = cluster("raw", 4, Set.of());
    byte[] key = "stable-key".getBytes(java.nio.charset.StandardCharsets.UTF_8);
    int keyedPartition = partitioner.partition("raw", "stable-key", key, "value",
        new byte[] {1}, cluster);
    assertEquals(keyedPartition, partitioner.partition("raw", "stable-key", key, "value",
        new byte[] {2}, cluster));

    int threads = 8;
    int sendsPerThread = 1000;
    AtomicIntegerArray counts = new AtomicIntegerArray(4);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(threads);
    try (var executor = Executors.newFixedThreadPool(threads)) {
      for (int thread = 0; thread < threads; thread++) {
        executor.execute(() -> {
          try {
            start.await();
            for (int send = 0; send < sendsPerThread; send++) {
              counts.incrementAndGet(partition(partitioner, "raw", null, cluster));
            }
          } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
          } finally {
            done.countDown();
          }
        });
      }
      start.countDown();
      assertTrue(done.await(10, TimeUnit.SECONDS));
    }
    for (int partition = 0; partition < 4; partition++) {
      assertEquals(threads * sendsPerThread / 4, counts.get(partition));
    }
  }

  private static int partition(FfmlibRawTopicPartitioner partitioner, String topic, byte[] key,
      Cluster cluster) {
    return partitioner.partition(topic, key, key, "value", new byte[] {1}, cluster);
  }

  private static Cluster cluster(String topic, int count, Set<Integer> unavailable) {
    Node[] nodes = new Node[count];
    List<PartitionInfo> partitions = new ArrayList<>();
    for (int index = 0; index < count; index++) {
      nodes[index] = new Node(index, "broker" + index, 9092 + index);
    }
    for (int index = 0; index < count; index++) {
      Node leader = unavailable.contains(index) ? null : nodes[index];
      partitions.add(new PartitionInfo(topic, index, leader, nodes, nodes));
    }
    return new Cluster("test", List.of(nodes), partitions, Set.of(), Set.of());
  }
}
