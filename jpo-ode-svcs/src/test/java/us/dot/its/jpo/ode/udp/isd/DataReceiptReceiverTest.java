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
package us.dot.its.jpo.ode.udp.isd;

import java.net.DatagramSocket;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class DataReceiptReceiverTest {

   //TODO open-ode
//   @Tested
//   DataReceiptReceiver testDataReceiptReceiver;
//   @Injectable
//   OdeProperties mockOdeProperties;
//   @Injectable
//   DatagramSocket mockDatagramSocket;
//
//   @Test
//   public void test(@Mocked final J2735Util mockJ2735Util, @Mocked final LoggerFactory disabledLoggerFactory) {
//      try {
//         new Expectations() {
//            {
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = new DataReceipt();
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e1) {
//         fail("Unexpected exception in expectations block.");
//      }
//      try {
//         assertTrue(testDataReceiptReceiver.processPacket(new byte[0]) instanceof DataReceipt);
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception." + e);
//      }
//   }
//
//   @Test
//   public void shouldReturnNullNotDataReceipt(@Mocked final J2735Util mockJ2735Util,
//         @Mocked final LoggerFactory disabledLoggerFactory) {
//      try {
//         new Expectations() {
//            {
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = new ServiceResponse();
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e1) {
//         fail("Unexpected exception in expectations block.");
//      }
//      try {
//         assertNull(testDataReceiptReceiver.processPacket(new byte[0]));
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception." + e);
//      }
//   }

}
