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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpUtil {

   private UdpUtil() {
      throw new UnsupportedOperationException("Cannot instantiate static class.");
   }

   public static class UdpUtilException extends Exception {
      private static final long serialVersionUID = 1L;

      public UdpUtilException(String errMsg, Exception e) {
         super(errMsg, e);
      }
   }

   public static void send(DatagramSocket sock, byte[] msgBytes, String ip, int port)
         throws UdpUtilException {
      try {
         sock.send(new DatagramPacket(msgBytes, msgBytes.length, new InetSocketAddress(ip, port)));
      } catch (IOException e) {
         throw new UdpUtilException("Failed to encode and send message.", e);
      }
   }
}
