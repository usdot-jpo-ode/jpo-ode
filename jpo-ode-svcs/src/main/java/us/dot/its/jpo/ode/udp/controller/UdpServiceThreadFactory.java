package us.dot.its.jpo.ode.udp.controller;

import java.util.concurrent.ThreadFactory;

public class UdpServiceThreadFactory implements ThreadFactory {

   private String threadName;
   
   public UdpServiceThreadFactory(String name) {
      this.threadName = name;
   }

   @Override
   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName(this.threadName);
      return t;
   }

   public String getThreadName() {
      return threadName;
   }

   public void setThreadName(String threadName) {
      this.threadName = threadName;
   }
}