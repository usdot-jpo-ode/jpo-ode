package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public interface HasPosition extends OdeFilterable {
   J2735Position3D getPosition();
   boolean isWithinBounds(J2735GeoRegion region);
}
