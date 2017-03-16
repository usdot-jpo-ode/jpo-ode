package us.dot.its.jpo.ode.pdm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.traveler.TravelerMessageController;

@Controller
public class PDMController {
   private static Logger logger = LoggerFactory.getLogger(PDMController.class);

   private OdeProperties odeProperties;
   private String myString;
   
   @ResponseBody
   @RequestMapping(value = "/pdm", method = RequestMethod.POST, produces = "application/json")
   public String pdmMessage(@RequestBody String jsonString) {
      
      return null;
   }
}
