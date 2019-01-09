package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class NodeSetXY extends Asn1Object {
  private static final long serialVersionUID = 1L;

  @JsonProperty("NodeXY")
  private NodeXY[] NodeXY;

  public NodeXY[] getNodeXY() {
    return NodeXY;
  }

  public void setNodeXY(NodeXY[] nodeXY) {
    this.NodeXY = nodeXY;
  }
  
  
}
