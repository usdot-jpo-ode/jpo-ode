package us.dot.its.jpo.ode.wrapper;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
 
public class MQPartitioner implements Partitioner {
    public MQPartitioner (VerifiableProperties props) {
 
    }
 
    public int partition(Object key, int a_numPartitions) {
        int partition = 0;
        partition = key.hashCode() % a_numPartitions;
       return partition;
  }
 
}