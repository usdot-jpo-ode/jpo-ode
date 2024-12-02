package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class ValidRegion extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private String direction;
  private Extent extent;
  private Area area;
  
  public String getDirection() {
    return direction;
  }
  public void setDirection(String direction) {
    this.direction = direction;
  }
  public Extent getExtent() {
    return extent;
  }
  public void setExtent(Extent extent) {
    this.extent = extent;
  }
  public Area getArea() {
    return area;
  }
  public void setArea(Area area) {
    this.area = area;
  }

}
