package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class TravelerInformationFromAsnToHumanConverter {

   private TravelerInformationFromAsnToHumanConverter() {
      throw new UnsupportedOperationException();
   }

   public static J2735TravelerInformationMessage genericTim(JsonNode asnTim) {
      J2735TravelerInformationMessage genericTim = new J2735TravelerInformationMessage();

      genericTim.setMsgCnt(asnTim.get("msgCnt").asInt());

      // TODO - Pure J2735 TIMs only contain time offset from an unknown year
      // Instead, time must be extracted from log file metadata
      genericTim.setTimeStamp(DateTimeUtils.now());

      genericTim.setPacketID(asnTim.get("packetID").asText());

      if (asnTim.get("urlB") != null) {
         genericTim.setUrlB(asnTim.get("urlB").asText());
      }

      // TODO - the rest of the message translation
      genericTim.setAsnDataFrames(asnTim.get("dataFrames"));

      return genericTim;
   }

}
