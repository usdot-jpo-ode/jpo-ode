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
package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
//TODO open-ode
//import us.dot.its.jpo.ode.udp.trust.TrustManager;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class SubscriberDepositorTest {

   @Tested
   AbstractSubscriberDepositor testAbstractSubscriberDepositor;

   @Injectable
   OdeProperties mockOdeProperties;

   @Injectable
   int mockPort;

   @Capturing
   MessageConsumer<String, byte[]> capturingMessageConsumer;

   @Test
   public void testSendToSDC(@Capturing UdpUtil capturingUdpUtil) {
      try {
         testAbstractSubscriberDepositor.sendToSdc(new byte[] { 0 });
      } catch (UdpUtilException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testSubscribe(@Capturing Executors capturingExecutors, @Mocked ExecutorService mockExecutorService) {

      new Expectations() {
         {
            Executors.newSingleThreadExecutor();
            result = mockExecutorService;

            mockExecutorService.submit((Runnable) any);
         }
      };

      testAbstractSubscriberDepositor.start("this is a test topic");
   }

   @Test
   public void testCallWhenRecordNull(@Mocked ConsumerRecord<String, byte[]> mockConsumerRecord) {
      new Expectations() {
         {
            mockConsumerRecord.value();
            result = null;
         }
      };
      testAbstractSubscriberDepositor.setRecord(mockConsumerRecord);
      assertNull(testAbstractSubscriberDepositor.call());
   }

   //TODO open-ode
//   @Ignore
//   @Test
//   public void testCall(@Capturing TrustManager capturingTrustManager,
//         @Mocked ConsumerRecord<String, byte[]> mockConsumerRecord, @Capturing HexUtils mockHexUtils,
//         @Capturing TemporaryID capturingTemporaryID, @Mocked TemporaryID mockTemporaryID) {
//      new Expectations() {
//         {
//            new TemporaryID((byte[]) any);
//            result = mockTemporaryID;
//
//            mockTemporaryID.byteArrayValue();
//            result = new byte[] { 0 };
//
//            mockConsumerRecord.value();
//            result = new byte[] { 0 };
//         }
//      };
//      testAbstractSubscriberDepositor.setRecord(mockConsumerRecord);
//      assertTrue(testAbstractSubscriberDepositor.call() instanceof byte[]);
//   }

}
