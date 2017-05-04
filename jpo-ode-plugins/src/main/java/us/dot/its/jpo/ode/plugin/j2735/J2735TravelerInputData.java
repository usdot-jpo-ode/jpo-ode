package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.OperationalDataEnvironment.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.TravelerInformationMessage.TIM;

public class J2735TravelerInputData extends OdeObject {

   private static final long serialVersionUID = 8769107278440796699L;

   private TIM tim;
   private RSU[] rsus;
   private SNMP snmp;
   private ODE ode;
   private SDW sdw;

   public TIM getTim() {
      return tim;
   }

   public void setTim(TIM tim) {
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
