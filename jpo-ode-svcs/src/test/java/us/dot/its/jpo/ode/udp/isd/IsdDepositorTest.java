package us.dot.its.jpo.ode.udp.isd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.udp.trust.TrustManager;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.serdes.IntersectionSituationDataDeserializer;

public class IsdDepositorTest {

   @Tested
   IsdDepositor testIsdDepositor;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessageConsumer<?, ?> capturingMessageConsumer;

   @Capturing
   IntersectionSituationDataDeserializer capturingIntersectionSituationDataDeserializer;

   @Capturing
   J2735 capturingJ2735;
   @Capturing
   TrustManager capturingTrustManager;

   @Mocked
   PERUnalignedCoder mockPERUnalignedCoder;
   @Capturing
   PERUnalignedCoder capturingPERUnalignedCoder;
   @Capturing
   InetSocketAddress capturingInetSocketAddress;
   @Capturing
   DatagramSocket capturingDatagramSocket;
   @Capturing
   Executors capturingExecutors;
   @Mocked
   IntersectionSituationData mockIntersectionSituationData;
   @Mocked
   Future<AbstractData> mockFuture;

   @Mocked
   ExecutorService mockExecutorService;

   @Mocked
   DataReceipt mockDataReceipt;

   @Mocked
   TemporaryID mockTemporaryID;

   @Capturing
   Coder capturingCoder;

   @Test
   public void shouldReturnCorrectSemiDialogID() {
      assertEquals(SemiDialogID.intersectionSitDataDep, testIsdDepositor.getDialogId());
   }

   @Test
   public void testGetRequestID() {

      TemporaryID expectedID = new TemporaryID();
      new Expectations() {
         {
            capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
            result = mockIntersectionSituationData;

            mockIntersectionSituationData.getRequestID();
            result = expectedID;
         }
      };

      assertEquals(expectedID, testIsdDepositor.getRequestId(new byte[0]));
   }

   @Test
   public void testSendDataReceiptSuccess() {
      try {
         new Expectations() {
            {
               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
               result = mockIntersectionSituationData;

               mockIntersectionSituationData.getRequestID();
               result = mockTemporaryID;

               Executors.newSingleThreadExecutor();
               result = mockExecutorService;
               mockExecutorService.submit((DataReceiptReceiver) any);
               result = mockFuture;
               mockFuture.get(anyLong, (TimeUnit) any);
               result = mockDataReceipt;
            }
         };
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testSendDataReceiptNullResult() {
      try {
         new Expectations() {
            {
               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
               result = mockIntersectionSituationData;

               mockIntersectionSituationData.getRequestID();
               result = mockTemporaryID;

               Executors.newSingleThreadExecutor();
               result = mockExecutorService;
               mockExecutorService.submit((DataReceiptReceiver) any);
               result = mockFuture;
               mockFuture.get(anyLong, (TimeUnit) any);
               result = null;
            }
         };
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testSendDataReceiptTimeoutException() {
      try {
         new Expectations() {
            {
               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
               result = mockIntersectionSituationData;

               mockIntersectionSituationData.getRequestID();
               result = mockTemporaryID;

               Executors.newSingleThreadExecutor();
               result = mockExecutorService;
               mockExecutorService.submit((DataReceiptReceiver) any);
               result = mockFuture;
               mockFuture.get(anyLong, (TimeUnit) any);
               result = new TimeoutException();
            }
         };
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testEncodeMessageException() {
      try {
         new Expectations() {
            {
               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);

               capturingCoder.encode((AbstractData) any);
            }
         };
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }

      assertNotNull(testIsdDepositor.encodeMessage(new byte[0]));
   }

   @Test
   public void testEncodeMessageSuccess() {
      try {
         new Expectations() {
            {
               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);

               capturingCoder.encode((AbstractData) any).array();
               result = new byte[0];
            }
         };
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }

      assertNotNull(testIsdDepositor.encodeMessage(new byte[0]));
   }


}
