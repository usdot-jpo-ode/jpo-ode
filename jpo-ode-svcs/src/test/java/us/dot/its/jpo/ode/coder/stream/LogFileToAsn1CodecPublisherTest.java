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
package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class LogFileToAsn1CodecPublisherTest {

   @Tested
   LogFileToAsn1CodecPublisher testLogFileToAsn1CodecPublisher;

   @Injectable
   StringPublisher injectableStringPublisher;
   
   @Test
   public void testPublishEOF(@Mocked LogFileParser mockLogFileParser) throws Exception {
      new Expectations() {
         {
            LogFileParser.factory(anyString);
            result = mockLogFileParser;

            mockLogFileParser.parseFile((BufferedInputStream) any, anyString);
            result = ParserStatus.EOF;
         }
      };

      testLogFileToAsn1CodecPublisher.publish(new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
            "fileName", ImporterFileType.LEAR_LOG_FILE);

   }
   
   @Test
   public void testPublichBsmTxLogFile() throws Exception {
    
     byte[] buf = new byte[] { 
         (byte)0x00,                                     //1. direction 
         (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
         (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
         (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
         (byte)0x04, (byte)0x00,                         //2.3 speed
         (byte)0x09, (byte)0x27,                         //2.4 heading
         (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
         (byte)0x8f, (byte)0x01,                         //4. mSec
         (byte)0x00,                                     //5. securityResultCode
         (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                         //6.1 payload
         (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
         };

     BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

     String filename = RecordType.bsmTx.name();

//     new Expectations() {
//       {
//         LogFileParser logFileParser = LogFileParser.factory(filename); times = 1;
//         logFileParser.parseFile(bis, filename); times = 1;
//       }
//     };

     List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LEAR_LOG_FILE);
     
     for (OdeData data : dataList) {
       assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getOdeReceivedAt()), DateTimeUtils.nowZDT()) > 0);
       data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
       data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
       assertEquals("{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"bsmTx\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":null},\"encodings\":[{\"elementName\":\"root\",\"elementType\":\"Ieee1609Dot2Data\",\"encodingRule\":\"COER\"},{\"elementName\":\"unsecuredData\",\"elementType\":\"MessageFrame\",\"encodingRule\":\"UPER\"}],\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"038100400380\"}}}", data);
     }
   }

}
