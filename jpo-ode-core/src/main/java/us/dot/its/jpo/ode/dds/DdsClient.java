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
         ) throws URISyntaxException, SSLException {
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
