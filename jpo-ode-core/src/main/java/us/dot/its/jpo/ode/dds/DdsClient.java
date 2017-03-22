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
package us.dot.its.jpo.ode.dds;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.wrapper.SSLBuilder;
import us.dot.its.jpo.ode.wrapper.SSLBuilder.SSLException;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public class DdsClient<MessageType> {	//NOSONAR

   private static final Logger logger = LoggerFactory
         .getLogger(DdsClient.class);

   private URI uri = null;
   private InputStream keystoreStream = null;
   private SSLContext sslContext = null;

   private CASClient casClient;
   

   public DdsClient(
         String ddsCasUrl,
         String ddsCasUsername,
         String ddsCasPassword,
         String websocketURL,
         String keystoreFile,
         String keystorePass)
         throws DdsClientException {

      try {

         init(websocketURL,
              keystoreFile,
              keystorePass);

         casClient = CASClient.configure(
               sslContext,
               ddsCasUrl,
               ddsCasUsername,
               ddsCasPassword);
         
         logger.info("CAS Client created.");

      } catch (Exception e) {
         throw new DdsClientException(e);
      }
   }

   private void init(
         String websocketURL,
         String keystoreFile,
         String keystorePass
         ) throws URISyntaxException,
         SSLException, CASException {
      if (uri == null) {
         uri = new URI(websocketURL);
      }

      if (keystoreStream == null && keystoreFile != null) {
         keystoreStream = CASClient.class.getClassLoader().getResourceAsStream(keystoreFile );
      }

      if (sslContext == null) {
         sslContext = SSLBuilder.buildSSLContext(keystoreStream, keystorePass);
      }

   }

   public WebSocketEndpoint<MessageType> login (
         Class<? extends WebSocketMessageDecoder<?>> decoderClass, 
         WebSocketMessageHandler<MessageType> messageHandler)
         throws DdsClientException {

      WebSocketEndpoint<MessageType> ddsClient = null;
      try {

         casClient.login(uri.toString());

         Map<String, Map<String, String>> cookieHeader = Collections
               .singletonMap("Cookie", Collections.singletonMap(
                     CASClient.JSESSIONID_KEY, casClient.getSessionID()));

         List<Class<? extends WebSocketMessageDecoder<?>>> decoders = 
               new ArrayList<>();
         decoders.add(decoderClass);
         
         ddsClient = new WebSocketEndpoint<>(
               uri, sslContext, null,
               cookieHeader, messageHandler,
               decoders);
         
         logger.info("DDS Client created.");

      } catch (Exception e) {
         throw new DdsClientException(e);
      }
      return ddsClient;
   }

   public static class DdsClientException extends Exception {

      private static final long serialVersionUID = 1L;

      public DdsClientException(Throwable cause) {
         super(cause);
      }

   }
}
