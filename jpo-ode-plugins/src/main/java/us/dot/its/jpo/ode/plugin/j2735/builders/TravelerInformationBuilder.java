package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.nio.ByteOrder;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class TravelerInformationBuilder {

   private TravelerInformationBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735TravelerInformationMessage genericTim(JsonNode asnTim) {
      J2735TravelerInformationMessage genericTim = new J2735TravelerInformationMessage();

      genericTim.setMsgCnt(asnTim.get("msgCnt").asInt());

      // TODO - Pure J2735 TIMs only contain time offset from an unknown year
      // Instead, time must be extracted from log file metadata
      genericTim.setTimeStamp(DateTimeUtils.now());

      byte[] packetIDbytes = CodecUtils.fromHex(asnTim.get("packetID").asText());

      genericTim.setPacketID(CodecUtils.bytesToInt(packetIDbytes, 0, packetIDbytes.length, ByteOrder.BIG_ENDIAN));

      if (asnTim.get("urlB") != null) {
         genericTim.setUrlB(asnTim.get("urlB").asText());
      }

      // TODO - the rest of the message translation
      genericTim.setAsnDataFrames(asnTim.get("dataFrames"));

      return genericTim;
   }

}
