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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public class ConcurrentUdpReceiverTest {

   //TODO open-ode
//   @Tested
//   AbstractConcurrentUdpReceiver testAbstractConcurrentUdpReceiver;
//   @Injectable
//   DatagramSocket mockDatagramSocket;
//   @Injectable
//   int bufSize;
//
//   @Test
//   public void shouldCatchSocketIOException() {
//
//      try {
//         new Expectations() {
//            {
//               mockDatagramSocket.receive((DatagramPacket) any);
//            }
//         };
//      } catch (IOException e1) {
//         fail("Unexpected exception in expectations block.");
//      }
//
//      try {
//         testAbstractConcurrentUdpReceiver.receiveDatagram();
//         fail("Expected exception to be thrown.");
//      } catch (Exception e) {
//         assertEquals("Empty datagram packet.", e.getCause().getMessage());
//      }
//   }
}
