package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.GNSSstatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;

public class OssGNSSstatus {
    
    private OssGNSSstatus() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735GNSSstatus genericGNSSstatus (GNSSstatus s) {
        J2735GNSSstatus status = new J2735GNSSstatus();
        
        for (int i = 0; i < s.getSize(); i++) {
             String statusName = s.getNamedBits().getMemberName(i);
             Boolean statusValue = s.getBit(i);

             if (statusName != null) {
                 status.put(statusName, statusValue);
             }
         }
        
        return status;
    }

}
