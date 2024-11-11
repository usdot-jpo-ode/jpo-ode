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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.rsu.RSUProperties;


@Configuration
@ConfigurationProperties(prefix = "ode")
@Data
@Slf4j
public class OdeProperties implements EnvironmentAware {

    @Autowired
    private Environment env;

    /*
     * General Properties
     */
    private int outputSchemaVersion;
    private String pluginsLocations;
    private String hostIP;
    private boolean verboseJson;

    /*
     * Security Services Module Properties
     */
    private String securitySvcsSignatureUri;
    private int securitySvcsPort;
    private String securitySvcsSignatureEndpoint;

    /*
     * Security Properties
     */
    private String caCertPath;
    private String selfCertPath;
    private String selfPrivateKeyReconstructionFilePath;
    private String selfSigningPrivateKeyFilePath;

    private static final byte[] JPO_ODE_GROUP_ID = "jode".getBytes();

    @Autowired
    BuildProperties buildProperties;

    @PostConstruct
    void initialize() {
        log.info("groupId: {}", buildProperties.getGroup());
        log.info("artifactId: {}", buildProperties.getArtifact());
        log.info("version: {}", buildProperties.getVersion());
        OdeMsgMetadata.setStaticSchemaVersion(this.outputSchemaVersion);

        // URI for the security services /sign endpoint
        if (securitySvcsSignatureUri == null) {
            securitySvcsSignatureUri = "http://" + hostIP + ":" + securitySvcsPort + "/"
                    + securitySvcsSignatureEndpoint;
        }
    }

    public String getVersion() {
        return buildProperties.getVersion();
    }

    public String getProperty(String key) {
        return env.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    public Object getProperty(String key, int i) {
        return env.getProperty(key, Integer.class, i);
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    @Bean
    public RSUProperties rsuProperties() {
        return new RSUProperties();
    }
}
