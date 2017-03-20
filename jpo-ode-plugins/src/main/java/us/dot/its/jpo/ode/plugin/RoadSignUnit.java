package us.dot.its.jpo.ode.plugin;

public class RoadSignUnit {
   public static class RSU {
      private String target;
      private String username;
      private String password;
      private int retries;
      private int timeout;
      public String getTarget() {
         return target;
      }
      public void setTarget(String target) {
         this.target = target;
      }
      public String getUsername() {
         return username;
      }
      public void setUsername(String username) {
         this.username = username;
      }
      public String getPassword() {
         return password;
      }
      public void setPassword(String password) {
         this.password = password;
      }
      public int getRetries() {
         return retries;
      }
      public void setRetries(int retries) {
         this.retries = retries;
      }
      public int getTimeout() {
         return timeout;
      }
      public void setTimeout(int timeout) {
         this.timeout = timeout;
      }
   }
   private RoadSignUnit() {
      
   }
}
