package us.dot.its.jpo.ode.udp.trust;

public class TrustManagerTest {

   //TODO open-ode
//   @Injectable
//   DatagramSocket injectableDatagramSocket;
//
//   @Injectable
//   OdeProperties injectableOdeProperties;
//
//   @Mocked
//   OdeProperties mockOdeProperties;
//
//   @Test
//   public void isTrustEstablishedShouldReturnFalse() {
//      TrustManager testTrustManager = new TrustManager(injectableOdeProperties, injectableDatagramSocket);
//      assertFalse(testTrustManager.isTrustEstablished(new TemporaryID()));
//   }
//
//   @Test
//   public void isTrustEstablishedShouldeturnTrue() {
//      new Expectations() {
//         {
//            mockOdeProperties.getMessagesUntilTrustReestablished();
//            result = 1;
//
//         }
//      };
//      TemporaryID testTempID = new TemporaryID(new byte[] { 1, 2, 3 });
//
//      TrustManager testTrustManager = new TrustManager(mockOdeProperties, injectableDatagramSocket);
//
//      testTrustManager.createSession(testTempID);
//      assertTrue(testTrustManager.isTrustEstablished(testTempID));
//   }
//
//   @Test
//   public void isTrustEstablishedShouldReturnFalseTooManyMessagesSent() {
//      new Expectations() {
//         {
//            mockOdeProperties.getMessagesUntilTrustReestablished();
//            result = 0;
//
//         }
//      };
//      TemporaryID testTempID = new TemporaryID(new byte[] { 1, 2, 3 });
//
//      TrustManager testTrustManager = new TrustManager(mockOdeProperties, injectableDatagramSocket);
//
//      testTrustManager.createSession(testTempID);
//      testTrustManager.incrementSessionTracker(testTempID);
//      assertFalse(testTrustManager.isTrustEstablished(testTempID));
//   }
//
//   @Test
//   public void getSessionCountShouldReturn2() {
//      TemporaryID testTempID = new TemporaryID(new byte[] { 1, 2, 3 });
//
//      TrustManager testTrustManager = new TrustManager(injectableOdeProperties, injectableDatagramSocket);
//      testTrustManager.createSession(testTempID);
//      testTrustManager.incrementSessionTracker(testTempID);
//      testTrustManager.incrementSessionTracker(testTempID);
//      assertEquals(Integer.valueOf(2), testTrustManager.getSessionMessageCount(testTempID));
//   }
//
//   @Test
//   public void endSessionShouldRemoveSession() {
//
//      new Expectations() {
//         {
//            mockOdeProperties.getMessagesUntilTrustReestablished();
//            result = 2;
//
//         }
//      };
//      TemporaryID testTempID = new TemporaryID(new byte[] { 1, 2, 3 });
//
//      TrustManager testTrustManager = new TrustManager(mockOdeProperties, injectableDatagramSocket);
//      testTrustManager.createSession(testTempID);
//      assertTrue("Session not established", testTrustManager.isTrustEstablished(testTempID));
//      testTrustManager.endTrustSession(testTempID);
//      assertFalse("Session not ended", testTrustManager.isTrustEstablished(testTempID));
//   }
//
//   @Test
//   public void establishTrustReturnsTrueIfAlreadyExists() {
//      new Expectations() {
//         {
//            mockOdeProperties.getMessagesUntilTrustReestablished();
//            result = 2;
//
//         }
//      };
//      TemporaryID testTempID = new TemporaryID(new byte[] { 1, 2, 3 });
//
//      TrustManager testTrustManager = new TrustManager(mockOdeProperties, injectableDatagramSocket);
//      testTrustManager.createSession(testTempID);
//      assertTrue("Session not established", testTrustManager.establishTrust(testTempID, SemiDialogID.vehSitData));
//   }
//
//   @Test
//   public void testEstablishTrust(@Capturing UdpUtil capturingUdpUtil, @Capturing Executors capturingExecutors,
//         @Mocked Future<ServiceResponse> mockFuture, @Mocked ExecutorService mockExecutorService,
//         @Mocked ServiceResponse mockServiceResponse, @Mocked TemporaryID mockRequestID) {
//      try {
//         new Expectations() {
//            {
//               mockOdeProperties.getTrustRetries();
//               result = 1;
//
//               UdpUtil.send((DatagramSocket) any, (AbstractData) any, anyString, anyInt);
//
//               Executors.newSingleThreadExecutor();
//               result = mockExecutorService;
//               mockExecutorService.submit((ServiceResponseReceiver) any);
//               result = mockFuture;
//
//               mockFuture.get(anyLong, TimeUnit.SECONDS);
//               result = mockServiceResponse;
//               
//               mockServiceResponse.getRequestID();
//               result = mockRequestID;
//               
//               mockRequestID.equals((ServiceRequest) any);
//               result = true;
//               
//
//            }
//         };
//      } catch (UdpUtilException | InterruptedException | ExecutionException | TimeoutException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      TemporaryID testTempID = new TemporaryID(new byte[] { 1, 2, 3 });
//      TrustManager testTrustManager = new TrustManager(mockOdeProperties, injectableDatagramSocket);
//      testTrustManager.establishTrust(testTempID, SemiDialogID.vehSitData);
//   }

}
