package us.dot.its.jpo.ode.services.vsd;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
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

public class VsdBundler {

   private static final Logger logger = LoggerFactory.getLogger(VsdBundler.class);
   private static final int VSD_PACKAGE_SIZE = 10;

   private ConcurrentHashMap<String, Queue<J2735Bsm>> bsmQueueMap;

   public VsdBundler() {

      this.bsmQueueMap = new ConcurrentHashMap<>();
   }

   public VehSitDataMessage addToVsdBundle(J2735Bsm j2735Bsm) {

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
