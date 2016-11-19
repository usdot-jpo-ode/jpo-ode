/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeData extends OdeMsgPayload implements OdeFilterable {
   private static final long serialVersionUID = -7711340868799607662L;
   private String serialId;
   private String receivedAt;

   
   public OdeData() {
      super();
      this.receivedAt = DateTimeUtils.isoDateTime(ZonedDateTime.now(ZoneOffset.UTC));
   }

   public OdeData(String serialId) {
      super();
      setSerialId(serialId);
      this.receivedAt = DateTimeUtils.isoDateTime(ZonedDateTime.now(ZoneOffset.UTC));
   }

   public OdeData(String streamId, long bundleId, long recordId) {
      super();
      setSerialId(buildSerialId(streamId, bundleId, recordId));
      this.receivedAt = DateTimeUtils.isoDateTime(ZonedDateTime.now(ZoneOffset.UTC));
   }

   public String getSerialId() {
      return serialId;
   }

   public void setSerialId(String serialId) {
      this.serialId = serialId;
   }

   public static String buildSerialId(String streamId, long bundleId, long recordId) {
      return streamId + "." + bundleId + "." + recordId;
   }

   public String getReceivedAt() {
      return receivedAt;
   }

   public void setReceivedAt(String receivedAt) {
      this.receivedAt = receivedAt;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((receivedAt == null) ? 0 : receivedAt.hashCode());
      result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
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
      OdeData other = (OdeData) obj;
      if (receivedAt == null) {
         if (other.receivedAt != null)
            return false;
      } else if (!receivedAt.equals(other.receivedAt))
         return false;
      if (serialId == null) {
         if (other.serialId != null)
            return false;
      } else if (!serialId.equals(other.serialId))
         return false;
      return true;
   }


}
