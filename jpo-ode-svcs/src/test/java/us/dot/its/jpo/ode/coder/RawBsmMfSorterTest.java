package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;

public class RawBsmMfSorterTest {
   @Mocked
   RawBsmMfSorter mockRawBsmMfSorter;
   @Mocked
   OdeObject mockOdeObject;
   @Mocked
   J2735MessageFrame mockJ2735MessageFrame;
   
   @Capturing
   OssJ2735Coder j2735Coder;
   @Mocked
   OssJ2735Coder mockOssJ2735Coder;
   @Tested
   RawBsmMfSorter testRawBsmMfSorter;
   @Injectable
   OssJ2735Coder injectedOssJ2735Coder;
   @Ignore
   @Test
   public void decodeBufferedInputStreamBsm() {

      new Expectations() {
         {
            j2735Coder.decodeUPERMessageFrameStream((BufferedInputStream) any);
            result = mockJ2735MessageFrame;

            
         }
      };
    //  assertEquals( ,new RawBsmMfSorter(mockOssJ2735Coder).decodeBsm(new BufferedInputStream(new ByteArrayInputStream(new byte[0]))));
      
   //   assertEquals(mockOdeObject, new BufferedInputStream(new ByteArrayInputStream(new byte[0])));
   }

}
