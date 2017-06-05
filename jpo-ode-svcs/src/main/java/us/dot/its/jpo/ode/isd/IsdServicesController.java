package us.dot.its.jpo.ode.isd;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

@Controller
public class IsdServicesController {


    private static Logger logger = LoggerFactory.getLogger(IsdServicesController.class);
    protected OdeProperties odeProperties;

    @Autowired
    public IsdServicesController(OdeProperties odeProps) {
        this.odeProperties = odeProps;
        
        logger.info("Starting {} ...", this.getClass().getSimpleName());

        IsdDepositor isdDepositor = new IsdDepositor(odeProps);
        logger.info("Launching {} ...", isdDepositor.getClass().getSimpleName());
        MessageConsumer<String, byte[]> consumer = 
                MessageConsumer.defaultByteArrayMessageConsumer(
                        odeProperties.getKafkaBrokers(), 
                        odeProperties.getHostId() + this.getClass().getSimpleName(),
                        isdDepositor);
        isdDepositor.subscribe(consumer, odeProps.getKafkaTopicEncodedIsd());
        
        IsdReceiver isdReceiver = new IsdReceiver(odeProps);
        logger.info("Launching {} ...", isdReceiver.getClass().getSimpleName());
        Executors.newSingleThreadExecutor().submit(isdReceiver);
    }

}