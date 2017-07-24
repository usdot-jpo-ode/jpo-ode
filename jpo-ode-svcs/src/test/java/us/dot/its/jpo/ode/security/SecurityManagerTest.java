package us.dot.its.jpo.ode.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import gov.usdot.cv.security.msg.MessageException;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class SecurityManagerTest {

   @Tested
   SecurityManager testSecurityManager;

   @Mocked
   IEEE1609p2Message mockIEEE1609p2Message;

   @Capturing
   IEEE1609p2Message capturingIEEE1609p2Message;

   @Test
   public void isValidFalseBecauseNull() {
      assertFalse(testSecurityManager.isValid(null));
   }

   @Test
   public void isValidFalseBecauseOldDate() {
      new Expectations() {
         {
            mockIEEE1609p2Message.getGenerationTime().getTime();
            result = (new Date().getTime() + 1000);
         }
      };

      assertFalse(testSecurityManager.isValid(mockIEEE1609p2Message));
   }

   @Test
   public void isValidFalseExceptionOccured(@Mocked Exception mockException) {
      new Expectations() {
         {
            mockIEEE1609p2Message.getGenerationTime().getTime();
            result = mockException;
         }
      };

      assertFalse(testSecurityManager.isValid(mockIEEE1609p2Message));
   }

   @Test
   public void isValidTrueCorrectDate() {
      new Expectations() {
         {
            mockIEEE1609p2Message.getGenerationTime().getTime();
            result = (new Date().getTime() - 1000);
         }
      };

      assertTrue(testSecurityManager.isValid(mockIEEE1609p2Message));
   }

   @Test
   public void decodeSignedMessageShouldCallParse() {
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
               times = 1;
            }
         };

         testSecurityManager.decodeSignedMessage(null);
      } catch (EncodeFailedException | MessageException | CertificateException | CryptoException
            | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void getMessagePayloadShouldReturnNullInvalid() {
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
               result = mockIEEE1609p2Message;

               mockIEEE1609p2Message.getGenerationTime().getTime();
               result = (new Date().getTime() + 1000);
            }
         };
         assertNull(testSecurityManager.getMessagePayload(new byte[] { 0 }));
      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
            | CryptoException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void getMessagePayloadShouldReturnOriginalMsgIfEncodingExceptionOccured(
         @Mocked EncodeFailedException mockEncodeFailedException) {
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
               result = mockIEEE1609p2Message;

               mockIEEE1609p2Message.getGenerationTime().getTime();
               result = (new Date().getTime() - 1000);

               mockIEEE1609p2Message.getPayload();
               result = mockEncodeFailedException;
            }
         };

         byte[] testBytes = new byte[] { 42 };
         assertEquals(testBytes, testSecurityManager.getMessagePayload(testBytes));
      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
            | CryptoException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void getMessagePayloadShouldThrowSecurityManagerExceptionIfCertificateExceptionOccured(
         @Mocked CertificateException mockCertificateException) {
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
               result = mockCertificateException;
            }
         };

         testSecurityManager.getMessagePayload(new byte[] { 0 });
         fail("Expected SecurityManagerException");
      } catch (EncodeFailedException | MessageException | CertificateException | CryptoException
            | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      } catch (SecurityManagerException e) {
         assertTrue(e.getCause() instanceof CertificateException);
      }
   }

   @Test
   public void getMessagePayloadShouldReturnMessagePayload() {

      byte[] expectedBytes = new byte[] { 123 };
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
               result = mockIEEE1609p2Message;

               mockIEEE1609p2Message.getGenerationTime().getTime();
               result = (new Date().getTime() - 1000);

               mockIEEE1609p2Message.getPayload();
               result = expectedBytes;
            }
         };

         assertEquals(expectedBytes, testSecurityManager.getMessagePayload(new byte[] { 42 }));
      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
            | CryptoException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
