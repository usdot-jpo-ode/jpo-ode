/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import us.dot.its.jpo.ode.util.JsonUtils;

@RestController
public class OdePropertiesController {

  private static final Logger logger = LoggerFactory.getLogger(OdePropertiesController.class);

  private OdeProperties odeProperties;

  @Autowired
  public OdePropertiesController(OdeProperties odeProperties) {
    super();
    this.odeProperties = odeProperties;
  }

  @CrossOrigin
  @GetMapping(value = "/version")
  public ResponseEntity<String> getVersion() { // NOSONAR
    logger.debug("Request for Version info received: {}", this.odeProperties.getVersion());

    return ResponseEntity.ok().body(JsonUtils.jsonKeyValue("version", this.odeProperties.getVersion()));
  }

}
