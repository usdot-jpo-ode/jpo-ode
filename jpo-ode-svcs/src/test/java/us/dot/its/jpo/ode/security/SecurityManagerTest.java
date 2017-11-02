package us.dot.its.jpo.ode.security;

public class SecurityManagerTest {

 //TODO open-ode
//   @Mocked
//   IEEE1609p2Message mockIEEE1609p2Message;
//
//   @Capturing
//   IEEE1609p2Message capturingIEEE1609p2Message;
//
//   @Test
//   public void isValidFalseBecauseOldDate() {
//      new Expectations() {
//         {
//            mockIEEE1609p2Message.getGenerationTime().getTime();
//            result = (new Date().getTime() + 1000);
//         }
//      };
//
//      try {
//          SecurityManager.validateGenerationTime(mockIEEE1609p2Message);
//          fail("expected exception");
//      } catch (SecurityManagerException e) {
//      }
//   }
//
//   @Test
//   public void isValidFalseExceptionOccured(@Mocked SecurityManagerException mockException) {
//      new Expectations() {
//         {
//            mockIEEE1609p2Message.getGenerationTime().getTime();
//            result = mockException;
//         }
//      };
//
//      try {
//          SecurityManager.validateGenerationTime(mockIEEE1609p2Message);
//          fail("expected exception");
//      } catch (SecurityManagerException e) {
//      }
//   }
//
//   @Test
//   public void isValidTrueCorrectDate() {
//      new Expectations() {
//         {
//            mockIEEE1609p2Message.getGenerationTime().getTime();
//            result = (new Date().getTime() - 1000);
//         }
//      };
//
//      try {
//          SecurityManager.validateGenerationTime(mockIEEE1609p2Message);
//      } catch (SecurityManagerException e) {
//          fail("unexpected exception");
//      }
//   }
//
//   @Test
//   public void decodeSignedMessageShouldCallParse() {
//      try {
//         new Expectations() {
//            {
//               IEEE1609p2Message.parse((byte[]) any);
//               times = 1;
//            }
//         };
//
//         SecurityManager.decodeSignedMessage(null);
//      } catch (EncodeFailedException | MessageException | CertificateException | CryptoException
//            | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }
//
//   @Test
//   public void getMessagePayloadShouldReturnNullInvalid() {
//      try {
//         new Expectations() {
//            {
//               IEEE1609p2Message.parse((byte[]) any);
//               result = mockIEEE1609p2Message;
//            }
//         };
//         byte[] payload = SecurityManager.getMessagePayload(new byte[] { 0 });
//         assertTrue(payload.length == 0);
//      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
//            | CryptoException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }
//
//   @Test
//   public void getMessagePayloadShouldReturnOriginalMsgIfEncodingExceptionOccured(
//         @Mocked EncodeFailedException mockEncodeFailedException) {
//      try {
//         new Expectations() {
//            {
//               IEEE1609p2Message.parse((byte[]) any);
//               result = mockIEEE1609p2Message;
//
//               mockIEEE1609p2Message.getPayload();
//               result = mockEncodeFailedException;
//            }
//         };
//
//         byte[] testBytes = new byte[] { 42 };
//         assertEquals(testBytes, SecurityManager.getMessagePayload(testBytes));
//      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
//            | CryptoException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }
//
//   @Test
//   public void getMessagePayloadShouldThrowSecurityManagerExceptionIfCertificateExceptionOccured(
//         @Mocked CertificateException mockCertificateException) {
//      try {
//         new Expectations() {
//            {
//               IEEE1609p2Message.parse((byte[]) any);
//               result = mockCertificateException;
//            }
//         };
//
//         SecurityManager.getMessagePayload(new byte[] { 0 });
//         fail("Expected SecurityManagerException");
//      } catch (EncodeFailedException | MessageException | CertificateException | CryptoException
//            | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      } catch (SecurityManagerException e) {
//         assertTrue(e.getCause() instanceof CertificateException);
//      }
//   }
//
//   @Test
//   public void getMessagePayloadShouldReturnMessagePayload() {
//
//      byte[] expectedBytes = new byte[] { 123 };
//      try {
//         new Expectations() {
//            {
//               IEEE1609p2Message.parse((byte[]) any);
//               result = mockIEEE1609p2Message;
//
//               mockIEEE1609p2Message.getPayload();
//               result = expectedBytes;
//            }
//         };
//
//         assertEquals(expectedBytes, SecurityManager.getMessagePayload(new byte[] { 42 }));
//      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
//            | CryptoException | EncodeNotSupportedException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }

}
