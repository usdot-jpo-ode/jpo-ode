package us.dot.its.jpo.ode.pdm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.snmp4j.ScopedPDU;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.pdm.PdmUtil;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleStatusRequest;

public class PdmUtilTest {

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
      ScopedPDU result = PdmUtil.createPDU(mockPdm);
      assertNotNull(result);
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<PdmUtil> constructor = PdmUtil.class.getDeclaredConstructor();
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
