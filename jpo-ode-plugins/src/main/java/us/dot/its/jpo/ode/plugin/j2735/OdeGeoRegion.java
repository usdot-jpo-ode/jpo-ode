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
package us.dot.its.jpo.ode.plugin.j2735;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

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
