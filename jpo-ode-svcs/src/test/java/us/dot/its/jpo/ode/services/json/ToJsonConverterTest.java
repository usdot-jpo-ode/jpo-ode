package us.dot.its.jpo.ode.services.json;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class ToJsonConverterTest {
   
   @Tested
   ToJsonConverter<String> testToJsonConverter;
   
   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   boolean mockVerbose;
   @Injectable
   String mockTopic;
   
   @Test
   public void shouldCalltoJson(@Mocked final MessageProducer<String, String> mockMessageProducer) {
      new Expectations() {{
         JsonUtils.toJson(any, anyBoolean);
      }};
      testToJsonConverter.transform(new String());
   }


}
