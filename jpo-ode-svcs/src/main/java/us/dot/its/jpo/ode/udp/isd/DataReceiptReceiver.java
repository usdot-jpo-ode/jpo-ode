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
package us.dot.its.jpo.ode.udp.isd;
//TODO open-ode
//import java.net.DatagramSocket;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.oss.asn1.AbstractData;
//import com.oss.asn1.DecodeFailedException;
//import com.oss.asn1.DecodeNotSupportedException;
//
//import us.dot.its.jpo.ode.OdeProperties;
//import us.dot.its.jpo.ode.udp.AbstractConcurrentUdpReceiver;
//
//public class DataReceiptReceiver extends AbstractConcurrentUdpReceiver {
//
//   private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//   public DataReceiptReceiver(OdeProperties odeProps, DatagramSocket sock) {
//      super(sock, odeProps.getDataReceiptBufferSize());
//      logger.debug("DataReceiptReceiver spawned.");
//   }
//
//   @Override
//   protected AbstractData processPacket(byte[] data) throws DecodeFailedException, DecodeNotSupportedException {
//      return null;
//
//      DataReceipt receipt = null;
//      AbstractData response = J2735Util.decode(J2735.getPERUnalignedCoder(), data);
//
//      if (response instanceof DataReceipt) {
//         receipt = (DataReceipt) response;
//
//         String hex = HexUtils.toHexString(data);
//         logger.debug("Received DataReceipt (hex): {}", hex);
//         logger.debug("Received DataReceipt (json): {}", receipt);
//      }
//      return receipt;
//   }
//
//}
