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
package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.udp.UdpUtil;

public class UdpUtilTest {

   @Test
   public void testPrivateConstructor() {

      Constructor<UdpUtil> constructor = null;
      try {
         constructor = UdpUtil.class.getDeclaredConstructor();
      } catch (NoSuchMethodException | SecurityException e) {
         fail("Unexpected exception: " + e);
      }

      if (null == constructor) {
         fail("Test failed to instantiate constructor.");
      }

      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);

      try {
         constructor.newInstance();
         fail("Expected " + UdpUtil.UdpUtilException.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | UnsupportedOperationException | InvocationTargetException e) {
         assertTrue("Incorrect exception thrown: " + e.getCause(),
               e.getCause() instanceof UnsupportedOperationException);
         assertEquals("Incorrect exception message returned", "Cannot instantiate static class.",
               e.getCause().getMessage());
      }
   }

}
