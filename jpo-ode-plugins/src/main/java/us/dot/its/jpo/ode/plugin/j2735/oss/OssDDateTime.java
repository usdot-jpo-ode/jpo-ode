package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.plugin.j2735.J2735DDateTime;

public class OssDDateTime {

    private static final long YEAR_LOWER_BOUND = 0L;
    private static final long YEAR_UPPER_BOUND = 4095L;
    private static final long MONTH_LOWER_BOUND = 0L;
    private static final long MONTH_UPPER_BOUND = 12L;
    private static final long DAY_LOWER_BOUND = 0L;
    private static final long DAY_UPPER_BOUND = 31L;
    private static final long HOUR_LOWER_BOUND = 0L;
    private static final long HOUR_UPPER_BOUND = 31L;
    private static final long MINUTE_LOWER_BOUND = 0L;
    private static final long MINUTE_UPPER_BOUND = 60L;
    private static final long SECOND_LOWER_BOUND = 0L;
    private static final long SECOND_UPPER_BOUND = 65535L;
    private static final long OFFSET_LOWER_BOUND = -840L;
    private static final long OFFSET_UPPER_BOUND = 840L;

    private OssDDateTime() {
       throw new UnsupportedOperationException();
    }

    public static J2735DDateTime genericDDateTime(DDateTime dateTime) {

        // Bounds checks
        if (dateTime.year.longValue() < YEAR_LOWER_BOUND || dateTime.year.longValue() > YEAR_UPPER_BOUND) {
            throw new IllegalArgumentException("Year value out of bounds [0..4095]");
        }
        if (dateTime.month.longValue() < MONTH_LOWER_BOUND || dateTime.month.longValue() > MONTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Month value out of bounds [0..12]");
        }
        if (dateTime.day.longValue() < DAY_LOWER_BOUND || dateTime.day.longValue() > DAY_UPPER_BOUND) {
            throw new IllegalArgumentException("Day value out of bounds [0..31]");
        }
        if (dateTime.hour.longValue() < HOUR_LOWER_BOUND || dateTime.hour.longValue() > HOUR_UPPER_BOUND) {
            throw new IllegalArgumentException("Hour value out of bounds [0..31]");
        }
        if (dateTime.minute.longValue() < MINUTE_LOWER_BOUND || dateTime.minute.longValue() > MINUTE_UPPER_BOUND) {
            throw new IllegalArgumentException("Minute value out of bounds [0..60]");
        }
        if (dateTime.second.longValue() < SECOND_LOWER_BOUND || dateTime.second.longValue() > SECOND_UPPER_BOUND) {
            throw new IllegalArgumentException("Second value out of bounds [0..65535]");
        }
        if (dateTime.offset.longValue() < OFFSET_LOWER_BOUND || dateTime.offset.longValue() > OFFSET_UPPER_BOUND) {
            throw new IllegalArgumentException("Offset value out of bounds [-840..840]");
        }

        // DDateTime creation
        J2735DDateTime dt = new J2735DDateTime();

        if (dateTime.year.intValue() != 0) {
            dt.setYear(dateTime.year.intValue());
        }

        if (dateTime.month.intValue() != 0) {
            dt.setMonth(dateTime.month.intValue());
        }

        if (dateTime.day.intValue() != 0) {
            dt.setDay(dateTime.day.intValue());
        }

        if (dateTime.hour.intValue() != 31) {
            dt.setHour(dateTime.hour.intValue());
        }

        if (dateTime.minute.intValue() != 60) {
            dt.setMinute(dateTime.minute.intValue());
        }

        if (dateTime.second.intValue() != 65535) {
            dt.setSecond(dateTime.second.intValue());
        }

        dt.setOffset(dateTime.offset.intValue());

        return dt;
    }

}
