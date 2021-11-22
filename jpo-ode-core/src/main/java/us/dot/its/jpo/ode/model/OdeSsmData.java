package us.dot.its.jpo.ode.model;

public class OdeSsmData extends OdeData {

    private static final long serialVersionUID = 2057222204896561615L;

   public OdeSsmData() {
       super();
   }

   public OdeSsmData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
       super(metadata, payload);
   }
}
