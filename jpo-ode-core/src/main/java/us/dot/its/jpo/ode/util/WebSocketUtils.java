package us.dot.its.jpo.ode.util;

import java.io.IOException;

import javax.websocket.Session;

public class WebSocketUtils {
   
   public static void send(Session session, String message) throws IOException {
      synchronized(session) {
         if (session != null)
            session.getBasicRemote().sendText(message);
      }
   }
   
   public static void sendSync(Session session, String message) throws IOException {
      synchronized(session) {
         if (session != null)
            session.getBasicRemote().sendText(message);
      }
   }
   
   public static void sendAsync(Session session, String message) throws IOException {
      synchronized(session) {
         if (session != null)
            session.getAsyncRemote().sendText(message);
      }
   }
}
