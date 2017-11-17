package us.dot.its.jpo.ode.services.asn1;

import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1DecodedDataRouterTest {

   @Tested
   Asn1DecodedDataRouter testAsn1DecodedDataRouter;

   @Injectable
   OdeProperties injectableOdeProperties;

   @SuppressWarnings("rawtypes")
   @Capturing
   MessageProducer capturingMessageProducer;

   @Capturing
   XmlUtils capturingXmlUtils;

   @Capturing
   OdeBsmDataCreatorHelper capturingOdeBsmDataCreatorHelper;

   @Mocked
   MessageProducer<String, OdeBsmData> mockBsmMessageProducer;
   @Mocked
   MessageProducer<String, String> mockTimMessageProducer;

   @Mocked
   JSONObject mockConsumed;
   @Mocked
   ConsumerRecord<String, String> mockConsumerRecord;

   @SuppressWarnings("unchecked")
   @Before
   public void createMockProducers() {

      new Expectations() {
         {
            new MessageProducer<String, OdeBsmData>(null, null, null, anyString, (Set<String>) any);
            result = mockBsmMessageProducer;

            MessageProducer.defaultStringMessageProducer(null, null, (Set<String>) any);
            result = mockTimMessageProducer;

         }
      };
   }

   @Test
   public void testProcessBsmLogDuringEvent() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.BasicSafetyMessage.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "bsmLogDuringEvent";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 2;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 0;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }

   @Test
   public void testProcessRxMsgBsm() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.BasicSafetyMessage.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "rxMsg";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 2;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 0;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }

   @Test
   public void testProcessTxMsgBsm() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.BasicSafetyMessage.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "bsmTx";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 2;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 0;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }

   @Test
   public void testProcessDnsTim() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.TravelerInformation.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "dnMsg";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 0;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 2;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }

   @Test
   public void testProcessRxTim() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.TravelerInformation.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "rxMsg";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 0;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 2;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }

   @Test
   public void testProcessUnsupportedTim() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.TravelerInformation.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "unsupported";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 0;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 1;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }

   @Test
   public void testProcessUnsupportedBsm() throws XmlUtilsException {
      new Expectations() {

         {
            XmlUtils.toJSONObject(anyString);
            result = mockConsumed;

            mockConsumed.getJSONObject(anyString);
            result = mockConsumed;

            // mock the messageID to be BSM
            mockConsumed.getInt(anyString);
            result = J2735DSRCmsgID.BasicSafetyMessage.getMsgID();

            // mock the record type to be bsmLogDuringEvent
            mockConsumed.getString(anyString);
            result = "unsupported";

            mockConsumerRecord.key();
            result = "testKey";

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 1;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 0;
         }
      };

      testAsn1DecodedDataRouter.setRecord(mockConsumerRecord);
      testAsn1DecodedDataRouter.process("testConsumedData");
   }
   

   @Test
   public void testProcessXmlException() throws XmlUtilsException {
      new Expectations() {

         @Mocked
         XmlUtilsException mockXmlUtilsException;

         {
            XmlUtils.toJSONObject(anyString);
            result = mockXmlUtilsException;

            mockBsmMessageProducer.send(null, anyString, (OdeBsmData) any);
            times = 0;

            mockTimMessageProducer.send(null, anyString, anyString);
            times = 0;
         }
      };

      testAsn1DecodedDataRouter.process("testConsumedData");
   }
}
