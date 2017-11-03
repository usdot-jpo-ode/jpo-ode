package us.dot.its.jpo.ode.exporter;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Created by anthonychen on 10/16/17.
 */
public class StompStringExporter extends Exporter{

    private OdeProperties odeProperties;
    private SimpMessagingTemplate template;
    private String odeTopic;

    public StompStringExporter(
            OdeProperties odeProperties,
            String stompTopic,
            SimpMessagingTemplate template,
            String odeTopic) {
        super(stompTopic);
        this.odeProperties = odeProperties;
        this.template = template;
        this.odeTopic = odeTopic;
    }

    @Override
    protected void subscribe() {
        setConsumer(MessageConsumer.defaultStringMessageConsumer(odeProperties.getKafkaBrokers(),
                odeProperties.getHostId() + this.getClass().getSimpleName(),
                new StompStringMessageDistributor(template, getTopic())));

        getConsumer().setName(this.getClass().getSimpleName());
        getConsumer().subscribe(odeTopic);
    }
}
