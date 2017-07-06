package us.dot.its.jpo.ode.services.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Centralized UDP service dispatcher.
 *
 */
@Controller
public class ToJsonServiceController {

   private static Logger logger = LoggerFactory.getLogger(ToJsonServiceController.class);
   org.apache.kafka.common.serialization.Serdes bas;

   @Autowired
   public ToJsonServiceController(OdeProperties odeProps) {
      super();

      logger.info("Starting {}", this.getClass().getSimpleName());
      
      logger.info("Converting {} records from topic {} and publishing to topic {} ",
              J2735Bsm.class.getSimpleName(),
              odeProps.getKafkaTopicBsmSerializedPojo(),
              odeProps.getKafkaTopicBsmRawJson());
      
      ToJsonConverter<J2735Bsm> converter = new ToJsonConverter<J2735Bsm>(
              odeProps, false, odeProps.getKafkaTopicBsmRawJson());
      
      converter.start(
              new MessageConsumer<String, J2735Bsm>(
                  odeProps.getKafkaBrokers(), 
                  this.getClass().getSimpleName(), 
                  converter), 
              odeProps.getKafkaTopicBsmSerializedPojo());
      
   }

}
