package us.dot.its.jpo.ode.services.asn1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.traveler.TimDepositController;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1EncodedDataRouterTest {
   
   @Tested
   Asn1EncodedDataRouter testAsn1EncodedDataRouter;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   
   @Capturing
   MessageProducer<?,?> capturingMessageProducer;
   
   @Capturing
   Asn1CommandManager capturingAsn1CommandManager;
   
   @Capturing
   XmlUtils capturingXmlUtils;
   
   @Test
   public void testNoRequest(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      new Expectations() {{
         XmlUtils.toJSONObject(anyString);
         result = mockJSONObject;
         
         mockJSONObject.has("request");
         result = false;
      }};
      testAsn1EncodedDataRouter.process("stringthing");
   }

   @Test
   public void testNoRsus(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      new Expectations() {{
         XmlUtils.toJSONObject(anyString);
         result = mockJSONObject;
         
         mockJSONObject.has("request");
         result = true;
         
         mockJSONObject.has("rsus");
         result = false;
      }};
      testAsn1EncodedDataRouter.process("stringthing");
   }
   
   @Test
   public void testSingleRsu(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      new Expectations() {{
         XmlUtils.toJSONObject(anyString);
         result = mockJSONObject;
         
         mockJSONObject.has("request");
         result = true;
         
         mockJSONObject.has("rsus");
         result = true;
         
         mockJSONObject.get("rsus");
         //result = new JSONObject();
      }};
      testAsn1EncodedDataRouter.process("stringthing");
   }
   
   @Test
   public void testRsuArray(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      new Expectations() {{
         XmlUtils.toJSONObject(anyString);
         result = mockJSONObject;
         
         mockJSONObject.has("request");
         result = true;
         
         mockJSONObject.has("rsus");
         result = true;
         
         mockJSONObject.get("rsus");
         result = new JSONArray();
      }};
      testAsn1EncodedDataRouter.process("stringthing");
   }
   
   @Test
   public void testWithASD(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      new Expectations() {{
         XmlUtils.toJSONObject(anyString);
         result = mockJSONObject;
         
         mockJSONObject.getJSONObject(AppContext.METADATA_STRING);
         result = mockJSONObject;
         
         mockJSONObject.has(TimDepositController.REQUEST_STRING);
         result = true;

         mockJSONObject.getJSONObject(TimDepositController.REQUEST_STRING);
         result = mockJSONObject;
         
         mockJSONObject.has(TimDepositController.RSUS_STRING);
         result = true;
         times = 2;
         
         mockJSONObject.get(TimDepositController.RSUS_STRING);
         result = mockJSONObject;
         times = 2;
         
         mockJSONObject.has(Asn1CommandManager.ADVISORY_SITUATION_DATA_STRING);
         result = true;
      }};
      testAsn1EncodedDataRouter.process("stringthing");
   }

}
