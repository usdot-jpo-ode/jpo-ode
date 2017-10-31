package us.dot.its.jpo.ode.services.json;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubPubTransformer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

/* 
 * The MessageProcessor value type is String 
 */
public class ToJsonConverter<V> extends AbstractSubPubTransformer<String, V, String> {

    private boolean verbose;

    public ToJsonConverter(OdeProperties odeProps, boolean verbose, String outTopic) {
        super(MessageProducer.defaultStringMessageProducer(
           odeProps.getKafkaBrokers(),
           odeProps.getKafkaProducerType(), 
           odeProps.getKafkaTopicsDisabledSet()), outTopic);
        this.verbose = verbose;
    }

    @Override
    protected String process(V consumedData) {
        return JsonUtils.toJson(consumedData, verbose);
    }
}
