package us.dot.its.jpo.ode.vsdm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;

@Controller
public class VsdmServicesController {
	
	private static Logger logger = LoggerFactory.getLogger(VsdmServicesController.class);
	
	private ExecutorService vsdmReceiver;
	
	@Autowired
	public VsdmServicesController(OdeProperties odeProps) {
		super();
		
		logger.info("Vsdm Services Controller starting.");
		vsdmReceiver = Executors.newSingleThreadExecutor();
		vsdmReceiver.submit(new VsdmReceiver(odeProps));
		
	}

}
