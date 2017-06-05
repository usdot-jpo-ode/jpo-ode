package us.dot.its.jpo.ode.vsd;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

@Controller
public class VsdServicesController {
	
	private static Logger logger = LoggerFactory.getLogger(VsdServicesController.class);
	protected OdeProperties odeProperties;
	
	@Autowired
	public VsdServicesController(OdeProperties odeProps) {
		this.odeProperties = odeProps;
		
		logger.info("Starting {} ...", this.getClass().getSimpleName());

        VsdDepositor vsdDepositor = new VsdDepositor(odeProps);
        logger.info("Launching {} ...", vsdDepositor.getClass().getSimpleName());
        MessageConsumer<String, String> consumer = 
                MessageConsumer.defaultStringMessageConsumer(
                        odeProperties.getKafkaBrokers(), 
                        odeProperties.getHostId() + this.getClass().getSimpleName(),
                        vsdDepositor);
        vsdDepositor.subscribe(consumer, odeProps.getKafkaTopicBsmRawJson());
        
		VsdReceiver vsdReceiver = new VsdReceiver(odeProps);
        logger.info("Launching {} ...", vsdReceiver.getClass().getSimpleName());
		Executors.newSingleThreadExecutor().submit(vsdReceiver);
		
	}

}
