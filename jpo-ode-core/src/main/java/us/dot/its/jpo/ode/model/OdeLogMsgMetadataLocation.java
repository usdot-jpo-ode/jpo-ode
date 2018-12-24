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

public class OdeLogMsgMetadataLocation extends OdeObject {

   private static final long serialVersionUID = 3174683494132372801L;

   String latitude;
   String longitude;
   String elevation;
   String speed;
   String heading;

   public OdeLogMsgMetadataLocation() {
    super();
  }

  public OdeLogMsgMetadataLocation(String latitude, String longitude, String elevation, String speed, String heading) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
      this.elevation = elevation;
      this.speed = speed;
      this.heading = heading;
   }

   public String getLatitude() {
      return latitude;
   }

   public void setLatitude(String latitude) {
      this.latitude = latitude;
   }

   public String getLongitude() {
      return longitude;
   }

   public void setLongitude(String longitude) {
      this.longitude = longitude;
   }

   public String getElevation() {
      return elevation;
   }

   public void setElevation(String elevation) {
      this.elevation = elevation;
   }

   public String getSpeed() {
      return speed;
   }

   public void setSpeed(String speed) {
      this.speed = speed;
   }

   public String getHeading() {
      return heading;
   }

   public void setHeading(String heading) {
      this.heading = heading;
   }

}
