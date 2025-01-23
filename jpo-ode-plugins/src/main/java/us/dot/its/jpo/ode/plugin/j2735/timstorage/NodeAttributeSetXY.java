package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * NodeAttributeSetXY.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class NodeAttributeSetXY extends Asn1Object {
  private static final long serialVersionUID = 1L;

  //  localNode NodeAttributeXYList OPTIONAL,
  //  -- Attribute states which pertain to this node point
  //  disabled SegmentAttributeXYList OPTIONAL,
  //  -- Attribute states which are disabled at this node point
  //  enabled SegmentAttributeXYList OPTIONAL,
  //  -- Attribute states which are enabled at this node point
  //  -- and which remain enabled until disabled or the lane ends
  //  data LaneDataAttributeList OPTIONAL,
  //  -- Attributes which require an additional data values
  //  -- some of these are local to the node point, while others
  //  -- persist with the provided values until changed
  //  -- and this is indicated in each entry
  //  dWidth Offset-B10 OPTIONAL,
  //  -- A value added to the current lane width
  //  -- at this node and from this node onwards, in 1cm steps
  //  -- lane width between nodes are a linear taper between pts
  //  -- the value of zero shall not be sent here
  //  dElevation Offset-B10 OPTIONAL,
  //  -- A value added to the current Elevation
  //  -- at this node from this node onwards, in 10cm steps
  //  -- elevations between nodes are a linear taper between pts
  //  -- the value of zero shall not be sent here

  private LaneDataAttributeList data;
}
