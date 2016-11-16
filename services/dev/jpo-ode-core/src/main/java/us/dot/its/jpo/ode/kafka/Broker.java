package us.dot.its.jpo.ode.kafka;

import java.io.Serializable;

public class Broker implements Serializable, Comparable<Broker> {

   private static final long serialVersionUID = 7765453245650072140L;
   public final String host;
   public final int port;

   public Broker(String host, int port) {
      this.host = host;
      this.port = port;
   }

   public Broker(String host) {
      this(host, 9092);
   }


   @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((host == null) ? 0 : host.hashCode());
	result = prime * result + port;
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
	Broker other = (Broker) obj;
	if (host == null) {
		if (other.host != null)
			return false;
	} else if (!host.equals(other.host))
		return false;
	if (port != other.port)
		return false;
	return true;
}

@Override
   public String toString() {
      return host + ":" + port;
   }

   public static Broker fromString(String host) {
      Broker hp;
      String[] spec = host.split(":");
      if (spec.length == 1) {
         hp = new Broker(spec[0]);
      } else if (spec.length == 2) {
         hp = new Broker(spec[0], Integer.parseInt(spec[1]));
      } else {
         throw new IllegalArgumentException("Invalid host specification: "
               + host);
      }
      return hp;
   }

   public int compareTo(Broker o) {
      if (this.host.equals(o.host)) {
         return this.port - o.port;
      } else {
         return this.host.compareTo(o.host);
      }
   }
}