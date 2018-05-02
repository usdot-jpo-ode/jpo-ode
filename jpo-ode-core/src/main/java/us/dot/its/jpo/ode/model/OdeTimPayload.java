package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;

public class OdeTimPayload extends OdeMsgPayload {
   
   private static final long serialVersionUID = 7061315628111448390L;

   public OdeTimPayload() {
       this(new OdeTravelerInformationMessage());
   }

   public OdeTimPayload(OdeTravelerInformationMessage tim) {
       super(tim);
       this.setData(tim);
   }

   public OdeTravelerInformationMessage getTim() {
       return (OdeTravelerInformationMessage) getData();
   }

   public void setTim(OdeTravelerInformationMessage tim) {
       setData(tim);
   }

}
