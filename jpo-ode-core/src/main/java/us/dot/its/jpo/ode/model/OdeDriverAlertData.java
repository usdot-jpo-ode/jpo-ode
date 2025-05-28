/*******************************************************************************
 * Copyright 2018 572682
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.model;

/**
 * Class that encapsulates driver alert data when processing Driver Alert Log Files.

 * @author Anthony Chen - 11/4/17
 */
public class OdeDriverAlertData extends OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> {

  private static final long serialVersionUID = 2057040404896561615L;

  public OdeDriverAlertData() {
    super();
  }

  public OdeDriverAlertData(OdeLogMetadata metadata, OdeMsgPayload<OdeObject> payload) {
    super(metadata, payload);
  }


}
