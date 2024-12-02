package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class ComputedLane extends Asn1Object {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private int referenceLaneId;
  private OffsetAxis offsetXaxis;
  private OffsetAxis offsetYaxis;
  private int rotateXY;
  private int scaleXaxis;
  private int scaleYaxis;
  public int getReferenceLaneId() {
    return referenceLaneId;
  }
  public void setReferenceLaneId(int referenceLaneId) {
    this.referenceLaneId = referenceLaneId;
  }
  public OffsetAxis getOffsetXaxis() {
    return offsetXaxis;
  }
  public void setOffsetXaxis(OffsetAxis offsetXaxis) {
    this.offsetXaxis = offsetXaxis;
  }
  public OffsetAxis getOffsetYaxis() {
    return offsetYaxis;
  }
  public void setOffsetYaxis(OffsetAxis offsetYaxis) {
    this.offsetYaxis = offsetYaxis;
  }
  public int getRotateXY() {
    return rotateXY;
  }
  public void setRotateXY(int rotateXY) {
    this.rotateXY = rotateXY;
  }
  public int getScaleXaxis() {
    return scaleXaxis;
  }
  public void setScaleXaxis(int scaleXaxis) {
    this.scaleXaxis = scaleXaxis;
  }
  public int getScaleYaxis() {
    return scaleYaxis;
  }
  public void setScaleYaxis(int scaleYaxis) {
    this.scaleYaxis = scaleYaxis;
  }
  

}
