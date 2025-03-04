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
package us.dot.its.jpo.ode.context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import us.dot.its.jpo.ode.eventlog.EventLogger;

@Slf4j
@Configuration
public class AppContext {
   // CONSTANTS
   public static final String ODE_HOSTNAME = "ODE_HOSTNAME";
   public static final String PAYLOAD_STRING = "payload";
   public static final String METADATA_STRING = "metadata";
   public static final String ODE_ASN1_DATA = "OdeAsn1Data";
   public static final String DATA_STRING = "data";
   public static final String ENCODINGS_STRING = "encodings";
   public static final String RECEIVEDMSGDETAILS_STRING = "receivedMessageDetails";

   private static AppContext instance = null;

   @Autowired
   Environment env;

   private String hostId;

   public AppContext() {
   }

   public static AppContext getInstance() {
      if (null == instance) {
         synchronized (AppContext.class) {
            if (null == instance)
               instance = new AppContext();
            instance.init();
         }
      }
      return instance;
   }

   private void init() {
      String hostname;

      try {
         hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
         // Let's just use a random hostname
         hostname = UUID.randomUUID().toString();
      }
      hostId = hostname;
   }

   public String getHostId() {
      if (this.hostId == null || this.hostId.isEmpty()) {
         initializeHostId();
      }
      return hostId;
   }

   private void initializeHostId() {
      String hostname;
      try {
         hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
         // Let's just use a random hostname
         hostname = UUID.randomUUID().toString();
         log.error("Unknown host error: {}, using random", e);
      }
      this.hostId = hostname;
      log.info("Host ID: {}", hostId);
      EventLogger.logger.info("Initializing services on host {}", hostId);
   }

   public String getParam(String key) {
      String result = null;
      if (key != null) {
         result = env.getProperty(key);
      }

      return result;
   }

   public String getParam(String key, String defaultValue) {
      String result = getParam(key);
      if (result != null) {
         return result;
      } else {
         return defaultValue;
      }
   }

   public int getInt(String key, int defaultValue) {
      String result = getParam(key);
      if (result != null) {
         return Integer.parseInt(result);
      } else {
         return defaultValue;
      }
   }

   public long getLong(String key, long defaultValue) {
      String result = getParam(key);
      if (result != null) {
         return Long.parseLong(result);
      } else {
         return defaultValue;
      }
   }

   public double getDouble(String key, double defaultValue) {
      String result = getParam(key);
      if (result != null) {
         return Double.parseDouble(result);
      } else {
         return defaultValue;
      }
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      String result = getParam(key);
      if (result != null) {
         return Boolean.parseBoolean(result);
      } else {
         return defaultValue;
      }
   }

}
