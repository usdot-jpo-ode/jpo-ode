package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.TimeToLive;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.util.CodecUtils;

public class DotWarehouseData extends OdeData
      implements HasPosition {

   private static final long serialVersionUID = 2228128081854583187L;

   public enum OdeTimeToLive {
      minute, halfHour, day, week, month, year
   }

   private J2735Position3D centerPosition;
   private String groupID;
   private OdeTimeToLive timeToLive;
   private J2735GeoRegion serviceRegion;

   public DotWarehouseData() {
      super();
   }

   public DotWarehouseData(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public DotWarehouseData(String serialId) {
      super(serialId);
   }

   public DotWarehouseData(String serialId, GroupID groupID, TimeToLive timeToLive,
         J2735GeoRegion serviceRegion) {
      super(serialId);
      if (groupID != null)
         setGroupId(CodecUtils.toHex(groupID.byteArrayValue()));

      if (timeToLive != null)
         setTimeToLive(OdeTimeToLive.valueOf(timeToLive.name()));
      
      if (serviceRegion != null) {
         setServiceRegion(serviceRegion);
      
         setCenterPosition(getServiceRegion().getCenterPosition());
      }
      
   }

   public J2735Position3D getCenterPosition() {
      return centerPosition;
   }

   public void setCenterPosition(J2735Position3D centerPosition) {
      this.centerPosition = centerPosition;
   }

   public String getGroupId() {
      return groupID;
   }

   public void setGroupId(String groupID) {
      this.groupID = groupID;
   }

   public OdeTimeToLive getTimeToLive() {
      return timeToLive;
   }

   public void setTimeToLive(OdeTimeToLive timeToLive) {
      this.timeToLive = timeToLive;
   }

   public J2735GeoRegion getServiceRegion() {
      return serviceRegion;
   }

   public void setServiceRegion(J2735GeoRegion serviceRegion) {
      this.serviceRegion = serviceRegion;
   }

   @Override
   public J2735Position3D getPosition() {
      return getCenterPosition();
   }

   @Override
   public boolean isWithinBounds(J2735GeoRegion region) {
      return region.contains(centerPosition);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((centerPosition == null) ? 0 : centerPosition.hashCode());
      result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
      result = prime * result
            + ((serviceRegion == null) ? 0 : serviceRegion.hashCode());
      result = prime * result
            + ((timeToLive == null) ? 0 : timeToLive.hashCode());
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
      DotWarehouseData other = (DotWarehouseData) obj;
      if (centerPosition == null) {
         if (other.centerPosition != null)
            return false;
      } else if (!centerPosition.equals(other.centerPosition))
         return false;
      if (groupID == null) {
         if (other.groupID != null)
            return false;
      } else if (!groupID.equals(other.groupID))
         return false;
      if (serviceRegion == null) {
         if (other.serviceRegion != null)
            return false;
      } else if (!serviceRegion.equals(other.serviceRegion))
         return false;
      if (timeToLive != other.timeToLive)
         return false;
      return true;
   }


}
