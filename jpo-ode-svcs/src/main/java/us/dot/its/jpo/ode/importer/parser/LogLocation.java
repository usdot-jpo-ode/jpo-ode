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
package us.dot.its.jpo.ode.importer.parser;

/**
 * POJO class for TIM log file location data
 */
public class LogLocation {

   private int latitude;
   private int longitude;
   private int elevation;
   private short speed;
   private short heading;

   public LogLocation() {
      super();
   }

   public int getLatitude() {
      return latitude;
   }

   public void setLatitude(int latitude) {
      this.latitude = latitude;
   }

   public int getLongitude() {
      return longitude;
   }

   public void setLongitude(int longitude) {
      this.longitude = longitude;
   }

   public int getElevation() {
      return elevation;
   }

   public void setElevation(int elevation) {
      this.elevation = elevation;
   }

   public short getSpeed() {
      return speed;
   }

   public void setSpeed(short speed) {
      this.speed = speed;
   }

   public short getHeading() {
      return heading;
   }

   public void setHeading(short heading) {
      this.heading = heading;
   }
}
