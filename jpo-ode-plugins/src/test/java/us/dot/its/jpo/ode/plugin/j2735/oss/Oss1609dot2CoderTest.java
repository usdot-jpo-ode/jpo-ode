package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.COERCoder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.ValidateFailedException;
import com.oss.asn1.ValidateNotSupportedException;

import gov.usdot.asn1.generated.ieee1609dot2.Ieee1609dot2;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class Oss1609dot2CoderTest {

   @Tested
   Oss1609dot2Coder testOss1609dot2Coder;

   @Capturing
   Ieee1609dot2 capturingIeee1609dot2;

   @Mocked
   COERCoder mockCOERCoder;

   @Capturing
   COERCoder capturingCOERCoder;

   @Mocked
   Ieee1609Dot2Data mockIeee1609Dot2Data;

   @Capturing
   DatatypeConverter capturingDatatypeConverter;

   @Capturing
   LoggerFactory capturingLoggerFactory; // needed otherwise test fails

   @Test
   public void constructorGetsCoerEncoder() {
      new Expectations() {
         {
            Ieee1609dot2.getCOERCoder();
            result = mockCOERCoder;
         }
      };
      new Oss1609dot2Coder();
   }

   @Test
   public void successfulByteDecodingReturnsObject() {
      try {
         new Expectations() {
            {
               capturingCOERCoder.decode((InputStream) any, (Ieee1609Dot2Data) any);
               result = mockIeee1609Dot2Data;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected errror: " + e);
      }
      assertEquals(mockIeee1609Dot2Data, testOss1609dot2Coder.decodeIeee1609Dot2DataBytes(new byte[] { 1, 2, 3 }));
   }

   @Test
   public void successfulHexDecodingReturnsObject() {
      try {
         new Expectations() {
            {
               capturingCOERCoder.decode((InputStream) any, (Ieee1609Dot2Data) any);
               result = mockIeee1609Dot2Data;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected errror: " + e);
      }
      assertEquals(mockIeee1609Dot2Data, testOss1609dot2Coder.decodeIeee1609Dot2DataHex("test hex message"));
   }

   @Test
   public void failedInputStreamDecodingReturnsNull() {

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3}));
      assertNull(testOss1609dot2Coder.decodeIeee1609Dot2DataStream(bis));
   }

   @Test
   public void emptyInputStreamReturnsNull() {
      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));
      assertNull(testOss1609dot2Coder.decodeIeee1609Dot2DataStream(bis));
   }

   @Test
   public void successfulInputStreamDecodingReturnsObject() {
      try {
         new Expectations() {
            {
               capturingCOERCoder.decode((InputStream) any, (Ieee1609Dot2Data) any);
               result = mockIeee1609Dot2Data;
               
               mockIeee1609Dot2Data.getContent().isValid();
               result = true;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException | ValidateFailedException | ValidateNotSupportedException e) {
         fail("Unexpected errror: " + e);
      }

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3}));
      assertEquals(mockIeee1609Dot2Data, testOss1609dot2Coder.decodeIeee1609Dot2DataStream(bis));
   }

   @Test
   public void handleDecodeExceptionDecodeNotSupported(@Mocked AbstractData mockAbstractData) {
      testOss1609dot2Coder.handleDecodeException(new Exception());
   }

   @Test
   public void handleDecodeExceptionDecodeNotSupportedNull() {
      testOss1609dot2Coder.handleDecodeException(new Exception());
   }

   @Test
   public void handleDecodeExceptionDecodeNotSupportedException() {
      testOss1609dot2Coder.handleDecodeException(new Exception());
   }

   @Test
   public void handleDecodeExceptionIOException() {
      testOss1609dot2Coder.handleDecodeException(new IOException("testIOException123"));
   }

   @Test
   public void handleDecodeExceptionGenericException() {
      testOss1609dot2Coder.handleDecodeException(new Exception("testException123"));
   }

}
