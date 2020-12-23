package us.dot.its.jpo.ode.udp.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class ServiceManager implements UdpManager{

   private ThreadFactory threadFactory;

   public ServiceManager(ThreadFactory tf) {
      this.threadFactory = tf;
   }

   public void submit(AbstractUdpReceiverPublisher rec) {
      Executors.newSingleThreadExecutor(threadFactory).submit(rec);
   }
}
