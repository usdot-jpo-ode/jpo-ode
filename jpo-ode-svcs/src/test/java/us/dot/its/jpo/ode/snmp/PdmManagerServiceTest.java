package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.pdm.PdmController;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleStatusRequest;

public class PdmManagerServiceTest {

   @Injectable
   RSU mockRSU;
   @Mocked
   J2735ProbeDataManagment mockPdm;
   @Mocked
   ScopedPDU mockScopedPDU;

   @Test
   public void createPDUshouldNotReturnNUll(@Mocked J2735VehicleStatusRequest vehicleStatusRequest) {
      J2735VehicleStatusRequest[] vehicleStatusRequestList = { vehicleStatusRequest };
      new Expectations() {
         {
            mockPdm.getVehicleStatusRequestList();
            result = vehicleStatusRequestList;
         }
      };
      ScopedPDU result = PdmManagerService.createPDU(mockPdm);
      assertNotNull(result);
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<PdmManagerService> constructor = PdmManagerService.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
         assertEquals(InvocationTargetException.class, e.getClass());
      }
   }

}
