package us.dot.its.jpo.ode.vsd;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;

@Controller
public class VsdServicesController {
	
	private static Logger logger = LoggerFactory.getLogger(VsdServicesController.class);
	
	@Autowired
	public VsdServicesController(OdeProperties odeProps) {
		super();
		
		logger.info("Starting {} ...", this.getClass().getSimpleName());

        VsdDepositor vsdDepositor = new VsdDepositor(odeProps);
        logger.info("Launching {} ...", vsdDepositor.getClass().getSimpleName());
        vsdDepositor.subscribe(odeProps.getKafkaTopicBsmRawJson());
        
		VsdReceiver vsdReceiver = new VsdReceiver(odeProps);
        logger.info("Launching {} ...", vsdReceiver.getClass().getSimpleName());
		Executors.newSingleThreadExecutor().submit(vsdReceiver);
		
	}

}
