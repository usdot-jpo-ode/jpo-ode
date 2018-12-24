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
