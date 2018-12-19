package us.dot.its.jpo.ode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.dot.its.jpo.ode.util.JsonUtils;

@Controller
public class OdePropertiesController {

  private static final Logger logger = LoggerFactory.getLogger(OdePropertiesController.class);

  private OdeProperties odeProperties;

  @Autowired
  public OdePropertiesController(OdeProperties odeProperties) {
    super();
    this.odeProperties = odeProperties;
  }

  @ResponseBody
  @CrossOrigin
  @RequestMapping(value = "/version", method = RequestMethod.GET)
  public ResponseEntity<String> getVersion() { // NOSONAR
    logger.debug("Request for Version info received: {}", this.odeProperties.getVersion());

    return ResponseEntity.ok().body(JsonUtils.jsonKeyValue("version", this.odeProperties.getVersion()));
  }

}
