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

import java.util.ArrayList;
import java.util.List;

public enum OdeRequestType {
   Subscription("sub"), Query("qry"), Test("tst"), Deposit("dep");

   private static List<String> shortNames = shortNamesAsList();
   private final String shortName;

   private OdeRequestType(String shortName) {
      this.shortName = shortName;
   }

   public String getShortName() {
      return shortName;
   }
   
   public static OdeRequestType getByShortName(String shortName) {
      OdeRequestType result = null;
      
      for (OdeRequestType value : OdeRequestType.values()) {
         if (shortName.equals(value.getShortName())) {
            result = value;
            break;
         }
      }
      return result;
   }
   
   public static String shortNames() {
      return shortNamesAsList().toString();
   }

   public static List<String> shortNamesAsList() {
      if (shortNames == null) {
         shortNames = new ArrayList<String>();
         
         for (OdeRequestType value : OdeRequestType.values()) {
            shortNames.add(value.getShortName());
         }
      }
      return shortNames;
   }
}
