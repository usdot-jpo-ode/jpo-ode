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
	
	@Autowired
	public VsdServicesController(OdeProperties odeProps) {
		super();
		
		logger.info("Starting {} ...", this.getClass().getSimpleName());

        VsdDepositor vsdDepositor = new VsdDepositor(odeProps);
        logger.info("Launching {} ...", vsdDepositor.getClass().getSimpleName());
        /* 
         * ODE-314
         * Changed to MessageConsumer.defaultStringMessageConsumer() method 
         */
        MessageConsumer<String, String> consumer = 
                MessageConsumer.defaultStringMessageConsumer(
                        odeProps.getKafkaBrokers(), 
                        odeProps.getHostId() + vsdDepositor.getClass().getSimpleName(),
                        vsdDepositor);

		Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                /* 
                 * ODE-314
                 * The argument to subscribe method changed to 
                 * odeProps.getKafkaTopicBsmFilteredJson()
                 */
                consumer.subscribe(odeProps.getKafkaTopicBsmFilteredJson());
            }
        });
        
		VsdReceiver vsdReceiver = new VsdReceiver(odeProps);
        logger.info("Launching {} ...", vsdReceiver.getClass().getSimpleName());
		Executors.newSingleThreadExecutor().submit(vsdReceiver);
		
	}

}
