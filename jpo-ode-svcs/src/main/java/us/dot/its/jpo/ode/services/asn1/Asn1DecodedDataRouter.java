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
package us.dot.its.jpo.ode.services.asn1;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeMapDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSpatDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSrmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdePsmDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

public class Asn1DecodedDataRouter extends AbstractSubscriberProcessor<String, String> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private OdeProperties odeProperties;
	private MessageProducer<String, OdeBsmData> bsmProducer;
	private MessageProducer<String, String> timProducer;
	private MessageProducer<String, String> spatProducer;
	private MessageProducer<String, String> mapProducer;
	private MessageProducer<String, String> ssmProducer;
	private MessageProducer<String, String> srmProducer;
	private MessageProducer<String, String> psmProducer;

	public Asn1DecodedDataRouter(OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
		super();
		this.odeProperties = odeProps;
		this.bsmProducer = new MessageProducer<>(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				null,
				OdeBsmSerializer.class.getName(),
				odeKafkaProperties.getDisabledTopics());
		this.timProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				odeKafkaProperties.getDisabledTopics());
		this.spatProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				odeKafkaProperties.getDisabledTopics());
		this.mapProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				odeKafkaProperties.getDisabledTopics());
		this.ssmProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				odeKafkaProperties.getDisabledTopics());
		this.srmProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				odeKafkaProperties.getDisabledTopics());
		this.psmProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
				odeKafkaProperties.getProducerType(),
				odeKafkaProperties.getDisabledTopics());
	}

	@Override
	public Object process(String consumedData) {
		try {
			JSONObject consumed = XmlUtils.toJSONObject(consumedData).getJSONObject(OdeAsn1Data.class.getSimpleName());
			int messageId = consumed.getJSONObject(AppContext.PAYLOAD_STRING).getJSONObject(AppContext.DATA_STRING)
					.getJSONObject("MessageFrame").getInt("messageId");

			RecordType recordType = RecordType
					.valueOf(consumed.getJSONObject(AppContext.METADATA_STRING).getString("recordType"));

			if (messageId == J2735DSRCmsgID.BasicSafetyMessage.getMsgID()) {
				// ODE-518/ODE-604 Demultiplex the messages to appropriate topics based on the
				// "recordType"
				OdeBsmData odeBsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
				if (recordType == RecordType.bsmLogDuringEvent) {
					bsmProducer.send(odeProperties.getKafkaTopicOdeBsmDuringEventPojo(), getRecord().key(), odeBsmData);
				} else if (recordType == RecordType.rxMsg) {
					bsmProducer.send(odeProperties.getKafkaTopicOdeBsmRxPojo(), getRecord().key(), odeBsmData);
				} else if (recordType == RecordType.bsmTx) {
					bsmProducer.send(odeProperties.getKafkaTopicOdeBsmTxPojo(), getRecord().key(), odeBsmData);
				}
				// Send all BSMs also to OdeBsmPojo
				bsmProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), getRecord().key(), odeBsmData);
				logger.debug("Submitted to BSM Pojo topic");
			} else if (messageId == J2735DSRCmsgID.TravelerInformation.getMsgID()) {
				String odeTimData = TimTransmogrifier.createOdeTimData(consumed).toString();
				if (recordType == RecordType.dnMsg) {
					timProducer.send(odeProperties.getKafkaTopicOdeDNMsgJson(), getRecord().key(), odeTimData);
				} else if (recordType == RecordType.rxMsg) {
					timProducer.send(odeProperties.getKafkaTopicOdeTimRxJson(), getRecord().key(), odeTimData);
				}
				// Send all TIMs also to OdeTimJson
				timProducer.send(odeProperties.getKafkaTopicOdeTimJson(), getRecord().key(), odeTimData);
				logger.debug("Submitted to TIM Pojo topic");
			} else if (messageId == J2735DSRCmsgID.SPATMessage.getMsgID()) {
				String odeSpatData = OdeSpatDataCreatorHelper.createOdeSpatData(consumedData).toString();
				if (recordType == RecordType.dnMsg) {
					spatProducer.send(odeProperties.getKafkaTopicOdeDNMsgJson(), getRecord().key(), odeSpatData);
				} else if (recordType == RecordType.rxMsg) {
					spatProducer.send(odeProperties.getKafkaTopicOdeSpatRxJson(), getRecord().key(), odeSpatData);
				} else if (recordType == RecordType.spatTx) {
					spatProducer.send(odeProperties.getKafkaTopicOdeSpatTxPojo(), getRecord().key(), odeSpatData);
				}
				// Send all SPATs also to OdeSpatJson
				spatProducer.send(odeProperties.getKafkaTopicOdeSpatJson(), getRecord().key(), odeSpatData);
				logger.debug("Submitted to SPAT Pojo topic");
			} else if (messageId == J2735DSRCmsgID.MAPMessage.getMsgID()) {
				String odeMapData = OdeMapDataCreatorHelper.createOdeMapData(consumedData).toString();
				if (recordType == RecordType.mapTx) {
					mapProducer.send(odeProperties.getKafkaTopicOdeMapTxPojo(), getRecord().key(), odeMapData);
				}
				// Send all Map also to OdeMapJson
				mapProducer.send(odeProperties.getKafkaTopicOdeMapJson(), getRecord().key(), odeMapData);
				logger.debug("Submitted to MAP Pojo topic");
			} else if (messageId == J2735DSRCmsgID.SSMMessage.getMsgID()) {
				String odeSsmData = OdeSsmDataCreatorHelper.createOdeSsmData(consumedData).toString();
				if (recordType == RecordType.ssmTx) {
					ssmProducer.send(odeProperties.getKafkaTopicOdeSsmPojo(), getRecord().key(), odeSsmData);
				}
				// Send all SSMs also to OdeSsmJson
				ssmProducer.send(odeProperties.getKafkaTopicOdeSsmJson(), getRecord().key(), odeSsmData);
				logger.debug("Submitted to SSM Pojo topic");
			} else if (messageId == J2735DSRCmsgID.SRMMessage.getMsgID()) {
				String odeSrmData = OdeSrmDataCreatorHelper.createOdeSrmData(consumedData).toString();
				if (recordType == RecordType.srmTx) {
					srmProducer.send(odeProperties.getKafkaTopicOdeSrmTxPojo(), getRecord().key(), odeSrmData);
				}
				// Send all SRMs also to OdeSrmJson
				srmProducer.send(odeProperties.getKafkaTopicOdeSrmJson(), getRecord().key(), odeSrmData);
				logger.debug("Submitted to SRM Pojo topic");
			} else if (messageId == J2735DSRCmsgID.PersonalSafetyMessage.getMsgID()) {
				String odePsmData = OdePsmDataCreatorHelper.createOdePsmData(consumedData).toString();
				if (recordType == RecordType.psmTx) {
					psmProducer.send(odeProperties.getKafkaTopicOdePsmTxPojo(), getRecord().key(), odePsmData);
				}
				// Send all PSMs also to OdePsmJson
				psmProducer.send(odeProperties.getKafkaTopicOdePsmJson(), getRecord().key(), odePsmData);
				logger.debug("Submitted to PSM Pojo topic");
			}
		} catch (Exception e) {
			logger.error("Failed to route received data: " + consumedData, e);
		}
		return null;
	}
}
