package us.dot.its.jpo.ode.model;

import java.time.ZonedDateTime;

public interface HasTimestamp extends OdeFilterable {
   ZonedDateTime getTimestamp();
   boolean isOnTime(ZonedDateTime start, ZonedDateTime end);/* {
   *** SAMPLE CODE ***
      return DateTimeUtils.isBetweenTimesInclusive(getTimestamp(),
            start, end);
   }*/
}
