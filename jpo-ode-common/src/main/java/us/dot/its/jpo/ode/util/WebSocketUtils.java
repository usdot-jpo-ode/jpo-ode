package us.dot.its.jpo.ode.util;

import java.io.IOException;

import javax.websocket.Session;

public class WebSocketUtils {

   /**
    * @param session
    * @param message
    * @throws IOException
    */
   public static void send(Session session, String message) throws IOException {
      if (session != null)
         synchronized (session) {
            session.getBasicRemote().sendText(message);
         }
   }

   public static void sendSync(Session session, String message) throws IOException {
      if (session != null)
         synchronized (session) {
            session.getBasicRemote().sendText(message);
         }
   }

   public static void sendAsync(Session session, String message) throws IOException {
      if (session != null)
         synchronized (session) {
            session.getAsyncRemote().sendText(message);
         }
   }
}
