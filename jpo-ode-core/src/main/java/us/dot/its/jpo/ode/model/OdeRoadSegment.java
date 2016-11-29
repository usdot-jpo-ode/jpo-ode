package us.dot.its.jpo.ode.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import us.dot.its.jpo.ode.util.GeoUtils;
import us.dot.its.jpo.ode.util.OdeGeoUtils;

public class OdeRoadSegment extends OdeObject {

   private static final long serialVersionUID = 3136451247998393604L;
   
   private String   id;
   private OdePoint startPoint;
   private OdePoint endPoint;
   private String   prevSegment;
   
   public Line2D toLine2D(OdeRoadSegment seg) {
      double dLat = seg.getStartPoint().getLatitude().doubleValue();
      double dLng = seg.getStartPoint().getLongitude().doubleValue();
      Point2D a = GeoUtils.latLngToMap(dLat , dLng );
      
      dLat = seg.getEndPoint().getLatitude().doubleValue();
      dLng = seg.getEndPoint().getLongitude().doubleValue();
      Point2D b = GeoUtils.latLngToMap(dLat , dLng );
      
      Line2D l = new Line2D.Double(a, b);
      return l;
   }
   
   public String getId() {
      return id;
   }
   public OdeRoadSegment setId(String id) {
      this.id = id;
      return this;
   }
   public OdePoint getStartPoint() {
      return startPoint;
   }
   public OdeRoadSegment setStartPoint(OdePoint startPoint) {
      this.startPoint = startPoint;
      return this;
   }
   public OdePoint getEndPoint() {
      return endPoint;
   }
   public OdeRoadSegment setEndPoint(OdePoint endPoint) {
      this.endPoint = endPoint;
      return this;
   }

   public String getPrevSegment() {
      return prevSegment;
   }
   public OdeRoadSegment setPrevSegment(String prevSegment) {
      this.prevSegment = prevSegment;
      return this;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result
            + ((prevSegment == null) ? 0 : prevSegment.hashCode());
      result = prime * result
            + ((startPoint == null) ? 0 : startPoint.hashCode());
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
      OdeRoadSegment other = (OdeRoadSegment) obj;
      if (endPoint == null) {
         if (other.endPoint != null)
            return false;
      } else if (!endPoint.equals(other.endPoint))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (prevSegment == null) {
         if (other.prevSegment != null)
            return false;
      } else if (!prevSegment.equals(other.prevSegment))
         return false;
      if (startPoint == null) {
         if (other.startPoint != null)
            return false;
      } else if (!startPoint.equals(other.startPoint))
         return false;
      return true;
   }
   
   
   

}
