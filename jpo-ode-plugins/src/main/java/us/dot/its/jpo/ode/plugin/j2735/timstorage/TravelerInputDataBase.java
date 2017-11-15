package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import java.util.Arrays;

import us.dot.its.jpo.ode.plugin.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class TravelerInputDataBase extends Asn1Object {

   private static final long serialVersionUID = 1L;
   private ODE ode;
   private SDW sdw;
   private RSU[] rsus;
   private SNMP snmp;

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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((ode == null) ? 0 : ode.hashCode());
      result = prime * result + Arrays.hashCode(rsus);
      result = prime * result + ((sdw == null) ? 0 : sdw.hashCode());
      result = prime * result + ((snmp == null) ? 0 : snmp.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TravelerInputDataBase other = (TravelerInputDataBase) obj;
      if (ode == null) {
         if (other.ode != null)
            return false;
      } else if (!ode.equals(other.ode))
         return false;
      if (!Arrays.equals(rsus, other.rsus))
         return false;
      if (sdw == null) {
         if (other.sdw != null)
            return false;
      } else if (!sdw.equals(other.sdw))
         return false;
      if (snmp == null) {
         if (other.snmp != null)
            return false;
      } else if (!snmp.equals(other.snmp))
         return false;
      return true;
   }

}
