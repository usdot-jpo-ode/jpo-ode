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
package us.dot.its.jpo.ode.services.vsd;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmToVsdPackagerControllerTest {

   @Mocked
   OdeProperties mockOdeProperties;

   @Capturing
   BsmToVsdPackager capturingBsmToVsdPackager;

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Test
   public void shouldNotCreatePackagerWhenBsmDepositDisabled() {

      new Expectations() {

         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = false;

            new BsmToVsdPackager((MessageProducer) any, anyString);
            times = 0;

         }
      };
      new BsmToVsdPackagerController(mockOdeProperties);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Test
   public void shouldCreatePackagerWhenBsmDepositEnabled(@Capturing MessageProducer<?,?> capturingMessageProducer,
         @Capturing MessageConsumer<?, ?> capturingMessageConsumer) {
      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = true;

            new BsmToVsdPackager((MessageProducer) any, anyString);
            times = 1;

         }
      };
      new BsmToVsdPackagerController(mockOdeProperties);
   }

}
