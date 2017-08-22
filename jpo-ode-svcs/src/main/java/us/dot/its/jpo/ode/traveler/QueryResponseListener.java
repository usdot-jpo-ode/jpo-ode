package us.dot.its.jpo.ode.traveler;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.VariableBinding;

public class QueryResponseListener implements ResponseListener {

   private static final Logger logger = LoggerFactory.getLogger(QueryResponseListener.class);

   private int i;
   private ConcurrentHashMap<Integer, Boolean> concHashMap;

   public QueryResponseListener(int i, ConcurrentHashMap<Integer, Boolean> concHashMap) {
      this.i = i;
      this.concHashMap = concHashMap;
   }

   @Override
   public void onResponse(ResponseEvent response) {
      ((Snmp) response.getSource()).cancel(response.getRequest(), this);
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
