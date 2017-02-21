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
package us.dot.its.jpo.ode.dds;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;

public abstract class DdsDecoder implements WebSocketMessageDecoder<DdsMessage> {

   public static class DdsDecoderException extends Exception {

      private static final long serialVersionUID = -4474047515873708804L;

      public DdsDecoderException(String string) {
         super(string);
      }

   }

   @Override
   public void init(EndpointConfig endpointConfig) {
   }

   @Override
   public void destroy() {
   }

   @Override
   public abstract DdsMessage decode(String message) throws DecodeException;
   
   @Override
   public abstract boolean willDecode(String message);}
