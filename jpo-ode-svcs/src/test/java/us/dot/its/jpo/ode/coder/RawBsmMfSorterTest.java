package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.oss.asn1.PERUnalignedCoder;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;

public class RawBsmMfSorterTest {
   @Mocked
   OdeObject mockOdeObject;
   @Mocked
   J2735MessageFrame mockJ2735MessageFrame;
   byte[] byteArray = new byte[1];
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
   public void decodeByteArrayBsmNull(@Mocked PERUnalignedCoder coder) {
      J2735MessageFrame mf = new J2735MessageFrame();
      new Expectations() {
         {
            mockOssJ2735Coder.decodeUPERMessageFrameBytes(byteArray);
            result = mf;
         }
      };
      RawBsmMfSorter rBSMFS = new RawBsmMfSorter(mockOssJ2735Coder);
      rBSMFS.decodeBsm(byteArray);
   }

   @Test
   public void decodeByteArrayBsmNotNull() {
      
      J2735MessageFrame mf = new J2735MessageFrame();
      new Expectations() {
         {
            mockOssJ2735Coder.decodeUPERMessageFrameBytes(byteArray);
            result = mf;
         }
      };
      RawBsmMfSorter rBSMFS = new RawBsmMfSorter(mockOssJ2735Coder);
      OdeObject OdeOj = rBSMFS.decodeBsm(byteArray);

   }

}
