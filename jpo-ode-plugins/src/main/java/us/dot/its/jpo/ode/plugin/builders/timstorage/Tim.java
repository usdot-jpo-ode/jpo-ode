package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;

public class Tim extends OdeObject {

   private static final long serialVersionUID = 1L;
   private ODE ode;
   private SDW sdw;
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
