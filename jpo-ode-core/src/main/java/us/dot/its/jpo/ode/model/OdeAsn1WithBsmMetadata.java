package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;

public class OdeAsn1WithBsmMetadata extends OdeAsn1Metadata {

   private static final long serialVersionUID = 299691716113824640L;

   private BsmSource bsmSource;
   
   public OdeAsn1WithBsmMetadata() {
      super();
   }

   public OdeAsn1WithBsmMetadata(OdeMsgPayload payload) {
      super(payload);
   }

   public BsmSource getBsmSource() {
      return bsmSource;
   }

   public void setBsmSource(BsmSource bsmSource) {
      this.bsmSource = bsmSource;
   }


}
