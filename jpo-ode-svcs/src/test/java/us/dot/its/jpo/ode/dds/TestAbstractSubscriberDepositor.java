package us.dot.its.jpo.ode.dds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;

public class TestAbstractSubscriberDepositor extends AbstractSubscriberDepositor<Object, Object>
{

   public TestAbstractSubscriberDepositor(OdeProperties odeProps, int port) {
      super(odeProps, port);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected TemporaryID getRequestId() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected SemiDialogID getDialogId() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected byte[] deposit() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

}
