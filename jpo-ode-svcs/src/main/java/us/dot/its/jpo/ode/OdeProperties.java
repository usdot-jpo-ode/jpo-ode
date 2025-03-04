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

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;


@Component
@Data
@Slf4j
@Import(BuildProperties.class)
public class OdeProperties {

    private static final byte[] JPO_ODE_GROUP_ID = "jode".getBytes();

    final BuildProperties buildProperties;

    public OdeProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @PostConstruct
    void initialize() {
        log.info("groupId: {}", buildProperties.getGroup());
        log.info("artifactId: {}", buildProperties.getArtifact());
        log.info("version: {}", buildProperties.getVersion());
    }

    public String getVersion() {
        return buildProperties.getVersion();
    }
}
