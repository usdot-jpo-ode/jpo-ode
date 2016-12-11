package us.dot.its.jpo.ode.wrapper;

import us.dot.its.jpo.ode.model.OdeDataMessage;

public interface DataPropagator<D, R> extends DataProcessor<D, R> {
   void filterAndSend(OdeDataMessage dataMsg) throws Exception;
}
