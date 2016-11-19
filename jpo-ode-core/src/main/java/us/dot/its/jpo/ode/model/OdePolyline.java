package us.dot.its.jpo.ode.model;

import java.util.List;

public class OdePolyline extends OdeObject {

   private static final long serialVersionUID = 5134542729829508388L;

   private List<OdeRoadSegment> segments;
   
   public OdePolyline addSegment(OdeRoadSegment segment) {
      updateStartPoint(segment);
      segments.add(segment);
      return this;
   }

   public void updateStartPoint(OdeRoadSegment segment) {
      String prevId = segment.getPrevSegment();
      if (prevId != null) {
         OdeRoadSegment prev = findById(prevId);
         if (prev != null)
            segment.setStartPoint(prev.getEndPoint());
      }
   }

   public OdeRoadSegment findById(String id) {
      OdeRoadSegment found = null;
      for (OdeRoadSegment seg : segments) {
         if (seg.getId().equals(id)) {
            found = seg;
            break;
         }
      }
      return found;
   }
   
   public void updateAllStartPoints() {
      for (OdeRoadSegment segment : segments) {
         updateStartPoint(segment);
      }
      
   }
   
   public List<OdeRoadSegment> getSegments() {
      return segments;
   }

   public OdePolyline setSegments(List<OdeRoadSegment> segments) {
      this.segments = segments;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((segments == null) ? 0 : segments.hashCode());
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
      OdePolyline other = (OdePolyline) obj;
      if (segments == null) {
         if (other.segments != null)
            return false;
      } else if (!segments.equals(other.segments))
         return false;
      return true;
   }

   
}
