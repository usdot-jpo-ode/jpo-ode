/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.asn;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.bah.ode.asn.oss.dsrc.DDateTime;
import com.bah.ode.asn.oss.dsrc.DFullTime;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeDateTime extends OdeObject {

   private static final long serialVersionUID = 2660375064468606589L;

   private Integer year;
   private Integer month;
   private Integer day;
   private Integer hour;
   private Integer minute;
   private BigDecimal second;

   public OdeDateTime() {
   }

   public OdeDateTime(Integer year, Integer month, Integer day, Integer hour,
         Integer minute, BigDecimal second) {
      super();
      this.year = year;
      this.month = month;
      this.day = day;
      this.hour = hour;
      this.minute = minute;
      this.second = second;
   }

   public OdeDateTime(DDateTime dDateTime) {
      if (dDateTime.hasYear())
         setYear(dDateTime.getYear().intValue());
      
      if (dDateTime.hasMonth())
         setMonth(dDateTime.getMonth().intValue());
      
      if (dDateTime.hasDay())
         setDay(dDateTime.getDay().intValue());
      
      if (dDateTime.hasHour())
         setHour(dDateTime.getHour().intValue());
      
      if (dDateTime.hasMinute())
      setMinute(dDateTime.getMinute().intValue());
      
      if (dDateTime.hasSecond()) {
         /*
          * 7.36 Data Element: DE_DSecond Use: The DSRC style second is a simple
          * value consisting of integer values from zero to 61000 representing
          * the milliseconds within a minute. A leap second is represented by
          * the value range 60001 to 61000. The value of 65535 SHALL represent
          * an unknown value in the range of the minute, other values from 61001
          * to 65534 are reserved.
          */
         int millisecs = dDateTime.getSecond().intValue();
         if (millisecs <= 61000) {
            setSecond(BigDecimal.valueOf(dDateTime.getSecond().intValue(), 3));
         }
      }
   }

   public OdeDateTime(DFullTime dFullTime) {
      if (dFullTime != null) {
         setYear(dFullTime.getYear().intValue());
         setMonth(dFullTime.getMonth().intValue());
         setDay(dFullTime.getDay().intValue());
         setHour(dFullTime.getHour().intValue());
         setMinute(dFullTime.getMinute().intValue());
         setSecond(BigDecimal.ZERO);
      }
   }

   public OdeDateTime(String dateTime) throws ParseException {
      ZonedDateTime zdt = DateTimeUtils.isoDateTime(dateTime);
      setYear(zdt.getYear());
      setMonth(zdt.getMonthValue());
      setDay(zdt.getDayOfMonth());
      setHour(zdt.getHour());
      setMinute(zdt.getMinute());
      setSecond(BigDecimal.valueOf(zdt.getSecond() + (double)zdt.getNano()/1000000000D));
   }

   public ZonedDateTime getZonedDateTime() {
      if (second != null && second.intValue() != 0) {
         BigDecimal secFract = second.remainder(BigDecimal.valueOf(second.intValue()));
         BigDecimal millisec = secFract.multiply(BigDecimal.valueOf(1000));
         return DateTimeUtils.isoDateTime(getYear(), getMonth(),
               getDay(), getHour(), getMinute(), second.intValue(), millisec.intValue());
      } else {
         return DateTimeUtils.isoDateTime(getYear(), getMonth(),
               getDay(), getHour(), getMinute(),0, 0);
      }
   }

   public String getISODateTime() {
      return getZonedDateTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
   }

   public Integer getYear() {
      return year;
   }

   public OdeDateTime setYear(Integer year) {
      this.year = year;
      return this;
   }

   public Integer getMonth() {
      return month;
   }

   public OdeDateTime setMonth(Integer month) {
      this.month = month;
      return this;
   }

   public Integer getDay() {
      return day;
   }

   public OdeDateTime setDay(Integer day) {
      this.day = day;
      return this;
   }

   public Integer getHour() {
      return hour;
   }

   public OdeDateTime setHour(Integer hour) {
      this.hour = hour;
      return this;
   }

   public Integer getMinute() {
      return minute;
   }

   public OdeDateTime setMinute(Integer minute) {
      this.minute = minute;
      return this;
   }

   public BigDecimal getSecond() {
      return second;
   }

   public OdeDateTime setSecond(BigDecimal second) {
      this.second = second;
      return this;
   }

}
