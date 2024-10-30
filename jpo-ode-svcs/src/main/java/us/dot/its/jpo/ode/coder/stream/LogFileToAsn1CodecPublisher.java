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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.DriverAlertFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.SpatLogFileParser;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeDriverAlertData;
import us.dot.its.jpo.ode.model.OdeDriverAlertPayload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.uper.UperUtil;

public class LogFileToAsn1CodecPublisher implements Asn1CodecPublisher {

	public static class LogFileToAsn1CodecPublisherException extends Exception {

		private static final long serialVersionUID = 1L;

		public LogFileToAsn1CodecPublisherException(String string, Exception e) {
			super(string, e);
		}

	}

	protected static final Logger logger = LoggerFactory.getLogger(LogFileToAsn1CodecPublisher.class);

	protected StringPublisher publisher;
	protected LogFileParser fileParser;
	protected SerialId serialId;

	public LogFileToAsn1CodecPublisher(StringPublisher dataPub) {
		this.publisher = dataPub;
		this.serialId = new SerialId();
	}

	public List<OdeData> publish(BufferedInputStream bis, String fileName, ImporterFileType fileType)
			throws LogFileToAsn1CodecPublisherException {
		XmlUtils xmlUtils = new XmlUtils();
		ParserStatus status;

		List<OdeData> dataList = new ArrayList<>();
		if (fileType == ImporterFileType.LOG_FILE) {
			fileParser = LogFileParser.factory(fileName);

			do {
				try {
					status = fileParser.parseFile(bis, fileName);
					if (status == ParserStatus.COMPLETE) {
						addDataToList(dataList);
					} else if (status == ParserStatus.EOF) {
						publishList(xmlUtils, dataList);
					} else if (status == ParserStatus.INIT) {
						logger.error("Failed to parse the header bytes.");
					} else {
						logger.error("Failed to decode ASN.1 data");
					}
					bis = removeNextNewLineCharacter(bis);
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

	private void publishList(XmlUtils xmlUtils, List<OdeData> dataList) throws JsonProcessingException {
		serialId.setBundleSize(dataList.size());

		for (OdeData odeData : dataList) {
			OdeLogMetadata msgMetadata = (OdeLogMetadata) odeData.getMetadata();
			OdeMsgPayload msgPayload = (OdeMsgPayload) odeData.getPayload();
			msgMetadata.setSerialId(serialId);

			if (isDriverAlertRecord()) {
				publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicDriverAlertJson());
			} else if (isBsmRecord()) {
				publisher.publish(JsonUtils.toJson(odeData, false),
				publisher.getOdeProperties().getKafkaTopicOdeRawEncodedBSMJson());
			} else if (isSpatRecord()) {
				publisher.publish(JsonUtils.toJson(odeData, false),
					publisher.getOdeProperties().getKafkaTopicOdeRawEncodedSPATJson());
			} else {
				// Determine the message type (MAP, TIM, SSM, SRM, or PSM)
				String messageType = UperUtil.determineMessageType(msgPayload);
				if (messageType.equals("MAP")) {
					publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicOdeRawEncodedMAPJson());
				} else if(messageType.equals("SPAT")){
					publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicOdeRawEncodedSPATJson());
				} else if (messageType.equals("TIM")) {
					publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicOdeRawEncodedTIMJson());
				} else if (messageType.equals("BSM")) {
						publisher.publish(JsonUtils.toJson(odeData, false),
							publisher.getOdeProperties().getKafkaTopicOdeRawEncodedBSMJson());
				} else if (messageType.equals("SSM")) {
					publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicOdeRawEncodedSSMJson());
				} else if (messageType.equals("SRM")) {
					publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicOdeRawEncodedSRMJson());
				} else if (messageType.equals("PSM")) {
					publisher.publish(JsonUtils.toJson(odeData, false),
						publisher.getOdeProperties().getKafkaTopicOdeRawEncodedPSMJson());
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
			e.printStackTrace();
		}
		return bis;
	}
}
