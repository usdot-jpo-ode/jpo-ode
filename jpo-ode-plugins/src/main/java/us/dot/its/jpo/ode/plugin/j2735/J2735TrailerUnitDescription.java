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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerUnitDescription extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private Boolean isDolly;
   private Integer width;
   private Integer length;
   private BigDecimal height;
   private Integer mass;
   private J2735BumperHeights bumperHeights;
   private BigDecimal centerOfGravity;
   private J2735PivotPointDescription frontPivot;
   private J2735PivotPointDescription rearPivot;
   private BigDecimal rearWheelOffset;
   private J2735Node_XY positionOffset;
   private BigDecimal elevationOffset;
   private List<J2735TrailerHistoryPoint> crumbData = new ArrayList<>();

   public J2735BumperHeights getBumperHeights() {
      return bumperHeights;
   }

   public void setBumperHeights(J2735BumperHeights bumperHeights) {
      this.bumperHeights = bumperHeights;
   }

   public BigDecimal getCenterOfGravity() {
      return centerOfGravity;
   }

   public void setCenterOfGravity(BigDecimal centerOfGravity) {
      this.centerOfGravity = centerOfGravity;
   }

   public List<J2735TrailerHistoryPoint> getCrumbData() {
      return crumbData;
   }

   public void setCrumbData(List<J2735TrailerHistoryPoint> crumbData) {
      this.crumbData = crumbData;
   }

   public BigDecimal getElevationOffset() {
      return elevationOffset;
   }

   public void setElevationOffset(BigDecimal elevationOffset) {
      this.elevationOffset = elevationOffset;
   }

   public J2735PivotPointDescription getFrontPivot() {
      return frontPivot;
   }

   public void setFrontPivot(J2735PivotPointDescription frontPivot) {
      this.frontPivot = frontPivot;
   }

   public BigDecimal getHeight() {
      return height;
   }

   public void setHeight(BigDecimal height) {
      this.height = height;
   }

   public Boolean getIsDolly() {
      return isDolly;
   }

   public void setIsDolly(Boolean isDolly) {
      this.isDolly = isDolly;
   }

   public Integer getLength() {
      return length;
   }

   public void setLength(Integer length) {
      this.length = length;
   }

   public Integer getMass() {
      return mass;
   }

   public void setMass(Integer mass) {
      this.mass = mass;
   }

   public J2735Node_XY getPositionOffset() {
      return positionOffset;
   }

   public void setPositionOffset(J2735Node_XY positionOffset) {
      this.positionOffset = positionOffset;
   }

   public J2735PivotPointDescription getRearPivot() {
      return rearPivot;
   }

   public void setRearPivot(J2735PivotPointDescription rearPivot) {
      this.rearPivot = rearPivot;
   }

   public BigDecimal getRearWheelOffset() {
      return rearWheelOffset;
   }

   public void setRearWheelOffset(BigDecimal rearWheelOffset) {
      this.rearWheelOffset = rearWheelOffset;
   }

   public Integer getWidth() {
      return width;
   }

   public void setWidth(Integer width) {
      this.width = width;
   }

}
