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

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

class TestData {
   private int i;
   private String s;
   private BigDecimal bd;
   
   public TestData() {
      super();
   }

   public TestData(int i, String s, BigDecimal bd) {
      super();
      this.i = i;
      this.s = s;
      this.bd = bd;
   }

   public int getI() {
      return i;
   }

   public void setI(int i) {
      this.i = i;
   }

   public String getS() {
      return s;
   }

   public void setS(String s) {
      this.s = s;
   }

   public BigDecimal getBd() {
      return bd;
   }

   public void setBd(BigDecimal bd) {
      this.bd = bd;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bd == null) ? 0 : bd.hashCode());
      result = prime * result + i;
      result = prime * result + ((s == null) ? 0 : s.hashCode());
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
      TestData other = (TestData) obj;
      if (bd == null) {
         if (other.bd != null)
            return false;
      } else if (!bd.equals(other.bd))
         return false;
      if (i != other.i)
         return false;
      if (s == null) {
         if (other.s != null)
            return false;
      } else if (!s.equals(other.s))
         return false;
      return true;
   }

}

public class SerializationUtilsTest {
   
   @Test
   public void test() {
      TestData td = new TestData(1, "string", BigDecimal.valueOf(2.3));
      
      SerializationUtils<TestData> serializer = new SerializationUtils<TestData>();
      
      byte[] actual = serializer.serialize(td);
      
      assertEquals(td, serializer.deserialize(actual));
   }

}
