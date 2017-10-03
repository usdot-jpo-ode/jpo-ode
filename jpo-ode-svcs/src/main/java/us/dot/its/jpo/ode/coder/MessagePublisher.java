package us.dot.its.jpo.ode.coder;

import us.dot.its.jpo.ode.OdeProperties;

public abstract class MessagePublisher {

   protected OdeProperties odeProperties;

   public MessagePublisher(OdeProperties odeProps) {
      this.odeProperties = odeProps;
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }   
}
