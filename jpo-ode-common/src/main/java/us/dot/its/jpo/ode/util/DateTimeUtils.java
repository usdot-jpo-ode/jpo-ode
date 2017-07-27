package us.dot.its.jpo.ode.util;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {
    
    private DateTimeUtils() {}

   public static String now() {
      return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
   }
   
   public static String isoDateTime(ZonedDateTime zonedDateTime) {
      return zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
   }
   
   public static ZonedDateTime isoDateTime(int year, int month, int dayOfMonth, 
         int hourOfDay, int minute, int second, int millisec) {
      return ZonedDateTime.of(year, month, dayOfMonth, 
            hourOfDay, minute, second, millisec * 1000000, 
            ZoneOffset.UTC);
   }

   public static ZonedDateTime isoDateTime(String s) throws ParseException {
      return ZonedDateTime.parse(s);
   }

   public static ZonedDateTime isoDateTime(Date date) {
       return ZonedDateTime.from(date.toInstant());
    }
    
   public static boolean isBetweenTimesInclusive(
         ZonedDateTime dateTime,
         ZonedDateTime startDateTime,
         ZonedDateTime endDateTime) {
      
      if (dateTime == null)
         return true;
      
      if (startDateTime == null) {
         if (endDateTime == null) {// Both startDate and endDate are null, so it's false
            return true;
         } else {// We only have the endDate, so any dateTime not after the endDateTime is true
            return !dateTime.isAfter(endDateTime);
         }
      } else {
         if (endDateTime == null) {// We only have the startDateTime, so any dateTime not before the startDateTime is true
            return !dateTime.isBefore(startDateTime);
         } else {// We have both startDateTime and endDateTime, so any dateTime not before the startDate and not after endDateTime is true
            return !dateTime.isBefore(startDateTime) &&
                  !dateTime.isAfter(endDateTime);
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
