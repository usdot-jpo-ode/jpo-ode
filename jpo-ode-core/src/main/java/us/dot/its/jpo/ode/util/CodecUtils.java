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
package us.dot.its.jpo.ode.util;

import javax.xml.bind.DatatypeConverter;

public class CodecUtils {

   static public String toHex(byte[] bytes) {
      return (bytes != null ? DatatypeConverter.printHexBinary(bytes) : "");
   }

   static public byte[] fromHex(String hex) {
      return DatatypeConverter.parseHexBinary(hex);
   }

   static public String toBase64(byte[] bytes) {
      return (bytes != null ? DatatypeConverter.printBase64Binary(bytes) : "");
   }

   public static byte[] fromBase64(String base64) {
      return DatatypeConverter.parseBase64Binary(base64);
   }
}
