package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

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
   OdeObject mockOdeObject;
   @Mocked
   J2735MessageFrame mockJ2735MessageFrame;
   @Mocked
   byte[] mockbyteArray;
   @Mocked
   OssJ2735Coder mockOssJ2735Coder;
   @Injectable
   OssJ2735Coder injectedOssJ2735Coder;

   @Test
   public void decodeBufferedInputStreamBsmNotNull() {
      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
      RawBsmMfSorter rBSMFS = new RawBsmMfSorter(mockOssJ2735Coder);
      OdeObject OdeOj = rBSMFS.decodeBsm(bis);

   }

   @Test
   public void decodeBufferedInputStreamBsmNull() {
      new Expectations() {
         {
            mockOssJ2735Coder.decodeUPERMessageFrameStream((BufferedInputStream) any);
            result = null;
         }
      };
      BufferedInputStream bis = new BufferedInputStream(null);
      RawBsmMfSorter rBSMFS = new RawBsmMfSorter(mockOssJ2735Coder);
      OdeObject OdeOj = rBSMFS.decodeBsm(bis);

   }

   @Test
   public void decodeByteArrayBsmNull() {
     
      
      byte[] bytes = null;
      RawBsmMfSorter rBSMFS = new RawBsmMfSorter(mockOssJ2735Coder);
      OdeObject OdeOj = rBSMFS.decodeBsm(bytes);

   }

   @Test
   public void decodeByteArrayBsmNotNull() {
      
      new Expectations() {
         {
            mockOssJ2735Coder.decodeUPERMessageFrameBytes(mockbyteArray);
            result = null;
         }
      };
      RawBsmMfSorter rBSMFS = new RawBsmMfSorter(mockOssJ2735Coder);
      OdeObject OdeOj = rBSMFS.decodeBsm(mockbyteArray);

   }

}
