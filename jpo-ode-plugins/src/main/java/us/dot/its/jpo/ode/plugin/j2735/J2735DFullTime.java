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
package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735DFullTime extends Asn1Object {

   private static final long serialVersionUID = -8184240048723364037L;

   private Integer year;
   private Integer month;
   private Integer day;
   private Integer hour;
   private Integer minute;
   private Integer second;
   private Integer offset;
   
   public Integer getDay() {
      return day;
   }
   public void setDay(Integer day) {
      this.day = day;
   }
   public Integer getHour() {
      return hour;
   }
   public void setHour(Integer hour) {
      this.hour = hour;
   }
   public Integer getMinute() {
      return minute;
   }
   public void setMinute(Integer minute) {
      this.minute = minute;
   }
   public Integer getMonth() {
      return month;
   }
   public void setMonth(Integer month) {
      this.month = month;
   }
   public Integer getOffset() {
      return offset;
   }
   public void setOffset(Integer offset) {
      this.offset = offset;
   }
   public Integer getSecond() {
      return second;
   }
   public void setSecond(Integer second) {
      this.second = second;
   }
   public Integer getYear() {
      return year;
   }
   public void setYear(Integer year) {
      this.year = year;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((day == null) ? 0 : day.hashCode());
      result = prime * result + ((hour == null) ? 0 : hour.hashCode());
      result = prime * result + ((minute == null) ? 0 : minute.hashCode());
      result = prime * result + ((month == null) ? 0 : month.hashCode());
      result = prime * result + ((offset == null) ? 0 : offset.hashCode());
      result = prime * result + ((second == null) ? 0 : second.hashCode());
      result = prime * result + ((year == null) ? 0 : year.hashCode());
      return result;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      J2735DFullTime other = (J2735DFullTime) obj;
      if (day == null) {
         if (other.day != null)
            return false;
      } else if (!day.equals(other.day))
         return false;
      if (hour == null) {
         if (other.hour != null)
            return false;
      } else if (!hour.equals(other.hour))
         return false;
      if (minute == null) {
         if (other.minute != null)
            return false;
      } else if (!minute.equals(other.minute))
         return false;
      if (month == null) {
         if (other.month != null)
            return false;
      } else if (!month.equals(other.month))
         return false;
      if (offset == null) {
         if (other.offset != null)
            return false;
      } else if (!offset.equals(other.offset))
         return false;
      if (second == null) {
         if (other.second != null)
            return false;
      } else if (!second.equals(other.second))
         return false;
      if (year == null) {
         if (other.year != null)
            return false;
      } else if (!year.equals(other.year))
         return false;
      return true;
   }

}
