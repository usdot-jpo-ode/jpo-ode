package us.dot.its.jpo.ode.coder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class AbstractCoderTest {
   @Tested private BsmCoder bsmCoder ;
   @Injectable private static Logger logger;
   @Injectable private Asn1Plugin asn1Coder;
   @Injectable private SerializableMessageProducerPool<String, byte[]> messageProducerPool;
   @Mocked private MessageProducer<String, byte[]> producer;
   
   @Test
   public void testDecodeFromStreamAndPublish() throws Exception {
      final InputStream is = 
            new ByteArrayInputStream("is".getBytes());

      Asn1Object decoded = new J2735Bsm();
      new Expectations() {{
         asn1Coder.UPER_DecodeBsmStream(is); result = decoded; times = 1;
         asn1Coder.UPER_DecodeBsmStream(is); result = null; times = 1;
         bsmCoder.publish("topic", decoded);
      }};

      bsmCoder.decodeFromStreamAndPublish(is, "topic");

   }

}
