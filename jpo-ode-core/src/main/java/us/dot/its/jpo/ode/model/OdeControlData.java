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
package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.dds.DdsStatusMessage;

public class OdeControlData extends OdeMessage {

   private static final long serialVersionUID = 1L;
   
   private Long dataSourceBundleCount; 
   private Long receivedRecordCount; 
   private Long sentRecordCount; 
   private Long depositCount; 
   private StatusTag tag;
   private String message;
   
   public OdeControlData() {
      super();
   }

   public OdeControlData(StatusTag tag) {
      super();
      setTag(tag);
   }

   public OdeControlData(DdsStatusMessage controlMessage) {
      setTag(controlMessage.getTag());
      if (controlMessage.getTag() == StatusTag.STOP)
         setDataSourceBundleCount(controlMessage.getRecordCount());
      else if (controlMessage.getTag() == StatusTag.DEPOSITED)
         setDepositCount(controlMessage.getRecordCount());
   }

   public Long getDataSourceBundleCount() {
      return dataSourceBundleCount;
   }

   public OdeControlData setDataSourceBundleCount(Long dataSourceBundleCount) {
      this.dataSourceBundleCount = dataSourceBundleCount;
      return this;
   }

   public Long getReceivedRecordCount() {
      return receivedRecordCount;
   }

   public OdeControlData setReceivedRecordCount(Long receivedRecordCount) {
      this.receivedRecordCount = receivedRecordCount;
      return this;
   }

   public Long getSentRecordCount() {
      return sentRecordCount;
   }

   public OdeControlData setSentRecordCount(Long sentRecordCount) {
      this.sentRecordCount = sentRecordCount;
      return this;
   }

   public Long getDepositCount() {
      return depositCount;
   }

   public void setDepositCount(Long depositCount) {
      this.depositCount = depositCount;
   }

   public StatusTag getTag() {
      return tag;
   }

   public void setTag(StatusTag tag) {
      this.tag = tag;
   }

   
   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((dataSourceBundleCount == null) ? 0
            : dataSourceBundleCount.hashCode());
      result = prime * result
            + ((depositCount == null) ? 0 : depositCount.hashCode());
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result + ((receivedRecordCount == null) ? 0
            : receivedRecordCount.hashCode());
      result = prime * result
            + ((sentRecordCount == null) ? 0 : sentRecordCount.hashCode());
      result = prime * result + ((tag == null) ? 0 : tag.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeControlData other = (OdeControlData) obj;
      if (dataSourceBundleCount == null) {
         if (other.dataSourceBundleCount != null)
            return false;
      } else if (!dataSourceBundleCount.equals(other.dataSourceBundleCount))
         return false;
      if (depositCount == null) {
         if (other.depositCount != null)
            return false;
      } else if (!depositCount.equals(other.depositCount))
         return false;
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      if (receivedRecordCount == null) {
         if (other.receivedRecordCount != null)
            return false;
      } else if (!receivedRecordCount.equals(other.receivedRecordCount))
         return false;
      if (sentRecordCount == null) {
         if (other.sentRecordCount != null)
            return false;
      } else if (!sentRecordCount.equals(other.sentRecordCount))
         return false;
      if (tag != other.tag)
         return false;
      return true;
   }

}
