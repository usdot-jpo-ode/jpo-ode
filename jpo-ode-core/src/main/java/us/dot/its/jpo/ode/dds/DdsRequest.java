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
package us.dot.its.jpo.ode.dds;

import us.dot.its.jpo.ode.model.BaseRequest;

@SuppressWarnings("serial")
public class DdsRequest extends BaseRequest {

   public enum Dialog {
      VSD (154),
      ISD (162),
      ASD (156),
      ALL (-1);

      private final int id;

      Dialog(int id) {
         this.id = id;
      }

      public int getId() {
         return id;
      }

      public static Dialog getById(int id) {
         Dialog result = null;
         for (Dialog d : Dialog.values()) {
            if (d.getId() == id) {
               result = d;
               break;
            }
         }
         return result;
      }
   }

   public enum SystemName {
      SDC ("SDC 2.3"),
      SDW ("SDW 2.3"),
      SDPC ("SDPC 2.3");

      private final String name;

      SystemName(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }
   }

   public enum EncodeType {BASE64, HEX}

   private int dialogID;
   private String resultEncoding;

   public int getDialogID() {
      return dialogID;
   }

   public DdsRequest setDialogID(int dialogID) {
      this.dialogID = dialogID;
      return this;
   }

   public String getResultEncoding() {
      return resultEncoding;
   }

   public DdsRequest setResultEncoding(String resultEncoding) {
      this.resultEncoding = resultEncoding;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + dialogID;
      result = prime * result + ((resultEncoding == null) ? 0 : resultEncoding.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DdsRequest other = (DdsRequest) obj;
      if (dialogID != other.dialogID)
         return false;
      if (resultEncoding == null) {
         if (other.resultEncoding != null)
            return false;
      } else if (!resultEncoding.equals(other.resultEncoding))
         return false;
      return true;
   }

}
