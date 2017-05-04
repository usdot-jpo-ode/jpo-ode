package us.dot.its.jpo.ode.plugin;

import java.text.ParseException;
import java.time.ZonedDateTime;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class SNMP extends OdeObject {
   private static final long serialVersionUID = 6622977652181526235L;

   private String rsuid;
   private int msgid;
   private int mode;
   private int channel;
   private int interval;
   private String deliverystart;
   private String deliverystop;
   private int enable;
   private int status;

   private SNMP() {
      throw new UnsupportedOperationException();
   }

   public SNMP(String rsuid, int msgid, int mode, int channel, int interval, String deliverystart, String deliverystop,
         int enable, int status) {
      super();
      this.rsuid = rsuid;
      this.msgid = msgid;
      this.mode = mode;
      this.channel = channel;
      this.interval = interval;
      this.deliverystart = deliverystart;
      this.deliverystop = deliverystop;
      this.enable = enable;
      this.status = status;
   }

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

   public static String snmpTimestampFromIso(String isoTimestamp) throws ParseException {
      ZonedDateTime zdt = DateTimeUtils.isoDateTime(isoTimestamp);
      byte[] bdt = new byte[6];
      bdt[0] = (byte) zdt.getMonthValue();
      bdt[1] = (byte) zdt.getDayOfMonth();
      bdt[2] = (byte) (zdt.getYear() / 100);
      bdt[3] = (byte) (zdt.getYear() % 100);
      bdt[4] = (byte) zdt.getHour();
      bdt[5] = (byte) zdt.getMinute();
      return CodecUtils.toHex(bdt);
   }

}
