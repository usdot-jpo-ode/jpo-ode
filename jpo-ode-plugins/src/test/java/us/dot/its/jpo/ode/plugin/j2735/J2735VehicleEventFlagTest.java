package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class J2735VehicleEventFlagTest {

   @Test
   public void checkLightsFlag() {
      assertNotNull(J2735VehicleEventFlag.EVENTHAZARDLIGHTS);
   }

   @Test
   public void checkStopLine() {
      assertNotNull(J2735VehicleEventFlag.EVENTSTOPLINEVIOLATION);
   }

   @Test
   public void checkABSActivated() {
      assertNotNull(J2735VehicleEventFlag.EVENTABSACTIVATED);
   }

   @Test
   public void checkTractionControlLoss() {
      assertNotNull(J2735VehicleEventFlag.EVENTTRACTIONCONTROLLOSS);
   }

   @Test
   public void checkStabilityControl() {
      assertNotNull(J2735VehicleEventFlag.EVENTSTABILITYCONTROLACTIVATED);
   }

   @Test
   public void checkHazardous() {
      assertNotNull(J2735VehicleEventFlag.EVENTHAZARDOUSMATERIALS);
   }

   @Test
   public void checkReserved() {
      assertNotNull(J2735VehicleEventFlag.EVENTRESERVED1);
   }

   @Test
   public void checkBreaking() {
      assertNotNull(J2735VehicleEventFlag.EVENTHARDBREAKING);
   }

   @Test
   public void checkLightsChanged() {
      assertNotNull(J2735VehicleEventFlag.EVENTLIGHTSCHANGED);
   }

   @Test
   public void checkWipers() {
      assertNotNull(J2735VehicleEventFlag.EVENTWIPERSCHANGED);
   }

   @Test
   public void checkEventFlatTire() {
      assertNotNull(J2735VehicleEventFlag.EVENTFLATTIRE);
   }

   @Test
   public void checkDisabledVehicle() {
      assertNotNull(J2735VehicleEventFlag.EVENTDISABLEDVEHICLE);
   }

   @Test
   public void checkAirbag() {
      assertNotNull(J2735VehicleEventFlag.EVENTAIRBAGDEPLOYMENT);
   }
}
