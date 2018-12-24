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
package us.dot.its.jpo.ode.coder;

public class Ieee1609ContentValidator {

   private Ieee1609ContentValidator() {
   }

//   public static byte[] getUnsecuredData(Ieee1609Dot2Content ieee1609dot2ContentIn) {
//      byte[] unsecuredData;
//      try {
//         if (ieee1609dot2ContentIn.getUnsecuredData() != null) {
//            unsecuredData = ieee1609dot2ContentIn.getUnsecuredData().byteArrayValue();
//         } else {
//            unsecuredData = ieee1609dot2ContentIn.getSignedData().getTbsData().getPayload().getData().getContent()
//                  .getUnsecuredData().byteArrayValue();
//         }
//      } catch (NullPointerException e) { // NOSONAR
//         unsecuredData = null;
//      }
//
//      return unsecuredData;
//   }

}
