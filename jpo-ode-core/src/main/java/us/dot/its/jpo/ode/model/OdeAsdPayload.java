package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;

public class OdeAsdPayload extends OdeMsgPayload {
   
   private static final long serialVersionUID = 7061315628111448390L;

   public OdeAsdPayload() {
       this(new DdsAdvisorySituationData());
   }

   public OdeAsdPayload(DdsAdvisorySituationData asd) {
       super(asd);
       this.setData(asd);
   }

   public DdsAdvisorySituationData getAsd() {
       return (DdsAdvisorySituationData) getData();
   }

   public void setAsd(DdsAdvisorySituationData asd) {
       setData(asd);
   }

}
