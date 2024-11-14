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

import java.lang.management.ManagementFactory;

import jakarta.annotation.PreDestroy;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.AuthHMAC128SHA224;
import org.snmp4j.security.AuthHMAC192SHA256;
import org.snmp4j.security.AuthHMAC256SHA384;
import org.snmp4j.security.AuthHMAC384SHA512;
import org.snmp4j.security.PrivAES128;

import org.snmp4j.security.SecurityProtocols;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties
public class OdeSvcsApplication {

   static final int DEFAULT_NO_THREADS = 10;
   static final String DEFAULT_SCHEMA = "default";

   public static void main(String[] args) throws MalformedObjectNameException, InterruptedException,
         InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      SpringApplication.run(OdeSvcsApplication.class, args);
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      SystemConfig mBean = new SystemConfig(DEFAULT_NO_THREADS, DEFAULT_SCHEMA);
      ObjectName name = new ObjectName("us.dot.its.jpo.ode:type=SystemConfig");
      mbs.registerMBean(mBean, name);

      SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthSHA());
      SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC128SHA224());
      SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC192SHA256());
      SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC256SHA384());
      SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthHMAC384SHA512());
      SecurityProtocols.getInstance().addAuthenticationProtocol(new AuthMD5());
      SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES128());

   }

   @Bean
   CommandLineRunner init(OdeProperties odeProperties) {
      return args -> {
      };
   }

   @PreDestroy
   public void cleanup() {
      // Unused
   }

}
