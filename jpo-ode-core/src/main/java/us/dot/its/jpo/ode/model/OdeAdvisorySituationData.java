package us.dot.its.jpo.ode.model;

import java.text.ParseException;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisorySituationData;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public final class OdeAdvisorySituationData extends J2735AdvisorySituationData
   implements HasTimestamp {

   private static final long serialVersionUID = -8570818421787304109L;

   private static Logger logger = LoggerFactory
         .getLogger(OdeAdvisorySituationData.class);

   @Override
   public ZonedDateTime getTimestamp() {
      try {
         if (advisoryDetails.getStartTime() == null ||
               advisoryDetails.getStopTime() == null) {
            return null;
         } else {
            ZonedDateTime startTime = DateTimeUtils.isoDateTime(advisoryDetails.getStartTime());
            ZonedDateTime endTime = DateTimeUtils.isoDateTime(advisoryDetails.getStopTime());
            return startTime.plusSeconds(endTime.toEpochSecond() - startTime.toEpochSecond());
         }
      } catch (ParseException e) {
         logger.error("Error getting timestamp: ", e);
      }

      return null;
   }

   @Override
   public boolean isOnTime(ZonedDateTime start, ZonedDateTime end) {
      return DateTimeUtils.isBetweenTimesInclusive(getTimestamp(),
            start, end);
   }

}
