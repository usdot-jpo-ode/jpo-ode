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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import junit.framework.TestCase;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class DateTimeUtilsTest extends TestCase {

    @Test
   public void testIsoDateTime() throws ParseException {
      ZonedDateTime expectedDate = ZonedDateTime.now(ZoneOffset.UTC);
      String sExpectedDate = DateTimeUtils.isoDateTime(expectedDate);
      String sdate = DateTimeUtils.isoDateTime(
            expectedDate.getYear(), 
            expectedDate.getMonthValue(), 
            expectedDate.getDayOfMonth(),
            expectedDate.getHour(),
            expectedDate.getMinute(),
            expectedDate.getSecond(),
            expectedDate.getNano()/1000000).format(DateTimeFormatter.ISO_INSTANT);
      
      assertEquals(sExpectedDate.substring(0, 18), sdate.substring(0, 18));
      ZonedDateTime date2 = DateTimeUtils.isoDateTime("2015-11-30T16:06:15.679Z");
      assertNotNull(date2);
   }
}
