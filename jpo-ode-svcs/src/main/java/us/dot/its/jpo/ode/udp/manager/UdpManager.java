package us.dot.its.jpo.ode.udp.manager;

import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public interface UdpManager {

   public void submit(AbstractUdpReceiverPublisher rec);

   public void submit(AbstractSubscriberDepositor<?, ?> dep, String... topics);

}
