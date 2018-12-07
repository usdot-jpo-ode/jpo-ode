package us.dot.its.jpo.ode.wrapper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.DecodeException;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

@RunWith(JMockit.class)
public class WebSocketEndpointTest {
   
   @Mocked
   private SSLContext sslContext;
   
   @Mocked
   private ContainerProvider provider;

   @Mocked
   private WebSocketContainer container;
   
   @Mocked
   private Session wsSession;
   
   private final Integer expectedMessage = null;
   
   private final WebSocketMessageHandler<Integer> handler = 
         new WebSocketMessageHandler<Integer>() {
      
      @Override
      public void onMessage(Integer message) {
         assertEquals(expectedMessage, message);
      }

      @Override
      public void onOpen(Session session, EndpointConfig config) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void onClose(Session session, CloseReason reason) {
         // TODO Auto-generated method stub
         
      }

      @Override
      public void onError(Session session, Throwable t) {
         // TODO Auto-generated method stub
         
      }
   };
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   static class String2IntegerDecoder implements WebSocketMessageDecoder<Integer> {

      @Override
      public Integer decode(java.lang.String s) throws DecodeException {
         return Integer.parseInt(s);
      }

      @Override
      public boolean willDecode(java.lang.String s) {
         Integer.parseInt(s);
         return true;
      }

      @Override
      public void init(EndpointConfig endpointConfig) {
      }

      @Override
      public void destroy() {
      }
      
   }
   
   @Test
   public void testWebSocketClientGeneralConstructor() throws URISyntaxException {
      String propName = "propName";
      String propValue = "propValue";
      
      List<Class<? extends WebSocketMessageDecoder<?>>> decoders = 
            new ArrayList<Class<? extends WebSocketMessageDecoder<?>>>();
      decoders.add(String2IntegerDecoder.class);
      
      WebSocketEndpoint<Integer> wsClient = new WebSocketEndpoint<Integer>(
            "http://host:port/path", sslContext, 
            Collections.singletonMap(propName, (Object)propValue), 
            Collections.singletonMap("headerKey", Collections.singletonMap("headerName", "headerValue")), 
            handler, decoders);
      
      assertEquals(2, wsClient.getWsConfig().getUserProperties().size());
      Object actualContext = wsClient.getWsConfig().getUserProperties().get("org.apache.tomcat.websocket.SSL_CONTEXT");
      assertNotNull(actualContext);
      assertEquals(Boolean.TRUE, actualContext instanceof SSLContext);
      assertEquals(sslContext, actualContext);
      
      Object actualPropValue = wsClient.getWsConfig().getUserProperties().get(propName);
      assertNotNull(actualPropValue);
      assertEquals(Boolean.TRUE, actualPropValue instanceof String);
      assertEquals(propValue, actualPropValue);
      
      assertArrayEquals(wsClient.getDecoders().toArray(),
            wsClient.getWsConfig().getDecoders().toArray());

      wsClient = new WebSocketEndpoint<Integer>(
            new URI("http://host:port/path"), null, null, null, handler, null);
      
      assertEquals(0, wsClient.getWsConfig().getUserProperties().size());
      actualContext = wsClient.getWsConfig().getUserProperties().get("org.apache.tomcat.websocket.SSL_CONTEXT");
      assertNull(actualContext);
      
      actualPropValue = wsClient.getWsConfig().getUserProperties().get(propName);
      assertNull(actualPropValue);
      
      assertEquals(0, wsClient.getDecoders().size());
   }

   @Test
   public void testConnect() throws URISyntaxException, WebSocketException, DeploymentException, IOException {
      
      final WebSocketEndpoint<Integer> wsClient = new WebSocketEndpoint<Integer>(
            "http://host:port/path", null, null, null, handler, null);
      
      new Expectations() {{
         ContainerProvider.getWebSocketContainer(); result = container;
         container.connectToServer(
               wsClient, wsClient.getWsConfig(), wsClient.getUri());
         wsSession.addMessageHandler(handler);
      }};
      
      wsClient.connect();
   }

   @Test
   public void testOnOpeng() {
   }

   @Test
   public void testClose() throws URISyntaxException, DeploymentException, IOException, WebSocketException {
      final WebSocketEndpoint<Integer> wsClient = new WebSocketEndpoint<Integer>(
            "http://host:port/path", null, null, null, handler, null);
      
      new Expectations() {{
         ContainerProvider.getWebSocketContainer(); result = container;
         container.connectToServer(
               wsClient, wsClient.getWsConfig(), wsClient.getUri());
         wsSession.addMessageHandler(handler);
         wsSession.getId(); result = "Test WebSocket Session";
         wsSession.close();
         wsSession.getId(); result = "Test WebSocket Session";
      }};
      
      wsClient.connect();
      
      assertNotNull(wsClient.getWsSession());
      wsClient.close();
      wsClient.onClose(wsClient.getWsSession(), 
            new CloseReason(CloseCodes.CANNOT_ACCEPT, "Simulating onClose()"));
      assertNull(wsClient.getWsSession());
   }

   @Test
   public void testOnError() {
   }

   @Test
   public void testSend() throws URISyntaxException, DeploymentException, IOException, WebSocketException {
      final WebSocketEndpoint<Integer> wsClient = new WebSocketEndpoint<Integer>(
            "http://host:port/path", null, null, null, handler, null);

      final String message = "Test Message";
      
      new Expectations() {{
         ContainerProvider.getWebSocketContainer(); result = container;
         container.connectToServer(
               wsClient, wsClient.getWsConfig(), wsClient.getUri());
         wsSession.addMessageHandler(handler);
         wsSession.getBasicRemote().sendText(message);
      }};

      wsClient.connect();
      wsClient.send(message);
      
   }

}
