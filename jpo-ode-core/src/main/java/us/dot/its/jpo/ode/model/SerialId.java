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

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

public class SerialId {
   private static final char UUID_DELIMITER = '_';
   private static final char BUNDLE_RECORD_DELIMITER = '.';
   private static final char SERIAL_NUMBER_DELIMITER = '#';
   
   private String streamId;
   private int bundleSize = 1;
   private long bundleId = 0;
   private int recordId = 0;
   private long serialNumber = 0;

   
   public SerialId() {
      streamId = UUID.randomUUID().toString();
   }

   public SerialId(String streamId, int bundleSize, 
         long bundleId, int recordId) {
      this();
      if (streamId != null)
         this.streamId = streamId;
      else
         this.streamId = this.streamId + "_null";
      
      this.bundleSize = bundleSize;
      this.bundleId = bundleId + (recordId / this.bundleSize);
      this.recordId = recordId % bundleSize;
      this.serialNumber = calculateSerialNumber();
   }

   public SerialId (String serialId) throws Exception {
      
      String[] splitId  = serialId.split(
            "[" + UUID_DELIMITER + SERIAL_NUMBER_DELIMITER + BUNDLE_RECORD_DELIMITER +"]+");
      
      if (splitId.length != 5) {
         throw new Exception("Invalid serialId! Expected length 5 but was " + splitId.length);
      }
      
      this.streamId = splitId[0];
      this.bundleSize = Integer.parseInt(splitId[1]);
      this.bundleId = Integer.parseInt(splitId[2]);
      this.recordId = Integer.parseInt(splitId[3]);
      this.serialNumber = Integer.parseInt(splitId[4]);
   }
   
   public SerialId(String streamId, 
         int bundleSize, long bundleId, int recordId,
         long serialNumber) {
      
      this.streamId = streamId;
      this.bundleSize = bundleSize;
      this.bundleId = bundleId;
      this.recordId = recordId;
      this.serialNumber = serialNumber;
   }

   public SerialId(JsonNode jsonNode) {
      this(jsonNode.get("streamId").asText(), 
           jsonNode.get("bundleSize").asInt(), 
           jsonNode.get("bundleId").asLong(), 
           jsonNode.get("recordId").asInt(), 
           jsonNode.get("serialNumber").asLong());
   }

   private long calculateSerialNumber() {
      return (this.bundleId * this.bundleSize) + this.recordId;
   }

   public int nextRecordId() {
      return (recordId + 1) % bundleSize;
   }
   
   public long nextSerialNumber() {
      return serialNumber + 1;
   }
   
   public SerialId nextSerialId() {
      SerialId next = clone();
      next.increment();
      return next; 
   }
   
   synchronized public long increment() {
      bundleId += (recordId + 1) / bundleSize;
      recordId = nextRecordId();
      return ++serialNumber;
   }
   
   public SerialId clone() {
      SerialId clone = new SerialId(streamId, bundleSize, bundleId, recordId);
      clone.serialNumber = this.serialNumber;
      return clone;
   }
   

   public boolean isRightAfter (SerialId prev) {
      return (this.getSerialNumber() == prev.getSerialNumber() + 1);
   }
   
   public boolean isRightBefore (SerialId next) {
      return (this.getSerialNumber() + 1 == next.getSerialNumber());
   }
   
   public String getStreamId() {
      return streamId;
   }

   public SerialId setStreamId(String streamId) {
      this.streamId = streamId;
      return this;
   }

   public int getBundleSize() {
      return bundleSize;
   }

   public SerialId setBundleSize(int bundleSize) {
      this.bundleSize = bundleSize;
      return this;
   }

   public long getBundleId() {
      return bundleId;
   }

   public SerialId setBundleId(long bundleId) {
      this.bundleId = bundleId;
      return this;
   }

   public SerialId addBundleId(long num) {
       this.bundleId += num;
       return this;
    }

   public int getRecordId() {
      return recordId;
   }

   public SerialId setRecordId(int recordId) {
       this.recordId = recordId;
       return this;
    }

   public SerialId addRecordId(int num) {
       this.recordId += num;
       return this;
    }

   public long getSerialNumber() {
      return serialNumber;
   }

   @Override
   public String toString() {
      return streamId + UUID_DELIMITER + bundleSize + 
            BUNDLE_RECORD_DELIMITER + bundleId + 
            BUNDLE_RECORD_DELIMITER + recordId +
            SERIAL_NUMBER_DELIMITER + serialNumber;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (bundleId ^ (bundleId >>> 32));
      result = prime * result + bundleSize;
      result = prime * result + recordId;
      result = prime * result + ((streamId == null) ? 0 : streamId.hashCode());
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
      SerialId other = (SerialId) obj;
      if (bundleId != other.bundleId)
         return false;
      if (bundleSize != other.bundleSize)
         return false;
      if (recordId != other.recordId)
         return false;
      if (streamId == null) {
         if (other.streamId != null)
            return false;
      } else if (!streamId.equals(other.streamId))
         return false;
      return true;
   }

}
