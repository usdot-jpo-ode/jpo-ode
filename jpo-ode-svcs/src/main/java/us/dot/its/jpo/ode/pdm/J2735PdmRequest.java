package us.dot.its.jpo.ode.pdm;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.OperationalDataEnvironment.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;

public class J2735PdmRequest extends OdeObject {

   private static final long serialVersionUID = 2154315328067723844L;

   private ODE ode;
   private RSU[] rsuList;
   private J2735ProbeDataManagment pdm;

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

   public J2735ProbeDataManagment getPdm() {
      return pdm;
   }

   public void setPdm(J2735ProbeDataManagment pdm) {
      this.pdm = pdm;
   }

}
