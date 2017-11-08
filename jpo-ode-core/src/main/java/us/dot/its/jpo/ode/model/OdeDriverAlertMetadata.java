package us.dot.its.jpo.ode.model;

/**
 * Created by anthonychen on 11/4/17.
 */
public class OdeDriverAlertMetadata extends OdeTimMetadata{


   private static final long serialVersionUID = -8601265839394150140L;

   public OdeDriverAlertMetadata(OdeMsgPayload driverAlertPayload) {
      super((OdeTimPayload) driverAlertPayload);
   }
}
