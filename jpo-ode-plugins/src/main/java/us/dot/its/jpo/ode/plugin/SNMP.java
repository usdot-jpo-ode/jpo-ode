/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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

   public SNMP() {
      super();
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + channel;
      result = prime * result + ((deliverystart == null) ? 0 : deliverystart.hashCode());
      result = prime * result + ((deliverystop == null) ? 0 : deliverystop.hashCode());
      result = prime * result + enable;
      result = prime * result + interval;
      result = prime * result + mode;
      result = prime * result + msgid;
      result = prime * result + ((rsuid == null) ? 0 : rsuid.hashCode());
      result = prime * result + status;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SNMP other = (SNMP) obj;
      if (channel != other.channel)
         return false;
      if (deliverystart == null) {
         if (other.deliverystart != null)
            return false;
      } else if (!deliverystart.equals(other.deliverystart))
         return false;
      if (deliverystop == null) {
         if (other.deliverystop != null)
            return false;
      } else if (!deliverystop.equals(other.deliverystop))
         return false;
      if (enable != other.enable)
         return false;
      if (interval != other.interval)
         return false;
      if (mode != other.mode)
         return false;
      if (msgid != other.msgid)
         return false;
      if (rsuid == null) {
         if (other.rsuid != null)
            return false;
      } else if (!rsuid.equals(other.rsuid))
         return false;
      if (status != other.status)
         return false;
      return true;
   }

}
