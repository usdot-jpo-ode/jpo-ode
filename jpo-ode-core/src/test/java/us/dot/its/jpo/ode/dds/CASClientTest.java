package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mockit.*;
import mockit.integration.junit4.JMockit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response.Status;

import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpClient;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpResponse;

@RunWith(JMockit.class)
public class CASClientTest {
	private static final Logger logger = LoggerFactory
	         .getLogger(CASClient.class);
	
//   @Mocked(stubOutClassInitialization = true)
//   final HttpClientFactory unused = null;
	
	@Mocked
	HttpClientFactory mockHttpClientFactory;

   @Mocked
   SSLContext sslContext;

   @Mocked
   String webSocketURL;

   String casUser = "testUser";
   String casPass = "testPass";
   String casUrl = "testUrl";

   @Test @Ignore
   public void testConfigure() {
      CASClient casClient = null;
      try {
         casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      } catch (CASException e) {
         fail("Unexpected exception: " + e.toString());
      }
      assertEquals(casClient.getDdsCasUrl(), casUrl);
      assertEquals(casClient.getDdsCasUsername(), casUser);
   }

   @Test(expected = CASException.class) @Ignore
   public void testConfigureException(@Mocked HttpClientFactory mockHttpClientFactory)
         throws CASException, HttpException {
      new Expectations() {
         {
            HttpClientFactory.build((SSLContext) any);
            result = new Exception();
         }
      };
      CASClient.configure(sslContext, casUrl, casUser, casPass);
   }

   @SuppressWarnings("unchecked")
   @Test @Ignore
   public void testLogin(
         @Mocked HttpResponse mockResponse,
         @Mocked Pattern mockPattern,
         @Mocked Matcher mockMatcher,
         @Mocked HttpClientFactory mockHttpClientFactory,
         @Mocked Map.Entry<String, String> entry) throws HttpException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations(Pattern.class) {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.OK;
            result = Status.OK;

            Pattern.compile(anyString);
            result = mockPattern;
            
            mockPattern.matcher(anyString);
            result = mockMatcher;

            mockMatcher.matches();
            result = true;
            mockMatcher.group(1);
            result = "TGT-1234-11112222333334444-cas01";

            mockResponse.getBody();
            result = "TGT-1234-11112222333334444-cas01";
            result = "ST-1234-1111222233334444-cas01";

            mockHttpClientFactory.createHttpClient().getCookies();
            result = cookies;
         }
      };

      CASClient casClient;
      String sessionId = "";
      try {
         casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
         sessionId = casClient.login(websocketURL);
         assertEquals(sessionId, casClient.getSessionID());
         assertEquals(sessionId, "1bif45f-testSessionId");
      } catch (CASException e) {
         fail("Unexpected exception: " + e.toString());
      }

      new Verifications() {
         {
            HttpClient httpClient = mockHttpClientFactory.createHttpClient();
            minTimes = 3;

            httpClient.post(anyString, null, (Map<String, String>) any, anyString);
            minTimes = 2;

            httpClient.get(anyString, null, (Map<String, String>) any);
            minTimes = 1;

            httpClient.close();
            minTimes = 3;

            Pattern.compile(anyString);
         }
      };
   }

   @Test(expected = CASException.class) @Ignore
   public void testLoginExceptionInGetTicket1(@Mocked HttpResponse mockResponse) throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations() {
         {
            mockResponse.getStatusCode();
            result = Status.BAD_REQUEST;
         }
      };

      CASClient casClient;

      casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }

   @Test(expected = CASException.class) @Ignore
   public void testLoginExceptionInGetTicket2(
         @Mocked HttpResponse mockResponse,
         @Mocked Pattern mockPattern,
         @Mocked Matcher mockMatcher) throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations(Pattern.class) {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;

            Pattern.compile(anyString);
            result = mockPattern;
            
            mockPattern.matcher(anyString);
            result = mockMatcher;

            mockMatcher.matches();
            result = false;
         }
      };

      CASClient casClient;

      casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }

   @Test(expected = CASException.class)
   public void testLoginExceptionInGetServiceTicket(
         @Mocked HttpResponse mockResponse,
         @Mocked Pattern mockPattern,
         @Mocked Matcher mockMatcher) throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations(Pattern.class) {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.BAD_REQUEST;

            Pattern.compile(anyString);
            result = mockPattern;
            
            mockPattern.matcher(anyString);
            result = mockMatcher;

            mockMatcher.matches();
            result = true;
            mockMatcher.group(1);
            result = "TGT-1234-11112222333334444-cas01";

            mockResponse.getBody();
            result = "TGT-1234-11112222333334444-cas01";
            result = "ST-1234-1111222233334444-cas01";
         }
      };

      CASClient casClient;

      casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }

   @SuppressWarnings("unchecked")
   @Test(expected = CASException.class)
   public void testLoginExceptionInGetServiceCall(
         @Mocked HttpResponse mockResponse,
         @Mocked Pattern mockPattern,
         @Mocked Matcher mockMatcher) throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
     // new Expectations(Pattern.class) {
      new Expectations() {
         {
//            mockHttpClient.post(anyString, (Map<String, String>) any, (ConcurrentHashMap<String, String>) any,
//                  anyString);
//            result = mockResponse;
//
//            mockHttpClient.get(anyString, (Map<String, String>) any, (Map<String, String>) any);
//            result = mockResponse;

            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.OK;
            result = Status.BAD_REQUEST;

//            Pattern.compile(anyString);
//            result = mockPattern;
//            
//            mockPattern.matcher(anyString);
//            result = mockMatcher;

            mockMatcher.matches();
            result = true;
            mockMatcher.group(1);
            result = "TGT-1234-11112222333334444-cas01";

            mockResponse.getBody();
            result = "TGT-1234-11112222333334444-cas01";
            result = "ST-1234-1111222233334444-cas01";

         }
      };

      CASClient casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }
}
