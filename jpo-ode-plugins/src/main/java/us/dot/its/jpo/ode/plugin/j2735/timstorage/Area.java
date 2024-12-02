package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class Area extends Asn1Object {
  private static final long serialVersionUID = 1L;
  
  private ShapePointSet shapepoint;
  private Circle circle;
  private RegionPointSet regionPoint;
  
  public ShapePointSet getShapepoint() {
    return shapepoint;
  }
  public void setShapepoint(ShapePointSet shapepoint) {
    this.shapepoint = shapepoint;
  }
  public Circle getCircle() {
    return circle;
  }
  public void setCircle(Circle circle) {
    this.circle = circle;
  }
  public RegionPointSet getRegionPoint() {
    return regionPoint;
  }
  public void setRegionPoint(RegionPointSet regionPoint) {
    this.regionPoint = regionPoint;
  }

}
