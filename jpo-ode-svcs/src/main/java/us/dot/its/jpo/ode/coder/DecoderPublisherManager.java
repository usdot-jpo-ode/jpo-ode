package us.dot.its.jpo.ode.coder;

import java.io.InputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.BinaryDecoderPublisher;
import us.dot.its.jpo.ode.coder.stream.HexDecoderPublisher;
import us.dot.its.jpo.ode.coder.stream.JsonDecoderPublisher;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;

public class DecoderPublisherManager {

   private static final Logger logger = LoggerFactory.getLogger(DecoderPublisherManager.class);

   private JsonDecoderPublisher jsonDecPub;
   private HexDecoderPublisher hexDecPub;
   private BinaryDecoderPublisher binDecPub;

   @Autowired
   public DecoderPublisherManager(OdeProperties odeProperties) throws Exception {

      OssJ2735Coder j2735Coder = null;

      logger.info("Loading ASN1 Coder: {}", odeProperties.getJ2735CoderClassName());
      try {
         j2735Coder = (OssJ2735Coder) PluginFactory.getPluginByName(odeProperties.getJ2735CoderClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         logger.error("Unable to load plugin: " + odeProperties.getJ2735CoderClassName(), e);
         throw new Exception("Unable to load plugin.", e);
      }

      Oss1609dot2Coder ieee1609dotCoder = new Oss1609dot2Coder();
      DecoderHelper decoderHelper = new DecoderHelper(j2735Coder, ieee1609dotCoder);

      MessagePublisher messagePub = new MessagePublisher(odeProperties);

      this.jsonDecPub = new JsonDecoderPublisher(messagePub);
      this.hexDecPub = new HexDecoderPublisher(messagePub, decoderHelper);
      this.binDecPub = new BinaryDecoderPublisher(messagePub, decoderHelper);
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
         binDecPub.decodeAndPublish(fileInputStream, fileName);
      }
   }

}
