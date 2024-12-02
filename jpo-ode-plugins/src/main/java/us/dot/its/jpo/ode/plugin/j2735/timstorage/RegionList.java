package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class RegionList extends Asn1Object {
  private static final long serialVersionUID = 1L;
  
  private RegionOffsets[] nodeList;

  public RegionOffsets[] getNodeList() {
    return nodeList;
  }

  public void setNodeList(RegionOffsets[] nodeList) {
    this.nodeList = nodeList;
  }
  
  
}
