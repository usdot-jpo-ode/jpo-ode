package us.dot.its.jpo.ode.model;

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

   public J2735TravelerInformationMessage getTim() {
       return (J2735TravelerInformationMessage) getData();
   }

   public void setTim(J2735TravelerInformationMessage tim) {
       setData(tim);
   }

}
