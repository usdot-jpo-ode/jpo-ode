package us.dot.its.jpo.ode.bsm;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;

@Controller
public class BsmServicesController {
	
	private static Logger logger = LoggerFactory.getLogger(BsmServicesController.class);
	
	@Autowired
	public BsmServicesController(OdeProperties odeProps) {
		super();
		
        logger.info("Starting {} ...", this.getClass().getSimpleName());

        BsmReceiver bsmReceiver = new BsmReceiver(odeProps);
        logger.info("Launching {} ...", bsmReceiver.getClass().getSimpleName());
        Executors.newSingleThreadExecutor().submit(bsmReceiver);
        try {
            Executors.newSingleThreadExecutor().submit(new BsmProcessor(odeProps));
        } catch (Exception e) {
            logger.error("Error launching Bsm Processor", e);
        }
	}

}
