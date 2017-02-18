package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

public interface HasPosition extends OdeFilterable {
   J2735Position3D getPosition();
   boolean isWithinBounds(J2735GeoRegion region); /* {
   *** SAMPLE CODE ***
      return GeoUtils.isPositionWithnBounds(getPosition(), region);
   }*/
}
