/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
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
