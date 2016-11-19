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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdeMsgPayload extends OdeMessage {
   private static final long serialVersionUID = -7711340868799607662L;

   private static Logger logger = LoggerFactory.getLogger(OdeMsgPayload.class);

   private OdeDataType  dataType;

   public OdeMsgPayload() {
      super();
      try {
         this.dataType = OdeDataType.getByClassName(this.getClass().getName());
      } catch (ClassNotFoundException e) {
         logger.error("Unable to determine data type.", e);
         this.dataType = OdeDataType.Unknown;
      }
   }

   public OdeDataType getDataType() {
      return dataType;
   }

   public void setDataType(OdeDataType dataType) {
      this.dataType = dataType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
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
      OdeMsgPayload other = (OdeMsgPayload) obj;
      if (dataType != other.dataType)
         return false;
      return true;
   }
}
