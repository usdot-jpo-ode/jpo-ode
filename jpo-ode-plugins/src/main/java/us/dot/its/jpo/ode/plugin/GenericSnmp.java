package us.dot.its.jpo.ode.plugin;

public class GenericSnmp {
   public static class SNMP {
      private String rsuid;
      private int msgid;
      private int mode;
      private int channel;
      private int interval;
      private String deliverystart;
      private String deliverystop;
      private int enable;
      private int status;
      public String getRsuid() {
         return rsuid;
      }
      public void setRsuid(String rsuid) {
         this.rsuid = rsuid;
      }
      public int getMsgid() {
         return msgid;
      }
      public void setMsgid(int msgid) {
         this.msgid = msgid;
      }
      public int getMode() {
         return mode;
      }
      public void setMode(int mode) {
         this.mode = mode;
      }
      public int getChannel() {
         return channel;
      }
      public void setChannel(int channel) {
         this.channel = channel;
      }
      public int getInterval() {
         return interval;
      }
      public void setInterval(int interval) {
         this.interval = interval;
      }
      public String getDeliverystart() {
         return deliverystart;
      }
      public void setDeliverystart(String deliverystart) {
         this.deliverystart = deliverystart;
      }
      public String getDeliverystop() {
         return deliverystop;
      }
      public void setDeliverystop(String deliverystop) {
         this.deliverystop = deliverystop;
      }
      public int getEnable() {
         return enable;
      }
      public void setEnable(int enable) {
         this.enable = enable;
      }
      public int getStatus() {
         return status;
      }
      public void setStatus(int status) {
         this.status = status;
      }
   }
   
   private GenericSnmp() {
      
   }
}
