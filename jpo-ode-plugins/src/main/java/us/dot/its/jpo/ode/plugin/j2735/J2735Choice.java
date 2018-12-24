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

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public abstract class J2735Choice extends Asn1Object {
   private static final long serialVersionUID = 2187214491924559144L;
   
   private static final Logger logger = LoggerFactory.getLogger(J2735Choice.class);

   private String chosenFieldName;

   public J2735Choice() {
      super();
   }

   public String getChosenFieldName() {
      return chosenFieldName;
   }

   public void setChosenFieldName(String fieldName) {
      this.chosenFieldName = fieldName;
   }

   public void setChosenField(String fieldName, Object fieldValue) {
      this.chosenFieldName = fieldName;
      Field field;
      try {
         field = this.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         field.set(this, fieldValue);
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
         logger.error("Error Setting Chosen Field", e);
      }
      
   }

}
