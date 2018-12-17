package us.dot.its.jpo.ode.udp.isd;

public class IsdDepositorTest {

   //TODO open-ode
//   @Tested
//   IsdDepositor testIsdDepositor;
//
//   @Injectable
//   OdeProperties injectableOdeProperties;
//
//   @Capturing
//   MessageConsumer<?, ?> capturingMessageConsumer;
//
//   @Capturing
//   IntersectionSituationDataDeserializer capturingIntersectionSituationDataDeserializer;
//
//   @Capturing
//   J2735 capturingJ2735;
//   @Capturing
//   TrustManager capturingTrustManager;
//
//   @Mocked
//   PERUnalignedCoder mockPERUnalignedCoder;
//   @Capturing
//   PERUnalignedCoder capturingPERUnalignedCoder;
//   @Capturing
//   InetSocketAddress capturingInetSocketAddress;
//   @Capturing
//   DatagramSocket capturingDatagramSocket;
//   @Capturing
//   Executors capturingExecutors;
//   @Mocked
//   IntersectionSituationData mockIntersectionSituationData;
//   @Mocked
//   Future<AbstractData> mockFuture;
//
//   @Mocked
//   ExecutorService mockExecutorService;
//
//   @Mocked
//   DataReceipt mockDataReceipt;
//
//   @Mocked
//   TemporaryID mockTemporaryID;
//
//   @Mocked
//   EncodeFailedException mockEncodeFailedException;
//   @Mocked
//   DecodeFailedException mockDecodeFailedException;
//   
//   @Capturing
//   Coder capturingCoder;
//
//   @Test
//   public void shouldReturnCorrectSemiDialogID() {
//      assertEquals(SemiDialogID.intersectionSitDataDep, testIsdDepositor.getDialogId());
//   }
//
//   @Test
//   public void testGetRequestID() {
//
//      TemporaryID expectedID = new TemporaryID();
//      new Expectations() {
//         {
//            //TODO open-ode
////            capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
////            result = mockIntersectionSituationData;
//
//            mockIntersectionSituationData.getRequestID();
//            result = expectedID;
//         }
//      };
//
//      assertEquals(expectedID, testIsdDepositor.getRequestId(new byte[0]));
//   }
//
//   @Test
//   public void testSendDataReceiptSuccess() {
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
////               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
////               result = mockIntersectionSituationData;
//
//               mockIntersectionSituationData.getRequestID();
//               result = mockTemporaryID;
//
//               Executors.newSingleThreadExecutor();
//               result = mockExecutorService;
//               mockExecutorService.submit((DataReceiptReceiver) any);
//               result = mockFuture;
//               mockFuture.get(anyLong, (TimeUnit) any);
//               result = mockDataReceipt;
//            }
//         };
//      } catch (InterruptedException | ExecutionException | TimeoutException e) {
//         fail("Unexpected exception: " + e);
//      }
//      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
//   }
//
//   @Test
//   public void testSendDataReceiptNullResult() {
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
////               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
////               result = mockIntersectionSituationData;
//
//               mockIntersectionSituationData.getRequestID();
//               result = mockTemporaryID;
//
//               Executors.newSingleThreadExecutor();
//               result = mockExecutorService;
//               mockExecutorService.submit((DataReceiptReceiver) any);
//               result = mockFuture;
//               mockFuture.get(anyLong, (TimeUnit) any);
//               result = null;
//            }
//         };
//      } catch (InterruptedException | ExecutionException | TimeoutException e) {
//         fail("Unexpected exception: " + e);
//      }
//      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
//   }
//
//   @Test
//   public void testSendDataReceiptTimeoutException() {
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
////               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
////               result = mockIntersectionSituationData;
//
//               mockIntersectionSituationData.getRequestID();
//               result = mockTemporaryID;
//
//               Executors.newSingleThreadExecutor();
//               result = mockExecutorService;
//               mockExecutorService.submit((DataReceiptReceiver) any);
//               result = mockFuture;
//               mockFuture.get(anyLong, (TimeUnit) any);
//               result = new TimeoutException();
//            }
//         };
//      } catch (InterruptedException | ExecutionException | TimeoutException e) {
//         fail("Unexpected exception: " + e);
//      }
//      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
//   }
//
//   @Test
//   public void testSendDataReceiptEncodeFailedException() {
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
////               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
////               result = mockIntersectionSituationData;
//
//               mockIntersectionSituationData.getRequestID();
//               result = mockTemporaryID;
//
//               capturingPERUnalignedCoder.encode((AbstractData) any).array();
//               result = mockEncodeFailedException;
//            }
//         };
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//      testIsdDepositor.sendDataReceipt(new byte[] { 1, 2, 3 });
//   }
//   
//   @Test
//   public void testEncodeMessageException() {
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
////               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
//
//               capturingCoder.encode((AbstractData) any);
//               result = mockEncodeFailedException;
//            }
//         };
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//
//      assertNull(testIsdDepositor.encodeMessage(new byte[0]));
//   }
//
//   @Test
//   public void testEncodeMessageSuccess() {
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
////               capturingIntersectionSituationDataDeserializer.deserialize(null, (byte[]) any);
//
//               capturingCoder.encode((AbstractData) any).array();
//               result = new byte[0];
//            }
//         };
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//
//      assertNotNull(testIsdDepositor.encodeMessage(new byte[0]));
//   }
//

}
