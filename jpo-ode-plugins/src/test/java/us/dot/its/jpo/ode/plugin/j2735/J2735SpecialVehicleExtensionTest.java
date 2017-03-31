package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mockit.Tested;

public class J2735SpecialVehicleExtensionTest {
   @Tested
   J2735SpecialVehicleExtensions sve;
   
   @Test
   public void testGettersAndSetters() {
      J2735TrailerData trailers = new J2735TrailerData();
      sve.setTrailers(trailers);
      assertEquals(trailers,sve.getTrailers());
      
      J2735EmergencyDetails vehicleAlerts = new J2735EmergencyDetails();
      sve.setVehicleAlerts(vehicleAlerts);
      assertEquals(vehicleAlerts,sve.getVehicleAlerts());
      
      J2735EventDescription description = new J2735EventDescription();
      sve.setDescription(description);
      assertEquals(description,sve.getDescription());
   }
}
