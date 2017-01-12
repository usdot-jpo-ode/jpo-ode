package us.dot.its.jpo.ode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.eventlog.EventLogger;

@ConfigurationProperties("ode")
@org.springframework.context.annotation.PropertySource("classpath:application.properties")
public class OdeProperties implements EnvironmentAware {
   private Logger logger = LoggerFactory.getLogger(this.getClass());


   /////////////////////////////////////////////////////////////////////////////
   // Kafka Topics
   public static final String KAFKA_TOPIC_J2735_BSM = "topic.J2735Bsm";
   /////////////////////////////////////////////////////////////////////////////

   @Autowired
   private Environment env;

   private String uploadLocation = "uploads";
   private String pluginsLocations = "plugins";
   private String asn1CoderClassName = "us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder";
   private String kafkaBrokers = null;
   private String kafkaProducerType = AppContext.DEFAULT_KAFKA_PRODUCER_TYPE;
   private int importerInterval = 1000;

   private String hostId;

   public OdeProperties() throws Exception {
      super();
      init();
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

   public String getHostId() {
      return hostId;
   }

   public String getUploadLocation() {
      return uploadLocation;
   }

   public void setUploadLocation(String uploadLocation) {
      this.uploadLocation = uploadLocation;
   }

   public String getPluginsLocations() {
      return pluginsLocations;
   }

   public void setPluginsLocations(String pluginsLocations) {
      this.pluginsLocations = pluginsLocations;
   }

   public String getAsn1CoderClassName() {
      return asn1CoderClassName;
   }

   public void setAsn1CoderClassName(String asn1CoderClassName) {
      this.asn1CoderClassName = asn1CoderClassName;
   }

   public String getKafkaBrokers() {
      return kafkaBrokers;
   }

   public void setKafkaBrokers(String kafkaBrokers) {
      this.kafkaBrokers = kafkaBrokers;
   }

   public String getKafkaProducerType() {
      return kafkaProducerType;
   }

   public void setKafkaProducerType(String kafkaProducerType) {
      this.kafkaProducerType = kafkaProducerType;
   }

   public int getImporterInterval() {
      return importerInterval;
   }

   public void setImporterInterval(int importerInterval) {
      this.importerInterval = importerInterval;
   }

   public Environment getEnv() {
      return env;
   }

   public void setEnv(Environment env) {
      this.env = env;
   }

   public void init() throws Exception {
      String hostname;
      try {
         hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
         // Let's just use a random hostname
         hostname = UUID.randomUUID().toString();
      }
      hostId = hostname;
      logger.info("Host ID: {}", hostId);
      EventLogger.logger.info("Initializing services on host {}", hostId);
      
      if (kafkaBrokers == null) {
         logger.info("ode.kafkaBrokers property not defined. Will try DOCKER_HOST_IP from which will derive the Kafka bootstrap-server");
         kafkaBrokers = System.getenv("DOCKER_HOST_IP") + ":9092";
      }

      if (kafkaBrokers == null)
         throw new Exception("ode.kafkaBrokers ode property nor DOCKER_HOST_IP environment variable are defined");
   }

   @Override
   public void setEnvironment(Environment environment) {
      env = environment;
   }

}
