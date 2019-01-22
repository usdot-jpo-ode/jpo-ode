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

public interface SystemConfigMBean {

   public void setThreadCount(int noOfThreads);

   public int getThreadCount();

   public void setSchemaName(String schemaName);

   public String getSchemaName();

   // any method starting with get and set are considered
   // as attributes getter and setter methods, so I am
   // using do* for operation.
   public String doConfig();
}
