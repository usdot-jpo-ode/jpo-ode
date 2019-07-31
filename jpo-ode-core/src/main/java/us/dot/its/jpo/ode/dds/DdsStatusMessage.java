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

import us.dot.its.jpo.ode.dds.DdsRequest.Dialog;
import us.dot.its.jpo.ode.model.StatusTag;

public class DdsStatusMessage implements DdsMessage {
   private StatusTag tag;
   private String encoding;
   private Dialog dialog;
   private long recordCount;
   private String connectionDetails;

   public DdsStatusMessage() {
      super();
   }

   public DdsStatusMessage(StatusTag tag, String encoding, Dialog dialog) {
      super();
      this.tag = tag;
      this.encoding = encoding;
      this.dialog = dialog;
   }

   public StatusTag getTag() {
      return tag;
   }

   public DdsStatusMessage setTag(StatusTag tag) {
      this.tag = tag;
      return this;
   }

   public String getEncoding() {
      return encoding;
   }

   public DdsStatusMessage setEncoding(String encoding) {
      this.encoding = encoding;
      return this;
   }

   public Dialog getDialog() {
      return dialog;
   }

   public DdsStatusMessage setDialog(Dialog dialog) {
      this.dialog = dialog;
      return this;
   }

   public long getRecordCount() {
      return recordCount;
   }

   public DdsStatusMessage setRecordCount(long recordCount) {
      this.recordCount = recordCount;
      return this;
   }

   public String getConnectionDetails() {
      return connectionDetails;
   }

   public DdsStatusMessage setConnectionDetails(String connectionDetails) {
      this.connectionDetails = connectionDetails;
      return this;
   }

   @Override
   public String toString() {
      return "DdsStatusMessage [tag=" + tag + ", encoding=" + encoding + ", dialog=" + dialog + ", recordCount="
            + recordCount + ", connectionDetails=" + connectionDetails + "]";
   }

   
}
