package us.dot.its.jpo.ode.udp.manager;

import java.util.concurrent.ThreadFactory;

public class UdpServiceThreadFactory implements ThreadFactory {

   public String threadName;
   
   public UdpServiceThreadFactory(String name) {
      this.threadName = name;
   }

   @Override
   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName(this.threadName);
      return t;
   }
}