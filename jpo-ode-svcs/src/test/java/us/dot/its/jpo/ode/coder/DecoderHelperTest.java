package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;

public class DecoderHelperTest {
   @Mocked
   String mockString;
   @Mocked
   private SerialId mockSerialId;
//   @Mocked
//   Ieee1609Dot2Data mockIeee1609Dot2Data; 
   @Mocked
   Oss1609dot2Coder mockOss1609dot2Coder;
 //  @Mocked
//   IEEE1609p2Message mockIEEE1609p2Message;
//   @Mocked
//   BsmDecoderPayloadHelper mockBsmDecoderPayloadHelper;
   


 
   
   @Test
   public void decodeBsmTestOne() {

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
      
      BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();
      try {
         testingBsmDecoderHelper.decode(bis, mockString, mockSerialId);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
   
   @Test
   public void decodeBsmTestTwo() {
      new Expectations() {
         {
            mockOss1609dot2Coder.decodeIeee1609Dot2DataStream((BufferedInputStream) any);
            result = null;
         }
      };
      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
      
      BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();
      try {
         testingBsmDecoderHelper.decode(bis, mockString, mockSerialId);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
   @Test
   public void decodeBsmTestThree() {
      try { 
      new Expectations() {
         {
            mockOss1609dot2Coder.decodeIeee1609Dot2DataStream((BufferedInputStream) any);
            result = null;
//            
//            IEEE1609p2Message.convert((Ieee1609Dot2Data) any);
//            result = null;
         }
      };
      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
      
      BsmDecoderHelper testingBsmDecoderHelper = new BsmDecoderHelper();
      
         testingBsmDecoderHelper.decode(bis, mockString, mockSerialId);
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
   
   
   
//
//   @Test
//   public void decodeBsmByteTest() {
//
//      byte[] testInput = { 6, 1, 1, 4, 6, 1, 1, 1, 6, 1 };
//
//      J2735MessageFrame expectedValue = new J2735MessageFrame();
//      expectedValue = null;
//      OdeObject actualValue;
//      
//      actualValue = decodeBsm((BufferedInputStream) testInput);
//
//      assertEquals(expectedValue, actualValue);
//   }
//
//   public void decodeBsmByteTestTwo() {
//
//      byte[] testInput = { 6, 1, 1, 4, 6, 1, 1, 1, 6, 1 };
//
//      J2735MessageFrame expectedValue = new J2735MessageFrame();
//      expectedValue = null;
//      OdeObject actualValue = decodeBsm(testInput);
//
//      assertEquals(expectedValue, actualValue);
//   }

}
