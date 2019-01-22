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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public enum OdeDataType {
   Status("status", OdeStatus.class),
   Control("control", OdeControlData.class),
   Authorization("auth", OdeAuthorization.class),
   BasicSafetyMessage("bsm", J2735Bsm.class),
   TravelerInformationMessage("tim", OdeTravelerInputData.class),
   AggregateData("agg", OdeAggregateData.class),
   AsnBase64("asnbase64", String.class),
   AsnHex("asnhex", String.class),
   OtherData("other", OdeMsgPayload.class), 
   Unknown("unknown", OdeMsgPayload.class);
   
   private static List<String> shortNames = shortNamesAsList();

   private final String shortName;
   
   private final Class<?> clazz;

   private OdeDataType(String shortName, Class<?> clazz) {
      this.shortName = shortName;
      this.clazz = clazz;
   }

   public String getShortName() {
      return shortName;
   }
   
   public Class<?> getClazz() {
      return clazz;
   }

   public static OdeDataType getByShortName(String shortName) {
      OdeDataType result = null;
      
      for (OdeDataType value : OdeDataType.values()) {
         if (shortName.equals(value.getShortName())) {
            result = value;
            break;
         }
      }
      return result;
   }
   
   public static OdeDataType getByClazz(Class<?> clazz) {
      OdeDataType result = null;
      
      for (OdeDataType value : OdeDataType.values()) {
         if (clazz == value.getClazz()) {
            result = value;
            break;
         }
      }
      return result;
   }

   public static OdeDataType getByClassName(String className) throws ClassNotFoundException {
      return getByClazz(Class.forName(className));
   }

   public static String shortNames() {
      return shortNamesAsList().toString();
   }

   public static List<String> shortNamesAsList() {
      if (shortNames == null) {
         shortNames = new ArrayList<String>();
         for (OdeDataType value : OdeDataType.values()) {
            shortNames.add(value.getShortName());
         }
      }
      return shortNames;
   }

   public static OdeDataType getFromJsonNode(JsonNode data, String name) throws IOException {
      OdeDataType odeDataType = null;
      if (data != null) {
         JsonNode dataType = data.get(name);
         if (dataType != null) { 
            odeDataType = OdeDataType.getByShortName(dataType.textValue());
         }
      }
      return odeDataType;
   }

}
