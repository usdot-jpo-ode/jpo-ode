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
package us.dot.its.jpo.ode.wrapper;

import static org.junit.Assert.fail;

import javax.net.ssl.SSLContext;

import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.junit.Test;
import org.junit.runner.RunWith;

import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpException;

@RunWith(JMockit.class)
public class HttpClientFactoryTest {

   @Mocked
   SSLContext sslContext;
   
   @Mocked
   SSLConnectionSocketFactory factory;
   
   @Test
   public void testBuild() {
      try {
         HttpClientFactory.build(sslContext);
         new Verifications() {{
            new SSLConnectionSocketFactory(withAny(sslContext)); times=1;
         }};
      } catch (HttpException e) {
         fail(e.toString());
      }
   }

   @Test
   public void testCreateHttpClient() {
      try {
         
         HttpClientFactory factory = HttpClientFactory.build(sslContext);
         factory.createHttpClient();

         new Verifications() {{
            new SSLConnectionSocketFactory(withAny(sslContext)); times=1;
         }};
      } catch (Exception e) {
         fail(e.toString());
      }
   }

}
