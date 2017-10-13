package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;

public class GNSSstatusBuilder {

   public enum GNSstatusNames {
      unavailable, isHealthy, isMonitored, baseStationType, aPDOPofUnder5, inViewOfUnder5, localCorrectionsPresent, networkCorrectionsPresent
   }

   private GNSSstatusBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735GNSSstatus genericGNSSstatus(JsonNode gnssStatus) {
      J2735GNSSstatus status = new J2735GNSSstatus();

      char[] gnsStatusBits = gnssStatus.asText().toCharArray();

      for (int i = 0; i < gnsStatusBits.length; i++) {
         String statusName = GNSstatusNames.values()[i].name();
         Boolean statusValue = (gnsStatusBits[i] == '1' ? true : false);
         status.put(statusName, statusValue);

      }
      return status;
   }

}
