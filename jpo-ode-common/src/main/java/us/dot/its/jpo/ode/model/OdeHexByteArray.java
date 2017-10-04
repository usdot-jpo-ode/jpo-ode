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

import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeHexByteArray extends OdeObject {

   private static final long serialVersionUID = 6106562581659367345L;
   
   private String bytes;

   public OdeHexByteArray() {
      super();
   }

   public OdeHexByteArray(String bytes) {
      super();
      this.bytes = bytes;
   }

   public OdeHexByteArray(byte[] bytes) {
      setBytes(bytes);
   }

   public String getBytes() {
      return bytes;
   }

   public void setBytes(String bytes) {
      this.bytes = bytes;
   }

   public void setBytes(byte[] bytes) {
      this.bytes = CodecUtils.toHex(bytes);
   }

   
}
