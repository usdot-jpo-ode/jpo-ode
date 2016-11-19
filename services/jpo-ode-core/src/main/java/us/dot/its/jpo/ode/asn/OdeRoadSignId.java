package us.dot.its.jpo.ode.asn;

import java.util.List;

import us.dot.its.jpo.ode.asn.OdeHeadingSlice.SliceMask;
import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
import us.dot.its.jpo.ode.model.OdeObject;

public class OdeRoadSignId extends OdeObject {

   private static final long serialVersionUID = 5729514080285088635L;
   
   public enum OdeMUTCDCode {
      none, regulatory, warning, maintenance, motoristService, guide, rec
   }
   
   private OdePosition3D position;
   private List<SliceMask> viewAngles;
   private OdeMUTCDCode mutcdCode;
   
   public OdeRoadSignId() {
      super();
   }
   
   public OdeRoadSignId(RoadSignID roadSignID) {
      if (roadSignID.position != null)
         setPosition(new OdePosition3D(roadSignID.position));
      if (roadSignID.viewAngle != null)
         setViewAngles(OdeHeadingSlice.SliceMask.getHeadingSlices(roadSignID.viewAngle));
      
      if (roadSignID.hasMutcdCode())
         setMutcdCode(OdeMUTCDCode.valueOf(roadSignID.getMutcdCode().name()));
   }

   public OdePosition3D getPosition() {
      return position;
   }
   public void setPosition(OdePosition3D position) {
      this.position = position;
   }
   public List<SliceMask> getViewAngles() {
      return viewAngles;
   }

   public void setViewAngles(List<SliceMask> viewAngles) {
      this.viewAngles = viewAngles;
   }

   public OdeMUTCDCode getMutcdCode() {
      return mutcdCode;
   }
   public void setMutcdCode(OdeMUTCDCode mutcdCode) {
      this.mutcdCode = mutcdCode;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((mutcdCode == null) ? 0 : mutcdCode.hashCode());
      result = prime * result + ((position == null) ? 0 : position.hashCode());
      result = prime * result
            + ((viewAngles == null) ? 0 : viewAngles.hashCode());
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
      OdeRoadSignId other = (OdeRoadSignId) obj;
      if (mutcdCode != other.mutcdCode)
         return false;
      if (position == null) {
         if (other.position != null)
            return false;
      } else if (!position.equals(other.position))
         return false;
      if (viewAngles == null) {
         if (other.viewAngles != null)
            return false;
      } else if (!viewAngles.equals(other.viewAngles))
         return false;
      return true;
   }
   
   
}
