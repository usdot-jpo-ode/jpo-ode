/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.services.asn1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1EncodedDataRouterTest {
   
   @Tested
   Asn1EncodedDataRouter testAsn1EncodedDataRouter;
   
   @Injectable
   OdeProperties injectableOdeProperties;
   
//   @Capturing
//   MessageProducer<?,?> capturingMessageProducer;
//   
//   @Capturing
//   Asn1CommandManager capturingAsn1CommandManager;
//   
//   @Capturing
//   XmlUtils capturingXmlUtils;
   
   @Test @Ignore
   public void testNoRequest(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      new Expectations() {{
         XmlUtils.toJSONObject(anyString);
         result = mockJSONObject;
         
         mockJSONObject.has("request");
         result = false;
      }};
      testAsn1EncodedDataRouter.process("stringthing");
   }

   @Test @Ignore
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
   
   @Test @Ignore
   public void testSingleRsu(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      try {
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
	} catch (XmlUtilsException e) {
		
		e.printStackTrace();
	} catch (JSONException e) {
		
		e.printStackTrace();
	}
      testAsn1EncodedDataRouter.process("stringthing");
   }
   
   @Test @Ignore
   public void testRsuArray(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      try {
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
	} catch (XmlUtilsException e) {
		
		e.printStackTrace();
	} catch (JSONException e) {
		
		e.printStackTrace();
	}
      testAsn1EncodedDataRouter.process("stringthing");
   }
   
   @Test @Ignore
   public void testWithASD(@Mocked JSONObject mockJSONObject) throws XmlUtilsException {
      try {
		new Expectations() {{
		     XmlUtils.toJSONObject(anyString);
		     result = mockJSONObject;
		     
		     mockJSONObject.getJSONObject(AppContext.METADATA_STRING);
		     result = mockJSONObject;
		     
		     mockJSONObject.has(TimTransmogrifier.REQUEST_STRING);
		     result = true;

		     mockJSONObject.getJSONObject(TimTransmogrifier.REQUEST_STRING);
		     result = mockJSONObject;
		     
		     mockJSONObject.has(TimTransmogrifier.RSUS_STRING);
		     result = true;
		     times = 2;
		     
		     mockJSONObject.get(TimTransmogrifier.RSUS_STRING);
		     result = mockJSONObject;
		     times = 2;
		     
		     mockJSONObject.has(Asn1CommandManager.ADVISORY_SITUATION_DATA_STRING);
		     result = true;
		  }};
	} catch (XmlUtilsException e) {
		
		e.printStackTrace();
	} catch (JSONException e) {
		
		e.printStackTrace();
	}
      testAsn1EncodedDataRouter.process("stringthing");
   }

}
