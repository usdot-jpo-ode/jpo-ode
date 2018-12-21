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
package us.dot.its.jpo.ode.security;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;

public class SecurityControllerTest {

   @Injectable
   OdeProperties injectableOdeProperties;
   @Capturing
   Executors capturingExecutors;
   @Mocked
   ExecutorService mockExecutorService;
   @Capturing
   CertificateLoader capturingCertificateLoader;

   @Test
   public void shouldLaunchCertificateLoader() {

      new Expectations() {
         {
            Executors.newSingleThreadExecutor();
            result = mockExecutorService;

            mockExecutorService.submit((CertificateLoader) any);

            new CertificateLoader((OdeProperties) any);
            times = 1;
         }
      };

      new SecurityController(injectableOdeProperties);
   }
}
