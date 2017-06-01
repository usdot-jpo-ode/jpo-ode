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

        BsmReceiver2 bsmReceiver = new BsmReceiver2(odeProps);
        logger.info("Launching {} ...", bsmReceiver.getClass().getSimpleName());
        Executors.newSingleThreadExecutor().submit(bsmReceiver);
	}

}
