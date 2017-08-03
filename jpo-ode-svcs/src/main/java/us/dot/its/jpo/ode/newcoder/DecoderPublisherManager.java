package us.dot.its.jpo.ode.newcoder;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.J2735Plugin;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;

public class DecoderPublisherManager {

   private static final Logger logger = LoggerFactory.getLogger(DecoderPublisherManager.class);
   
   private static AtomicInteger bundleId = new AtomicInteger(1);

   private JsonDecoderPublisher jsonDecPub;
   private HexDecoderPublisher hexDecPub;
   private BinaryDecoderPublisher binDecPub;
   

   @Autowired
   public DecoderPublisherManager(OdeProperties odeProperties) {
      
      OssJ2735Coder jDec = null;
      
      logger.info("Loading ASN1 Coder: {}", odeProperties.getJ2735CoderClassName());
      try {
         jDec = (OssJ2735Coder) PluginFactory.getPluginByName(odeProperties.getJ2735CoderClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
          logger.error("Unable to load plugin: " + odeProperties.getJ2735CoderClassName(), e);
      }
      
      Oss1609dot2Coder ieee1609dotCoder = new Oss1609dot2Coder();
      
      MessagePublisher messagePub = new MessagePublisher(odeProperties);
      
      SerialId serialId = new SerialId();

      this.jsonDecPub = new JsonDecoderPublisher(messagePub, serialId);
      this.hexDecPub = new HexDecoderPublisher(messagePub, jDec, serialId, bundleId);
      this.binDecPub = new BinaryDecoderPublisher(jDec, ieee1609dotCoder, serialId, messagePub);
   }

   public void decodeAndPublishFile(Path filePath, InputStream fileInputStream) throws Exception {
      logger.info("Decoding and publishing file {}", filePath.toFile());
      
      String fileName = filePath.toFile().getName();

      if (filePath.toString().endsWith(".hex") || filePath.toString().endsWith(".txt")) {
         logger.info("Decoding {} as hex/binary file.", filePath);
         hexDecPub.decodeAndPublish(fileInputStream, fileName);
      } else if (filePath.toString().endsWith(".json")) {
         logger.info("Decoding {} as json file.", filePath);
         jsonDecPub.decodeAndPublish(fileInputStream, fileName);
      } else {
         logger.info("Decoding {} as signed file.", filePath);
         binDecPub.decodeBinaryAndPublish(fileInputStream, fileName);
      }
   }

}
