package us.dot.its.jpo.ode.udp.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
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
import us.dot.its.jpo.ode.util.JsonUtils;

/* 
 * ODE-314
 * The MessageProcessor value type is String 
 */
public class VsdDepositor extends AbstractSubscriberDepositor<String, String> {

   private static final int VSD_PACKAGE_SIZE = 10;

   private ConcurrentHashMap<String, Queue<J2735Bsm>> bsmQueueMap;

   public VsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getVsdDepositorPort());
      bsmQueueMap = new ConcurrentHashMap<>();
   }

   @Override
   protected byte[] deposit() {
      byte[] encodedVsd = null;
      /*
       * ODE-314 The record.value() will return a J2735Bsm JSON string
       */
      String j2735BsmJson = record.value();

      try {
         /*
          * ODE-314 The record.value() will return a J2735Bsm JSON string
          */
         logger.debug("Consuming BSM.");

         if (odeProperties.getDepositSanitizedBsmToSdc()) {

            /*
             * ODE-314 bundle 10 BSMs with the same tempId into a VSD
             */

            J2735Bsm j2735Bsm = (J2735Bsm) JsonUtils.fromJson(j2735BsmJson, J2735Bsm.class);
            VehSitDataMessage vsd = addToVsdBundle(j2735Bsm);

            if (vsd != null) { // NOSONAR
               
               encodedVsd = J2735.getPERUnalignedCoder().encode(vsd).array();
               logger.info("VSD ready to send: (pojo) {}", vsd);
               logger.info("VSD ready to send: (hex) {}", HexUtils.toHexString(encodedVsd));
               if (trustMgr.establishTrust(getRequestId(), getDialogId())) {
                  logger.debug("Sending VSD to SDC IP: {} Port: {}", odeProperties.getSdcIp(),
                        odeProperties.getSdcPort());
                  socket.send(new DatagramPacket(encodedVsd, encodedVsd.length,
                        new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
               } else {
                  logger.error("Failed to establish trust, not sending VSD.");
               }
            }
         }
      } catch (IOException | EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Error Sending VSD to SDC", e);
      }
      return encodedVsd;
   }

   /**
    * Method will add a BSM to a hashmap of priority queues and returns a VSD
    * when it is fully populated
    * 
    * @param j2735Bsm
    * @return a VSD when the bundle is full, null otherwise
    */
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
         vsd.dialogID = getDialogId();
         vsd.seqID = SemiSequenceID.data;
         vsd.groupID = new GroupID(OdeProperties.JPO_ODE_GROUP_ID);
         vsd.requestID = getRequestId();
         vsd.bundle = vsrBundle;
         vsd.crc = new MsgCRC(new byte[] { 0 });
         vsd.type = new VsmType(new byte[] {1}); // "00000001", VehSitRcd that only contains the fundamental data elements

         // now that the vsd is crafted, clear the queue
         bsmQueueMap.get(tempId).clear();
         return vsd;
      } else {
         logger.info("Added BSM with tempID {} to existing VSD package queue ({}/{})", tempId,
               bsmQueueMap.get(tempId).size(), VSD_PACKAGE_SIZE);
         return null;
      }
   }

   /**
    * Comparator for the priority queue to keep the chronological order of bsms
    */
   private class BsmComparator implements Comparator<J2735Bsm> {
      @Override
      public int compare(J2735Bsm x, J2735Bsm y) {
         // TODO - determine message arrival time
         // for now we are using the BSM's time offset property

         int xt = x.getCoreData().getSecMark();
         int yt = y.getCoreData().getSecMark();

         return Integer.compare(xt, yt);
      }
   }

   @Override
   protected SemiDialogID getDialogId() {
      return SemiDialogID.vehSitData;
   }

   @Override
   protected TemporaryID getRequestId() {
      J2735Bsm j2735Bsm = (J2735Bsm) JsonUtils.fromJson(record.value(), J2735Bsm.class);
      return new TemporaryID(HexUtils.fromHexString(j2735Bsm.getCoreData().getId()));
   }

   @Override
   protected Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
}
