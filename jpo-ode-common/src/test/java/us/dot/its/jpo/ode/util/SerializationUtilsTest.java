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
package us.dot.its.jpo.ode.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class SerializationUtilsTest {
   
   @Test
   public void test() {
      TestData td = new TestData(1, "string", BigDecimal.valueOf(2.3));
      
      SerializationUtils<TestData> serializer = new SerializationUtils<TestData>();
      
      byte[] actual = serializer.serialize(td);
      
      assertEquals(td, serializer.deserialize(actual));
   }

}
