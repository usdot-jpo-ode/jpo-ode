package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInputDataBase;

public class OdeTravelerInputData extends TravelerInputDataBase {

   private static final long serialVersionUID = 8769107278440796699L;

   private OdeTravelerInformationMessage tim;
   public OdeTravelerInformationMessage getTim() {
      return tim;
   }

   public void setTim(OdeTravelerInformationMessage tim) {
      this.tim = tim;
   }

}
