package us.dot.its.jpo.ode.udp.vsd;

public class VsdDepositorTest {

 //TODO open-ode
//   @Tested
//   VsdDepositor testVsdDepositor;
//
//   @Injectable
//   OdeProperties injectableOdeProperties;
//
//   @Capturing
//   MessageConsumer<?,?> capturingMessageConsumer;
//
//   @Capturing
//   VehSitDataMessageDeserializer capturingVehSitDataMessageDeserializer;
//   @Capturing
//   Coder capturingCoder;
//
//   @Mocked
//   EncodeFailedException mockEncodeFailedException;
//   @Mocked
//   VehSitDataMessage mockVehSitDataMessage;

   //TODO open-ode
//   @Test
//   public void getDialogIdShouldReturnVSDDialog() {
//      assertEquals(SemiDialogID.vehSitData, testVsdDepositor.getDialogId());
//   }

   //TODO open-ode
//   @Test
//   public void getRequestIDUsesDeserializer() {
//      TemporaryID expectedID = new TemporaryID();
//      new Expectations() {
//         {
//            capturingVehSitDataMessageDeserializer.deserialize(null, (byte[]) any);
//            result = mockVehSitDataMessage;
//
//            mockVehSitDataMessage.getRequestID();
//            result = expectedID;
//         }
//      };
//
//      assertEquals(expectedID, testVsdDepositor.getRequestId(new byte[0]));
//   }
//
//   @Test
//   public void testEncodeMessageException() {
//      //TODO open-ode
//      try {
//         new Expectations() {
//            {
//               capturingVehSitDataMessageDeserializer.deserialize(null, (byte[]) any);
//
//               capturingCoder.encode((AbstractData) any);
//               result = mockEncodeFailedException;
//            }
//         };
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//
//      assertNull(testVsdDepositor.encodeMessage(new byte[0]));
//   }

//   @Test
//   public void testEncodeMessageSuccess() {
//      try {
//         new Expectations() {
//            {
//               capturingVehSitDataMessageDeserializer.deserialize(null, (byte[]) any);
//
//               capturingCoder.encode((AbstractData) any).array();
//               result = new byte[0];
//            }
//         };
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//
//      assertNotNull(testVsdDepositor.encodeMessage(new byte[0]));
//   }
}
