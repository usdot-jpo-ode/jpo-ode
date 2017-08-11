package us.dot.its.jpo.ode.udp.isd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
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

public class IsdDepositorTest {

   @Tested
   IsdDepositor testIsdDepositor;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessageConsumer<?, ?> capturingMessageConsumer;

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

   @Mocked
   EncodeFailedException mockEncodeFailedException;
   @Mocked
   DecodeFailedException mockDecodeFailedException;

   @Test
   public void shouldReturnCorrectSemiDialogID() {
      assertEquals(SemiDialogID.intersectionSitDataDep, testIsdDepositor.getDialogId());
   }

   @Test
   public void shouldReturnRequestID() {

      try {
         new Expectations() {
            {

               capturingPERUnalignedCoder.decode((ByteArrayInputStream) any, (IntersectionSituationData) any);
               result = mockIntersectionSituationData;

               mockIntersectionSituationData.getRequestID();
               result = mockTemporaryID;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }

      IsdDepositor testRequestIsdDepositor = new IsdDepositor(injectableOdeProperties);
      testRequestIsdDepositor.getRequestId(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testGetRequestIDThrowsException() {
      try {
         new Expectations() {
            {
               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.decode((ByteArrayInputStream) any, (IntersectionSituationData) any);
               result = mockDecodeFailedException;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      IsdDepositor testRequestIsdDepositor = new IsdDepositor(injectableOdeProperties);
      testRequestIsdDepositor.getRequestId(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testSendDataReceiptSuccess() {
      try {
         new Expectations() {
            {
               capturingPERUnalignedCoder.decode((InputStream) any, (AbstractData) any);
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
      } catch (DecodeFailedException | DecodeNotSupportedException | InterruptedException | ExecutionException
            | TimeoutException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testSendDataReceiptNullResult() {
      try {
         new Expectations() {
            {
               capturingPERUnalignedCoder.decode((InputStream) any, (AbstractData) any);
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
      } catch (DecodeFailedException | DecodeNotSupportedException | InterruptedException | ExecutionException
            | TimeoutException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testSendDataReceiptTimeoutException() {
      try {
         new Expectations() {
            {
               capturingPERUnalignedCoder.decode((InputStream) any, (AbstractData) any);
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
      } catch (DecodeFailedException | DecodeNotSupportedException | InterruptedException | ExecutionException
            | TimeoutException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testSendDataReceiptEncodeFailedException() {
      try {
         new Expectations() {
            {
               capturingPERUnalignedCoder.decode((InputStream) any, (AbstractData) any);
               result = mockIntersectionSituationData;
               mockIntersectionSituationData.getRequestID();
               result = mockTemporaryID;
               
               capturingPERUnalignedCoder.encode((AbstractData) any).array();
               result = mockEncodeFailedException;
            }
         };
      } catch (EncodeFailedException | EncodeNotSupportedException | DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
   }

}
