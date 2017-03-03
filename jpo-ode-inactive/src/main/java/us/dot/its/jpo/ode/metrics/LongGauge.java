package us.dot.its.jpo.ode.metrics;

import us.dot.its.jpo.ode.metrics.OdeMetrics.Gauge;

public class LongGauge implements Gauge<Long> {
   private Long value;
   
   public Long getValue() {
       return value;
   }

   public void setValue(Long value) {
      this.value = value;
   }

   public void register(String prefix, String... names) {
      OdeMetrics.getInstance().registerGauge(this, prefix, names);
   }
   
   
}
