package us.dot.its.jpo.ode.services.vsd;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCRC;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmType;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssVehicleSituationRecord;
import us.dot.its.jpo.ode.udp.bsm.BsmComparator;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubPubTransformer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

/**
 * Kafka consumer/publisher that creates VSDs from BSMs.
 * 
 * Input stream: j2735FilteredBsm (JSON) Output stream: topic.asnVsd (byte)
 * 
 * dialogID = SemiDialogID.vehSitData seqID = SemiSequenceID.data groupID =
 * "jode".bytes requestID from BSMs
 * 
 * VehSitDataMessage ::= SEQUENCE { dialogID SemiDialogID, -- 0x9A Vehicle
 * Situation Data Deposit seqID SemiSequenceID, -- 0x05 Data Content groupID
 * GroupID, -- unique ID used to identify an organization requestID
 * DSRC.TemporaryID, -- random 4 byte ID generated following trust establishment
 * type VsmType, -- the type of vehicle situation data included bundle SEQUENCE
 * (SIZE (1..10)) OF VehSitRecord, -- sets of situation data records crc
 * DSRC.MsgCRC }
 */
public class BsmToVsdPackager<V> extends AbstractSubPubTransformer<String, V, byte[]> {

   private static final Logger logger = LoggerFactory.getLogger(BsmToVsdPackager.class);

   private static final int VSD_PACKAGE_SIZE = 10;
   private final PERUnalignedCoder coder;
   private ConcurrentHashMap<String, Queue<J2735Bsm>> bsmQueueMap;

   public BsmToVsdPackager(MessageProducer<String, byte[]> producer, String outputTopic) {
      super(producer, (java.lang.String) outputTopic);
      this.coder = J2735.getPERUnalignedCoder();
      this.bsmQueueMap = new ConcurrentHashMap<>();
   }

   @Override
   protected byte[] transform(V consumedData) {

      if (null == consumedData) {
         return new byte[0];
      }

      logger.debug("VsdDepositor received data: {}", consumedData);

      J2735Bsm bsmData = (J2735Bsm) JsonUtils.fromJson((String) consumedData, J2735Bsm.class);

      byte[] encodedVsd = null;
      try {
         logger.debug("Consuming BSM.");

         VehSitDataMessage vsd = addToVsdBundle(bsmData);

         // Only full VSDs (10) will be published
         // TODO - toggleable mechanism for periodically publishing not-full
         // VSDs
         if (vsd != null) {
            logger.debug("VSD ready to send: (pojo) {}", vsd); // if encoding
                                                               // fails, look at
                                                               // this for
                                                               // mistakes
            encodedVsd = coder.encode(vsd).array();
            String hexVsd = HexUtils.toHexString(encodedVsd);
            logger.debug("VSD ready to send: (hex) {}", hexVsd);
         }
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Error Sending VSD to SDC", e);
      }
      return encodedVsd;
   }

   private VehSitDataMessage addToVsdBundle(J2735Bsm j2735Bsm) {

      String tempId = j2735Bsm.getCoreData().getId();
      if (!bsmQueueMap.containsKey(tempId)) {
         logger.info("Creating new VSD package queue for BSMs with tempID {} to VSD package queue", tempId);
         Queue<J2735Bsm> bsmQueue = new PriorityQueue<>(VSD_PACKAGE_SIZE, new BsmComparator());
         bsmQueueMap.put(tempId, bsmQueue);
      }

      bsmQueueMap.get(tempId).add(j2735Bsm);

      // After receiving enough messages, craft the VSD and return it
      if (bsmQueueMap.get(tempId).size() == VSD_PACKAGE_SIZE) {

         logger.info("BSM queue ID {} full, crafting VSD", tempId);

         // convert the BSMs in the priority queue to VSRs to craft VSD bundle
         Bundle vsrBundle = new Bundle();
         Queue<J2735Bsm> bsmArray = bsmQueueMap.get(tempId);
         for (J2735Bsm entry : bsmArray) {
            VehSitRecord vsr = OssVehicleSituationRecord.convertBsmToVsr(entry);
            vsrBundle.add(vsr);
         }

         VehSitDataMessage vsd = new VehSitDataMessage();
         vsd.bundle = vsrBundle;
         vsd.dialogID = SemiDialogID.vehSitData;
         vsd.seqID = SemiSequenceID.data;
         vsd.groupID = new GroupID(OdeProperties.getJpoOdeGroupId());
         vsd.requestID = new TemporaryID(CodecUtils.fromHex(tempId));
         vsd.crc = new MsgCRC(new byte[] { 0, 0 });
         vsd.type = new VsmType(new byte[] { 1 }); // 1=fundamental sit. status

         bsmQueueMap.remove(tempId); // prevent duplicates
         return vsd;
      } else {
         logger.info("Added BSM with tempID {} to existing VSD package queue ({}/{})", tempId,
               bsmQueueMap.get(tempId).size(), VSD_PACKAGE_SIZE);
         return null;
      }
   }
}