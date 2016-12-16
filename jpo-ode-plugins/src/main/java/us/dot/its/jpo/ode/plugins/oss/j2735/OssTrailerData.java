package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerData;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerData;

public class OssTrailerData {

	public static J2735TrailerData genericTrailerData(TrailerData trailers) {
		J2735TrailerData td = new J2735TrailerData();
		
		td.connection = OssPivotPointDescription.genericPivotPointDescription(trailers.connection);
		td.sspRights = trailers.sspRights.intValue();
		
		Iterator<TrailerUnitDescription> iter = trailers.units.elements.iterator();
		
		while(iter.hasNext()) {
			td.units.add(OssTrailerUnitDescription.genericTrailerUnitDescription(iter.next()));
		}
		
		return td ;
	}

}
