package us.dot.its.jpo.ode.bsm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.exporter.RawBsmExporter;

@Controller
public class BsmServicesController {
	
	private static Logger logger = LoggerFactory.getLogger(BsmServicesController.class);
	
	private ExecutorService bsmReceiveExecutor;
	
	@Autowired
	public BsmServicesController(OdeProperties odeProps) {
		super();
		
		logger.info("Bsm Services Controller starting.");
		bsmReceiveExecutor = Executors.newSingleThreadExecutor();
		bsmReceiveExecutor.submit(new BsmReceiver(odeProps));
		
		/* Ode-314 stuff
        try {
            Executors.newSingleThreadExecutor().submit(new BsmProcessor(odeProps));
        } catch (Exception e) {
            logger.error("Error launching Bsm Processor", e);
        }
        */
	}

}
