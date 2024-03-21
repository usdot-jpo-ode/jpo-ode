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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

public class LogFileToAsn1CodecPublisherTest {

   private static final String GZ = ".gz";

   @Tested
   LogFileToAsn1CodecPublisher testLogFileToAsn1CodecPublisher;

   @Injectable
   StringPublisher injectableStringPublisher;

   @BeforeAll
   public static void setupClass() {
      OdeMsgMetadata.setStaticSchemaVersion(OdeProperties.OUTPUT_SCHEMA_VERSION);
   }

   @Test
   public void testPublishInit(@Mocked LogFileParser mockLogFileParser) throws Exception {
      new Expectations() {
         {
            LogFileParser.factory(anyString);
            result = mockLogFileParser;

            mockLogFileParser.parseFile((BufferedInputStream) any, anyString);
            result = ParserStatus.INIT;
         }
      };

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(
            new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
            "fileName", ImporterFileType.LOG_FILE);

      assertTrue(dataList.isEmpty());
   }

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

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(
            new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
            "fileName", ImporterFileType.LOG_FILE);

      assertTrue(dataList.isEmpty());
   }

   @Test
   public void testPublishThrowsIllegalArgumentException() throws Exception {
      // If the filename does not follow expected filename pattern,
      // IllegalArgumentException should be thrown
      assertThrows(IllegalArgumentException.class, () -> {
         // If the filename does not follow expected filename pattern,
         // IllegalArgumentException should be thrown
         testLogFileToAsn1CodecPublisher.publish(new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
               "fileName", ImporterFileType.LOG_FILE);
         fail("Expected an IllegalArgumentException to be thrown");
      });
   }

   @Test
   public void testPublishThrowsLogFileToAsn1CodecPublisherException(@Mocked LogFileParser mockLogFileParser)
         throws Exception {
         assertThrows(LogFileToAsn1CodecPublisherException.class, () -> {
            new Expectations() {
               {
                  LogFileParser.factory(anyString);
                  result = mockLogFileParser;
   
                  /*
                  * If the embedded parser fails to parse a log file header, it may throw an
                  * exception
                  * which is then caught by the parser and re-thrown as
                  * LogFileToAsn1CodecPublisherException.
                  * This mocked object will simulate that eventuality.
                  */
                  mockLogFileParser.parseFile((BufferedInputStream) any, anyString);
                  result = new LogFileToAsn1CodecPublisherException(anyString, (Exception) any);
               }
            };
   
            testLogFileToAsn1CodecPublisher.publish(new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
                  "fileName", ImporterFileType.LOG_FILE);
            fail("Expected an LogFileToAsn1CodecPublisherException to be thrown");
         });
   }

   @Test
   public void testPublishDecodeFailure(@Mocked LogFileParser mockLogFileParser) throws Exception {
      new Expectations() {
         {
            LogFileParser.factory(anyString);
            result = mockLogFileParser;

            mockLogFileParser.parseFile((BufferedInputStream) any, anyString);
            result = ParserStatus.ERROR;
         }
      };

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(
            new BufferedInputStream(new ByteArrayInputStream(new byte[0])),
            "fileName", ImporterFileType.LOG_FILE);

      assertTrue(dataList.isEmpty());
   }

   @Test
   public void testPublishBsmTxLogFile() throws Exception {

      byte[] buf = new byte[] {
            (byte) 0x00, // 1. direction
            (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 2.0 latitude
            (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 2.1 longitude
            (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 2.3 elevation
            (byte) 0x04, (byte) 0x00, // 2.3 speed
            (byte) 0x09, (byte) 0x27, // 2.4 heading
            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 3. utcTimeInSec
            (byte) 0x8f, (byte) 0x01, // 4. mSec
            (byte) 0x00, // 5. securityResultCode
            (byte) 0x06, (byte) 0x00, // 6.0 payloadLength
                                      // 6.1 payload
            (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80
      };

      String filename = RecordType.bsmTx.name() + GZ;

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE);

      for (OdeData data : dataList) {
         assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
               DateTimeUtils.nowZDT()) > 0);
         data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
         data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
         var expected = "{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"bsmTx.gz\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"00140380\"}}}";
         assertEquals(expected, data.toJson());
      }
   }

   @Test
   public void testPublishDistressNotificationLogFile() throws Exception {

      byte[] buf = new byte[] {
            (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 1.1 latitude
            (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 1.2 longitude
            (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 1.3 elevation
            (byte) 0x04, (byte) 0x00, // 1.4 speed
            (byte) 0x09, (byte) 0x27, // 1.5 heading
            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 2. utcTimeInSec
            (byte) 0x8f, (byte) 0x01, // 3. mSec
            (byte) 0x00, // 4. securityResultCode
            (byte) 0x06, (byte) 0x00, // 5.1 payloadLength
                                      // 5.2 payload
            (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
      };

      String filename = RecordType.dnMsg.name() + GZ;

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE);

      for (OdeData data : dataList) {
         assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
               DateTimeUtils.nowZDT()) > 0);
         data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
         data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
         var expected = "{\"metadata\":{\"logFileName\":\"dnMsg.gz\",\"recordType\":\"dnMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"NA\"},\"encodings\":[{\"elementName\":\"root\",\"elementType\":\"Ieee1609Dot2Data\",\"encodingRule\":\"COER\"},{\"elementName\":\"unsecuredData\",\"elementType\":\"MessageFrame\",\"encodingRule\":\"UPER\"}],\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"038100400380\"}}}";
      }
   }

   @Test
   public void testPublishDriverAlertLogFile() throws Exception {

      byte[] buf = new byte[] {
            (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 1.0 latitude
            (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 1.1 longitude
            (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 1.2 elevation
            (byte) 0x04, (byte) 0x00, // 1.3 speed
            (byte) 0x09, (byte) 0x27, // 1.4 heading

            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 2. utcTimeInSec
            (byte) 0x8f, (byte) 0x01, // 3. mSec
            (byte) 0x11, (byte) 0x00, // 4.0 payloadLength
                                      // 4.1 payload
            'T', 'e', 's', 't', ' ', 'D', 'r', 'i', 'v', 'e', 'r', ' ', 'A', 'l', 'e', 'r', 't'
      };

      String filename = RecordType.driverAlert.name() + GZ;

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE);

      for (OdeData data : dataList) {
         assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
               DateTimeUtils.nowZDT()) > 0);
         data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
         data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
         var expected = "{\"metadata\":{\"logFileName\":\"driverAlert.gz\",\"recordType\":\"driverAlert\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeDriverAlertPayload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"alert\":\"Test Driver Alert\"}}";
         assertEquals(expected, data.toJson());
      }
   }

   @Test
   public void testPublishRxMsgTIMLogFile() throws Exception {

      byte[] buf = new byte[] {
            (byte) 0x01, // 1. RxSource = SAT
            (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 2.0 latitude
            (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 2.1 longitude
            (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 2.3 elevation
            (byte) 0x04, (byte) 0x00, // 2.3 speed
            (byte) 0x09, (byte) 0x27, // 2.4 heading
            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 2. utcTimeInSec
            (byte) 0x8f, (byte) 0x01, // 4. mSec
            (byte) 0x00, // 5. securityResultCode
            (byte) 0x06, (byte) 0x00, // 6.0 payloadLength
                                      // 6.1 payload
            (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80
      };

      String filename = RecordType.rxMsg.name() + GZ;

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE);

      for (OdeData data : dataList) {
         assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
               DateTimeUtils.nowZDT()) > 0);
         data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
         data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
         var expected = "{\"metadata\":{\"logFileName\":\"rxMsg.gz\",\"recordType\":\"rxMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"SAT\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"TMC_VIA_SAT\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"00140380\"}}}";
         assertEquals(expected, data.toJson());
      }
   }

   @Test
   public void testPublishRxMsgBSMLogFile() throws Exception {

      byte[] buf = new byte[] {
            (byte) 0x02, // 1. RxSource = RV
            (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 2.0 latitude
            (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 2.1 longitude
            (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 2.3 elevation
            (byte) 0x04, (byte) 0x00, // 2.3 speed
            (byte) 0x09, (byte) 0x27, // 2.4 heading
            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 3. utcTimeInSec
            (byte) 0x8f, (byte) 0x01, // 4. mSec
            (byte) 0x00, // 5. securityResultCode
            (byte) 0x06, (byte) 0x00, // 6.0 payloadLength
                                      // 6.1 payload
            (byte) 0x11, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80
      };

      String filename = RecordType.rxMsg.name() + GZ;

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE);

      for (OdeData data : dataList) {
         assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
               DateTimeUtils.nowZDT()) > 0);
         data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
         data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
         var expected = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"rxMsg.gz\",\"recordType\":\"rxMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"00140380\"}}}";
         assertEquals(expected, data.toJson());
      }
   }

   @Test
   public void testPublishNonLearLogFile() throws Exception {

      String filename = RecordType.rxMsg.name() + GZ;

      String jsonData = "{\"fakeJsonKey\":\"fakeJsonValue\"";
      byte[] buf = jsonData.getBytes();
      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      /*
       * This call to publish method does not actually try to parse the data. It
       * short-circuits the parsing because
       * currently we dont' support JSON input records. We may in the future.
       */

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.JSON_FILE);

      assertTrue(dataList.isEmpty());
   }

   @Test
   public void testPublishRxMsgBSMLogFileNewLine() throws Exception {

      byte[] buf = new byte[] {
            (byte) 0x02, // 1. RxSource = RV
            (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 2.0 latitude
            (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 2.1 longitude
            (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 2.3 elevation
            (byte) 0x04, (byte) 0x00, // 2.3 speed
            (byte) 0x09, (byte) 0x27, // 2.4 heading
            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 3. utcTimeInSec
            (byte) 0x8f, (byte) 0x01, // 4. mSec
            (byte) 0x00, // 5. securityResultCode
            (byte) 0x06, (byte) 0x00, // 6.0 payloadLength
                                      // 6.1 payload
            (byte) 0x11, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80,
            (byte) 0x0a
      };

      String filename = RecordType.rxMsg.name() + GZ;

      BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

      List<OdeData> dataList = testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE);

      for (OdeData data : dataList) {
         assertTrue(DateTimeUtils.difference(DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
               DateTimeUtils.nowZDT()) > 0);
         data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
         data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
         var expected = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"rxMsg.gz\",\"recordType\":\"rxMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"00140380\"}}}";
         assertEquals(expected, data.toJson());
      }
   }

   @Test
   public void testDetermineMessageType() throws JsonUtilsException {
      String mapHexString = "0012839338023000205E96094D40DF4C2CA626C8516E02DC3C2010640000000289E01C009F603F42E88039900000000A41107B027D80FD0A4200C6400000002973021C09F603DE0C16029200000080002A8A008D027D98FEE805404FB0E1085F60588200028096021200000080002AA0007D027D98FE9802E04FB1200C214456228000A02B1240005022C03240000020000D56B40BC04FB35FF655E2C09F623FB81C835FEC0DB240A0A2BFF4AEBF82C660000804B0089000000800025670034013ECD7FB9578E027D9AFF883C4E050515FFA567A41635000040258024800000400012B8F81F409F663FAC094013ECD7FC83DDB02829AFFA480BC04FB02C6E0000804B09C5000000200035EA98A9604F60DA6C7C113D505C35FFE941D409F65C05034C050500C9880004409BC800000006D2BD3CEC813C40CDE062C1FD400000200008791EA3DB3CF380A009F666F05005813D80FFE0A0588C00040092106A00000000BC75CAC009F66DB54C04A813D80A100801241ED40000000078EBAE3B6DA7A008809E2050904008811F100000000BC72389009F60ECA8002049C400000002F1B2CA3027D93A71FA813EC204BC400000002F1B2B34027B0397608880CD10000000039B8E1A51036820505080D51000000003A7461ED1036760505080DD1000000003B2F62311006260505160BCA00000080002B785E2A80A0A6C028DE728145037F1F9E456488000202B2540001022C1894000001000057058C5B81414D806DBCD4028A18F4DF23A050502C8D0000404B05A5000000800035B6471BC05053602431F380A2864087BDB0141458064AB0D6C00053FC013EC0B0680006012C15940000020000D6C06C6581414D807FB972028A1901D78DC050536020EC1800A0A6C039D639813D80B0780006012C1494000002000096AB8C6581414D8062BE32028A1B01417E04050A360172D77009E2058440003009409C200000040006B3486A480A0A1CAB7134C8117DCC02879B018FAE2C050F3601CED54809E21012720000000067FBAD0007E7E84045C80000000100661580958004041C8000000019F3658401CDFA2C0D64000002000144016C02C36DDFFF0282984ACC1EE05052C36F0AC02828669D82DA8F821480A0A10F140002C8E0001004B03190000008000519FD190C43B2E0066108B08401428C342A0CE02828258A0604A6BE959AEE0E6050502C920001004B02D90000008000459FA164404FB30A8580A00A14619C306701414C32CE10E02829659081F814141029030164B0000802E8000802000035FDB1D84C09EC6C003BA14814140B0540003012C187400040080011B13F6EDB804F115FA6DFC10AFC94FC6A57EE07DCE2BFA7BED3B5FFCD72E80A1E018C900008000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
      OdeAsn1Payload mapPayload = new OdeAsn1Payload(HexUtils.fromHexString(mapHexString));
      assertEquals(testLogFileToAsn1CodecPublisher.determineMessageType(mapPayload), "MAP");

      String timHexString = "001F79201000000000012AA366D080729B8987D859717EE22001FFFE4FD0011589D828007E537130FB0B2E2FDC440001F46FFFF002B8B2E46E926E27CE6813D862CB90EDC9B89E11CE2CB8E98F9B89BCC4050518B2E365B66E26AE3B8B2E291A66E2591D8141462CB873969B89396C62CB86AFE9B89208E00000131560018300023E43A6A1351800023E4700EFC51881010100030180C620FB90CAAD3B9C5082080E1DDC905E10168E396921000325A0D73B83279C83010180034801090001260001808001838005008001F0408001828005008001304000041020407E800320409780050080012040000320409900018780032040958005000001E0408183E7139D7B70987019B526B8A950052F5C011D3C4B992143E885C71F95DA6071658082346CC03A50D66801F65288C30AB39673D0494536C559047E457AD291C99C20A7FB1244363E993EE3EE98C78742609340541DA01545A0F7339C26A527903576D30000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
      OdeAsn1Payload timPayload = new OdeAsn1Payload(HexUtils.fromHexString(timHexString));
      assertEquals(testLogFileToAsn1CodecPublisher.determineMessageType(timPayload), "TIM");
   }
}
