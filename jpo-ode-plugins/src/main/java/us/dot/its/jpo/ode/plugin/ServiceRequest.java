package us.dot.its.jpo.ode.plugin;

import java.util.Arrays;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

public class ServiceRequest extends OdeObject {

   public static class OdeInternal {
     
     public enum RequestVerb {
       POST, PUT, DELETE, GET
     }

     private int version = 2;
     private int rsuIndex;
     private RequestVerb verb;

     public int getVersion() {
        return version;
     }

     public void setVersion(int version) {
        this.version = version;
     }

     public int getRsuIndex() {
        return rsuIndex;
     }

     public void setRsuIndex(int index) {
        this.rsuIndex = index;
     }
     
    public RequestVerb getVerb() {
      return verb;
    }

    public void setVerb(RequestVerb verb) {
      this.verb = verb;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + rsuIndex;
      result = prime * result + ((verb == null) ? 0 : verb.hashCode());
      result = prime * result + version;
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
      OdeInternal other = (OdeInternal) obj;
      if (rsuIndex != other.rsuIndex)
        return false;
      if (verb != other.verb)
        return false;
      if (version != other.version)
        return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;
   private OdeInternal ode;
   private SituationDataWarehouse.SDW sdw;
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

   public OdeInternal getOde() {
     return ode;
   }

   public void setOde(OdeInternal ode) {
     this.ode = ode;
   }

   public SituationDataWarehouse.SDW getSdw() {
     return sdw;
   }

   public void setSdw(SituationDataWarehouse.SDW sdw) {
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
      ServiceRequest other = (ServiceRequest) obj;
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
