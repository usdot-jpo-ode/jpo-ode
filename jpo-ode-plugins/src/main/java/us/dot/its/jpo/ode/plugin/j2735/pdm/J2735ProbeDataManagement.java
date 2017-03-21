package us.dot.its.jpo.ode.plugin.j2735.pdm;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSignUnit.RSU;

public class J2735ProbeDataManagement extends OdeObject {

   private static final long serialVersionUID = 2154315328067723844L;

   private transient ODE ode;
   private transient RSU[] rsuList;
   private transient PDM pdm;

   public static class ODE {
      private int version = 1;

      public int getVersion() {
         return version;
      }

      public void setVersion(int version) {
         this.version = version;
      }
   }

   public ODE getOde() {
      return ode;
   }

   public void setOde(ODE ode) {
      this.ode = ode;
   }

   public RSU[] getRsuList() {
      return rsuList;
   }

   public void setRsuList(RSU[] rsuList) {
      this.rsuList = rsuList;
   }

   public PDM getPdm() {
      return pdm;
   }

   public void setPdm(PDM pdm) {
      this.pdm = pdm;
   }

}
