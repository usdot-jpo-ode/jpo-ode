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

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class OdeException extends Exception {

	/**
	 * 
	 */
   private static final long serialVersionUID = -4133732677963262764L;
   
   OdeStatus.Code code;
   
   public OdeException() {
      super();
      this.code = OdeStatus.Code.FAILURE;
   }

   public OdeException(Throwable cause) {
      super(cause);
      this.code = OdeStatus.Code.FAILURE;
   }

   public OdeException (String message) {
      super(message);
      this.code = OdeStatus.Code.FAILURE;
   }

   public OdeException (String message, Throwable cause) {
      super(message, cause);
      this.code = OdeStatus.Code.FAILURE;
   }

   public OdeException (OdeStatus.Code code, String message) {
   	super(message);
   	this.code = code;
   }

   public OdeException (OdeStatus.Code code, String message, Throwable cause) {
   	super(message, cause);
   	this.code = code;
   }

   public String toJson() {
      ObjectNode jsonObject = JsonUtils.newObjectNode("code", code.name());
      JsonUtils.addNode(jsonObject, "message", getMessage());
      JsonUtils.addNode(jsonObject, "cause", getCause().toString());
   	return jsonObject.toString();
   }
}
