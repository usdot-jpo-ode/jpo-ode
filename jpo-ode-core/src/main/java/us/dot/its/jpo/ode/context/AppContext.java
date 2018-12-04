/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under
 * this task order. All documents and materials, to include the source code of
 * any software produced under this contract, shall be Government owned and the
 * property of the Government with all rights and privileges of ownership/copyright
 * belonging exclusively to the Government. These documents and materials may
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the
 * Government and may not be used for any other purpose. This right does not
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppContext {
   // CONSTANTS
   public static final String ODE_HOSTNAME = "ODE_HOSTNAME";
   public static final String PAYLOAD_STRING = "payload";
   public static final String METADATA_STRING = "metadata";
   public static final String ODE_ASN1_DATA = "OdeAsn1Data";
   public static final String DATA_STRING = "data";
   public static final String ENCODING_STRING = "encodings";
   public static final String ENCODINGS_STRING = "encodings";
   public static final String PAYLOAD_TYPE_STRING = "payloadType";
   public static final String DATA_TYPE_STRING = "dataType";
   public static final String SERIAL_ID_STRING = "serialId";
   public static final String SANITIZED_STRING = "Sanitized-";
   public static final String DEFAULT_KAFKA_PRODUCER_TYPE = "sync";

   public static final int DEFAULT_SPARK_ROAD_SEGMENT_SNAPPING_TOLERANCE = 20;
   public static final int DEFAULT_METRICS_GRAPHITE_PORT = 2003;
   public static final int DEFAULT_METRICS_POLLING_RATE_SECONDS = 10;

   // Kafka Parameters
   public static final String KAFKA_METADATA_BROKER_LIST = "kafka.metadata.broker.list";
   public static final String KAFKA_CONSUMER_THREADS = "kafka.consumer.threads";
   public static final String ZK_CONNECTION_STRINGS = "zk.connection.strings";
   public static final int DEFAULT_KAFKA_CONSUMER_THREADS = 1;

   public static final String TOKEN_KEY_RSA_PEM = "token.key.rsa.pem";

   public static final String METRICS_PREFIX = "metrics.prefix";
   public static final String METRICS_POLLING_RATE_SECONDS = "metrics.polling.rate.seconds";
   public static final String METRICS_GRAPHITE_HOST = "metrics.graphite.host";
   public static final String METRICS_GRAPHITE_PORT = "metrics.graphite.port";

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
      return hostId;
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
