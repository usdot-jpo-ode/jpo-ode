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

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeObject implements Serializable {
   private static final long serialVersionUID = 7514526408925039533L;

   public String toJson() {
      return JsonUtils.toJson(this, false);
   }

   public String toJson(boolean verbose) {
      return JsonUtils.toJson(this, verbose);
   }

   public String toXml() throws XmlUtilsException, JsonProcessingException {
      return XmlUtils.toXmlS(this);
   }

   @Override
   public String toString() {
      return this.toJson(true);
   }

}
