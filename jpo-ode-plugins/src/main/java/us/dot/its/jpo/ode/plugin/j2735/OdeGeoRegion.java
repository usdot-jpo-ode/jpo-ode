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

import java.awt.geom.Point2D;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeGeoRegion extends OdeObject{
   public class GeoRegionException extends Exception {

      private static final long serialVersionUID = 1L;

      public GeoRegionException(String string) {
         super(string);
      }

   }

   private static final long serialVersionUID = 6646494196808253598L;

   private OdePosition3D nwCorner;
   private OdePosition3D seCorner;

   public OdeGeoRegion() {
      super();
   }

   public OdeGeoRegion(OdePosition3D nwCorner, OdePosition3D seCorner) {
      super();
      this.nwCorner = nwCorner;
      this.seCorner = seCorner;
   }

   public OdeGeoRegion(String serviceRegion) throws GeoRegionException {
      String[] region = serviceRegion.split("[, ] *");
      if (region != null && region.length == 4) {
         nwCorner = new OdePosition3D(
               BigDecimal.valueOf(Double.parseDouble(region[0])), 
               BigDecimal.valueOf(Double.parseDouble(region[1])), null);
         
         seCorner = new OdePosition3D(
               BigDecimal.valueOf(Double.parseDouble(region[2])), 
               BigDecimal.valueOf(Double.parseDouble(region[3])), null);
      } else {
         throw new GeoRegionException("Invalid service.region configuration.");
      }
   }

   public OdePosition3D getNwCorner() {
      return nwCorner;
   }

   public OdeGeoRegion setNwCorner(OdePosition3D nwCorner) {
      this.nwCorner = nwCorner;
      return this;
   }

   public OdePosition3D getSeCorner() {
      return seCorner;
   }

   public OdeGeoRegion setSeCorner(OdePosition3D seCorner) {
      this.seCorner = seCorner;
      return this;
   }

   @JsonIgnore
   public OdePosition3D getCenterPosition() {
      Point2D nw = new Point2D.Double(
            nwCorner.getLongitude().doubleValue(), 
            nwCorner.getLatitude().doubleValue());
      
      Point2D se = new Point2D.Double(
            seCorner.getLongitude().doubleValue(), 
            seCorner.getLatitude().doubleValue());

      Point2D midPoint = new Point2D.Double(
            nw.getX() + (se.getX() - nw.getX()) / 2,
            se.getY() + (nw.getY() - se.getY()) / 2);
      
      if (nwCorner.getElevation() != null && seCorner.getElevation() != null) {
         double minElev = Math.min(nwCorner.getElevation().doubleValue(), seCorner.getElevation().doubleValue());
         double diffElev = Math.abs(nwCorner.getElevation().doubleValue() - seCorner.getElevation().doubleValue());
         return new OdePosition3D(
               BigDecimal.valueOf(midPoint.getY()), 
               BigDecimal.valueOf(midPoint.getX()), 
               BigDecimal.valueOf(minElev + diffElev/2));
      } else {
         return new OdePosition3D(
               BigDecimal.valueOf(midPoint.getY()), 
               BigDecimal.valueOf(midPoint.getX()), 
               null);
      }
   }
   

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((nwCorner == null) ? 0 : nwCorner.hashCode());
      result = prime * result + ((seCorner == null) ? 0 : seCorner.hashCode());
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
      OdeGeoRegion other = (OdeGeoRegion) obj;
      if (nwCorner == null) {
         if (other.nwCorner != null)
            return false;
      } else if (!nwCorner.equals(other.nwCorner))
         return false;
      if (seCorner == null) {
         if (other.seCorner != null)
            return false;
      } else if (!seCorner.equals(other.seCorner))
         return false;
      return true;
   }

   public boolean contains(OdePosition3D pos) {
      if (pos == null)
         return false;
      
      OdePosition3D nw = this.getNwCorner();
      OdePosition3D se = this.getSeCorner();
      
      if (nw == null || nw.getLatitude() == null  
            || pos.getLatitude().doubleValue() > nw.getLatitude().doubleValue())
         return false;
      if (nw.getLongitude() == null 
            || pos.getLongitude().doubleValue() < nw.getLongitude().doubleValue())
         return false;
      if (se == null || se.getLatitude() == null 
            || pos.getLatitude().doubleValue() < se.getLatitude().doubleValue())
         return false;
      if (se.getLongitude() == null 
            || pos.getLongitude().doubleValue() > se.getLongitude().doubleValue())
         return false;
      
      return true;
   }

   public boolean contains(OdeGeoRegion requestRegion) {
      return contains(requestRegion.getNwCorner())
            && contains(requestRegion.getSeCorner());
   }

}
