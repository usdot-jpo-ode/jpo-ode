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
package us.dot.its.jpo.ode;

public class SystemConfig implements SystemConfigMBean {

   private int threadCount;
   private String schemaName;

   public SystemConfig(int numThreads, String schema) {
      this.threadCount = numThreads;
      this.schemaName = schema;
   }

   @Override
   public void setThreadCount(int noOfThreads) {
      this.threadCount = noOfThreads;
   }

   @Override
   public int getThreadCount() {
      return this.threadCount;
   }

   @Override
   public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
   }

   @Override
   public String getSchemaName() {
      return this.schemaName;
   }

   @Override
   public String doConfig() {
      return "No of Threads=" + this.threadCount + " and DB Schema Name=" + this.schemaName;
   }
}
