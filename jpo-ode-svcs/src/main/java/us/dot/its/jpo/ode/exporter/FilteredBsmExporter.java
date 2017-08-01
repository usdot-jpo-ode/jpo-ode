package us.dot.its.jpo.ode.exporter;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class FilteredBsmExporter extends Exporter {

    private OdeProperties odeProperties;
    private SimpMessagingTemplate template;

    public FilteredBsmExporter(
          OdeProperties odeProperties,
          String topic, 
          SimpMessagingTemplate template) {
        super(topic);
        this.odeProperties = odeProperties;
        this.template = template;
    }

    @Override
    protected void subscribe() {
        setConsumer(MessageConsumer.defaultStringMessageConsumer(odeProperties.getKafkaBrokers(),
                odeProperties.getHostId() + this.getClass().getSimpleName(), 
                new StompStringMessageDistributor(template, getTopic())));

        getConsumer().setName(this.getClass().getSimpleName());
        getConsumer().subscribe(odeProperties.getKafkaTopicFilteredBsmJson());
    }

}
