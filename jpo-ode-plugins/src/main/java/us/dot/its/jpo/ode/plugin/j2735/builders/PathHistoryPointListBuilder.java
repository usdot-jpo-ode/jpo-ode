package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class PathHistoryPointListBuilder {

   private PathHistoryPointListBuilder() {
      throw new UnsupportedOperationException();
   }

   public static List<J2735PathHistoryPoint> genericPathHistoryPointList(JsonNode crumbData) {

      List<J2735PathHistoryPoint> phpl = new ArrayList<>();

      JsonNode php = crumbData.get("PathHistoryPoint");
      if (php.isArray()) {
         Iterator<JsonNode> iter = php.elements();
   
         while (iter.hasNext() && phpl.size() < 23) {
            phpl.add(PathHistoryPointBuilder.genericPathHistoryPoint(iter.next()));
         }
      } else {
         phpl.add(PathHistoryPointBuilder.genericPathHistoryPoint(php));
      }
      return phpl;
   }
}
