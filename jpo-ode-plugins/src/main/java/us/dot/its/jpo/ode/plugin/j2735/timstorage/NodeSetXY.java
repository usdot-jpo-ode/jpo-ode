package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class NodeSetXY extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private NodeXY[] NodeXY;
  
  @JsonProperty("NodeXY")
  public NodeXY[] getNodeXY() {
    return NodeXY;
  }

  public void setNodeXY(NodeXY[] nodeXY) {
    this.NodeXY = nodeXY;
  }
  
  
}
