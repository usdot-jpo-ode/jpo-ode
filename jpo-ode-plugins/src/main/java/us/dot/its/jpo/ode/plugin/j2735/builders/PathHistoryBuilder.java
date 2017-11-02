package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistory;

public class PathHistoryBuilder {

   private PathHistoryBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735PathHistory genericPathHistory(JsonNode pathHistory) {
      J2735PathHistory ph = new J2735PathHistory();

      // Required element
      ph.setCrumbData(PathHistoryPointListBuilder.genericPathHistoryPointList(pathHistory.get("crumbData")));

      // Optional elements
      JsonNode currGNSSstatus = pathHistory.get("currGNSSstatus");
      if (currGNSSstatus != null) {
         ph.setCurrGNSSstatus(GNSSstatusBuilder.genericGNSSstatus(currGNSSstatus));
      }
      JsonNode initialPosition = pathHistory.get("initialPosition");
      if (initialPosition != null) {
         ph.setInitialPosition(FullPositionVectorBuilder.genericFullPositionVector(initialPosition));
      }

      return ph;
   }

}
