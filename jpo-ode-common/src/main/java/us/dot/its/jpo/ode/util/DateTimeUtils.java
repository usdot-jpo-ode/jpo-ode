/*=============================================================================
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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for operations related to date and time, primarily using ZonedDateTime and ISO-8601 formats.
 */
public class DateTimeUtils {

  private static Clock clock = Clock.systemUTC();

  private DateTimeUtils() {
  }

  /**
   * Sets a new clock object to be used within the DateTimeUtils class and returns the previous clock.
   * The method is intended only for use within unit tests. Ideally, this method wouldn't exist, but there is a tight coupling
   * between the DateTimeUtils class and the {@link us.dot.its.jpo.ode.model.OdeObject} model creation. When using this method for testing,
   * remember to call this `setClock` method with the previous clock object at the end of your test. This will keep the tests from interfering
   * with each other.
   *
   * @param clock the new Clock object to be used
   *
   * @return the previously set Clock object
   */
  public static Clock setClock(Clock clock) {
    var previousClock = DateTimeUtils.clock;
    DateTimeUtils.clock = clock;
    return previousClock;
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

  public static ZonedDateTime isoDateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millisec) {
    return ZonedDateTime.of(year, month, dayOfMonth, hourOfDay, minute, second, millisec * 1000000, ZoneOffset.UTC);
  }

  public static ZonedDateTime isoDateTime(String s) {
    return ZonedDateTime.parse(s);
  }

  public static ZonedDateTime isoDateTime(long epochMillis) {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.of("UTC"));
  }

  public static long difference(ZonedDateTime t1, ZonedDateTime t2) {
    return t2.toInstant().toEpochMilli() - t1.toInstant().toEpochMilli();
  }
}
