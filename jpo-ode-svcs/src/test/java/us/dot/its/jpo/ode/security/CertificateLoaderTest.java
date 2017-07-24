package us.dot.its.jpo.ode.security;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.cert.FileCertificateStore;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class CertificateLoaderTest {
   
   @Tested
   CertificateLoader testCertificateLoader;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   
   @Capturing
   FileCertificateStore capturingFileCertificateStore;
   
   @Mocked Path mockPath;

   @Test
   public void loadCertTrue() {
      try {
         new Expectations() {{
            FileCertificateStore.load((CryptoProvider) any, anyString, (Path) any, (Path) any, (Path) any);
            result = true;
         }};
         assertTrue(testCertificateLoader.loadCert(null, "my name is jonas", mockPath, mockPath, mockPath));
         } catch (DecodeFailedException | EncodeFailedException | CertificateException | IOException | DecoderException
            | CryptoException | DecodeNotSupportedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void loadCert3ArgsTrue() {
      try {
         new Expectations() {{
            FileCertificateStore.load((CryptoProvider) any, anyString, (Path) any, null, null);
            result = true;
         }};
         assertTrue(testCertificateLoader.loadCert(null, "my name is jonas", mockPath));
         } catch (DecodeFailedException | EncodeFailedException | CertificateException | IOException | DecoderException
            | CryptoException | DecodeNotSupportedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   };

}
