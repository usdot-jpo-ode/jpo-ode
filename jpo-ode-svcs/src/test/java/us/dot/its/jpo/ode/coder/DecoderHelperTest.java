
package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.oss.asn1.EncodeFailedException;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Content;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;

public class DecoderHelperTest {
   @Mocked
   private SerialId mockSerialId;
   @Mocked
   Ieee1609Dot2Data mockIeee1609Dot2Data;
   @Capturing
   Ieee1609Dot2Data capturingIeee1609Dot2Data;
   @Capturing
   Oss1609dot2Coder capturingOss1609dot2Coder;
   @Capturing
   IEEE1609p2Message capturingIEEE1609p2Message;
   @Mocked
   Ieee1609Dot2Content mockIeee1609Dot2Content;
   @Mocked
   BsmLogFileParser mockBsmFileParser;

   @Test
   public void decodeBsmTestTwo() {
      new Expectations() {
         {
            capturingOss1609dot2Coder.decodeIeee1609Dot2DataBytes((byte[]) any);
            result = null;
         }
      };

      BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();
      try {
         testingBsmDecoderHelper.decode(mockBsmFileParser, mockSerialId);
      } catch (Exception e) {

         fail("Unexpected error" + e);
      }

   }

   @Test
   public void decodeBsmTestThree() {
      try {

         BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();

         testingBsmDecoderHelper.decode(mockBsmFileParser, mockSerialId);
      } catch (Exception e) {

         fail("Unexpected error" + e);
      }

   }

   @Test
   public void decodeBsmTestFour() {
      try {
         new Expectations() {
            {
               capturingOss1609dot2Coder.decodeIeee1609Dot2DataBytes((byte[]) any);
               result = mockIeee1609Dot2Data;

            }
         };

         BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();

         testingBsmDecoderHelper.decode(mockBsmFileParser, mockSerialId);
      } catch (Exception e) {
         fail("Unexpected error" + e);
      }

   }

   @Test
   public void decodeBsmTestFive() {
      try {
         new Expectations() {
            {
               capturingOss1609dot2Coder.decodeIeee1609Dot2DataBytes((byte[]) any);
               result = mockIeee1609Dot2Data;

               IEEE1609p2Message.convert((Ieee1609Dot2Data) any);

               result = null;
            }
         };

         BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();

         testingBsmDecoderHelper.decode(mockBsmFileParser, mockSerialId);
      } catch (Exception e) {
         fail("Unexpected error" + e);
      }

   }

   @Test
   public void decodeBsmTestSix() {
      try {
         new Expectations() {
            {
               capturingOss1609dot2Coder.decodeIeee1609Dot2DataBytes((byte[]) any);
               result = mockIeee1609Dot2Data;

               Ieee1609ContentValidator.getUnsecuredData((Ieee1609Dot2Content) any);
               result = new byte[0];
            }
         };

         BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();

         assertNull(testingBsmDecoderHelper.decode(mockBsmFileParser, mockSerialId));
      } catch (Exception e) {
         fail("Unexpected error" + e);
      }

   }
}
