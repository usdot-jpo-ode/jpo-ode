package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;

public class Tim extends OdeObject {
   
      private static final long serialVersionUID = 1L;
      private TravelerInformation tim;
      private RSU[] rsus;
      private SNMP snmp;
      public TravelerInformation getTim() {
         return tim;
      }
      public void setTim(TravelerInformation tim) {
         this.tim = tim;
      }
      public RSU[] getRsus() {
         return rsus;
      }
      public void setRsus(RSU[] rsus) {
         this.rsus = rsus;
      }
      public SNMP getSnmp() {
         return snmp;
      }
      public void setSnmp(SNMP snmp) {
         this.snmp = snmp;
      }

}
