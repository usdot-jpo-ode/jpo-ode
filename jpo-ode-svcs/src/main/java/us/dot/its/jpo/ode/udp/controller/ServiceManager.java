/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.udp.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class ServiceManager implements UdpManager{

   private ThreadFactory threadFactory;

   public ServiceManager(ThreadFactory tf) {
      this.threadFactory = tf;
   }

   public void submit(AbstractUdpReceiverPublisher rec) {
      Executors.newSingleThreadExecutor(threadFactory).submit(rec);
   }
   
   public void submit(AbstractSubscriberDepositor dep, String... topics) {
      dep.start(topics);
   }
}
