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

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.kafka.JsonTopics;
import us.dot.its.jpo.ode.kafka.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.util.DateTimeUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogFileToAsn1CodecPublisherTest {

    private static final String GZ = ".gz";
    private static final String schemaVersion = "7";

    @Tested
    LogFileToAsn1CodecPublisher testLogFileToAsn1CodecPublisher;

    @Injectable
    StringPublisher injectableStringPublisher;

    @Injectable
    JsonTopics injectableJsonTopics;

    @Injectable
    RawEncodedJsonTopics injectableRawEncodedJsonTopics;


    @BeforeAll
    public static void setupClass() {
        OdeMsgMetadata.setStaticSchemaVersion(Integer.parseInt(schemaVersion));
    }

    @Test
    void testPublishInit(@Mocked LogFileParser mockLogFileParser) throws Exception {
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
    void testPublishEOF(@Mocked LogFileParser mockLogFileParser) throws Exception {
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
    void testPublishThrowsIllegalArgumentException() {
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
    void testPublishThrowsLogFileToAsn1CodecPublisherException(@Mocked LogFileParser mockLogFileParser) {
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
    void testPublishDecodeFailure(@Mocked LogFileParser mockLogFileParser) throws Exception {
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
    void testPublishBsmTxLogFile() throws Exception {

        byte[] buf = new byte[]{
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
            String asn1String = data.getMetadata().getAsn1();
            var expected = String.format("{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"bsmTx.gz\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"asn1\":\"%s\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"%s\"}}}", schemaVersion, asn1String, asn1String);
            assertEquals(expected, data.toJson());
        }
    }

    @Test
    void testPublishDistressNotificationLogFile() throws Exception {

        byte[] buf = new byte[]{
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
            var expected = String.format("{\"metadata\":{\"logFileName\":\"dnMsg.gz\",\"recordType\":\"dnMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"NA\"},\"encodings\":[{\"elementName\":\"root\",\"elementType\":\"Ieee1609Dot2Data\",\"encodingRule\":\"COER\"},{\"elementName\":\"unsecuredData\",\"elementType\":\"MessageFrame\",\"encodingRule\":\"UPER\"}],\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"038100400380\"}}}", schemaVersion);
        }
    }

    @Test
    void testPublishDriverAlertLogFile() throws Exception {

        byte[] buf = new byte[]{
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
            var expected = String.format("{\"metadata\":{\"logFileName\":\"driverAlert.gz\",\"recordType\":\"driverAlert\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"NA\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeDriverAlertPayload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false},\"payload\":{\"alert\":\"Test Driver Alert\"}}", schemaVersion);
            assertEquals(expected, data.toJson());
        }
    }

    @Test
    void testPublishRxMsgTIMLogFile() throws Exception {

        byte[] buf = new byte[]{
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
            String asn1String = data.getMetadata().getAsn1();
            var expected = String.format("{\"metadata\":{\"logFileName\":\"rxMsg.gz\",\"recordType\":\"rxMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"SAT\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"TMC_VIA_SAT\",\"sanitized\":false,\"asn1\":\"%s\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"%s\"}}}", schemaVersion, asn1String, asn1String);
            assertEquals(expected, data.toJson());
        }
    }

    @Test
    void testPublishRxMsgBSMLogFile() throws Exception {

        byte[] buf = new byte[]{
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
            String asn1String = data.getMetadata().getAsn1();
            var expected = String.format("{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"rxMsg.gz\",\"recordType\":\"rxMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"asn1\":\"%s\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"%s\"}}}", schemaVersion, asn1String, asn1String);
            assertEquals(expected, data.toJson());
        }
    }

    @Test
    void testPublishNonLearLogFile() throws Exception {

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
    void testPublishRxMsgBSMLogFileNewLine() throws Exception {

        byte[] buf = new byte[]{
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
            String asn1String = data.getMetadata().getAsn1();
            var expected = String.format("{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"rxMsg.gz\",\"recordType\":\"rxMsg\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"42.4506735\",\"longitude\":\"-83.2790108\",\"elevation\":\"163.9\",\"speed\":\"0.08\",\"heading\":\"124.9125\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c7bbb42e-1e39-442d-98ac-62740ca50f92\",\"bundleSize\":1,\"bundleId\":1,\"recordId\":0,\"serialNumber\":1},\"odeReceivedAt\":\"2019-03-05T20:31:17.579Z\",\"schemaVersion\":%s,\"maxDurationTime\":0,\"recordGeneratedAt\":\"2018-04-26T19:46:49.399Z\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"asn1\":\"%s\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"%s\"}}}", schemaVersion, asn1String, asn1String);
            assertEquals(expected, data.toJson());
        }
    }
}
