package us.dot.its.jpo.ode.security;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Ignore;
import org.junit.Test;

public class SecurityManagerFunctionalTest {
   
   @Test @Ignore
   public void test() {
      
      String hexMsg = "00143e5c7b6540002fa826e260c3165c65baa5af14967ffff0006a17fdfa1fa1007fff80000000010038c00100bb400abfff24b6fffe6400207240d10000004bf0";
      
      byte[] bMsg = HexUtils.fromHexString(hexMsg);
      
      SecurityManager sm = new SecurityManager();
//TODO open-ode
//      try {
//         System.out.println(sm.decodeSignedMessage(bMsg));
//      } catch (EncodeFailedException | MessageException | CertificateException | CryptoException
//            | EncodeNotSupportedException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      }
   }

}
