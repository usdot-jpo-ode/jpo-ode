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
package us.dot.its.jpo.ode.plugin.j2735;

public class J2735VehicleSafetyExtensions extends J2735BsmPart2ExtensionBase {
   private static final long serialVersionUID = 1L;

   private J2735BitString events;
   private J2735PathHistory pathHistory;
   private J2735PathPrediction pathPrediction;
   private J2735BitString lights;

   public J2735BitString getEvents() {
      return events;
   }

   public void setEvents(J2735BitString events) {
      this.events = events;
   }

   public J2735PathHistory getPathHistory() {
      return pathHistory;
   }

   public void setPathHistory(J2735PathHistory pathHistory) {
      this.pathHistory = pathHistory;
   }

   public J2735PathPrediction getPathPrediction() {
      return pathPrediction;
   }

   public void setPathPrediction(J2735PathPrediction pathPrediction) {
      this.pathPrediction = pathPrediction;
   }

   public J2735BitString getLights() {
      return lights;
   }

   public void setLights(J2735BitString lights) {
      this.lights = lights;
   }
}
