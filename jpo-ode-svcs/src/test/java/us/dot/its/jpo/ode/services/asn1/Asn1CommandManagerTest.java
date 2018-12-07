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
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.services.asn1.Asn1CommandManager.Asn1CommandManagerException;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;
public class Asn1CommandManagerTest {
   
   @Tested
   Asn1CommandManager testAsn1CommandManager;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   
   @Capturing
   DdsDepositor<?> capturingDdsDepositor;
   @Capturing
   SnmpSession capturingSnmpSession;
   
   @Injectable
   OdeTravelerInputData injectableOdeTravelerInputData;

   @Test
   public void testPackageSignedTimIntoAsd() {
      testAsn1CommandManager.packageSignedTimIntoAsd(injectableOdeTravelerInputData.getRequest(), "message");
   }
   
   @Test
   public void testDepositToDDS() throws DdsRequestManagerException, Asn1CommandManagerException {
      new Expectations() {{
         capturingDdsDepositor.deposit(anyString);
         times = 1;
      }};
      testAsn1CommandManager.depositToDDS("message");
   }
   
   @Test(expected = Asn1CommandManagerException.class)
   public void testDepositToDDSException() throws DdsRequestManagerException, Asn1CommandManagerException {
      new Expectations() {{
         capturingDdsDepositor.deposit(anyString);
         result = new Asn1CommandManagerException(anyString, (Exception) any);
      }};
      testAsn1CommandManager.depositToDDS("message");
   }
   
   @Test
   public void testSendToRsus(@Mocked OdeTravelerInputData mockOdeTravelerInputData) throws DdsRequestManagerException, IOException, TimPduCreatorException {
      new Expectations() {{
         mockOdeTravelerInputData.getRequest().getRsus();
         result = new RSU[]{new RSU()};
         
         SnmpSession.createAndSend(null, null, anyString, (RequestVerb) any);
         times = 1;
      }};
      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData.getRequest(), "message");
   }
   
   @Test
   public void testSendToRsusSnmpException(@Mocked OdeTravelerInputData mockOdeTravelerInputData) throws DdsRequestManagerException, IOException, TimPduCreatorException {
      new Expectations() {{
         mockOdeTravelerInputData.getRequest().getRsus();
         result = new RSU[]{new RSU()};
         
         SnmpSession.createAndSend(null, null, anyString, (RequestVerb) any);
         result = new IOException();
      }};
      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData.getRequest(), "message");
   }

}
