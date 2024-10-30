package us.dot.its.jpo.ode.udp;

import java.net.DatagramPacket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.JsonUtils;

public class UdpHexDecoder {
    
    private static Logger logger = LoggerFactory.getLogger(UdpHexDecoder.class);

    public static OdeAsn1Payload getPayloadHexString(DatagramPacket packet, UperUtil.SupportedMessageTypes msgType) {
      String startFlag = UperUtil.getStartFlag(msgType);
      // extract the actual packet from the buffer
      byte[] payload = packet.getData();
      if (payload == null)
         return null;
      // convert bytes to hex string and verify identity
      String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
      if (payloadHexString.indexOf(startFlag) == -1)
         return null;
      
      logger.debug("Full {} packet: {}", msgType, payloadHexString);
      
      payloadHexString = UperUtil.stripTrailingZeros(UperUtil.stripDot3Header(payloadHexString, startFlag)).toLowerCase();
      logger.debug("Stripped {} packet: {}", msgType, payloadHexString);

      OdeAsn1Payload odePayload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));
      
      return odePayload;
    }

    public static String buildJsonMapFromPacket(DatagramPacket packet){
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        logger.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload mapPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.MAP);
        if (mapPayload == null)
            return null;
        OdeMapMetadata mapMetadata = new OdeMapMetadata(mapPayload);
      
        // Add header data for the decoding process
        mapMetadata.setOdeReceivedAt(getUtcTimeString());

        mapMetadata.setOriginIp(senderIp);
        mapMetadata.setMapSource(MapSource.RSU);
        mapMetadata.setRecordType(RecordType.mapTx);
        mapMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        mapMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(mapMetadata, mapPayload), false);
   }

   public static String buildJsonSpatFromPacket(DatagramPacket packet){
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        logger.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload spatPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.SPAT);
        if (spatPayload == null)
            return null;
        OdeSpatMetadata spatMetadata = new OdeSpatMetadata(spatPayload);

        // Add header data for the decoding process
        spatMetadata.setOdeReceivedAt(getUtcTimeString());

        spatMetadata.setOriginIp(senderIp);
        spatMetadata.setSpatSource(SpatSource.RSU);
        spatMetadata.setRecordType(RecordType.spatTx);
        spatMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        spatMetadata.setSecurityResultCode(SecurityResultCode.success);


        return JsonUtils.toJson(new OdeAsn1Data(spatMetadata, spatPayload), false); 
   }

   public static String buildJsonTimFromPacket(DatagramPacket packet){

        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        logger.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload timPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.TIM);
        if (timPayload == null)
            return null;
        OdeTimMetadata timMetadata = new OdeTimMetadata(timPayload);

        // Add header data for the decoding process
        timMetadata.setOdeReceivedAt(getUtcTimeString());

        timMetadata.setOriginIp(senderIp);
        timMetadata.setRecordType(RecordType.timMsg);
        timMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        timMetadata.setSecurityResultCode(SecurityResultCode.success);
        return JsonUtils.toJson(new OdeAsn1Data(timMetadata, timPayload), false);
   }

   public static String buildJsonBsmFromPacket(DatagramPacket packet){
      String senderIp = packet.getAddress().getHostAddress();
      int senderPort = packet.getPort();
      logger.debug("Packet received from {}:{}", senderIp, senderPort);

      OdeAsn1Payload bsmPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.BSM);
      if (bsmPayload == null)
         return null;
      OdeBsmMetadata bsmMetadata = new OdeBsmMetadata(bsmPayload);
      
      // Set BSM Metadata values that can be assumed from the UDP endpoint
      bsmMetadata.setOdeReceivedAt(getUtcTimeString());

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

   public static String buildJsonSsmFromPacket(DatagramPacket packet){
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        logger.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload ssmPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.SSM);
        if (ssmPayload == null)
            return null;
        OdeSsmMetadata ssmMetadata = new OdeSsmMetadata(ssmPayload);

        // Add header data for the decoding process
        ssmMetadata.setOdeReceivedAt(getUtcTimeString());

        ssmMetadata.setOriginIp(senderIp);
        ssmMetadata.setSsmSource(SsmSource.RSU);
        ssmMetadata.setRecordType(RecordType.ssmTx);
        ssmMetadata.setRecordGeneratedBy(GeneratedBy.RSU);
        ssmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(ssmMetadata, ssmPayload), false);
    }

    public static String buildJsonSrmFromPacket(DatagramPacket packet){
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        logger.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload srmPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.SRM);
        if (srmPayload == null)
            return null;
        OdeSrmMetadata srmMetadata = new OdeSrmMetadata(srmPayload);

        // Add header data for the decoding process
        srmMetadata.setOdeReceivedAt(getUtcTimeString());

        srmMetadata.setOriginIp(senderIp);
        srmMetadata.setSrmSource(SrmSource.RSU);
        srmMetadata.setRecordType(RecordType.srmTx);
        srmMetadata.setRecordGeneratedBy(GeneratedBy.OBU);
        srmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(srmMetadata, srmPayload), false);
    }

    public static String buildJsonPsmFromPacket(DatagramPacket packet){
        String senderIp = packet.getAddress().getHostAddress();
        int senderPort = packet.getPort();
        logger.debug("Packet received from {}:{}", senderIp, senderPort);

        // Create OdeMsgPayload and OdeLogMetadata objects and populate them
        OdeAsn1Payload psmPayload = getPayloadHexString(packet, UperUtil.SupportedMessageTypes.PSM);
        if (psmPayload == null)
            return null;
        OdePsmMetadata psmMetadata = new OdePsmMetadata(psmPayload);
        // Add header data for the decoding process
        psmMetadata.setOdeReceivedAt(getUtcTimeString());

        psmMetadata.setOriginIp(senderIp);
        psmMetadata.setPsmSource(PsmSource.RSU);
        psmMetadata.setRecordType(RecordType.psmTx);
        psmMetadata.setRecordGeneratedBy(GeneratedBy.UNKNOWN);
        psmMetadata.setSecurityResultCode(SecurityResultCode.success);

        return JsonUtils.toJson(new OdeAsn1Data(psmMetadata, psmPayload), false);
    }

    public static String getUtcTimeString(){
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return timestamp;
    }

   
}
