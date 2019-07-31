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
package us.dot.its.jpo.ode.model;

import java.math.BigDecimal;

public final class OdeAggregateData extends OdeData implements HasKey {

   private static final long serialVersionUID = 1595406822366147861L;

   private String     key;
   private Long       count;
   private BigDecimal minSpeed;
   private BigDecimal avgSpeed;
   private BigDecimal maxSpeed;

   public OdeAggregateData() {
      super();
   }

   public OdeAggregateData(String key, Long count, BigDecimal minSpeed, BigDecimal avgSpeed, BigDecimal maxSpeed) {
    super();
    this.key = key;
    this.count = count;
    this.minSpeed = minSpeed;
    this.avgSpeed = avgSpeed;
    this.maxSpeed = maxSpeed;
}

@Override
   public String getKey() {
      return key;
   }

   public OdeAggregateData setKey(String key) {
      this.key = key;
      return this;
   }

   public Long getCount() {
      return count;
   }

   public OdeAggregateData setCount(Long count) {
      this.count = count;
      return this;
   }

   public BigDecimal getMinSpeed() {
      return minSpeed;
   }

   public OdeAggregateData setMinSpeed(BigDecimal minSpeed) {
      this.minSpeed = minSpeed;
      return this;
   }

   public BigDecimal getAvgSpeed() {
      return avgSpeed;
   }

   public OdeAggregateData setAvgSpeed(BigDecimal avgSpeed) {
      this.avgSpeed = avgSpeed;
      return this;
   }

   public BigDecimal getMaxSpeed() {
      return maxSpeed;
   }

   public void setMaxSpeed(BigDecimal maxSpeed) {
      this.maxSpeed = maxSpeed;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((avgSpeed == null) ? 0 : avgSpeed.hashCode());
      result = prime * result + ((count == null) ? 0 : count.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((maxSpeed == null) ? 0 : maxSpeed.hashCode());
      result = prime * result + ((minSpeed == null) ? 0 : minSpeed.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeAggregateData other = (OdeAggregateData) obj;
      if (avgSpeed == null) {
         if (other.avgSpeed != null)
            return false;
      } else if (!avgSpeed.equals(other.avgSpeed))
         return false;
      if (count == null) {
         if (other.count != null)
            return false;
      } else if (!count.equals(other.count))
         return false;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (maxSpeed == null) {
         if (other.maxSpeed != null)
            return false;
      } else if (!maxSpeed.equals(other.maxSpeed))
         return false;
      if (minSpeed == null) {
         if (other.minSpeed != null)
            return false;
      } else if (!minSpeed.equals(other.minSpeed))
         return false;
      return true;
   }

   
}
