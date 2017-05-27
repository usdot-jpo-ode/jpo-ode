package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpClient;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpResponse;

@RunWith(JMockit.class)
public class CASClientTest {
	@Mocked HttpClientFactory mockHttpClientFactory;
   @Mocked SSLContext sslContext;
   @Mocked private HttpResponse mockResponse;
   @Mocked private Pattern mockPattern;
   @Mocked private Matcher mockMatcher;
   
   String casUser = "testUser";
   String casPass = "testPass";
   String casUrl = "testUrl";

   @Test
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

   @Test(expected = CASException.class)
   public void testConfigureException()
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
   @Test
   public void testLogin() throws HttpException {
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

   @Test(expected = CASException.class)
   public void testLoginExceptionInGetTicket1() throws HttpException, CASException {
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

   @Test(expected = CASException.class)
   public void testLoginExceptionInGetTicket2() throws HttpException, CASException {
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
   public void testLoginExceptionInGetServiceTicket() 
         throws HttpException, CASException {
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


   @Test(expected = CASException.class)
   public void testLoginExceptionInGetServiceCall() 
               throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations(Pattern.class) {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.OK;
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

      CASClient casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }
}
