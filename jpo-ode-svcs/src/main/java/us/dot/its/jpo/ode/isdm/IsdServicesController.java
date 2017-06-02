package us.dot.its.jpo.ode.isdm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.OdeProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class IsdServicesController {


    private static Logger logger = LoggerFactory.getLogger(IsdServicesController.class);

    private ExecutorService isdmReceiver;

    @Autowired
    public IsdServicesController(OdeProperties odeProps) {
        super();

        logger.info("Isd Services Controller starting.");
        isdmReceiver = Executors.newSingleThreadExecutor();
        isdmReceiver.submit(new IsdmReceiver(odeProps));

    }

}