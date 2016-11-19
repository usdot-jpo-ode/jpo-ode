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


public class OdeStatus extends OdeMsgPayload {
   private static final long serialVersionUID = -8787345244306039604L;

   public static enum Code {
      SUCCESS, FAILURE, 
      SOURCE_CONNECTION_ERROR, INVALID_REQUEST_TYPE_ERROR, 
      INVALID_DATA_TYPE_ERROR, COMMENT, DATA_TYPE_NOT_SUPPORTED
   }

   Code code;
   String message;
   String requestId;

   public OdeStatus() {
      super();
   }

   public OdeStatus(Code code, String message) {
      super();
      this.code = code;
      this.message = message;
   }

   public Code getCode() {
      return code;
   }

   public OdeStatus setCode(Code code) {
      this.code = code;
      return this;
   }

   public String getMessage() {
      return message;
   }

   public OdeStatus setMessage(String message) {
      this.message = message;
      return this;
   }

   public String getRequestId() {
      return requestId;
   }

   public OdeStatus setRequestId(String requestId) {
      this.requestId = requestId;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((code == null) ? 0 : code.hashCode());
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result
            + ((requestId == null) ? 0 : requestId.hashCode());
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
      OdeStatus other = (OdeStatus) obj;
      if (code != other.code)
         return false;
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      if (requestId == null) {
         if (other.requestId != null)
            return false;
      } else if (!requestId.equals(other.requestId))
         return false;
      return true;
   }

}
