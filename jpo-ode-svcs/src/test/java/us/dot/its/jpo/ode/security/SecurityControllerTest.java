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
