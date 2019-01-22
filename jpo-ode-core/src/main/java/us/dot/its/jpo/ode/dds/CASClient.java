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

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.wrapper.HttpClientFactory;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpClient;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpResponse;

public class CASClient {

   public static final String JSESSIONID_KEY = "JSESSIONID";
   private static final Logger logger = LoggerFactory
         .getLogger(CASClient.class);

   private String ddsCasUrl;
   private String ddsCasUsername;
   private String ddsCasPassword;
   private String body = " body: ";
   private String http = "Error closing HTTP client";

   private HttpClientFactory httpClientFactory;
   private String sessionID;

   public String getSessionID() {
      return sessionID;
   }
   
   public String getDdsCasUrl() {
	      return ddsCasUrl;
   }
   
   public String getDdsCasUsername() {
	      return ddsCasUsername;
   }

   public static CASClient configure(SSLContext sslContext,
         String ddsCasUrl,
         String ddsCasUsername,
         String ddsCasPassword
         ) throws CASException {

      CASClient casClient = new CASClient();
      try {

         casClient.ddsCasUrl = ddsCasUrl;
         casClient.ddsCasUsername = ddsCasUsername;
         casClient.ddsCasPassword = ddsCasPassword;
         casClient.httpClientFactory = HttpClientFactory.build(sslContext);
      } catch (Exception e) {
         throw casClient.new CASException(e);
      }
      return casClient;
   }

   public String login(String websocketURL) throws CASException {

      try {
         String ticketGrantingTicket = getTicketGrantingTicket(ddsCasUrl,
               ddsCasUsername,
               ddsCasPassword);
         logger.info("Got ticketGrantingTicket {}", ticketGrantingTicket);

         String httpWebsocketURL = 
               "https" + websocketURL.substring(websocketURL.indexOf(':'));
         String ddsHttpWebSocketUrl = 
               new URL(httpWebsocketURL).toExternalForm();
         String serviceTicket = getServiceTicket(
               ddsCasUrl, 
               ticketGrantingTicket,
               ddsHttpWebSocketUrl);
         logger.info("Got serviceTicket {}", serviceTicket);

         sessionID = getServiceCall(ddsHttpWebSocketUrl, serviceTicket);
         logger.info("Successful CAS login with sessionID {}", sessionID);

         return sessionID;
      } catch (Exception e) {
         throw new CASException(e);
      }
   }

   private String getTicketGrantingTicket(String server, String username,
         String password) throws CASException {

      HttpClient httpClient = httpClientFactory.createHttpClient();
      try {

         ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();
         params.put("username", username);
         params.put("password", password);
         HttpResponse response = httpClient.post(server, null, params, null);

         Status statusCode = response.getStatusCode();
         String responseBody = response.getBody();

         if (statusCode == Status.CREATED) { // 201
            Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*")
                  .matcher(responseBody);
            if (matcher.matches()) {
               return matcher.group(1);
            } else {
               throw new CASException(
                     "CAS getTicketGrantingTicket failed. No ticket found in body: "
                           + responseBody);
            }
         } else {
            throw new CASException(
                  "CAS getTicketGrantingTicket failed. Response code: "
                        + statusCode + body + responseBody);
         }

      } catch (Exception e) {
         throw new CASException(e);
      } finally {
         try {
            httpClient.close();
         } catch (HttpException e) {
            logger.warn(http, e);
         }
      }
   }

   private String getServiceTicket(String server, String ticketGrantingTicket,
         String service) throws CASException {

      HttpClient httpClient = httpClientFactory.createHttpClient();

      try {

         HttpResponse response = httpClient.post(
               server + "/" + ticketGrantingTicket, null,
               Collections.singletonMap("service", service), null);

         Status statusCode = response.getStatusCode();
         String responseBody = response.getBody();

         if (statusCode == Status.OK) { // 200
            return responseBody;
         } else {
            throw new CASException(
                  "CAS getServiceTicket failed. Response code: " + statusCode
                        + body + responseBody);
         }

      } catch (HttpException e) {
         throw new CASException(e);
      } finally {
         try {
            httpClient.close();
         } catch (HttpException e) {
            logger.warn(http, e);
         }
      }
   }

   private String getServiceCall(String service, String serviceTicket)
         throws CASException {

      HttpClient httpClient = httpClientFactory.createHttpClient();

      try {

         HttpResponse response = httpClient.get(service, null,
               Collections.singletonMap("ticket", serviceTicket));

         Status statusCode = response.getStatusCode();
         String responseBody = response.getBody();

         if (statusCode == Status.OK || statusCode == Status.NOT_FOUND) { // 200
                                                                          // or
                                                                          // 404
            return getSessionID(httpClient.getCookies());
         } else {
            throw new CASException("CAS getServiceCall failed. Response code: "
                  + statusCode + body + responseBody);
         }

      } catch (HttpException e) {
         throw new CASException(e);
      } finally {
         try {
            httpClient.close();
         } catch (HttpException e) {
            logger.warn(http, e);
         }
      }
   }

   private String getSessionID(Map<String, String> cookies) {
      sessionID = "";
      for (Map.Entry<String, String> c : cookies.entrySet()) {
         if (c.getKey().equals(JSESSIONID_KEY)) {
            sessionID = c.getValue();
            break;
         }
      }
      return sessionID;
   }

   public class CASException extends Exception {

      private static final long serialVersionUID = 3103235434315019560L;

      public CASException(String message) {
         super(message);
      }

      public CASException(Throwable cause) {
         super(cause);
      }

      public CASException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
