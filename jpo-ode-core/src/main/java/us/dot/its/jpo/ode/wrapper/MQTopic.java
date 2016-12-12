package us.dot.its.jpo.ode.wrapper;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeObject;

public class MQTopic extends OdeObject {

   private static final long serialVersionUID = 2740074498632994276L;
   
   private String name;
   private Integer partitions;

   private MQTopic(String name, Integer partitions) {
      super();
      this.name = name;
      this.partitions = partitions;
   }

   public static MQTopic create(String topicName, Integer partitions) {
/* 
 * For now, we just create a topic name and let the topic be created automatically
 * when the first data item is posted to it. In case we need to create the topic
 * in advance the following code should do the job but it's not been tested.
 * For now, we'll keep it commented out.
 */
//      // Create a ZooKeeper client
//      int sessionTimeoutMs = 10000;
//      int connectionTimeoutMs = 10000;
//      String zkClientUrl = AppContext.getInstance().getParam(AppContext.ZK_CONNECTION_STRINGS);
//      ZkClient zkClient = new ZkClient(zkClientUrl);
//      
//      // Create a topic named "myTopic" with 8 partitions and a replication factor of 3
//      int replicationFactor = 3;
//      Properties topicConfig = new Properties();
//      AdminUtils.createTopic(zkClient, topicName, partitions, 
//            replicationFactor, topicConfig);
      return new MQTopic(topicName, partitions);
   }

   public String getName() {
      return name;
   }

   public MQTopic setName(String name) {
      this.name = name;
      return this;
   }

   public Integer getPartitions() {
      return partitions;
   }
   
   public static int defaultPartitions() {
      return AppContext.getInstance().getInt(AppContext.KAFKA_CONSUMER_THREADS, 
            AppContext.DEFAULT_KAFKA_CONSUMER_THREADS);
   }

   public MQTopic setPartitions(Integer partitions) {
      this.partitions = partitions;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result
            + ((partitions == null) ? 0 : partitions.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MQTopic other = (MQTopic) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (partitions == null) {
         if (other.partitions != null)
            return false;
      } else if (!partitions.equals(other.partitions))
         return false;
      return true;
   }
}
