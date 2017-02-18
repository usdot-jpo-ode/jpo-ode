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
package us.dot.its.jpo.ode.sdcsdw.wsclient;

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

import us.dot.its.jpo.ode.model.ControlMessage;
import us.dot.its.jpo.ode.sdcsdw.wsclient.CASClient.CASException;
import us.dot.its.jpo.ode.wrapper.SSLBuilder;
import us.dot.its.jpo.ode.wrapper.SSLBuilder.SSLException;
import us.dot.its.jpo.ode.wrapper.WebSocketClient;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;

public class DdsClientFactory {

   private static final Logger logger = LoggerFactory
         .getLogger(DdsClientFactory.class);

   private static URI uri = null;
   private static InputStream keystoreStream = null;
   private static SSLContext sslContext = null;
   

   public static WebSocketClient<ControlMessage> create(
         String ddsCasUrl,
         String ddsCasUsername,
         String ddsCasPassword,
         String websocketURL,
         String keystoreFile,
         String keystorePass,
         Class<? extends WebSocketMessageDecoder<?>> decoderClass)
         throws DdsClientException {

      CASClient casClient = null;
      WebSocketClient<ControlMessage> ddsClient = null;
      try {

         init(websocketURL,
              keystoreFile,
              keystorePass);

         casClient = CASClient.configure(
               sslContext,
               ddsCasUrl,
               ddsCasUsername,
               ddsCasPassword);
         casClient.login(websocketURL);
         
         Map<String, Map<String, String>> cookieHeader = Collections
               .singletonMap("Cookie", Collections.singletonMap(
                     CASClient.JSESSIONID_KEY, casClient.getSessionID()));

         List<Class<? extends WebSocketMessageDecoder<?>>> decoders = 
               new ArrayList<Class<? extends WebSocketMessageDecoder<?>>>();
         decoders.add(decoderClass);
         
         ddsClient = new WebSocketClient<ControlMessage>(
               uri, sslContext, null,
               cookieHeader, new ControlMessageHandler(),
               decoders);
         
         logger.info("DDS Client created.");

      } catch (Exception e) {
         throw new DdsClientException(e);
      }
      return ddsClient;
   }

   private static void init(String websocketURL,
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

   public static class DdsClientException extends Exception {

      private static final long serialVersionUID = 1L;

      public DdsClientException(Throwable cause) {
         super(cause);
      }

   }
}
