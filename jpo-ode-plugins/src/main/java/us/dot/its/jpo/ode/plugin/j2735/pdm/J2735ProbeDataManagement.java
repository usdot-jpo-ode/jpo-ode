package us.dot.its.jpo.ode.plugin.j2735.pdm;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData.ODE;

public class J2735ProbeDataManagement extends OdeObject {

   private static final long serialVersionUID = 2154315328067723844L;

   private ODE ode;
   private RSU[] rsuList;
   private PDM pdm;

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
