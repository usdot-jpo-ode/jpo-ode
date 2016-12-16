package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_24b;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;

public class OssNode_XY {

	public static J2735Node_XY genericNode_XY(Node_XY_24b node) {
		return new J2735Node_XY(OssOffset.genericOffset(node.x), OssOffset.genericOffset(node.y));
	}

}
