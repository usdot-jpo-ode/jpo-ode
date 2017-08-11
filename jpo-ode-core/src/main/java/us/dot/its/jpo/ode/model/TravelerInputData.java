package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.snmp.SNMP;

public class TravelerInputData extends OdeObject {

   private static final long serialVersionUID = 8769107278440796699L;

   private J2735TravelerInformationMessage tim;
   private RSU[] rsus;
   private SNMP snmp;
   private ODE ode;
   private SDW sdw;

   public J2735TravelerInformationMessage getTim() {
      return tim;
   }

   public void setTim(J2735TravelerInformationMessage tim) {
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

   public ODE getOde() {
      return ode;
   }

   public void setOde(ODE ode) {
      this.ode = ode;
   }

   public SDW getSdw() {
      return sdw;
   }

   public void setSdw(SDW sdw) {
      this.sdw = sdw;
   }

}
