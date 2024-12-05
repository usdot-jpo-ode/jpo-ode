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

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import us.dot.its.jpo.ode.coder.*;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.PojoTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

@Slf4j
public class Asn1DecodedDataRouter extends AbstractSubscriberProcessor<String, String> {

    private final PojoTopics pojoTopics;
    private final JsonTopics jsonTopics;
    private final MessageProducer<String, OdeBsmData> bsmProducer;
    private final MessageProducer<String, String> timProducer;
    private final MessageProducer<String, String> spatProducer;
    private final MessageProducer<String, String> ssmProducer;
    private final MessageProducer<String, String> srmProducer;
    private final MessageProducer<String, String> psmProducer;

    public Asn1DecodedDataRouter(OdeKafkaProperties odeKafkaProperties, PojoTopics pojoTopics, JsonTopics jsonTopics) {
        super();

        this.pojoTopics = pojoTopics;
        this.jsonTopics = jsonTopics;
        this.bsmProducer = new MessageProducer<>(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getKafkaType(),
                null,
                OdeBsmSerializer.class.getName(),
                odeKafkaProperties.getDisabledTopics());
        this.timProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getKafkaType(),
                odeKafkaProperties.getDisabledTopics());
        this.spatProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getKafkaType(),
                odeKafkaProperties.getDisabledTopics());
        this.ssmProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getKafkaType(),
                odeKafkaProperties.getDisabledTopics());
        this.srmProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getKafkaType(),
                odeKafkaProperties.getDisabledTopics());
        this.psmProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getKafkaType(),
                odeKafkaProperties.getDisabledTopics());
    }

    @Override
    public Object process(String consumedData) {
        try {
            JSONObject consumed = XmlUtils.toJSONObject(consumedData).getJSONObject(OdeAsn1Data.class.getSimpleName());
            J2735DSRCmsgID messageId = J2735DSRCmsgID.valueOf(
                    consumed.getJSONObject(AppContext.PAYLOAD_STRING)
                            .getJSONObject(AppContext.DATA_STRING)
                            .getJSONObject("MessageFrame")
                            .getInt("messageId")
            );

            RecordType recordType = RecordType
                    .valueOf(consumed.getJSONObject(AppContext.METADATA_STRING).getString("recordType"));

            switch (messageId) {
                case BasicSafetyMessage -> routeBSM(consumedData, recordType);
                case TravelerInformation -> routeTIM(consumed, recordType);
                case SPATMessage -> routeSPAT(consumedData, recordType);
                case MAPMessage -> log.debug("MAP data processing no longer supported in this router.");
                case SSMMessage -> routeSSM(consumedData, recordType);
                case SRMMessage -> routeSRM(consumedData, recordType);
                case PersonalSafetyMessage -> routePSM(consumedData, recordType);
                case null, default -> log.warn("Unknown message type: {}", messageId);
            }
        } catch (Exception e) {
            log.error("Failed to route received data: {}", consumedData, e);
        }
        return null;
    }

    private void routePSM(String consumedData, RecordType recordType) throws XmlUtils.XmlUtilsException {
        String odePsmData = OdePsmDataCreatorHelper.createOdePsmData(consumedData).toString();
        if (recordType == RecordType.psmTx) {
            psmProducer.send(pojoTopics.getTxPsm(), getRecord().key(), odePsmData);
        }
        // Send all PSMs also to OdePsmJson
        psmProducer.send(jsonTopics.getPsm(), getRecord().key(), odePsmData);
        log.debug("Submitted to PSM Pojo topic {}", jsonTopics.getPsm());
    }

    private void routeSRM(String consumedData, RecordType recordType) throws XmlUtils.XmlUtilsException {
        String odeSrmData = OdeSrmDataCreatorHelper.createOdeSrmData(consumedData).toString();
        if (recordType == RecordType.srmTx) {
            srmProducer.send(pojoTopics.getTxSrm(), getRecord().key(), odeSrmData);
        }
        // Send all SRMs also to OdeSrmJson
        srmProducer.send(jsonTopics.getSrm(), getRecord().key(), odeSrmData);
        log.debug("Submitted to SRM Pojo topic {}", jsonTopics.getSrm());
    }

    private void routeSSM(String consumedData, RecordType recordType) throws XmlUtils.XmlUtilsException {
        String odeSsmData = OdeSsmDataCreatorHelper.createOdeSsmData(consumedData).toString();
        if (recordType == RecordType.ssmTx) {
            ssmProducer.send(pojoTopics.getSsm(), getRecord().key(), odeSsmData);
        }
        // Send all SSMs also to OdeSsmJson
        ssmProducer.send(jsonTopics.getSsm(), getRecord().key(), odeSsmData);
        log.debug("Submitted to SSM Pojo topic {}", jsonTopics.getSsm());
    }

    private void routeSPAT(String consumedData, RecordType recordType) throws XmlUtils.XmlUtilsException {
        String odeSpatData = OdeSpatDataCreatorHelper.createOdeSpatData(consumedData).toString();
        switch (recordType) {
            case dnMsg -> spatProducer.send(jsonTopics.getDnMessage(), getRecord().key(), odeSpatData);
            case rxMsg -> spatProducer.send(jsonTopics.getRxSpat(), getRecord().key(), odeSpatData);
            case spatTx -> spatProducer.send(pojoTopics.getTxSpat(), getRecord().key(), odeSpatData);
            default -> log.trace("Consumed SPAT data with record type: {}", recordType);
        }
        // Send all SPATs also to OdeSpatJson
        spatProducer.send(jsonTopics.getSpat(), getRecord().key(), odeSpatData);
        log.debug("Submitted to SPAT Pojo topic {}", jsonTopics.getSpat());
    }

    private void routeTIM(JSONObject consumed, RecordType recordType) {
        String odeTimData = TimTransmogrifier.createOdeTimData(consumed).toString();
        switch (recordType) {
            case dnMsg -> timProducer.send(jsonTopics.getDnMessage(), getRecord().key(), odeTimData);
            case rxMsg -> timProducer.send(jsonTopics.getRxTim(), getRecord().key(), odeTimData);
            default -> log.trace("Consumed TIM data with record type: {}", recordType);
        }
        // Send all TIMs also to OdeTimJson
        timProducer.send(jsonTopics.getTim(), getRecord().key(), odeTimData);
        log.debug("Submitted to TIM Pojo topic: {}", jsonTopics.getTim());
    }

    private void routeBSM(String consumedData, RecordType recordType) throws XmlUtils.XmlUtilsException {
        // ODE-518/ODE-604 Demultiplex the messages to appropriate topics based on the "recordType"
        OdeBsmData odeBsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
        switch (recordType) {
            case bsmLogDuringEvent -> bsmProducer.send(pojoTopics.getBsmDuringEvent(), getRecord().key(), odeBsmData);
            case rxMsg -> bsmProducer.send(pojoTopics.getRxBsm(), getRecord().key(), odeBsmData);
            case bsmTx -> bsmProducer.send(pojoTopics.getTxBsm(), getRecord().key(), odeBsmData);
            default -> log.trace("Consumed BSM data with record type: {}", recordType);
        }
        // Send all BSMs also to OdeBsmPojo
        bsmProducer.send(pojoTopics.getBsm(), getRecord().key(), odeBsmData);
        log.debug("Submitted to BSM Pojo topic {}", pojoTopics.getBsm());
    }
}
