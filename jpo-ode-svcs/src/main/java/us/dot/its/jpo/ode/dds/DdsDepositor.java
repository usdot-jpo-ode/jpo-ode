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
package us.dot.its.jpo.ode.dds;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.AbstractWebSocketClient;
import us.dot.its.jpo.ode.model.OdeDataType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public class DdsDepositor<T> extends AbstractWebSocketClient { // NOSONAR

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private OdeProperties odeProperties;
   private DdsRequestManager<DdsStatusMessage> requestManager;
   private OdeDepRequest depRequest;

   public DdsDepositor(OdeProperties odeProperties) {
      super();
      this.odeProperties = odeProperties;

      depRequest = new OdeDepRequest();
      depRequest.setDataSource(DataSource.SDW);
      depRequest.setDataType(OdeDataType.AsnHex);
      depRequest.setEncodeType("hex");
      depRequest.setRequestType(OdeRequestType.Deposit);
   }

   public void deposit(DdsAdvisorySituationData asdMsg) throws DdsRequestManagerException {
      deposit(asdMsg.getAsdmDetails().getAdvisoryMessageBytes());
   }

   public void deposit(String encodedAsdMsg) throws DdsRequestManagerException {

      setUpReqMgr();

      depRequest.setData(encodedAsdMsg);

      this.requestManager.sendRequest(depRequest);
   }

   private void setUpReqMgr() throws DdsRequestManagerException {
      if (this.requestManager == null) {
         this.requestManager = new DdsDepositRequestManager(odeProperties);
      }

      if (!this.requestManager.isConnected()) {
         this.requestManager.connect((WebSocketMessageHandler<DdsStatusMessage>) new StatusMessageHandler(this),
            DepositResponseDecoder.class);
      }
   }

   @Override
   public void onClose(CloseReason reason) {
      try {
         this.requestManager.close();
      } catch (DdsRequestManagerException e) {
         logger.error("Error closing DDS Request Manager", e);
      }
   }

   @Override
   public void onMessage(OdeMessage message) {
      logger.info("Deposit Response: {}", message);
   }

   @Override
   public void onOpen(Session session) {
      logger.info("DDS Message Handler Opened Session {} ", session.getId());
   }

   @Override
   public void onError(Throwable t) {
      logger.error("Error reported by DDS Message Handler", t);
   }

   public OdeDepRequest getDepRequest() {
      return depRequest;
   }

   @SuppressWarnings("unchecked")
   public void setRequestManager(DdsRequestManager<T> requestManager) {
      this.requestManager = (DdsRequestManager<DdsStatusMessage>) requestManager;
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }

   public void setLogger(Logger logger) {
      this.logger = logger;
   }

}
