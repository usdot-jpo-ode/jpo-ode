package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
public class Asn1CommandManagerTest {
   
   @Tested
   Asn1CommandManager testAsn1CommandManager;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   
   @Capturing
   DdsDepositor capturingDdsDepositor;
   @Capturing
   SnmpSession capturingSnmpSession;
   
   @Injectable
   OdeTravelerInputData injectableOdeTravelerInputData;

   @Test
   public void testPackageSignedTimIntoAsd() {
      testAsn1CommandManager.packageSignedTimIntoAsd(injectableOdeTravelerInputData, "message");
   }
   
   @Test
   public void testDepositToDDS() throws DdsRequestManagerException {
      new Expectations() {{
         capturingDdsDepositor.deposit(anyString);
         times = 1;
      }};
      testAsn1CommandManager.depositToDDS("message");
   }
   
   @Test
   public void testDepositToDDSException() throws DdsRequestManagerException {
      new Expectations() {{
         capturingDdsDepositor.deposit(anyString);
         result = new DdsRequestManagerException(null);
      }};
      testAsn1CommandManager.depositToDDS("message");
   }
   
   @Test
   public void testSendToRsus(@Mocked OdeTravelerInputData mockOdeTravelerInputData) throws DdsRequestManagerException, IOException, TimPduCreatorException {
      new Expectations() {{
         mockOdeTravelerInputData.getRsus();
         result = new RSU[]{new RSU()};
         
         SnmpSession.createAndSend(null, null, anyInt, anyString, anyInt);
         times = 1;
      }};
      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData, "message");
   }
   
   @Test
   public void testSendToRsusSnmpException(@Mocked OdeTravelerInputData mockOdeTravelerInputData) throws DdsRequestManagerException, IOException, TimPduCreatorException {
      new Expectations() {{
         mockOdeTravelerInputData.getRsus();
         result = new RSU[]{new RSU()};
         
         SnmpSession.createAndSend(null, null, anyInt, anyString, anyInt);
         result = new IOException();
      }};
      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData, "message");
   }

}
