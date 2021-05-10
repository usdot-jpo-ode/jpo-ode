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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

//import mockit.integration.junit4.JMockit;

//@RunWith(JMockit.class)
public class SystemConfigTest {

    @Test
    public void testDoConfig() {

        SystemConfig testSystemConfig = new SystemConfig(14, "testSchemaName");

        testSystemConfig.doConfig();
    }

    @Test
    public void testSettersAndGetters() {

        String testSchemaName = "testSchemaName12356";
        int testThreadCount = 5;

        SystemConfig testSystemConfig = new SystemConfig(123, "originalSchemaName");

        testSystemConfig.setSchemaName(testSchemaName);
        testSystemConfig.setThreadCount(testThreadCount);

        assertEquals("Incorrect schemaName", testSchemaName, testSystemConfig.getSchemaName());
        assertEquals("Incorrect threadCount", testThreadCount, testSystemConfig.getThreadCount());
    }

}
