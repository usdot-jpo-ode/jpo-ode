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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

//import com.bah.ode.wrapper.MQTopic;

@Configuration
public class AppContext {
   private static Logger logger = LoggerFactory.getLogger(AppContext.class);

   // CONSTANTS
   public static final String ODE_HOSTNAME = "ODE_HOSTNAME";
   public static final String PAYLOAD_STRING = "payload";
   public static final String METADATA_STRING = "metadata";
   public static final String METADATA_VIOLATIONS_STRING = "violations";
   public static final String PAYLOAD_TYPE_STRING = "payloadType";
   public static final String DATA_TYPE_STRING = "dataType";
   public static final String SERIAL_ID_STRING = "serialId";
   public static final String SANITIZED_STRING = "Sanitized-";
   public static final int DEFAULT_DDS_NUM_VSR_IN_BUNDLE_TO_USE = 10;
   public static final int DEFAULT_DATA_SEQUENCE_REORDER_DELAY = 1000;
   public static final int DEFAULT_DATA_SEQUENCE_REORDER_PERIOD = 3500;
   public static final int DEFAULT_SPARK_STREAMING_MICROBATCH_DURATION_MS = 1000;
   public static final int DEFAULT_SPARK_STREAMING_WINDOW_MICROBATCHES = 60;
   public static final int DEFAULT_SPARK_STREAMING_SLIDE_MICROBATCHES = 30;
   public static final int DEFAULT_KAFKA_CONSUMER_THREADS = 1;
   public static final String DEFAULT_KAFKA_PRODUCER_TYPE = "sync";

   public static final int DEFAULT_SPARK_ROAD_SEGMENT_SNAPPING_TOLERANCE = 20;
   public static final int DEFAULT_METRICS_GRAPHITE_PORT = 2003;
   public static final int DEFAULT_METRICS_POLLING_RATE_SECONDS = 10;

   public static final String DEFAULT_SPARK_SCHEDULER_MODE = "FIFO";
   public static final boolean DEFAULT_SPARK_RUN_AGGREGATOR_IN_TRANSFORMER = false;
   public static final boolean DEFAULT_SPARK_ODE_AGGREGATOR_ENABLED = true;
   public static final boolean DEFAULT_SPARK_RUN_AGGREGATOR_IN_SLIDING_WINDOW = true;
   public static final String DEFAULT_SPARK_RECEIVER = "single";


   private static final String HOST_SPECIFIC_UID = "HOST_SPECIFIC_UID";


   public static final String LOOPBACK_TEST = "loopback.test";

   // SparkConf related Constants
   public static final String ODE_SPARK_JAR = "ode.spark.jar";

   // web.xml config parameter keys
   public static final String WEB_SERVER_ROOT = "web.server.root";
   public static final String LIFERAY_DB_NAME = "liferay.db.name";
   public static final String LIFERAY_DB_HOST = "liferay.db.host";
   public static final String LIFERAY_WS_SERVER_HOST = "liferay.ws.serverhost";
   public static final String LIFERAY_WS_COMPANY_ID = "liferay.ws.companyId";

   public static final String MAIL_SMTP_HOST = "mail.smtp.host";
   public static final String MAIL_SMTP_PORT = "mail.smtp.port";
   public static final String MAIL_SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";
   public static final String MAIL_FROM = "mail.from";
   public static final String MAIL_USERNAME = "mail.user";
   public static final String MAIL_PASSWORD = "mail.password";

   public static final String DDS_WEBSOCKET_URI = "dds.websocket.uri";
   public static final String DDS_KEYSTORE_FILE_PATH = "dds.keystore.file.path";
   public static final String DDS_KEYSTORE_PASSWORD = "dds.keystore.password";
   public static final String DDS_CAS_URL = "dds.cas.url";
   public static final String DDS_CAS_USERNAME = "dds.cas.username";
   public static final String DDS_CAS_PASSWORD = "dds.cas.password";
   public static final String DDS_NUM_VSR_IN_BUNDLE_TO_USE = "dds.num.vsr.in.bundle.to.use";
   public static final String DATA_SEQUENCE_REORDER_DELAY = "data.sequence.reorder.delay";
   public static final String DATA_SEQUENCE_REORDER_PERIOD = "data.sequence.reorder.period";
   public static final String SERVICE_REGION = "service.region";
   
   // Spark parameters
   /* 
    * All spark parameters must start with "spark." for Spark to recognize them.
    */
   public static final String SPARK_HOME = "spark.home";
   public static final String SPARK_MASTER = "spark.master";
   public static final String SPARK_STREAMING_MICROBATCH_DURATION_MS = "spark.streaming.microbatch.duration.ms";
   public static final String SPARK_STREAMING_WINDOW_MICROBATCHES = "spark.streaming.window.microbatches";
   public static final String SPARK_STREAMING_SLIDE_MICROBATCHES = "spark.streaming.slide.microbatches";
   public static final String SPARK_ASSEMBLY_JAR = "spark.assembly.jar";
   public static final String SPARK_EXECUTOR_MEMORY = "spark.executor.memory";
   public static final String SPARK_DRIVER_MEMORY = "spark.driver.memory";
   public static final String SPARK_SCHEDULER_MODE = "spark.scheduler.mode";



   public static final String SPARK_STATIC_WEATHER_FILE_LOCATION = "spark.static.weather.file.location";
   public static final String SPARK_STATIC_SANITIZATION_FILE_LOCATION = "spark.static.sanitization.file.location";
   public static final String SPARK_STATIC_VALIDATION_FILE_LOCATION = "spark.static.validation.file.location";

   public static final String SPARK_CONFIGURATION_DIRECTORY_HOME = "spark.configuration.file.directory";
   public static final String SPARK_YARN_TRANSFORMER_CONFIGURATION_FILE = "spark.yarn.transformer.configuration.file";
   public static final String SPARK_YARN_AGGREGATOR_CONFIGURATION_FILE = "spark.yarn.aggregator.configuration.file";
   public static final String SPARK_STANDALONE_PROPERTIES_FILE = "spark.standalone.properties.file";
   
   // SPARK ON YARN Cluster Resource Params
   // Can be used to override defaults
   public static final String SPARK_YARN_TRANSFORMER_DRIVER_CORES = "spark.yarn.transformer.driver.cores";
   public static final String SPARK_YARN_TRANSFORMER_DRIVER_MEMORY = "spark.yarn.transformer.driver.memory";
   public static final String SPARK_YARN_TRANSFORMER_EXECUTOR_CORES = "spark.yarn.transformer.executor.cores";
   public static final String SPARK_YARN_TRANSFORMER_EXECUTOR_MEMORY = "spark.yarn.transformer.executor.memory";
   public static final String SPARK_YARN_AGGREGATOR_DRIVER_CORES = "spark.yarn.aggregator.driver.cores";
   public static final String SPARK_YARN_AGGREGATOR_DRIVER_MEMORY = "spark.yarn.aggregator.driver.memory";
   public static final String SPARK_YARN_AGGREGATOR_EXECUTOR_CORES = "spark.yarn.aggregator.executor.cores";
   public static final String SPARK_YARN_AGGREGATOR_EXECUTOR_MEMORY = "spark.yarn.aggregator.executor.memory";
   
   
   public static final String SPARK_METRICS_TRANSFORMER_CONFIGURATION_FILE = "spark.metrics.transformer.configuration.file";
   public static final String SPARK_METRICS_AGGREGATOR_CONFIGURATION_FILE = "spark.metrics.aggregator.configuration.file";

   public static final String SPARK_RUN_AGGREGATOR_IN_TRANSFORMER = "spark.run.aggregator.in.transformer";
   public static final String SPARK_RUN_AGGREGATOR_IN_SLIDING_WINDOW = "spark.run.aggregator.in.sliding.window";
   public static final String SPARK_ODE_AGGREGATOR_ENABLED = "spark.ode.aggregator.enabled";
   public static final String SPARK_RECEIVER = "spark.receiver";

   public static final String SPARK_ROAD_SEGMENT_SNAPPING_TOLERANCE = "spark.road.segment.snapping.tolerance";

   // Kafka Parameters
   public static final String KAFKA_METADATA_BROKER_LIST = "kafka.metadata.broker.list";
   public static final String KAFKA_CONSUMER_THREADS = "kafka.consumer.threads";
   public static final String ZK_CONNECTION_STRINGS = "zk.connection.strings";
   public static final String SPARK_KAFKA_PRODUCER_TYPE = "spark.kafka.produce.type";

   /////////////////////////////////////////////////////////////////////////////
   // Topics used by Spark
   public static final String SPARK_TRANSFORMER_INPUT_TOPIC = "spark.vehicle.transformer.input.topic";
   public static final String SPARK_TRANSFORMER_OUTPUT_TOPIC = "spark.vehicle.transformer.output.topic";
   public static final String SPARK_AGGREGATOR_INPUT_TOPIC = "spark.vehicle.aggregator.input.topic";
   public static final String SPARK_AGGREGATOR_OUTPUT_TOPIC = "spark.vehicle.aggregator.output.topic";
   /////////////////////////////////////////////////////////////////////////////
   
   public static final String TOKEN_KEY_RSA_PEM = "token.key.rsa.pem";

   public static final String METRICS_PREFIX = "metrics.prefix";
   public static final String METRICS_POLLING_RATE_SECONDS = "metrics.polling.rate.seconds";
   public static final String METRICS_GRAPHITE_HOST = "metrics.graphite.host";
   public static final String METRICS_GRAPHITE_PORT = "metrics.graphite.port";

   public static final String PASS_THROUGH_OUTPUT_TOPIC = "pass.through.output.topic";


   
   private static AppContext instance = null;

   @Autowired
   Environment env;
   
   private AppContext() {
   }

   public static AppContext getInstance() {
      if (null == instance) {
         synchronized (AppContext.class) {
            if (null == instance)
               instance = new AppContext();
         }
      }
      return instance;
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
