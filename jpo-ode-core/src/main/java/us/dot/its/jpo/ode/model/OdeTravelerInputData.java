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

import lombok.Data;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;

@Data
public class OdeTravelerInputData extends OdeObject {

  private static final long serialVersionUID = 8769107278440796699L;

  private ServiceRequest request;

  private OdeTravelerInformationMessage tim;

  public ServiceRequest getRequest() {
    return request;
  }

  public void setRequest(ServiceRequest request) {
    this.request = request;
  }

  public OdeTravelerInformationMessage getTim() {
    return tim;
  }

  public void setTim(OdeTravelerInformationMessage tim) {
    this.tim = tim;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof OdeTravelerInputData)) {
      return false;
    }
    final OdeTravelerInputData other = (OdeTravelerInputData) o;
    if (!other.canEqual((Object) this)) {
      return false;
    }
    final Object this$request = this.getRequest();
    final Object other$request = other.getRequest();
    if (this$request == null ? other$request != null : !this$request.equals(other$request)) {
      return false;
    }
    final Object this$tim = this.getTim();
    final Object other$tim = other.getTim();
    if (this$tim == null ? other$tim != null : !this$tim.equals(other$tim)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof OdeTravelerInputData;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $request = this.getRequest();
    result = result * PRIME + ($request == null ? 43 : $request.hashCode());
    final Object $tim = this.getTim();
    result = result * PRIME + ($tim == null ? 43 : $tim.hashCode());
    return result;
  }
}
