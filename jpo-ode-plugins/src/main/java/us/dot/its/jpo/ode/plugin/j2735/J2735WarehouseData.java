package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735WarehouseData extends Asn1Object {

   private static final long serialVersionUID = 2228128081854583187L;

   public enum OdeTimeToLive {
      minute, halfHour, day, week, month, year
   }

   protected J2735Position3D centerPosition;
   protected String groupID;
   protected OdeTimeToLive timeToLive;
   protected J2735GeoRegion serviceRegion;

   public J2735WarehouseData() {
      super();
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((centerPosition == null) ? 0 : centerPosition.hashCode());
      result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
      result = prime * result + ((serviceRegion == null) ? 0 : serviceRegion.hashCode());
      result = prime * result + ((timeToLive == null) ? 0 : timeToLive.hashCode());
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
      J2735WarehouseData other = (J2735WarehouseData) obj;
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
