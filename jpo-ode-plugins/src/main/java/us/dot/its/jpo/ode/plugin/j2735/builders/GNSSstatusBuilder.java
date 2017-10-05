package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;

public class GNSSstatusBuilder {
    
    private GNSSstatusBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735GNSSstatus genericGNSSstatus (JsonNode gnssStatus) {
        J2735GNSSstatus status = new J2735GNSSstatus();
        
        for (int i = 0; i < gnssStatus.getSize(); i++) {
             String statusName = gnssStatus.getNamedBits().getMemberName(i);
             Boolean statusValue = gnssStatus.getBit(i);

             if (statusName != null) {
                 status.put(statusName, statusValue);
             }
         }
        
        return status;
    }

}
