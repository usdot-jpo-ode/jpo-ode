package us.dot.its.jpo.ode.asn;

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.TravelerInformation.DataFrames.Sequence_.Regions;

import us.dot.its.jpo.ode.model.OdeObject;

import com.bah.ode.asn.oss.dsrc.ValidRegion;

public class OdeValidRegion extends OdeObject {
   private static final long serialVersionUID = 5029639469363555078L;
   
   public enum OdeExtent {
      useInstantlyOnly, 
      useFor3meters, 
      useFor10meters, 
      useFor50meters, 
      useFor100meters, 
      useFor500meters, 
      useFor1000meters, 
      useFor5000meters, 
      useFor10000meters, 
      useFor50000meters, 
      useFor100000meters, 
      forever
   }

   private List<OdeHeadingSlice.SliceMask> directions;
   private OdeExtent extent;
   private OdeArea area;
   
   public OdeValidRegion(ValidRegion element) {
      if (element.area != null)
         setArea(new OdeArea(element.area));
      if (element.direction != null)
         setDirections(OdeHeadingSlice.SliceMask.getHeadingSlices(element.direction));
      if (element.hasExtent())
         setExtent(OdeExtent.valueOf(element.getExtent().name()));
   }
   public List<OdeHeadingSlice.SliceMask> getDirections() {
      return directions;
   }
   public void setDirections(List<OdeHeadingSlice.SliceMask> directions) {
      this.directions = directions;
   }
   public OdeExtent getExtent() {
      return extent;
   }
   public void setExtent(OdeExtent extent) {
      this.extent = extent;
   }
   public OdeArea getArea() {
      return area;
   }
   public void setArea(OdeArea area) {
      this.area = area;
   }
   
   public static ArrayList<OdeValidRegion> createList(Regions regions) {
      if (regions == null)
         return null;
      
      ArrayList<ValidRegion> elements = regions.elements;
      ArrayList<OdeValidRegion> vrs = new ArrayList<OdeValidRegion>();
      for (ValidRegion vr : elements) {
         if (vr != null)
            vrs.add(new OdeValidRegion(vr));
      }
      return vrs;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((area == null) ? 0 : area.hashCode());
      result = prime * result
            + ((directions == null) ? 0 : directions.hashCode());
      result = prime * result + ((extent == null) ? 0 : extent.hashCode());
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
      OdeValidRegion other = (OdeValidRegion) obj;
      if (area == null) {
         if (other.area != null)
            return false;
      } else if (!area.equals(other.area))
         return false;
      if (directions == null) {
         if (other.directions != null)
            return false;
      } else if (!directions.equals(other.directions))
         return false;
      if (extent != other.extent)
         return false;
      return true;
   }
   
   

}
