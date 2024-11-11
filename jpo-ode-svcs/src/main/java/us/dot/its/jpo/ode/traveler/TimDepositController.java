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
package us.dot.its.jpo.ode.traveler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.kafka.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.PojoTopics;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.*;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier.TimTransmogrifierException;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeTimSerializer;
import java.util.UUID;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@Slf4j
public class TimDepositController {

    private static final TimIngestTracker INGEST_MONITOR = TimIngestTracker.getInstance();

    private static final String ERRSTR = "error";
    private static final String WARNING = "warning";
    private static final String SUCCESS = "success";

    private final Asn1CoderTopics asn1CoderTopics;
    private final PojoTopics pojoTopics;
    private final JsonTopics jsonTopics;

    private final SerialId serialIdJ2735;
    private final SerialId serialIdOde;

    private final MessageProducer<String, String> stringMsgProducer;
    private final MessageProducer<String, OdeObject> timProducer;

    private final boolean dataSigningEnabledSDW;

    public static class TimDepositControllerException extends Exception {

        private static final long serialVersionUID = 1L;

        public TimDepositControllerException(String errMsg) {
            super(errMsg);
        }

    }

    @Autowired
    public TimDepositController(OdeKafkaProperties odeKafkaProperties, Asn1CoderTopics asn1CoderTopics, PojoTopics pojoTopics, JsonTopics jsonTopics, TimIngestTrackerProperties ingestTrackerProperties) {
        super();

        this.asn1CoderTopics = asn1CoderTopics;
        this.pojoTopics = pojoTopics;
        this.jsonTopics = jsonTopics;
        this.serialIdJ2735 = new SerialId();
        this.serialIdOde = new SerialId();

        this.stringMsgProducer = MessageProducer.defaultStringMessageProducer(odeKafkaProperties.getBrokers(),
                odeKafkaProperties.getProducerType(), odeKafkaProperties.getDisabledTopics());
        this.timProducer = new MessageProducer<>(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducerType(),
                null, OdeTimSerializer.class.getName(), odeKafkaProperties.getDisabledTopics());

        this.dataSigningEnabledSDW = System.getenv("DATA_SIGNING_ENABLED_SDW") == null || System.getenv("DATA_SIGNING_ENABLED_SDW").isEmpty() || Boolean.parseBoolean(System.getenv("DATA_SIGNING_ENABLED_SDW"));

        // start the TIM ingest monitoring service if enabled
        if (ingestTrackerProperties.isTrackingEnabled()) {
            log.info("TIM ingest monitoring enabled.");

            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

            scheduledExecutorService.scheduleAtFixedRate(
                    new TimIngestWatcher(ingestTrackerProperties.getInterval()),
                    ingestTrackerProperties.getInterval(),
                    ingestTrackerProperties.getInterval(),
                    java.util.concurrent.TimeUnit.SECONDS);
        } else {
            log.info("TIM ingest monitoring disabled.");
        }
    }

    /**
     * Send a TIM with the appropriate deposit type, ODE.PUT or ODE.POST
     *
     * @param jsonString
     * @param verb
     * @return
     */
    public synchronized ResponseEntity<String> depositTim(String jsonString, RequestVerb verb) {

        if (null == jsonString || jsonString.isEmpty()) {
            String errMsg = "Empty request.";
            log.error(errMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
        }

        OdeTravelerInputData odeTID = null;
        ServiceRequest request;
        try {
            // Convert JSON to POJO
            odeTID = (OdeTravelerInputData) JsonUtils.jacksonFromJson(jsonString, OdeTravelerInputData.class, true);
            if (odeTID == null) {
                String errMsg = "Malformed or non-compliant JSON syntax.";
                log.error(errMsg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
            }

            request = odeTID.getRequest();
            if (request == null) {
                throw new TimDepositControllerException("Request element is required as of version 3.");
            }

            if (request.getOde() == null) {
                request.setOde(new OdeInternal());
            }

            request.getOde().setVerb(verb);

        } catch (TimDepositControllerException e) {
            String errMsg = "Missing or invalid argument: " + e.getMessage();
            log.error(errMsg, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
        } catch (JsonUtilsException e) {
            String errMsg = "Malformed or non-compliant JSON syntax.";
            log.error(errMsg, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
        }

        // Add metadata to message and publish to kafka
        OdeTravelerInformationMessage tim = odeTID.getTim();
        OdeMsgPayload timDataPayload = new OdeMsgPayload(tim);
        OdeRequestMsgMetadata timMetadata = new OdeRequestMsgMetadata(timDataPayload, request);

        // set packetID in tim Metadata
        timMetadata.setOdePacketID(tim.getPacketID());
        // set maxDurationTime in tim Metadata and set latest startDatetime in tim
        // metadata
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        if (null != tim.getDataframes() && tim.getDataframes().length > 0) {
            int maxDurationTime = 0;
            Date latestStartDateTime = null;
            for (DataFrame dataFrameItem : tim.getDataframes()) {
                maxDurationTime = maxDurationTime > dataFrameItem.getDurationTime() ? maxDurationTime
                        : dataFrameItem.getDurationTime();
                try {
                    latestStartDateTime = (latestStartDateTime == null || (latestStartDateTime != null
                            && latestStartDateTime.before(dateFormat.parse(dataFrameItem.getStartDateTime())))
                            ? dateFormat.parse(dataFrameItem.getStartDateTime())
                            : latestStartDateTime);
                } catch (ParseException e) {
                    log.error("Invalid dateTime parse: " + e);
                }
            }
            timMetadata.setMaxDurationTime(maxDurationTime);
            timMetadata.setOdeTimStartDateTime(dateFormat.format(latestStartDateTime));
        }
        // Setting the SerialId to OdeBradcastTim serialId to be changed to
        // J2735BroadcastTim serialId after the message has been published to
        // OdeTimBrodcast topic
        timMetadata.setSerialId(serialIdOde);
        timMetadata.setRecordGeneratedBy(GeneratedBy.TMC);

        try {
            timMetadata.setRecordGeneratedAt(DateTimeUtils.isoDateTime(DateTimeUtils.isoDateTime(tim.getTimeStamp())));
        } catch (ParseException | DateTimeParseException e) {
            String errMsg = "Invalid timestamp in tim record: " + tim.getTimeStamp();
            log.error(errMsg, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
        }

        OdeTimData odeTimData = new OdeTimData(timMetadata, timDataPayload);
        timProducer.send(pojoTopics.getTimBroadcast(), null, odeTimData);

        String obfuscatedTimData = TimTransmogrifier.obfuscateRsuPassword(odeTimData.toJson());
        stringMsgProducer.send(jsonTopics.getTimBroadcast(), null, obfuscatedTimData);

        // Now that the message gas been published to OdeBradcastTim topic, it should be
        // changed to J2735BroadcastTim serialId
        timMetadata.setSerialId(serialIdJ2735);

        // Short circuit
        // If the TIM has no RSU/SNMP or SDW structures, we are done
        if ((request.getRsus() == null || request.getSnmp() == null) && request.getSdw() == null) {
            String warningMsg = "Warning: TIM contains no RSU, SNMP, or SDW fields. Message only published to broadcast streams.";
            log.warn(warningMsg);
            return ResponseEntity.status(HttpStatus.OK).body(JsonUtils.jsonKeyValue(WARNING, warningMsg));
        }

        // Craft ASN-encodable TIM
        ObjectNode encodableTid;
        try {
            encodableTid = JsonUtils.toObjectNode(odeTID.toJson());
            TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(encodableTid);

            log.debug("Encodable Traveler Information Data: {}", encodableTid);

        } catch (JsonUtilsException e) {
            String errMsg = "Error converting to encodable TravelerInputData.";
            log.error(errMsg, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
        }

        try {
            String xmlMsg;
            DdsAdvisorySituationData asd = null;
            if (!this.dataSigningEnabledSDW) {
                // We need to send data UNSECURED, so we should try to build the ASD as well as
                // MessageFrame
                asd = TimTransmogrifier.buildASD(odeTID.getRequest());
            }
            xmlMsg = TimTransmogrifier.convertToXml(asd, encodableTid, timMetadata, serialIdJ2735);
            log.debug("XML representation: {}", xmlMsg);

            JSONObject jsonMsg = XmlUtils.toJSONObject(xmlMsg);

            String j2735Tim = TimTransmogrifier.createOdeTimData(jsonMsg.getJSONObject(AppContext.ODE_ASN1_DATA))
                    .toString();

            stringMsgProducer.send(asn1CoderTopics.getEncoderInput(), null, xmlMsg);

            String obfuscatedJ2735Tim = TimTransmogrifier.obfuscateRsuPassword(j2735Tim);
            // publish Broadcast TIM to a J2735 compliant topic.
            stringMsgProducer.send(jsonTopics.getJ2735TimBroadcast(), null, obfuscatedJ2735Tim);
            // publish J2735 TIM also to general un-filtered TIM topic
            // with streamID as key
         stringMsgProducer.send(jsonTopics.getTim(), serialIdJ2735.getStreamId(), obfuscatedJ2735Tim);

            serialIdOde.increment();
            serialIdJ2735.increment();
        } catch (JsonUtils.JsonUtilsException | XmlUtils.XmlUtilsException | TimTransmogrifierException e) {
            String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
            log.error(errMsg, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, errMsg));
        }

        INGEST_MONITOR.incrementTotalMessagesReceived();
        return ResponseEntity.status(HttpStatus.OK).body(JsonUtils.jsonKeyValue(SUCCESS, "true"));
    }

    /**
     * Update an already-deposited TIM
     *
     * @param jsonString TIM in JSON
     * @return list of success/failures
     */
    @PutMapping(value = "/tim", produces = "application/json")
    @CrossOrigin
    public ResponseEntity<String> putTim(@RequestBody String jsonString) {

        return depositTim(jsonString, ServiceRequest.OdeInternal.RequestVerb.PUT);
    }

    /**
     * Deposit a new TIM
     *
     * @param jsonString TIM in JSON
     * @return list of success/failures
     */
    @PostMapping(value = "/tim", produces = "application/json")
    @CrossOrigin
    public ResponseEntity<String> postTim(@RequestBody String jsonString) {

        return depositTim(jsonString, ServiceRequest.OdeInternal.RequestVerb.POST);
    }

}
