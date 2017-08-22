package us.dot.its.jpo.ode.traveler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;

public class QueryThread implements Runnable {

   private static final Logger logger = LoggerFactory.getLogger(QueryThread.class);

   private Snmp snmp;
   private PDU pdu;
   private UserTarget target;
   private ConcurrentHashMap<Integer, Boolean> concHashMap;
   private int i;

   public QueryThread(Snmp snmp, PDU pdu, UserTarget target, ConcurrentHashMap<Integer, Boolean> concHashMap, int i) {
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
      }

      if (null == response || null == response.getResponse()) {
         logger.error("Timeout error, no response from RSU.");
         return;
      }

      Integer status = ((VariableBinding) (response.getResponse().getVariableBindings().firstElement())).getVariable()
            .toInt();

      if (1 == status) { // 1 == set, 129 == unset
         concHashMap.put(i, true);
         logger.info("TIM query: index {} set.", i);
      } else {
         logger.info("TIM query: index {} unset.", i);
      }
   }
}
