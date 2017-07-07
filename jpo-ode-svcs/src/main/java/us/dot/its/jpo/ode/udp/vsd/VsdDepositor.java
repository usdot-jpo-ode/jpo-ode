package us.dot.its.jpo.ode.udp.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tomcat.util.buf.HexUtils;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
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
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssVehicleSituationRecord;
import us.dot.its.jpo.ode.util.JsonUtils;

/* 
 * ODE-314
 * The MessageProcessor value type is String 
 */
public class VsdDepositor extends AbstractSubscriberDepositor<String, String> {
    
    private static Coder coder = J2735.getPERUnalignedCoder();
    private ConcurrentHashMap<String, Queue<J2735Bsm>> bsmQueueMap;

    public VsdDepositor(OdeProperties odeProps) {
        super(odeProps, odeProps.getVsdDepositorPort());
        bsmQueueMap = new ConcurrentHashMap<>();
    }

   @Override
    protected byte[] deposit() {
      logger.debug("Message deposit initiated vsd deposit routine (json) {}", record.value());
        byte[] encodedVsd = null;
        /* 
         * ODE-314
         * The record.value() will return a J2735Bsm JSON string 
         */
        String j2735BsmJson = record.value();

        try {
            /* 
             * ODE-314
             * The record.value() will return a J2735Bsm JSON string
             */
            logger.debug("\nConsuming BSM: \n{}\n", j2735BsmJson);

            if (odeProperties.getDepositSanitizedBsmToSdc()) {
                logger.debug(
                        "Sending VSD to SDC IP: {} Port: {}",
                        odeProperties.getSdcIp(),
                        odeProperties.getSdcPort());
                /* 
                 * ODE-314
                 * bundle 10 BSMs with the same tempId into a VSD 
                 */
                
                J2735Bsm j2735Bsm = (J2735Bsm) JsonUtils.fromJson(j2735BsmJson, J2735Bsm.class);
                VehSitDataMessage vsd = null;
                vsd = addToVsdBundle(j2735Bsm);
                
                if (vsd != null) { // NOSONAR
                    encodedVsd = J2735Util.encode(coder, vsd);
                    socket.send(new DatagramPacket(encodedVsd, encodedVsd.length,
                            new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
                }
            }
        } catch (IOException e) {
            logger.error("Error Sending VSD to SDC", e);
        }
        return encodedVsd;
    }

    /**
     * Method will add a BSM to a hashmap of priority queues and returns a VSD when it is fully populated
     * @param j2735Bsm
     * @return a VSD when the bundle is full, null otherwise
     */
   private VehSitDataMessage addToVsdBundle(J2735Bsm j2735Bsm) {
      logger.info("Adding BSM to bundle {}", j2735Bsm.toJson(false));

      VehSitDataMessage vsd = new VehSitDataMessage();
      String tempId = j2735Bsm.getCoreData().getId();
      if (!bsmQueueMap.containsKey(tempId)) {
         logger.info("Adding BSM with tempID {} to VSD package queue", tempId);
         Queue<J2735Bsm> bsmQueue = new PriorityQueue<>(10);
         bsmQueueMap.put(tempId, bsmQueue);
      }
      bsmQueueMap.get(tempId).add(j2735Bsm);

      // After receiving 10 messages, craft the VSD and return it
      if (bsmQueueMap.get(tempId).size() == 10) {

         Bundle vsrBundle = new Bundle();
         logger.info("Received 10 BSMs with identical tempID, creating VSD.");

         // extract the 10 bsms, convert them to vsr, and create VSD
         Queue<J2735Bsm> bsmArray = bsmQueueMap.get(tempId);
         for (J2735Bsm entry : bsmArray) {
            logger.debug("Bsm in array: {}", entry);
            VehSitRecord vsr = OssVehicleSituationRecord.convertBsmToVsr(entry);
            vsrBundle.add(vsr);
         }

         vsd.dialogID = SemiDialogID.vehSitData;
         vsd.seqID = SemiSequenceID.data;
         vsd.groupID = new GroupID(OdeProperties.JPO_ODE_GROUP_ID);
         vsd.requestID = new TemporaryID(HexUtils.fromHexString(tempId));
         vsd.bundle = vsrBundle;
         vsd.crc = new MsgCRC(new byte[] { 0 });
         return vsd;
      } else {
         // Otherwise return null to show we're not ready
         return null;
      }
   }

    // Comparator for the priority queue to keep the chronological order of bsms
    private class BsmComparator implements Comparator<J2735Bsm> {
        @Override
        public int compare(J2735Bsm x, J2735Bsm y) {
            // here getTime would return the time the bsm was received by the
            // ode
            // if (x.getTime() < y.getTime())
            // {
            // return -1;
            // }
            // if (x.getTime() > y.getTime())
            // {
            // return 1;
            // }
            return 0;
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
}
