package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class ShapePointSet extends Asn1Object {
  private static final long serialVersionUID = 1L;
  
  private Position anchor;
  private int laneWidth;
  private DirectionOfUse directionality;
  private NodeListXY nodeList;
  
  public Position getAnchor() {
    return anchor;
  }
  public void setAnchor(Position anchor) {
    this.anchor = anchor;
  }
  public int getLaneWidth() {
    return laneWidth;
  }
  public void setLaneWidth(int laneWidth) {
    this.laneWidth = laneWidth;
  }
  public DirectionOfUse getDirectionality() {
    return directionality;
  }
  public void setDirectionality(DirectionOfUse directionality) {
    this.directionality = directionality;
  }
  public NodeListXY getNodeList() {
    return nodeList;
  }
  public void setNodeList(NodeListXY nodeList) {
    this.nodeList = nodeList;
  }

}
