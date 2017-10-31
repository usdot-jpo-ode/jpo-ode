package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public interface HasPosition extends OdeFilterable {
   OdePosition3D getPosition();
   boolean isWithinBounds(OdeGeoRegion region);
}
