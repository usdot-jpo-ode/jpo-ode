package us.dot.its.jpo.ode.security;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.msg.MessageException;

public class SecurityManagerFunctionalTest {
   
   @Test @Ignore
   public void test() {
      
      String hexMsg = "00143e5c7b6540002fa826e260c3165c65baa5af14967ffff0006a17fdfa1fa1007fff80000000010038c00100bb400abfff24b6fffe6400207240d10000004bf0";
      
      byte[] bMsg = HexUtils.fromHexString(hexMsg);
      
      SecurityManager sm = new SecurityManager();
      try {
         System.out.println(sm.decodeSignedMessage(bMsg));
      } catch (EncodeFailedException | MessageException | CertificateException | CryptoException
            | EncodeNotSupportedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
