package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInputDataBase;

public class OdeTravelerInputData extends TravelerInputDataBase {

   private static final long serialVersionUID = 8769107278440796699L;

   private J2735TravelerInformationMessage tim;
   public J2735TravelerInformationMessage getTim() {
      return tim;
   }

   public void setTim(J2735TravelerInformationMessage tim) {
      this.tim = tim;
   }

}
