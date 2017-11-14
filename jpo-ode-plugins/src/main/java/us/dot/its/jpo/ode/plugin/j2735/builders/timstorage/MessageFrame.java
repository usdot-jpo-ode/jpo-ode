package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;

public class MessageFrame extends Asn1Object {

   private static final long serialVersionUID = 3450586016818874906L;
   
   private J2735MessageFrame MessageFrame;

   public J2735MessageFrame getMessageFrame() {
      return MessageFrame;
   }

   public void setMessageFrame(J2735MessageFrame messageFrame) {
      MessageFrame = messageFrame;
   }
   
   

}
