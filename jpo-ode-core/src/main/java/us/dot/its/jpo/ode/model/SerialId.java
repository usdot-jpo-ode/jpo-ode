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
      super();
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

   public SerialId (String serialId) {
      
      String[] splitId  = serialId.split(
            "[" + UUID_DELIMITER + SERIAL_NUMBER_DELIMITER +"]+");
      if (splitId.length >= 1)
         this.streamId     = splitId[0];
      else
         this.streamId     = serialId;

      if (splitId.length >= 3)
         this.serialNumber = Integer.parseInt(splitId[2]);
      else
         this.serialNumber = -1;
      
      if (splitId.length >= 2)
         splitId  = splitId[1].split(
            "[" + BUNDLE_RECORD_DELIMITER +"]+");
      
      if (splitId.length >= 1)
         this.bundleSize   = Integer.parseInt(splitId[0]);
      
      if (splitId.length >= 2)
         this.bundleId     = Long.parseLong(splitId[1]);
      
      if (splitId.length >= 3)
         this.recordId     = Integer.parseInt(splitId[2]);

      if (this.serialNumber == -1)
         this.serialNumber = calculateSerialNumber();
   }
   
   public SerialId(String streamId, 
         int bundleSize, long bundleId, int recordId,
         long serialNumber) {
      super();
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
      return this.bundleId * this.bundleSize + this.recordId;
   }

   public static SerialId create(String serialIdStr) throws Exception {
      int bundleSize;
      long bundleId;
      int recordId;
      try {
         if (serialIdStr != null && !serialIdStr.equals("")) {
            String[] splitId  = serialIdStr.split(
                  "[" + UUID_DELIMITER + SERIAL_NUMBER_DELIMITER +"]+");
            
            if (splitId.length == 3) {
               String streamId = splitId[0];
               long serialNumber;
               try {
                  serialNumber = Integer.parseInt(splitId[2]);
               } catch (Exception e) {
                  throw new Exception("SerialId has Non-Numeric Serial Number: "
                        + splitId[2], e);
               }
               splitId  = splitId[1].split(
                     "[" + BUNDLE_RECORD_DELIMITER +"]+");
               
               if (splitId.length == 3) {
                  try {
                     bundleSize = Integer.parseInt(splitId[0]);
                  } catch (Exception e) {
                     throw new Exception("SerialId has Non-Numeric Bundle Size: "
                           + splitId[0], e);
                  }
                  try {
                     bundleId = Long.parseLong(splitId[1]);
                  } catch (Exception e) {
                     throw new Exception("SerialId has Non-Numeric Bundle ID: "
                           + splitId[1], e);
                  }
                  try {
                     recordId = Integer.parseInt(splitId[2]);
                  } catch (Exception e) {
                     throw new Exception("SerialId has Non-Numeric Record ID: "
                           + splitId[2], e);
                  }
               } else {
                  throw new Exception("Serial ID missing BundleSize.BundleId.RecordId");
               }
               
               return new SerialId(streamId, 
                     bundleSize, bundleId, recordId, 
                     serialNumber);
            } else {
               throw new Exception("Serial ID missing StreamId, BundleSize.BundleId.RecordId and SerialNumber components");
            }
         } else {
            throw new Exception("SerialId cannot be null or blank");
         }
      } catch (Exception e) {
         throw new Exception("Serial ID Format Must be "
               + "StreamId_BundleSize.BundleId.RecordId#SerialNumber"
               + "where BundleSize, BundleId, RecordId and SerialNumber components"
               + "are integer values", e);
      }
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