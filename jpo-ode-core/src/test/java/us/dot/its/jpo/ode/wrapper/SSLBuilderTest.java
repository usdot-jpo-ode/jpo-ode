/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
//import mockit.integration.junit4.JMockit;

//@RunWith(JMockit.class)
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
