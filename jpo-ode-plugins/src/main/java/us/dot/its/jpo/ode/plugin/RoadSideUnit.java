package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;

public class RoadSideUnit {
   public static class RSU extends OdeObject {

      private static final long serialVersionUID = 3149576493038209597L;

      private String rsuTarget;
      private String rsuUsername;
      private String rsuPassword;
      private int rsuRetries;
      private int rsuTimeout;

      public RSU() {
         super();
      }

      public RSU(String rsuTarget, String rsuUsername, String rsuPassword, int rsuRetries, int rsuTimeout) {
         super();
         this.rsuTarget = rsuTarget;
         this.rsuUsername = rsuUsername;
         this.rsuPassword = rsuPassword;
         this.rsuRetries = rsuRetries;
         this.rsuTimeout = rsuTimeout;
      }

      public String getRsuTarget() {
         return rsuTarget;
      }

      public void setRsuTarget(String rsuTarget) {
         this.rsuTarget = rsuTarget;
      }

      public String getRsuUsername() {
         return rsuUsername;
      }

      public void setRsuUsername(String rsuUsername) {
         this.rsuUsername = rsuUsername;
      }

      public String getRsuPassword() {
         return rsuPassword;
      }

      public void setRsuPassword(String rsuPassword) {
         this.rsuPassword = rsuPassword;
      }

      public int getRsuRetries() {
         return rsuRetries;
      }

      public void setRsuRetries(int rsuRetries) {
         this.rsuRetries = rsuRetries;
      }

      public int getRsuTimeout() {
         return rsuTimeout;
      }

      public void setRsuTimeout(int rsuTimeout) {
         this.rsuTimeout = rsuTimeout;
      }
   }

   private RoadSideUnit() {
      throw new UnsupportedOperationException();
   }
}
