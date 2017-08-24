package us.dot.its.jpo.ode.traveler;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;

public class TimQueryThread implements Runnable {

   private static final Logger logger = LoggerFactory.getLogger(TimQueryThread.class);
   
   public static final Integer TIMEOUT_FLAG = -1;

   private Snmp snmp;
   private PDU pdu;
   private UserTarget target;
   private ConcurrentSkipListMap<Integer, Integer> concHashMap;
   private int i;

   public TimQueryThread(Snmp snmp, PDU pdu, UserTarget target, ConcurrentSkipListMap<Integer, Integer> concHashMap, int i) {
      this.snmp = snmp;
      this.pdu = pdu;
      this.target = target;
      this.concHashMap = concHashMap;
      this.i = i;
   }

   @Override
   public void run() {

      ResponseEvent response = null;
      try {
         response = snmp.get(pdu, target);
      } catch (IOException e) {
         logger.error("Error sending asynchronous TIM query request.", e);
         concHashMap.put(i, TIMEOUT_FLAG);
         return;
      }

      if (null == response || null == response.getResponse()) {
         // timeout, put -1 flag
         concHashMap.put(i, TIMEOUT_FLAG);
         logger.error("Request index {} timed out!", i);
      } else {
         Integer status = ((VariableBinding) (response.getResponse().getVariableBindings().firstElement())).getVariable()
               .toInt();
         if (1 == status) { // 1 == set, 129 == unset
            concHashMap.put(i, status);
         }
      }
   }
}
