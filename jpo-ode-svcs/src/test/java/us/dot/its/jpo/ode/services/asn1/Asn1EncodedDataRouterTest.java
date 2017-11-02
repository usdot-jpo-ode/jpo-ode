package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;

public class Asn1EncodedDataRouterTest {

   @Tested
   Asn1EncodedDataRouter testAsn1EncodedDataRouter;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Mocked
   ResponseEvent mockResponseEvent;
   @Mocked
   PDU mockResponsePDU;

   // capture the snmp objects to prevent actual session creation
   @Capturing
   SnmpSession capturingSnmpSession;

   String testXml = "<?xml version=\"1.0\"?><OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsdPayload</payloadType><serialId><streamId>4af0abea-6743-426e-9eab-894b6832e128</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2017-10-31T19:32:40.073Z[UTC]</odeReceivedAt><schemaVersion>3</schemaVersion><validSignature>false</validSignature><sanitized>false</sanitized><request><ode><version>2</version><index>3</index></ode><sdw><serviceRegion><nwCorner><latitude>45.035685245316394</latitude><longitude>-110.95195770263672</longitude></nwCorner><seCorner><latitude>40.96538194577477</latitude><longitude>-104.15382385253906</longitude></seCorner></serviceRegion><ttl>oneday</ttl></sdw><rsus><rsuTarget>192.168.0.145</rsuTarget><rsuUsername>v3user</rsuUsername><rsuPassword>password</rsuPassword><rsuRetries>3</rsuRetries><rsuTimeout>5000</rsuTimeout></rsus><rsus><rsuTarget>192.168.1.99</rsuTarget><rsuUsername>v3user</rsuUsername><rsuPassword>password</rsuPassword><rsuRetries>3</rsuRetries><rsuTimeout>5000</rsuTimeout></rsus><snmp><rsuid>00000083</rsuid><msgid>31</msgid><mode>1</mode><channel>178</channel><interval>2</interval><deliverystart>2017-06-01T17:47:11-05:00</deliverystart><deliverystop>2018-01-01T17:47:11-05:15</deliverystop><enable>1</enable><status>4</status></snmp></request><encodings><encodings><elementName>MessageFrame</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings><encodings><elementName>AdvisorySituationData</elementName><elementType>AdvisorySituationData</elementType><encodingRule>UPER</encodingRule></encodings></encodings></metadata><payload><dataType>us.dot.its.jpo.ode.model.OdeHexByteArray</dataType><data><MessageFrame><bytes>001F80A8701008620000000000000000030F775D9B0309EA715B91396742FC5AB857FFF900003F0B35E10032A107F8AA9979F4D3BB3A0A9266C000000854E2B72272CE85F8B570AFF79BFFFC401062CE819FA9C56F32262CE7DD0C9C57105462CE7A9F99C57384062CE784339C57614662CE771A89C57826862CE766649C57B44662CE75EA49C57CE7662CE7611A9C57E12A62CE76A889C57F07862CE772649C580FA01080013073DDD766C0</bytes></MessageFrame><AdvisorySituationData><bytes>4446A6F646596B937C04283E67BA1493F29309C1F76765A6A6A83CB5C9BE02107E160C6F7E210C6F158003F0150E02010C40000000000000000061EEEBB360613D4E2B72272CE85F8B570AFFFF200007E166BC20065420FF15532F3E9A776741524CD80000010A9C56E44E59D0BF16AE15FEF37FFF88020C59D033F538ADE644C59CFBA1938AE20A8C59CF53F338AE7080C59CF086738AEC28CC59CEE35138AF04D0C59CECCC938AF688CC59CEBD4938AF9CECC59CEC23538AFC254C59CED51138AFE0F0C59CEE4C938B01F4021000260E7BBAECD800</bytes></AdvisorySituationData></data></payload></OdeAsn1Data>";

   @Test
   public void testWithRealXmlTimeout1() throws IOException {
      new Expectations() {
         {
            new SnmpSession((RSU) any);

            capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
            result = null;
         }
      };
      testAsn1EncodedDataRouter.process(testXml);
   }

   @Test
   public void testWithRealXmlTimeout2() throws IOException {
      new Expectations() {
         {
            new SnmpSession((RSU) any);

            capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = null;
         }
      };

      testAsn1EncodedDataRouter.process(testXml);
   }

   @Test
   public void testWithRealXmlSuccess() throws IOException {
      new Expectations() {
         {
            new SnmpSession((RSU) any);

            capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockResponsePDU;

            mockResponsePDU.getErrorStatus();
            result = 0;
         }
      };

      testAsn1EncodedDataRouter.process(testXml);
   }

   @Test
   public void testWithRealXmlMessageAlreadyExists() throws IOException {
      new Expectations() {
         {
            new SnmpSession((RSU) any);

            capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockResponsePDU;

            mockResponsePDU.getErrorStatus();
            result = 5;
         }
      };

      testAsn1EncodedDataRouter.process(testXml);
   }

   @Test
   public void testWithRealXmlMessageMiscError() throws IOException {
      new Expectations() {
         {
            new SnmpSession((RSU) any);

            capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
            result = mockResponseEvent;

            mockResponseEvent.getResponse();
            result = mockResponsePDU;

            mockResponsePDU.getErrorStatus();
            result = 2;

            mockResponsePDU.getErrorStatusText();
            result = null;
         }
      };

      testAsn1EncodedDataRouter.process(testXml);
   }

   @Test
   public void testWithRealXmlSnmpException() throws IOException {
      new Expectations() {
         {
            new SnmpSession((RSU) any);

            capturingSnmpSession.set((PDU) any, (Snmp) any, (UserTarget) any, anyBoolean);
            result = new IOException("testException123");
         }
      };

      testAsn1EncodedDataRouter.process(testXml);
   }

}
