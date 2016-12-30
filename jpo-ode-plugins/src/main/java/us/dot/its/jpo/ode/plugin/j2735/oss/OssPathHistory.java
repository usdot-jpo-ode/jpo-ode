package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.PathHistory;
import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistory;

public class OssPathHistory {

	public static J2735PathHistory genericPathHistory(PathHistory pathHistory) {
		J2735PathHistory ph = new J2735PathHistory();
		
		
		// Required element
		ph.crumbData = OssPathHistoryPointList.genericPathHistoryPointList(pathHistory.crumbData);
		
		// Optional elements
		if (pathHistory.currGNSSstatus != null) {
		    J2735GNSSstatus status = new J2735GNSSstatus();
		    
		    for (int i = 0; i < pathHistory.currGNSSstatus.getSize(); i++) {
	            String statusName = pathHistory.currGNSSstatus.getNamedBits().getMemberName(i);
	            Boolean statusValue = pathHistory.currGNSSstatus.getBit(pathHistory.currGNSSstatus.getSize() - i - 1);

	            if (statusName != null) {
	                status.put(statusName, statusValue);
	            }
	        }
		    
		    ph.currGNSSstatus = status;
		    //ph.currGNSSstatus = OssBitString.genericBitString(pathHistory.currGNSSstatus);
		}
		if (pathHistory.initialPosition != null) {
		    ph.initialPosition = OssFullPositionVector.genericFullPositionVector(pathHistory.initialPosition);
		}
		
		return ph ;
	}

}
