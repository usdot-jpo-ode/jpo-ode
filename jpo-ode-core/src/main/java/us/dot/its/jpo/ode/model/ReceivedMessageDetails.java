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

public class ReceivedMessageDetails extends OdeObject {

   private static final long serialVersionUID = -122988228561853841L;

   private OdeLogMsgMetadataLocation locationData;
   private RxSource rxSource;

   public ReceivedMessageDetails() {
      super();
   }
   
   public ReceivedMessageDetails(OdeLogMsgMetadataLocation locationData, RxSource rxSource) {
      super();
      this.setLocationData(locationData);
      this.setRxSource(rxSource);
   }

   public OdeLogMsgMetadataLocation getLocationData() {
      return locationData;
   }

   public void setLocationData(OdeLogMsgMetadataLocation locationData) {
      this.locationData = locationData;
   }

   public RxSource getRxSource() {
      return rxSource;
   }

   public void setRxSource(RxSource rxSource) {
      this.rxSource = rxSource;
   }
}
