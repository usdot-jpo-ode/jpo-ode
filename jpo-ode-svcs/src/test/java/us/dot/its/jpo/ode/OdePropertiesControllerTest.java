package us.dot.its.jpo.ode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OdePropertiesControllerTest {

   @Mock
   OdeProperties injectableOdeProperties;

   @InjectMocks
   OdePropertiesController testOdePropertiesController;

   @Test
   public void shouldReturnVersionFromOdeProperties1() {
      when(injectableOdeProperties.getVersion()).thenReturn("5");

      assertEquals("{\"version\":\"5\"}", testOdePropertiesController.getVersion().getBody());
   }

   @Test
   public void shouldReturnVersionFromOdeProperties2() {
      when(injectableOdeProperties.getVersion()).thenReturn("testStringNotARealVersion");

      assertEquals("{\"version\":\"testStringNotARealVersion\"}", testOdePropertiesController.getVersion().getBody());
   }

}
