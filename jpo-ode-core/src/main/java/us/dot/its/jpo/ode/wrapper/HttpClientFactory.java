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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides an abstraction layer for making HTTP and HTTPS
 * connections to an HTTP server. Follow the steps below to create an HTTP
 * client and send HTTP requests to the HTTP server.
 * 
 * <ol>
 *  <li>Call {@link HttpClientFactory#build(SSLContext)} to create a factory 
 *     instance. This factory can be used to create individual HTTP connections
 *     to an HTTP Server.</li>
 *  <li>Use the obtained factory from step 1 to create a {@link HttpClient} 
 *     object by calling {@link HttpClientFactory#createHttpClient()} method.</li>
 *  <li>Use {@link HttpClient} object to send HTTP GET, POST and other requests
 *  to the server using {@link HttpClient#get(String, Map, Map)}, 
 *  {@link HttpClient#post(String, Map, Map, String)} methods. </li>
 *  <li>Use the returned {@link HttpResponse} object to retrieve the various
 *  components of a response including: {@link HttpResponse#getBody()} and
 *  {@link HttpResponse#getStatusCode()}</li>
 * </ol>
 */
public class HttpClientFactory {

   private SSLConnectionSocketFactory sslSocketFactory;

   public class HttpClient {
      private CloseableHttpClient closeableHttpClient;
      private CookieStore cookieStore;

      private HttpClient() {
         cookieStore = new BasicCookieStore();
         closeableHttpClient = HttpClientFactory.buildHttpClient(cookieStore,
               sslSocketFactory);
      }

      /**
       * Sends HTTP GET request to <code>server</code> with the given 
       * <code>headers</code> and specified <code>params</code>.
       * 
       * @param server - to which request will be sent
       * @param headers - request headers
       * @param params - request query parameters
       * @return the server's response.
       * @throws HttpException - in case of a problem or the connection was 
       * aborted or an http protocol error
       */
      public HttpResponse get(String server, Map<String, String> headers,
            Map<String, String> params) throws HttpException {

         try {
            RequestBuilder builder = RequestBuilder.get().setUri(server);

            CloseableHttpResponse closeableresponse = executeRequest(headers,
                  params, builder);

            return new HttpResponse(Response.Status.fromStatusCode(closeableresponse
                  .getStatusLine().getStatusCode()),
                  EntityUtils.toString(closeableresponse.getEntity()));

         } catch (Throwable t) {
            throw new HttpException(t);
         } finally {
         }
      }

      /**
       * Sends HTTP POST request to <code>server</code> with the given 
       * <code>headers</code> and specified <code>params</code> and <code>body</code>.
       * 
       * @param server - to which request will be sent
       * @param headers - request headers
       * @param params - request query parameters
       * @param body - the body of the post request
       * @return the server's response.
       * @throws HttpException - in case of a problem or the connection was 
       * aborted or an http protocol error
       */
      public HttpResponse post(String server, Map<String, String> headers,
            Map<String, String> params, String body) throws HttpException {

         try {
            RequestBuilder builder = RequestBuilder.post().setUri(server);

            if (body != null)
               builder.setEntity(new StringEntity(body));

            CloseableHttpResponse closeableresponse = executeRequest(headers,
                  params, builder);

            return new HttpResponse(Response.Status.fromStatusCode(closeableresponse
                  .getStatusLine().getStatusCode()),
                  EntityUtils.toString(closeableresponse.getEntity()));

         } catch (Throwable t) {
            throw new HttpException(t);
         } finally {
         }
      }

      /**
       * Executes a HTTP Client request using the given parameters.
       * 
       * @param headers - request headers
       * @param params - request query parameters
       * @param builder - the {@link RequestBuilder} to be used for building the
       * request.
       * @return the {@link CloseableHttpResponse} returned by the server
       * @throws IOException - in case of a problem or the connection was aborted 
       * @throws ClientProtocolException - in case of an http protocol error       
       * 
       */
      private CloseableHttpResponse executeRequest(Map<String, String> headers,
            Map<String, String> params, RequestBuilder builder)
            throws IOException, ClientProtocolException {
         if (params != null) {
            for (Map.Entry<String,String> param : params.entrySet()) {
               builder.addParameter(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
         }

         HttpUriRequest request = builder.build();

         if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()){
               request.setHeader(header.getKey(), header.getValue());
            }
         }

         CloseableHttpResponse closeableresponse = 
               closeableHttpClient.execute(request);
         return closeableresponse;
      }

      /**
       * Returns the cookies. 
       * 
       * @return - returning the cookie parameters from the HTTP connection.
       */
      public Map<String, String> getCookies() {
         ConcurrentHashMap<String, String> cookies = new ConcurrentHashMap<String, String>();
         for (Cookie c : cookieStore.getCookies()) {
            cookies.put(c.getName(), c.getValue());
         }

         return cookies;
      }

      /**
       * Closes the connection.
       * 
       * @throws HttpException - in case an I/O error occurs 
       */
      public void close() throws HttpException {
         try {
            closeableHttpClient.close();
         } catch (Exception e) {
            throw new HttpException(e);
         }
      }
   }

   /**
    * Encapsulates an HTTP Response containing the <code>statusCode</code> and
    * <code>body</code> of the response which would be the resource returned
    * by a REST call.
    */
   public class HttpResponse {
      private Response.Status statusCode;
      private String body;

      /**
       * General constructor.
       * 
       * @param statucCode - response status code
       * @param body - response body
       */
      private HttpResponse(Response.Status statucCode, String body) {
         super();
         this.statusCode = statucCode;
         this.body = body;
      }

      /**
       * @return - status code
       */
      public Response.Status getStatusCode() {
         return statusCode;
      }

      /**
       * Sets the status code in the response object 
       * @param statucCode - the status code
       * @return the response object.
       */
      public HttpResponse setStatusCode(Response.Status statucCode) {
         this.statusCode = statucCode;
         return this;
      }

      /**
       * @return the response body.
       */
      public String getBody() {
         return body;
      }

      /**
       * Sets the response body/entity.
       * 
       * @param body - the response body/entity. 
       * @return - the response object.
       */
      public HttpResponse setBody(String body) {
         this.body = body;
         return this;
      }

   }

   public class HttpException extends Exception {

      private static final long serialVersionUID = 1L;

      public HttpException(Throwable t) {
         super(t);
      }
   }

   /**
    * Builds {@link HttpClientFactory} object which is used to create {@link HttpClient} object.
    * 
    * @param sslContext with which the client factory to be built.
    * @return {@link HttpClientFactory} object
    * @throws HttpException if building the socket factory fails.
    */
   public static HttpClientFactory build(SSLContext sslContext)
         throws HttpException {
      HttpClientFactory factory = new HttpClientFactory();

      try {
         factory.sslSocketFactory = buildSSLConnectionSocketFactory(sslContext);
      } catch (Throwable t) {
         throw factory.new HttpException(t);
      }

      return factory;
   }

   /**
    * Builds a {@link SSLConnectionSocketFactory} object
    * 
    * @param sslContext with which the client factory to be built.
    * @return SSL connection factory object that is used to create SSL socket
    * connections. 
    */
   private static SSLConnectionSocketFactory buildSSLConnectionSocketFactory(
         SSLContext sslContext) {
      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
//      , new String[] { "TLSv1" }, null,
//            SSLConnectionSocketFactory.getDefaultHostnameVerifier());
      return sslsf;
   }

   /**
    * Creates a {@link CloseableHttpClient} object.
    * @param cookieStore
    *           or null if no cookies required
    * @param sslSocketFactory
    *           or null if not using SSL
    * @return a {@link CloseableHttpClient} object
    */
   private static CloseableHttpClient buildHttpClient(CookieStore cookieStore,
         SSLConnectionSocketFactory sslSocketFactory) {
      return HttpClients.custom().setDefaultCookieStore(cookieStore)
            .setSSLSocketFactory(sslSocketFactory).build();
   }

   /**
    * Creates a {@link HttpClient} object.
    * @return a new {@link HttpClient} object
    */
   public HttpClient createHttpClient() {
      HttpClient httpClient = new HttpClient();

      return httpClient;
   }

}
