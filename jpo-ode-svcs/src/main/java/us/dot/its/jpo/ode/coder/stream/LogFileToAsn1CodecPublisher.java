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

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.*;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.*;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.JsonUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LogFileToAsn1CodecPublisher implements Asn1CodecPublisher {

    public static class LogFileToAsn1CodecPublisherException extends Exception {

        private static final long serialVersionUID = 1L;

        public LogFileToAsn1CodecPublisherException(String string, Exception e) {
            super(string, e);
        }

    }

    private final RawEncodedJsonTopics rawEncodedJsonTopics;
    private final JsonTopics jsonTopics;
    protected StringPublisher publisher;
    protected LogFileParser fileParser;
    protected SerialId serialId;

    public LogFileToAsn1CodecPublisher(StringPublisher stringPublisher, JsonTopics jsonTopics, RawEncodedJsonTopics rawEncodedJsonTopics) {
        this.jsonTopics = jsonTopics;
        this.rawEncodedJsonTopics = rawEncodedJsonTopics;
        this.publisher = stringPublisher;
        this.serialId = new SerialId();
    }

    public List<OdeData> publish(BufferedInputStream inputStream, String fileName, ImporterFileType fileType)
            throws LogFileToAsn1CodecPublisherException {
        ParserStatus status;

        List<OdeData> dataList = new ArrayList<>();
        if (fileType == ImporterFileType.LOG_FILE) {
            fileParser = LogFileParser.factory(fileName);

            do {
                try {
                    status = fileParser.parseFile(inputStream, fileName);
                    switch (status) {
                        case ParserStatus.COMPLETE -> addDataToList(dataList);
                        case ParserStatus.EOF -> publishList(dataList);
                        case ParserStatus.INIT -> log.error("Failed to parse the header bytes.");
                        default -> log.error("Failed to decode ASN.1 data");
                    }
                    inputStream = removeNextNewLineCharacter(inputStream);
                } catch (Exception e) {
                    throw new LogFileToAsn1CodecPublisherException("Error parsing or publishing data.", e);
                }
            } while (status == ParserStatus.COMPLETE);
        }

        return dataList;
    }

    private void addDataToList(List<OdeData> dataList) {

        OdeData odeData;

        OdeMsgPayload payload;
        OdeLogMetadata metadata;
        if (isDriverAlertRecord()) {
            payload = new OdeDriverAlertPayload(((DriverAlertFileParser) fileParser).getAlert());
            metadata = new OdeLogMetadata(payload);
            odeData = new OdeDriverAlertData(metadata, payload);
        } else if (isBsmRecord()) {
            payload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
            metadata = new OdeBsmMetadata(payload);
            odeData = new OdeAsn1Data(metadata, payload);
        } else if (isSpatRecord()) {
            payload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
            metadata = new OdeSpatMetadata(payload);
            odeData = new OdeAsn1Data(metadata, payload);
        } else {
            payload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
            metadata = new OdeLogMetadata(payload);
            odeData = new OdeAsn1Data(metadata, payload);
        }
        fileParser.updateMetadata(metadata);

        dataList.add(odeData);
    }

    public boolean isDriverAlertRecord() {
        return fileParser instanceof DriverAlertFileParser;
    }

    public boolean isBsmRecord() {
        return fileParser instanceof BsmLogFileParser || (fileParser instanceof RxMsgFileParser
                && ((RxMsgFileParser) fileParser).getRxSource() == RxSource.RV);
    }

    public boolean isSpatRecord() {
        return fileParser instanceof SpatLogFileParser;
    }

    private void publishList(List<OdeData> dataList) {
        serialId.setBundleSize(dataList.size());

        for (OdeData odeData : dataList) {
            OdeLogMetadata msgMetadata = (OdeLogMetadata) odeData.getMetadata();
            OdeMsgPayload msgPayload = odeData.getPayload();
            msgMetadata.setSerialId(serialId);

            if (isDriverAlertRecord()) {
                publisher.publish(jsonTopics.getDriverAlert(), JsonUtils.toJson(odeData, false));
            } else if (isBsmRecord()) {
                publisher.publish(rawEncodedJsonTopics.getBsm(), JsonUtils.toJson(odeData, false));
            } else if (isSpatRecord()) {
                publisher.publish(rawEncodedJsonTopics.getSpat(), JsonUtils.toJson(odeData, false));
            } else {
                String messageType = UperUtil.determineMessageType(msgPayload);
                switch (messageType) {
                    case "MAP" -> publisher.publish(rawEncodedJsonTopics.getMap(), JsonUtils.toJson(odeData, false));
                    case "SPAT" -> publisher.publish(rawEncodedJsonTopics.getSpat(), JsonUtils.toJson(odeData, false));
                    case "TIM" -> publisher.publish(rawEncodedJsonTopics.getTim(), JsonUtils.toJson(odeData, false));
                    case "BSM" -> publisher.publish(rawEncodedJsonTopics.getBsm(), JsonUtils.toJson(odeData, false));
                    case "SSM" -> publisher.publish(rawEncodedJsonTopics.getSsm(), JsonUtils.toJson(odeData, false));
                    case "SRM" -> publisher.publish(rawEncodedJsonTopics.getSrm(), JsonUtils.toJson(odeData, false));
                    case "PSM" -> publisher.publish(rawEncodedJsonTopics.getPsm(), JsonUtils.toJson(odeData, false));
                    default -> log.warn("Unknown message type: {}", messageType);
                }
            }

            serialId.increment();
        }
    }

    // This method will check if the next character is a newline character (0x0A in hex or 10 in converted decimal)
    // or if the next character does not contain a newline character it will put that character back into the buffered input stream
    public BufferedInputStream removeNextNewLineCharacter(BufferedInputStream bis) {
        try {
            bis.mark(1);
            int nextByte = bis.read();
            if (nextByte != 10) { // If the next byte is not a newline
                bis.reset(); // Reset the stream back to the most recent mark
            }
        } catch (IOException e) {
            log.error("Error removing next newline character: ", e);
        }
        return bis;
    }
}
