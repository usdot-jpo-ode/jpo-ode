package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.asn.OdeGeoRegion;
import us.dot.its.jpo.ode.asn.OdePosition3D;

public interface HasPosition extends OdeFilterable {
   OdePosition3D getPosition();
   boolean isWithinBounds(OdeGeoRegion region); /* {
   *** SAMPLE CODE ***
      return GeoUtils.isPositionWithnBounds(getPosition(), region);
   }*/
}
