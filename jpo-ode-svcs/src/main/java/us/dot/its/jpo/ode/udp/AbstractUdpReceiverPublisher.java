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
package us.dot.its.jpo.ode.udp;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SocketUtils;

import us.dot.its.jpo.ode.OdeProperties;

public abstract class AbstractUdpReceiverPublisher implements Runnable {

   public class UdpReceiverException extends Exception {
      private static final long serialVersionUID = 1L;

      public UdpReceiverException(String string, Exception e) {
         super(string, e);
      }
   }

   private static Logger logger = LoggerFactory.getLogger(AbstractUdpReceiverPublisher.class);

   protected DatagramSocket socket;

   protected String senderIp;
   protected int senderPort;

   protected OdeProperties odeProperties;
   protected int port;
   protected int bufferSize;

   private boolean stopped = false;

   public boolean isStopped() {
      return stopped;
   }

   public void setStopped(boolean stopped) {
      this.stopped = stopped;
   }

   @Autowired
   public AbstractUdpReceiverPublisher(OdeProperties odeProps, int port, int bufferSize) {
      this.odeProperties = odeProps;
      this.port = port;
      this.bufferSize = bufferSize;

      try {
         socket = new DatagramSocket(this.port);
         logger.info("Created UDP socket bound to port {}", this.port);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + this.port 
             + " Will use the next availabel port.", e);
         this.port = SocketUtils.findAvailableUdpPort();
         logger.info("Using alternate port {}", this.port);
         try {
           socket = new DatagramSocket(this.port);
           logger.info("Created UDP socket bound to port {}", this.port);
         } catch (SocketException e2) {
           logger.error("Error creating socket with port " + this.port, e);
         }
      }
   }

}
