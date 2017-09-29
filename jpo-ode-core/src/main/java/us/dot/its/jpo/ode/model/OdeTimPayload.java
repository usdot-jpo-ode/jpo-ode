package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;

public class OdeTimPayload extends OdeMsgPayload {
   
   private static final long serialVersionUID = 7061315628111448390L;

   public OdeTimPayload() {
       this(new J2735TravelerInformationMessage());
   }

   public OdeTimPayload(J2735TravelerInformationMessage tim) {
       super(tim);
       this.setData(tim);
   }

   public J2735Bsm getBsm() {
       return (J2735Bsm) getData();
   }

   public void setBsm(J2735Bsm bsm) {
       setData(bsm);
   }

}
