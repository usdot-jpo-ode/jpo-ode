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

import us.dot.its.jpo.ode.plugin.ServiceRequest;

public class OdeRequestMsgMetadata extends OdeMsgMetadata {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  ServiceRequest request;

  public OdeRequestMsgMetadata() {
    super();
  }

  public OdeRequestMsgMetadata(OdeMsgPayload payload) {
    super(payload);
  }

  public OdeRequestMsgMetadata(String payloadType, SerialId serialId, String receivedAt) {
    super(payloadType, serialId, receivedAt);
  }

  public OdeRequestMsgMetadata(OdeMsgPayload payload, ServiceRequest request) {
    super(payload);
    setRequest(request);
  }

  public ServiceRequest getRequest() {
    return request;
  }

  public void setRequest(ServiceRequest request) {
    this.request = request;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((request == null) ? 0 : request.hashCode());
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
    OdeRequestMsgMetadata other = (OdeRequestMsgMetadata) obj;
    if (request == null) {
      if (other.request != null)
        return false;
    } else if (!request.equals(other.request))
      return false;
    return true;
  }
  
  

}
