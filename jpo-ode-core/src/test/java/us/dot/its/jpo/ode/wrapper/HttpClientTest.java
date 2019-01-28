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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpClient;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpResponse;

@SuppressWarnings("deprecation")
@RunWith(JMockit.class)
public class HttpClientTest {

   @Mocked
   SSLContext sslContext;
   
   @Mocked
   CloseableHttpClient closeableHttpClient;

   @Test
   public void testGet() throws Exception {
      { // BEGIN Happy Path
         URI uri = new URI("http://ip:port/path");
         final String headerKey = "headerKey";
         final String headerValue = "headerValue";
         String paramKey = "paramKey";
         String paramValue = "paramValue";
         String entityContent = "entityContent";
         
         final MockCloseableHttpResponse closeableresponse = new MockCloseableHttpResponse();
         closeableresponse.setStatusLine(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 
               HttpStatus.SC_OK, "SUCCESS"));
         BasicHttpEntity entity = new BasicHttpEntity();
         entity.setContent(new ByteArrayInputStream(entityContent.getBytes()));
         closeableresponse.setEntity(entity);
         final HttpUriRequest request = RequestBuilder
               .get(uri)
               .addHeader(headerKey, headerValue)
               .addParameter(paramKey, paramValue)
               .build();
         
         new Expectations() {{
            // Note: withAny returns the request,, that's why object instance
            ///      is passed rather than a type.
            closeableHttpClient.execute(withAny(request)); result = closeableresponse;
         }};

         HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();

         HttpResponse response = client.get(uri.toString(), 
               Collections.singletonMap(headerKey, headerValue), 
               Collections.singletonMap(paramKey, paramValue));
         
         assertEquals(entityContent, response.getBody());
         assertEquals(Response.Status.OK, response.getStatusCode());
         
         closeableresponse.close();

//         new Verifications() {{
//            HttpUriRequest captured;
//            closeableHttpClient.execute(captured = withCapture());
//            assertEquals(request.toString(), captured.toString());
//            
//            Header[] capturedHeaders = captured.getAllHeaders();
//            assertEquals(1, capturedHeaders.length);
//            assertEquals(headerKey, capturedHeaders[0].getName());
//            assertEquals(headerValue, capturedHeaders[0].getValue());
//         }};
      }// END Happy Path

      { // BEGIN simulating a bad request
         final MockCloseableHttpResponse closeableresponse = new MockCloseableHttpResponse();
         closeableresponse.setStatusLine(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 
               HttpStatus.SC_BAD_REQUEST, "FAILURE"));
         BasicHttpEntity entity = new BasicHttpEntity();
         entity.setContent(new ByteArrayInputStream("".getBytes()));
         closeableresponse.setEntity(entity);
         final HttpUriRequest request = RequestBuilder.get().build();
         
         new Expectations() {{
            closeableHttpClient.execute(withAny(request)); result = closeableresponse;
         }};

         HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();

         HttpResponse response = client.get(null, null, null);
         
         assertEquals("", response.getBody());
         assertEquals(Response.Status.BAD_REQUEST, response.getStatusCode());
         
         closeableresponse.close();
         
//         new Verifications() {{
//            HttpUriRequest captured;
//            closeableHttpClient.execute(captured = withCapture());
//            
//            assertEquals(request.toString(), captured.toString());
//
//            Header[] capturedHeaders = captured.getAllHeaders();
//            assertEquals(0, capturedHeaders.length);
//         }};
      }// END simulating a bad request

      { // BEGIN execute throws exception
         final HttpUriRequest request = RequestBuilder.get().build();
         
         new Expectations() {{
            closeableHttpClient.execute(withAny(request)); result = new IOException();
         }};

         HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();

         try {
            client.get(null, null, null);
            fail("Exception was aexpected.");
         } catch (HttpException e) {
            
         } catch (Exception e) {
            fail("Unexpected Exception." + e);
         }
         
//         new Verifications() {{
//            HttpUriRequest captured;
//            closeableHttpClient.execute(captured = withCapture());
//            
//            assertEquals(request.toString(), captured.toString());
//
//            Header[] capturedHeaders = captured.getAllHeaders();
//            assertEquals(0, capturedHeaders.length);
//         }};
      }// END execute throws exception
   }

   @Test
   public void testPost() throws Exception {
      { // BEGIN Happy Path
         URI uri = new URI("http://ip:port/path");
         final String headerKey = "headerKey";
         final String headerValue = "headerValue";
         String paramKey = "paramKey";
         String paramValue = "paramValue";
         String entityContent = "entityContent";
         
         final MockCloseableHttpResponse closeableresponse = new MockCloseableHttpResponse();
         closeableresponse.setStatusLine(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 
               HttpStatus.SC_OK, "SUCCESS"));
         BasicHttpEntity entity = new BasicHttpEntity();
         entity.setContent(new ByteArrayInputStream(entityContent.getBytes()));
         closeableresponse.setEntity(entity);
         
         final HttpUriRequest request = RequestBuilder.post(uri)
               .addHeader(headerKey, headerValue)
               .addParameter(paramKey, paramValue)
               .setEntity(entity)
               .build();
         
         new Expectations() {{
            // Note: withAny returns the request,, that's why object instance
            ///      is passed rather than a type.
            closeableHttpClient.execute(withAny(request)); result = closeableresponse;
         }};

         HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();

         HttpResponse response = client.post(uri.toString(), 
               Collections.singletonMap(headerKey, headerValue), 
               Collections.singletonMap(paramKey, paramValue),
               entityContent);
         
         assertEquals(entityContent, response.getBody());
         assertEquals(Response.Status.OK, response.getStatusCode());
         
         closeableresponse.close();

//         new Verifications() {{
//            HttpUriRequest captured;
//            closeableHttpClient.execute(captured = withCapture());
//            assertEquals(request.toString(), captured.toString());
//            
//            Header[] capturedHeaders = captured.getAllHeaders();
//            assertEquals(1, capturedHeaders.length);
//            assertEquals(headerKey, capturedHeaders[0].getName());
//            assertEquals(headerValue, capturedHeaders[0].getValue());
//         }};
      }// END Happy Path

      { // BEGIN simulating a bad request
         final MockCloseableHttpResponse closeableresponse = new MockCloseableHttpResponse();
         closeableresponse.setStatusLine(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 
               HttpStatus.SC_BAD_REQUEST, "FAILURE"));
         BasicHttpEntity entity = new BasicHttpEntity();
         entity.setContent(new ByteArrayInputStream("".getBytes()));
         closeableresponse.setEntity(entity);
         final HttpUriRequest request = RequestBuilder.post().build();
         
         new Expectations() {{
            closeableHttpClient.execute(withAny(request)); result = closeableresponse;
         }};

         HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();

         HttpResponse response = client.post(null, null, null, null);
         
         assertEquals("", response.getBody());
         assertEquals(Response.Status.BAD_REQUEST, response.getStatusCode());
         
         closeableresponse.close();
         
//         new Verifications() {{
//            HttpUriRequest captured;
//            closeableHttpClient.execute(captured = withCapture());
//            
//            assertEquals(request.toString(), captured.toString());
//
//            Header[] capturedHeaders = captured.getAllHeaders();
//            assertEquals(0, capturedHeaders.length);
//         }};
      }// END simulating a bad request

      { // BEGIN execute throws exception
         final HttpUriRequest request = RequestBuilder.post().build();
         
         new Expectations() {{
            closeableHttpClient.execute(withAny(request)); result = new IOException();
         }};

         HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();

         try {
            client.post(null, null, null, null);
            fail("Exception was aexpected.");
         } catch (HttpException e) {
            
         } catch (Exception e) {
            fail("Unexpected Exception." + e);
         }
         
//         new Verifications() {{
//            HttpUriRequest captured;
//            closeableHttpClient.execute(captured = withCapture());
//            
//            assertEquals(request.toString(), captured.toString());
//
//            Header[] capturedHeaders = captured.getAllHeaders();
//            assertEquals(0, capturedHeaders.length);
//         }};
      }// END execute throws exception
   }

   @Test
   public void testGetCookies() throws HttpException {
      HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();
      Map<String, String> cookies = client.getCookies();
      
      // Not much can be tested here.
      assertEquals(0, cookies.size());
   }

   @Test
   public void testClose() throws Exception {
      HttpClient client = HttpClientFactory.build(sslContext).createHttpClient();
      
      // Not much can be tested here. If no exception is thrown, test is PASSed
      client.close();
   }

   class MockCloseableHttpResponse implements CloseableHttpResponse {
      private StatusLine statusLine;
      private HttpEntity entity;

      
      @Override
      public StatusLine getStatusLine() {
         return statusLine;
      }

      @Override
      public void setStatusLine(StatusLine statusLine) {
         this.statusLine = statusLine;
      }

      @Override
      public HttpEntity getEntity() {
         return entity;
      }

      @Override
      public void setEntity(HttpEntity entity) {
         this.entity = entity;
      }

      @Override
      public void setStatusLine(ProtocolVersion ver, int code) {
      }

      @Override
      public void setStatusLine(ProtocolVersion ver, int code, String reason) {
      }

      @Override
      public void setStatusCode(int code) throws IllegalStateException {
      }

      @Override
      public void setReasonPhrase(String reason) throws IllegalStateException {
      }

      @Override
      public Locale getLocale() {
         return null;
      }

      @Override
      public void setLocale(Locale loc) {
      }

      @Override
      public ProtocolVersion getProtocolVersion() {
         return null;
      }

      @Override
      public boolean containsHeader(String name) {
         return false;
      }

      @Override
      public Header[] getHeaders(String name) {
         return null;
      }

      @Override
      public Header getFirstHeader(String name) {
         return null;
      }

      @Override
      public Header getLastHeader(String name) {
         return null;
      }

      @Override
      public Header[] getAllHeaders() {
         return null;
      }

      @Override
      public void addHeader(Header header) {
      }

      @Override
      public void addHeader(String name, String value) {
      }

      @Override
      public void setHeader(Header header) {
      }

      @Override
      public void setHeader(String name, String value) {
      }

      @Override
      public void setHeaders(Header[] headers) {
      }

      @Override
      public void removeHeader(Header header) {
      }

      @Override
      public void removeHeaders(String name) {
      }

      @Override
      public HeaderIterator headerIterator() {
         return null;
      }

      @Override
      public HeaderIterator headerIterator(String name) {
         return null;
      }

      @Override
      public HttpParams getParams() {
         return null;
      }

      @Override
      public void setParams(HttpParams params) {
      }

      @Override
      public void close() throws IOException {
      }
      
   }
}
