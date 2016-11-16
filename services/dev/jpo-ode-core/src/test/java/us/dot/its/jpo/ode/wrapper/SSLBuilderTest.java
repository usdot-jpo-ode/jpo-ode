/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.wrapper;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class SSLBuilderTest {

   @Mocked 
   KeyStore keystore;
   
   @Mocked
   SSLContexts sslContexts;
   
   @Mocked
   TrustSelfSignedStrategy trustSelfSignedStrategy;
   
   @Test
   public void testBuildSSLContext() {
      try {
         final InputStream keystoreFile = 
               new ByteArrayInputStream("keystoreFile".getBytes());
         final String keystorePass = "keystorePass";
         
         {// BEGIN test custom SSLConext
            new Expectations() {{
               KeyStore.getInstance(KeyStore.getDefaultType());
               keystore.load(keystoreFile, keystorePass.toCharArray());
               SSLContexts.custom()
                  .loadTrustMaterial(keystore, withAny(trustSelfSignedStrategy))
                  .build();
            }};
            
            
            SSLBuilder.buildSSLContext(keystoreFile, keystorePass);
            
//            new Verifications() {{
//               KeyStore.getInstance(KeyStore.getDefaultType()); times = 1; 
//               keystore.load(keystoreFile, keystorePass.toCharArray()); times = 1;
//               SSLContexts.custom()
//                  .loadTrustMaterial(keystore, withAny(trustSelfSignedStrategy))
//                  .build(); times = 1;
//            }};
         } // END test normal path
         
         {// BEGIN test default SSLContext
            new Expectations() {{
               SSLContexts.createDefault();
            }};
            
            SSLBuilder.buildSSLContext(null, keystorePass);
            
//            new Verifications() {{
//               SSLContexts.createDefault(); times = 1;
//            }};
         }// END test default path

         {// BEGIN test null password
            new Expectations() {{
               KeyStore.getInstance(KeyStore.getDefaultType());
               keystore.load(keystoreFile, "".toCharArray());
               SSLContexts.custom()
                  .loadTrustMaterial(keystore, withAny(trustSelfSignedStrategy))
                  .build();
            }};
            
            SSLBuilder.buildSSLContext(keystoreFile, null);
            
//            new Verifications() {{
//               KeyStore.getInstance(KeyStore.getDefaultType()); times = 1; 
//               keystore.load(keystoreFile, "".toCharArray()); times = 1;
//               SSLContexts.custom()
//                  .loadTrustMaterial(keystore, withAny(trustSelfSignedStrategy))
//                  .build(); times = 1;
//            }};
         }// END test null password

      } catch (Exception e) {
         fail(e.toString());
      }
   }

}
