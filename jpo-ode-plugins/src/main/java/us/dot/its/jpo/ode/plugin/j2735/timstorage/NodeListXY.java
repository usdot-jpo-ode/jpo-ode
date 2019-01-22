package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class NodeListXY extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private NodeSetXY nodes;
  private ComputedLane computed;
  
  public NodeSetXY getNodes() {
    return nodes;
  }
  public void setNodes(NodeSetXY nodes) {
    this.nodes = nodes;
  }
  public ComputedLane getComputed() {
    return computed;
  }
  public void setComputed(ComputedLane computed) {
    this.computed = computed;
  }
  
  
}
