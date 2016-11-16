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

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;

/**
 * This class is used to build a SSL Context
 */
public class SSLBuilder {

   /**
    * Creates SSL context.
    * To create a custom SSLContext that accepts CA and self-signed certs,
    * pass a valid keystoreStream and keystorePass. To create a default SSLContext
    * pass null for keystoreStream;
    * 
    * @param keystoreStream
    *           Input Stream of the keystore file. If null, a default SSL context
    *           will be created.
    * @param keystorePass
    *           The password required to access the Keystore file. If null,
    *           blank is passed as password.
    *           
    * @return SSLContext object
    * @throws SSLException 
    *    - if no Provider supports a KeyStoreSpi implementation for the specified type.
    *    - if an I/O error occurs
    *    - or any other causes will be attached to the exception. 
    */
   public static SSLContext buildSSLContext(
         InputStream keystoreStream,
         String keystorePass) throws SSLException {

      SSLContext sslcontext = null;
      try {
         if (keystoreStream != null) { // Create custom context
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
   
            
            try {
               keystore.load(keystoreStream, 
                     keystorePass == null ? "".toCharArray() :
                        keystorePass.toCharArray());
            } finally {
               keystoreStream.close();
            }
   
            // Trust own CA and all self-signed certs
            sslcontext = SSLContexts.custom()
                  .loadTrustMaterial(keystore, new TrustSelfSignedStrategy())
                  .build();
         } else { //get the default context
            sslcontext = SSLContexts.createDefault();
         }
      } catch (Exception e) {
         throw new SSLException(e);
      }
      return sslcontext;
   }

   /**
    * The exception thrown when errors occur within this class
    */
   public static class SSLException extends Exception {


      private static final long serialVersionUID = 1L;

      public SSLException(String string) {
         super(string);
      }

      public SSLException(Exception e) {
         super(e);
      }

   }

}
