package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;

public class LogFileToAsn1CodecPublisherTest {

   @Tested
   LogFileToAsn1CodecPublisher testLogFileToAsn1CodecPublisher;

   @Injectable
   StringPublisher injectableStringPublisher;

   @Capturing
   LogFileParser capturingLogFileParser;

   @Mocked
   TimLogFileParser mockTimLogFileParser;

   @Test
   public void testPublishEOF() throws Exception {
      new Expectations() {
         {
            LogFileParser.factory(anyString, anyLong);
            result = mockTimLogFileParser;

            mockTimLogFileParser.parseFile((BufferedInputStream) any, anyString);
            result = ParserStatus.EOF;
         }
      };

      testLogFileToAsn1CodecPublisher.publish(new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
            "fileName", ImporterFileType.OBU_LOG_FILE);

   }

}
