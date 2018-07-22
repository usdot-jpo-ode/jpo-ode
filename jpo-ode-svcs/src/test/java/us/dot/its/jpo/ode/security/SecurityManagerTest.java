package us.dot.its.jpo.ode.security;

import static org.junit.Assert.assertEquals;
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
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class SecurityManagerTest {

   @Mocked
   IEEE1609p2Message mockIEEE1609p2Message;

   @Capturing
   IEEE1609p2Message capturingIEEE1609p2Message;

   @Test
   public void isValidFalseBecauseOldDate() {
      new Expectations() {
         {
            mockIEEE1609p2Message.getGenerationTime().getTime();
            result = (new Date().getTime() + 1000);
         }
      };

      try {
          SecurityManager.validateGenerationTime(mockIEEE1609p2Message);
          fail("expected exception");
      } catch (SecurityManagerException e) {
      }
   }

   @Test
   public void isValidTrueCorrectDate() {
      new Expectations() {
         {
            mockIEEE1609p2Message.getGenerationTime().getTime();
            result = (new Date().getTime() - 1000);
         }
      };

      try {
          SecurityManager.validateGenerationTime(mockIEEE1609p2Message);
      } catch (SecurityManagerException e) {
          fail("unexpected exception");
      }
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

         SecurityManager.decodeSignedMessage(null);
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
            }
         };
         byte[] payload = SecurityManager.getMessagePayload(new byte[] { 0 });
         assertTrue(payload.length == 0);
      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
            | CryptoException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void getMessagePayloadShouldReturnOriginalMsgIfEncodingExceptionOccured() {
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
               result = mockIEEE1609p2Message;

               mockIEEE1609p2Message.getPayload();
            }
         };

         byte[] testBytes = new byte[] { 42 };
         SecurityManager.getMessagePayload(testBytes);
      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
            | CryptoException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void getMessagePayloadShouldThrowSecurityManagerExceptionIfCertificateExceptionOccured() {
      try {
         new Expectations() {
            {
               IEEE1609p2Message.parse((byte[]) any);
            }
         };

         SecurityManager.getMessagePayload(new byte[] { 0 });
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

               mockIEEE1609p2Message.getPayload();
               result = expectedBytes;
            }
         };

         assertEquals(expectedBytes, SecurityManager.getMessagePayload(new byte[] { 42 }));
      } catch (SecurityManagerException | EncodeFailedException | MessageException | CertificateException
            | CryptoException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
