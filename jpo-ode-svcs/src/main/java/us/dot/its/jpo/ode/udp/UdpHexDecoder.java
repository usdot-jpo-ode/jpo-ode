package us.dot.its.jpo.ode.udp;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapMetadata.MapSource;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.model.OdePsmMetadata.PsmSource;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.model.OdeSpatMetadata.SpatSource;
import us.dot.its.jpo.ode.model.OdeSrmMetadata;
import us.dot.its.jpo.ode.model.OdeSrmMetadata.SrmSource;
import us.dot.its.jpo.ode.model.OdeSsmMetadata;
import us.dot.its.jpo.ode.model.OdeSsmMetadata.SsmSource;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

import java.net.DatagramPacket;

@Slf4j
public class UdpHexDecoder {

    private UdpHexDecoder() {
        throw new UnsupportedOperationException();
    }

    public static OdeAsn1Payload getPayloadHexString(DatagramPacket packet, SupportedMessageType msgType) throws InvalidPayloadException {
        // extract the actual packet from the buffer
        byte[] payload = packet.getData();
        if (payload == null)
            throw new InvalidPayloadException("Payload is null");
        // convert bytes to hex string and verify identity
        String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
        if (!payloadHexString.contains(msgType.getStartFlag()))
            throw new InvalidPayloadException("Payload does not contain start flag");

        log.debug("Full {} packet: {}", msgType, payloadHexString);

        payloadHexString = UperUtil.stripTrailingZeros(UperUtil.stripDot3Header(payloadHexString, msgType.getStartFlag())).toLowerCase();
        log.debug("Stripped {} packet: {}", msgType, payloadHexString);

        return new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
    }

    public static String buildJsonMapFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload mapPayload = getPayloadHexString(packet, SupportedMessageType.MAP);
        OdeMapMetadata mapMetadata = new OdeMapMetadata(mapPayload);

        // Add header data for the decoding process
        mapMetadata.setOdeReceivedAt(DateTimeUtils.now());

        mapMetadata.setOriginIp(senderIp);
        mapMetadata.setMapSource(MapSource.RSU);
        mapMetadata.setRecordType(RecordType.mapTx);
        mapMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        mapMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(mapMetadata, mapPayload), false);
    }

    public static String buildJsonSpatFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload spatPayload = getPayloadHexString(packet, SupportedMessageType.SPAT);
        OdeSpatMetadata spatMetadata = new OdeSpatMetadata(spatPayload);

        // Add header data for the decoding process
        spatMetadata.setOdeReceivedAt(DateTimeUtils.now());

        spatMetadata.setOriginIp(senderIp);
        spatMetadata.setSpatSource(SpatSource.RSU);
        spatMetadata.setRecordType(RecordType.spatTx);
        spatMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        spatMetadata.setSecurityResultCode(SecurityResultCode.success);


        return JsonUtils.toJson(new OdeAsn1Data(spatMetadata, spatPayload), false);
    }

    public static String buildJsonTimFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload timPayload = getPayloadHexString(packet, SupportedMessageType.TIM);
        OdeTimMetadata timMetadata = new OdeTimMetadata(timPayload);

        // Add header data for the decoding process
        timMetadata.setOdeReceivedAt(DateTimeUtils.now());

        timMetadata.setOriginIp(senderIp);
        timMetadata.setRecordType(RecordType.timMsg);
        timMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        timMetadata.setSecurityResultCode(SecurityResultCode.success);
        return JsonUtils.toJson(new OdeAsn1Data(timMetadata, timPayload), false);
    }

    public static String buildJsonBsmFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        OdeAsn1Payload bsmPayload = getPayloadHexString(packet, SupportedMessageType.BSM);
        OdeBsmMetadata bsmMetadata = new OdeBsmMetadata(bsmPayload);

        // Set BSM Metadata values that can be assumed from the UDP endpoint
        bsmMetadata.setOdeReceivedAt(DateTimeUtils.now());

        ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
        OdeLogMsgMetadataLocation locationData = new OdeLogMsgMetadataLocation(
                "unavailable",
                "unavailable",
                "unavailable",
                "unavailable",
                "unavailable");
        receivedMessageDetails.setRxSource(RxSource.RSU);
        receivedMessageDetails.setLocationData(locationData);
        bsmMetadata.setReceivedMessageDetails(receivedMessageDetails);

        bsmMetadata.setOriginIp(senderIp);
        bsmMetadata.setBsmSource(BsmSource.EV);
        bsmMetadata.setRecordType(RecordType.bsmTx);
        bsmMetadata.setRecordGeneratedBy(GeneratedBy.OBU);
        bsmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(bsmMetadata, bsmPayload), false);
    }

    public static String buildJsonSsmFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload ssmPayload = getPayloadHexString(packet, SupportedMessageType.SSM);
        OdeSsmMetadata ssmMetadata = new OdeSsmMetadata(ssmPayload);

        // Add header data for the decoding process
        ssmMetadata.setOdeReceivedAt(DateTimeUtils.now());

        ssmMetadata.setOriginIp(senderIp);
        ssmMetadata.setSsmSource(SsmSource.RSU);
        ssmMetadata.setRecordType(RecordType.ssmTx);
        ssmMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        ssmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(ssmMetadata, ssmPayload), false);
    }

    public static String buildJsonSrmFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload srmPayload = getPayloadHexString(packet, SupportedMessageType.SRM);
        OdeSrmMetadata srmMetadata = new OdeSrmMetadata(srmPayload);

        // Add header data for the decoding process
        srmMetadata.setOdeReceivedAt(DateTimeUtils.now());

        srmMetadata.setOriginIp(senderIp);
        srmMetadata.setSrmSource(SrmSource.RSU);
        srmMetadata.setRecordType(RecordType.srmTx);
        srmMetadata.setRecordGeneratedBy(GeneratedBy.OBU);
        srmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(srmMetadata, srmPayload), false);
    }

    public static String buildJsonPsmFromPacket(DatagramPacket packet) throws InvalidPayloadException {
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload psmPayload = getPayloadHexString(packet, SupportedMessageType.PSM);
        OdePsmMetadata psmMetadata = new OdePsmMetadata(psmPayload);
        // Add header data for the decoding process
        psmMetadata.setOdeReceivedAt(DateTimeUtils.now());

        psmMetadata.setOriginIp(senderIp);
        psmMetadata.setPsmSource(PsmSource.RSU);
        psmMetadata.setRecordType(RecordType.psmTx);
        psmMetadata.setRecordGeneratedBy(GeneratedBy.UNKNOWN);
        psmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(psmMetadata, psmPayload), false);
    }
}
