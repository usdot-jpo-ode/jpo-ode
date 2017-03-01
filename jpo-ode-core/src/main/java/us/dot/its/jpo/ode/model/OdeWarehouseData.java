package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735WarehouseData;

public class OdeWarehouseData extends J2735WarehouseData implements HasPosition {

   private static final long serialVersionUID = 2228128081854583187L;

   @Override
   public J2735Position3D getPosition() {
      return getCenterPosition();
   }

   @Override
   public boolean isWithinBounds(J2735GeoRegion region) {
      return region.contains(centerPosition);
   }

}
