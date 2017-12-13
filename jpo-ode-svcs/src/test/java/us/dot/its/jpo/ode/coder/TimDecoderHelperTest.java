package us.dot.its.jpo.ode.coder;

import org.junit.Test;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class TimDecoderHelperTest {
   
   @Mocked
   TimLogFileParser mockTimLogFileParser;
   
   @Capturing
   Oss1609dot2Coder capturingOss1609dot2Coder;
   @Capturing
   IEEE1609p2Message capturingIEEE1609p2Message;
   @Capturing
   SecurityManager capturingSecurityManager;

   /**
    * Test when fails security certificate time check
    */
   @Test
   public void testDecode() throws Exception {
      new Expectations() {{
         capturingOss1609dot2Coder.decodeIeee1609Dot2DataBytes((byte[]) any);
         result = new Ieee1609Dot2Data();
         
         IEEE1609p2Message.convert((Ieee1609Dot2Data) any);
         result = new IEEE1609p2Message();
         
         SecurityManager.validateGenerationTime((IEEE1609p2Message) any);
         result = new SecurityManagerException("testException123");
      }};
      new TimDecoderHelper().decode(mockTimLogFileParser, new SerialId());
   }

}
