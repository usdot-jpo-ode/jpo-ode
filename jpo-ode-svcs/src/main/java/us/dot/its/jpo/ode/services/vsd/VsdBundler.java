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
package us.dot.its.jpo.ode.services.vsd;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class VsdBundler {

   private static final Logger logger = LoggerFactory.getLogger(VsdBundler.class);
   private static final int VSD_PACKAGE_SIZE = 10;

   private ConcurrentHashMap<String, Queue<J2735Bsm>> bsmQueueMap;

   public VsdBundler() {
      this.bsmQueueMap = new ConcurrentHashMap<>();
   }

   //TODO open-ode
//   public VehSitDataMessage addToVsdBundle(J2735Bsm j2735Bsm) {
//
//      String tempId = j2735Bsm.getCoreData().getId();
//      if (!bsmQueueMap.containsKey(tempId)) {
//         logger.info("Creating new VSD package queue for BSMs with tempID {} to VSD package queue", tempId);
//         Queue<J2735Bsm> bsmQueue = new PriorityQueue<>(VSD_PACKAGE_SIZE, new BsmComparator());
//         bsmQueueMap.put(tempId, bsmQueue);
//      }
//
//      bsmQueueMap.get(tempId).add(j2735Bsm);
//
//      // After receiving enough messages, craft the VSD and return it
//      if (bsmQueueMap.get(tempId).size() == VSD_PACKAGE_SIZE) {
//
//         logger.info("BSM queue ID {} full, crafting VSD", tempId);
//
//         // convert the BSMs in the priority queue to VSRs to craft VSD bundle
//         Bundle vsrBundle = new Bundle();
//         Queue<J2735Bsm> bsmArray = bsmQueueMap.get(tempId);
//         for (J2735Bsm entry : bsmArray) {
//            VehSitRecord vsr = OssVehicleSituationRecord.convertBsmToVsr(entry);
//            vsrBundle.add(vsr);
//         }
//
//         VehSitDataMessage vsd = new VehSitDataMessage();
//         vsd.bundle = vsrBundle;
//         vsd.dialogID = SemiDialogID.vehSitData;
//         vsd.seqID = SemiSequenceID.data;
//         vsd.groupID = new GroupID(OdeProperties.getJpoOdeGroupId());
//         vsd.requestID = new TemporaryID(CodecUtils.fromHex(tempId));
//         vsd.crc = new MsgCRC(new byte[] { 0, 0 });
//         vsd.type = new VsmType(new byte[] { 1 }); // 1=fundamental sit. status
//
//         bsmQueueMap.remove(tempId); // prevent duplicates
//         return vsd;
//      } else {
//         logger.info("Added BSM with tempID {} to existing VSD package queue ({}/{})", tempId,
//               bsmQueueMap.get(tempId).size(), VSD_PACKAGE_SIZE);
//         return null;
//      }
//   }

}
