/*
 * ============================================================================= Copyright 2018
 * 572682
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.coder.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType.bsmTx;
import static us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType.rxMsg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.FileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.DateTimeUtils;

@ExtendWith(MockitoExtension.class)
class LogFileToAsn1CodecPublisherTest {

  private static final String GZ = ".gz";
  private static final String SCHEMA_VERSION = "9";

  @Test
  void logImportSwitchesBetweenExternalRawTopicAndFfmDecode(
      @Mock JsonTopics jsonTopics, @Mock RawEncodedJsonTopics rawTopics,
      @Mock KafkaTemplate<String, String> template, @Mock FfmlibDecodeService decoder)
      throws Exception {
    org.mockito.Mockito.when(rawTopics.getBsm()).thenReturn("topic.OdeRawEncodedBSMJson");
    byte[] logEntry = new byte[] {
        0x00, 0x6f, 0x75, 0x4d, 0x19, (byte) 0xa4, (byte) 0xa1, 0x5c, (byte) 0xce,
        0x67, 0x06, 0x00, 0x00, 0x04, 0x00, 0x09, 0x27, (byte) 0xa9, 0x2c,
        (byte) 0xe2, 0x5a, (byte) 0x8f, 0x01, 0x00, 0x06, 0x00,
        0x03, (byte) 0x81, 0x00, 0x14, 0x03, (byte) 0x80};
    String filename = bsmTx.name() + GZ;
    Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
    var external = new LogFileToAsn1CodecPublisher(template, jsonTopics, rawTopics, mode,
        decoder);
    external.publish(new BufferedInputStream(new ByteArrayInputStream(logEntry)), filename,
        ImporterFileType.LOG_FILE, LogFileParserFactory.getLogFileParser(filename));
    verify(template).send(org.mockito.ArgumentMatchers.eq("topic.OdeRawEncodedBSMJson"),
        org.mockito.ArgumentMatchers.any(String.class));
    verify(decoder, never()).decode(org.mockito.ArgumentMatchers.any(OdeAsn1Data.class),
        org.mockito.ArgumentMatchers.any());

    org.mockito.Mockito.clearInvocations(template, decoder);
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    var ffm = new LogFileToAsn1CodecPublisher(template, jsonTopics, rawTopics, mode, decoder);
    ffm.publish(new BufferedInputStream(new ByteArrayInputStream(logEntry)), filename,
        ImporterFileType.LOG_FILE, LogFileParserFactory.getLogFileParser(filename));
    ArgumentCaptor<OdeAsn1Data> captured = ArgumentCaptor.forClass(OdeAsn1Data.class);
    verify(decoder).decode(captured.capture(), org.mockito.ArgumentMatchers.isNull());
    assertEquals(bsmTx, captured.getValue().getMetadata().getRecordType());
    verify(template, never()).send(org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.anyString());
  }

  @Test
  void testPublishInit(@Mock JsonTopics jsonTopics, @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    var fileName = bsmTx.name() + "thisIsAFile.txt";
    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(
            new BufferedInputStream(new ByteArrayInputStream(new byte[0])), fileName,
            ImporterFileType.LOG_FILE, LogFileParserFactory.getLogFileParser(fileName));

    assertTrue(dataList.isEmpty());
  }

  @Test
  void testPublishEmptyInputStreamDoesNotThrowException(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);

    var fileName = rxMsg.name() + "fileName";
    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(
            new BufferedInputStream(new ByteArrayInputStream(new byte[0])), fileName,
            ImporterFileType.LOG_FILE, LogFileParserFactory.getLogFileParser(fileName));

    assertTrue(dataList.isEmpty());
  }

  @Test
  void testPublishThrowsLogFileToAsn1CodecPublisherException(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    assertThrows(LogFileToAsn1CodecPublisherException.class, () -> {
      var mockLogFileParser = mock(LogFileParser.class);
      when(mockLogFileParser.parseFile(any()))
          .thenThrow(new FileParser.FileParserException("exception msg", null));

      testLogFileToAsn1CodecPublisher.publish(
          new BufferedInputStream(new ByteArrayInputStream(new byte[0])), "fileName",
          ImporterFileType.LOG_FILE, mockLogFileParser);
      fail("Expected an LogFileToAsn1CodecPublisherException to be thrown");
    });
  }

  @Test
  void testPublishDecodeFailure(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate, @Mock LogFileParser mockLogFileParser)
      throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);

    when(mockLogFileParser.parseFile(any())).thenReturn(FileParser.ParserStatus.ERROR);
    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(
            new BufferedInputStream(new ByteArrayInputStream(new byte[0])), "fileName",
            ImporterFileType.LOG_FILE, mockLogFileParser);

    assertTrue(dataList.isEmpty());
  }

  @Test
  void testPublishBsmTxLogFile(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    byte[] buf = new byte[] {(byte) 0x00, // 1. direction
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
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80};

    String filename = bsmTx.name() + GZ;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE,
            LogFileParserFactory.getLogFileParser(filename));

    var expectedStringToFormat = loadResourceAsString(
        "src/test/resources/us.dot.its.jpo.ode.coder/expectedBsmTxLogFileToPublish.json");
    for (OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> data : dataList) {
      assertTrue(DateTimeUtils.difference(
          DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
          DateTimeUtils.nowZDT()) > 0);
      data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
      data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
      String asn1String = data.getMetadata().getAsn1();
      var expected = String.format(expectedStringToFormat, SCHEMA_VERSION, asn1String, asn1String);
      assertEquals(expected, data.toJson());
    }
  }

  @Test
  void testPublishDistressNotificationLogFile(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    byte[] buf = new byte[] {(byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 1.1 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 1.2 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 1.3 elevation
        (byte) 0x04, (byte) 0x00, // 1.4 speed
        (byte) 0x09, (byte) 0x27, // 1.5 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 2. utcTimeInSec
        (byte) 0x8f, (byte) 0x01, // 3. mSec
        (byte) 0x00, // 4. securityResultCode
        (byte) 0x06, (byte) 0x00, // 5.1 payloadLength
        // 5.2 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80};

    String filename = RecordType.dnMsg.name() + GZ;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE,
            LogFileParserFactory.getLogFileParser(filename));

    var expectedStringToFormat = loadResourceAsString(
        "src/test/resources/us.dot.its.jpo.ode.coder/expectedDistressNotificationLogFileToPublish.json");
    for (OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> data : dataList) {
      assertTrue(DateTimeUtils.difference(
          DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
          DateTimeUtils.nowZDT()) > 0);
      data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
      data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
      var expected = String.format(expectedStringToFormat, SCHEMA_VERSION);
      assertEquals(expected, data.toJson());
    }
  }

  @Test
  void testPublishDriverAlertLogFile(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    byte[] buf = new byte[] {(byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, // 1.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, // 1.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, // 1.2 elevation
        (byte) 0x04, (byte) 0x00, // 1.3 speed
        (byte) 0x09, (byte) 0x27, // 1.4 heading

        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, // 2. utcTimeInSec
        (byte) 0x8f, (byte) 0x01, // 3. mSec
        (byte) 0x11, (byte) 0x00, // 4.0 payloadLength
        // 4.1 payload
        'T', 'e', 's', 't', ' ', 'D', 'r', 'i', 'v', 'e', 'r', ' ', 'A', 'l', 'e', 'r', 't'};

    String filename = RecordType.driverAlert.name() + GZ;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE,
            LogFileParserFactory.getLogFileParser(filename));

    var expectedStringToFormat = loadResourceAsString(
        "src/test/resources/us.dot.its.jpo.ode.coder/expectedDriverAlertLogFileToPublish.json");
    for (OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> data : dataList) {
      assertTrue(DateTimeUtils.difference(
          DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
          DateTimeUtils.nowZDT()) > 0);
      data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
      data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
      var expected = String.format(expectedStringToFormat, SCHEMA_VERSION);
      assertEquals(expected, data.toJson());
    }
  }

  @Test
  void testPublishRxMsgTIMLogFile(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    byte[] buf = new byte[] {(byte) 0x01, // 1. RxSource = SAT
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
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80};

    String filename = rxMsg.name() + GZ;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE,
            LogFileParserFactory.getLogFileParser(filename));

    var expectedStringToFormat = loadResourceAsString(
        "src/test/resources/us.dot.its.jpo.ode.coder/expectedRxMsgTIMLogFileToPublish.json");
    for (OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> data : dataList) {
      assertTrue(DateTimeUtils.difference(
          DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
          DateTimeUtils.nowZDT()) > 0);
      data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
      data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
      String asn1String = data.getMetadata().getAsn1();
      var expected = String.format(expectedStringToFormat, SCHEMA_VERSION, asn1String, asn1String);
      assertEquals(expected, data.toJson());
    }
  }

  @Test
  void testPublishRxMsgBSMLogFile(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    byte[] buf = new byte[] {(byte) 0x02, // 1. RxSource = RV
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
        (byte) 0x11, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80};

    String filename = rxMsg.name() + GZ;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE,
            LogFileParserFactory.getLogFileParser(filename));

    var expectedStringToFormat = loadResourceAsString(
        "src/test/resources/us.dot.its.jpo.ode.coder/expectedRxMsgBSMLogFileToPublish.json");
    for (OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> data : dataList) {
      assertTrue(DateTimeUtils.difference(
          DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
          DateTimeUtils.nowZDT()) > 0);
      data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
      data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
      String asn1String = data.getMetadata().getAsn1();
      var expected = String.format(expectedStringToFormat, SCHEMA_VERSION, asn1String, asn1String);
      assertEquals(expected, data.toJson());
    }
  }

  @Test
  void testPublishNonLearLogFile(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    String filename = rxMsg.name() + GZ;

    String jsonData = "{\"fakeJsonKey\":\"fakeJsonValue\"";
    byte[] buf = jsonData.getBytes();
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    /*
     * This call to publish method does not actually try to parse the data. It short-circuits the
     * parsing because currently we don't support JSON input records. We may in the future.
     */

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.UNKNOWN,
            LogFileParserFactory.getLogFileParser(filename));

    assertTrue(dataList.isEmpty());
  }

  @Test
  void testPublishRxMsgBSMLogFileNewLine(@Mock JsonTopics jsonTopics,
      @Mock RawEncodedJsonTopics rawEncodedJsonTopics,
      @Mock KafkaTemplate<String, String> kafkaTemplate) throws Exception {
    var testLogFileToAsn1CodecPublisher =
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics);
    byte[] buf = new byte[] {(byte) 0x02, // 1. RxSource = RV
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
        (byte) 0x11, (byte) 0x81, (byte) 0x00, (byte) 0x14, (byte) 0x03, (byte) 0x80, (byte) 0x0a};

    String filename = rxMsg.name() + GZ;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> dataList =
        testLogFileToAsn1CodecPublisher.publish(bis, filename, ImporterFileType.LOG_FILE,
            LogFileParserFactory.getLogFileParser(filename));
    var expectedStringToFormat = loadResourceAsString(
        "src/test/resources/us.dot.its.jpo.ode.coder/expectedRxMsgBSMNewLine.json");
    for (OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>> data : dataList) {
      assertTrue(DateTimeUtils.difference(
          DateTimeUtils.isoDateTime(data.getMetadata().getRecordGeneratedAt()),
          DateTimeUtils.nowZDT()) > 0);
      data.getMetadata().setOdeReceivedAt("2019-03-05T20:31:17.579Z");
      data.getMetadata().getSerialId().setStreamId("c7bbb42e-1e39-442d-98ac-62740ca50f92");
      String asn1String = data.getMetadata().getAsn1();
      var expected = String.format(expectedStringToFormat, SCHEMA_VERSION, asn1String, asn1String);
      assertEquals(expected, data.toJson());
    }
  }

  private static String loadResourceAsString(String resourceName) throws IOException {
    File file = new File(resourceName);
    byte[] data = Files.readAllBytes(file.toPath());
    return new String(data, StandardCharsets.UTF_8);
  }
}
