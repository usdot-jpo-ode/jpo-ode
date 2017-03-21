package us.dot.its.jpo.ode.plugin;

public class RoadSignUnit {
   public static class RSU {
      private String rsuTarget;
      private String rsuUsername;
      private String rsuPassword;
      private int rsuRetries;
      private int rsuTimeout;
      public String getrsuTarget() {
         return rsuTarget;
      }
      public void setrsuTarget(String rsuTarget) {
         this.rsuTarget = rsuTarget;
      }
      public String getrsuUsername() {
         return rsuUsername;
      }
      public void setrsuUsername(String rsuUsername) {
         this.rsuUsername = rsuUsername;
      }
      public String getrsuPassword() {
         return rsuPassword;
      }
      public void setrsuPassword(String rsuPassword) {
         this.rsuPassword = rsuPassword;
      }
      public int getrsuRetries() {
         return rsuRetries;
      }
      public void setrsuRetries(int rsuRetries) {
         this.rsuRetries = rsuRetries;
      }
      public int getrsuTimeout() {
         return rsuTimeout;
      }
      public void setTimeout(int rsuTimeout) {
         this.rsuTimeout = rsuTimeout;
      }
   }
   private RoadSignUnit() {
      
   }
}
