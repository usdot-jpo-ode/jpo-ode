package us.dot.its.jpo.ode.model;

public final class OdeVehicleCount extends OdeData {

   private static final long serialVersionUID = 1595406822366147861L;

   private Long       count;

   public OdeVehicleCount() {
      super();
   }

   public OdeVehicleCount(String serialId) {
      super(serialId);
   }

   public OdeVehicleCount(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public Long getCount() {
      return count;
   }

   public OdeVehicleCount setCount(Long count) {
      this.count = count;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((count == null) ? 0 : count.hashCode());
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
      OdeVehicleCount other = (OdeVehicleCount) obj;
      if (count == null) {
         if (other.count != null)
            return false;
      } else if (!count.equals(other.count))
         return false;
      return true;
   }

   
}
