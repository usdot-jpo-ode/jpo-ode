package us.dot.its.jpo.ode.kafka;

import java.io.Serializable;

public class Partition implements Serializable {

   private static final long serialVersionUID = 2374953245650072140L;
   public final Broker host;
   public final int partition;

   public Partition(Broker host, int partition) {
      this.host = host;
      this.partition = partition;
   }

   @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((host == null) ? 0 : host.hashCode());
	result = prime * result + partition;
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
	Partition other = (Partition) obj;
	if (host == null) {
		if (other.host != null)
			return false;
	} else if (!host.equals(other.host))
		return false;
	if (partition != other.partition)
		return false;
	return true;
}

@Override
   public String toString() {
      return "Partition{" + "host=" + host + ", partition=" + partition + '}';
   }

   public String getId() {
      return "partition_" + partition;
   }

}