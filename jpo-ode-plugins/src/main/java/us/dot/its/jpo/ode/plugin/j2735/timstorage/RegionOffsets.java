package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class RegionOffsets extends Asn1Object {
  private static final long serialVersionUID = 1L;
  
  private int xOffset;
  private int yOffset;
  private int zOffset;
  public int getxOffset() {
    return xOffset;
  }
  public void setxOffset(int xOffset) {
    this.xOffset = xOffset;
  }
  public int getyOffset() {
    return yOffset;
  }
  public void setyOffset(int yOffset) {
    this.yOffset = yOffset;
  }
  public int getzOffset() {
    return zOffset;
  }
  public void setzOffset(int zOffset) {
    this.zOffset = zOffset;
  }
  
  
}
