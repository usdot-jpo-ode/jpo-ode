package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.udp.trust.TrustManager;
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

      testAbstractSubscriberDepositor.subscribe("this is a test topic");
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

   @Test
   public void testCall(@Capturing TrustManager capturingTrustManager,
         @Mocked ConsumerRecord<String, byte[]> mockConsumerRecord, @Capturing HexUtils mockHexUtils,
         @Capturing TemporaryID capturingTemporaryID, @Mocked TemporaryID mockTemporaryID) {
      new Expectations() {
         {
            new TemporaryID((byte[]) any);
            result = mockTemporaryID;

            mockTemporaryID.byteArrayValue();
            result = new byte[] { 0 };

            mockConsumerRecord.value();
            result = new byte[] { 0 };
         }
      };
      testAbstractSubscriberDepositor.setRecord(mockConsumerRecord);
      assertTrue(testAbstractSubscriberDepositor.call() instanceof byte[]);
   }

}
