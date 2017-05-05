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
   RSU mockSnmpProperties;
   @Mocked
   J2735ProbeDataManagment mockPdmParameters;
   @Mocked
   ScopedPDU mockPdu;

   @Test
   public void createAndSendShouldThrowFailedToCreateSnmpSession(@Mocked final SnmpSession mockSnmpSession) {

      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      try {
         PdmController.createAndSend(mockPdu, mockSnmpProperties);
         fail("Should have thrown IOException");
      } catch (IOException e) {
      }
   }

   @Test
   public void createAndSendShouldReturnNullWhenSetThrowsException(@Mocked final SnmpSession mockSnmpSession)
         throws IOException {

      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);

               mockSnmpSession.set((PDU) any, (Snmp) any, (TransportMapping) any, (UserTarget) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      assertNull(PdmController.createAndSend(mockPdu, mockSnmpProperties));
   }

   @Test
   public void testCreateAndSendShould(@Mocked final SnmpSession mockSnmpSession) throws IOException {

      try {
         new Expectations() {
            {
               new SnmpSession((RSU) any);

               mockSnmpSession.set((PDU) any, (Snmp) any, (TransportMapping) any, (UserTarget) any);
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      assertEquals(ResponseEvent.class, PdmController.createAndSend(mockPdu, mockSnmpProperties).getClass());
   }

   @Test
   public void createPDUshouldNotReturnNUll(@Mocked J2735VehicleStatusRequest vehicleStatusRequest) {
      J2735VehicleStatusRequest[] vehicleStatusRequestList = { vehicleStatusRequest };
      new Expectations() {
         {
            mockPdmParameters.getVehicleStatusRequestList();
            result = vehicleStatusRequestList;
         }
      };
      ScopedPDU result = PdmManagerService.createPDU(mockPdmParameters);
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
