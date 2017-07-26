package us.dot.its.jpo.ode.security;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.cert.CertificateManager;
import gov.usdot.cv.security.cert.FileCertificateStore;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
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

   @Mocked
   Path mockPath;

   @Mocked
   OdeProperties mockOdeProperties;

   @Test
   public void loadCertTrue() {
      try {
         new Expectations() {
            {
               FileCertificateStore.load((CryptoProvider) any, anyString, (Path) any, (Path) any, (Path) any);
               result = true;
            }
         };
         assertTrue(testCertificateLoader.loadCert(null, "my name is jonas", mockPath, mockPath, mockPath));
      } catch (DecodeFailedException | EncodeFailedException | CertificateException | IOException | DecoderException
            | CryptoException | DecodeNotSupportedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void loadCert3ArgsTrue() {
      try {
         new Expectations() {
            {
               FileCertificateStore.load((CryptoProvider) any, anyString, (Path) any, null, null);
               result = true;
            }
         };
         assertTrue(testCertificateLoader.loadCert(null, "my name is jonas", mockPath));
      } catch (DecodeFailedException | EncodeFailedException | CertificateException | IOException | DecoderException
            | CryptoException | DecodeNotSupportedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }
   };

   @Test
   public void testRun(@Capturing IEEE1609p2Message capturingIEEE1609p2Message,
         @Capturing CertificateManager capturingCertificateManager) {
      new Expectations() {
         {
            CertificateManager.clear();
            times = 1;

            mockOdeProperties.getCaCertPath();
            result = "testCaCertPath";
            mockOdeProperties.getSelfCertPath();
            result = "testSelfCertPath";
            mockOdeProperties.getSelfPrivateKeyReconstructionFilePath();
            result = "testSelfPrivateKeyReconstructionFilePath";
            mockOdeProperties.getSelfSigningPrivateKeyFilePath();
            result = "testSelfSigningPrivateKeyFilePath";

            IEEE1609p2Message.setSelfCertificateFriendlyName(anyString);
            times = 1;
         }
      };
      CertificateLoader runTestCertificateLoader = new CertificateLoader(mockOdeProperties);
      runTestCertificateLoader.run();
   }

   @Test
   public void testRunNoCertPaths() {
      testCertificateLoader.run();
   }

   @Test
   public void testLoadAllCerts(@Capturing Files capturingFiles, @Mocked DirectoryStream<Path> mockDirectoryStream,
         @Mocked Iterator mockIterator, @Mocked Iterator mockSubIterator, @Mocked Path mockPath, @Mocked DirectoryStream<Path> mockSubdirectoryStream ) {
      try {
         new Expectations() {
            {
               Files.newDirectoryStream((Path) any);
               returns(mockDirectoryStream, mockSubdirectoryStream);
               
               mockDirectoryStream.iterator();
               result = mockIterator;
               mockIterator.hasNext();
               returns(true, false, false);
               mockIterator.next();
               result = mockPath;
               
               mockSubdirectoryStream.iterator();
               result = mockSubIterator;
               mockSubIterator.hasNext();
               returns(true, false, false);
               mockSubIterator.next();
               result = mockPath;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
      testCertificateLoader.loadAllCerts("testCertString");
   }

}
