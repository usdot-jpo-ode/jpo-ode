package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;


import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.wrapper.MessageProducer;


@Ignore
public class StreamDecoderPublisherTest {

   @Tested
   AbstractStreamDecoderPublisher testAbstractStreamDecoderPublisher;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Injectable
   String injectableValueSerializerFQN = "testValueSerializerFQN";

   @SuppressWarnings("rawtypes")
   @Capturing
   MessageProducer capturingMessageProducer;

   @Capturing
   PluginFactory capturingPluginFactory;
   
   @Capturing
   Scanner capturingScanner;

   @Test(timeout=4000)
   public void testDecodeHexAndPublishEmptyScanner(@Mocked OssJ2735Coder mockOssJ2735Coder, @Mocked Scanner mockScanner) throws Exception {
      new Expectations() {{
         new Scanner((InputStream) any);
         result = mockScanner;
         mockScanner.hasNextLine();
         result = false;
      }};
      try {
         testAbstractStreamDecoderPublisher.decodeHexAndPublish(new ByteArrayInputStream(new byte[] { 1 }));
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
