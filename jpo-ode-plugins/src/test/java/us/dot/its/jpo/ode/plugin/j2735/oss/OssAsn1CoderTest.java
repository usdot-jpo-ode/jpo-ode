package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class OssAsn1CoderTest {

   static OssAsn1Coder coder;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      coder = new OssAsn1Coder();
   }

   @Ignore
   @Test
   public void testOssAsn1Coder() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_DecodeBase64() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_DecodeHex() {
      String asn_hex = "401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0";

      System.out.println(coder.UPER_DecodeHex(asn_hex));

   }

   @Ignore
   @Test
   public void testUPER_DecodeBytes() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_DecodeBase64ToJson() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_DecodeHexToJson() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_DecodeBytesToJson() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_EncodeBase64() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_EncodeHex() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_EncodeBytes() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_EncodeBase64FromJson() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_EncodeHexfromJson() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void testUPER_EncodeBytesFromJson() {
      fail("Not yet implemented");
   }

}
