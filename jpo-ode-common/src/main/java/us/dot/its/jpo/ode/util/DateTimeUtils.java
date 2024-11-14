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
package us.dot.its.jpo.ode.util;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {

   private static Clock clock = Clock.systemUTC();

   private DateTimeUtils() {
   }

    public static void setClock(Clock clock) {
        DateTimeUtils.clock = clock;
    }

   public static String now() {
      return nowZDT().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
   }

   public static ZonedDateTime nowZDT() {
      return ZonedDateTime.now(clock.withZone(ZoneId.of("UTC")));
   }

   public static String isoDateTime(ZonedDateTime zonedDateTime) {
      return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
   }

   public static ZonedDateTime
         isoDateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millisec) {
      return ZonedDateTime.of(year, month, dayOfMonth, hourOfDay, minute, second, millisec * 1000000, ZoneOffset.UTC);
   }

   public static ZonedDateTime isoDateTime(String s) {
      return ZonedDateTime.parse(s);
   }

   public static ZonedDateTime isoDateTime(Date date) {
      return ZonedDateTime.from(date.toInstant().atZone(ZoneId.of("UTC")));
   }

   public static ZonedDateTime isoDateTime(long epochMillis) {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("UTC"));
   }

   public static boolean
         isBetweenTimesInclusive(ZonedDateTime dateTime, ZonedDateTime startDateTime, ZonedDateTime endDateTime) {

      if (dateTime == null)
         return true;

      if (startDateTime == null) {
         if (endDateTime == null) {// Both startDate and endDate are null, so
                                   // it's false
            return true;
         } else {// We only have the endDate, so any dateTime not after the
                 // endDateTime is true
            return !dateTime.isAfter(endDateTime);
         }
      } else {
         if (endDateTime == null) {// We only have the startDateTime, so any
                                   // dateTime not before the startDateTime is
                                   // true
            return !dateTime.isBefore(startDateTime);
         } else {// We have both startDateTime and endDateTime, so any dateTime
                 // not before the startDate and not after endDateTime is true
            return !dateTime.isBefore(startDateTime) && !dateTime.isAfter(endDateTime);
         }
      }
   }

   public static long difference(ZonedDateTime t1, ZonedDateTime t2) {
      return t2.toInstant().toEpochMilli() - t1.toInstant().toEpochMilli();
   }

   public static Long elapsedTime(ZonedDateTime zonedDateTime) {
      return difference(zonedDateTime, ZonedDateTime.now());
   }
}
