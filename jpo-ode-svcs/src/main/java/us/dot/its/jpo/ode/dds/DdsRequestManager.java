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

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequest.SystemName;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public abstract class DdsRequestManager<T> {
   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private DdsClient<T> ddsClient;
   private Session session;

   private boolean connected = false;

   protected OdeProperties odeProperties;

   private WebSocketEndpoint<T> wsClient;

   public DdsRequestManager(OdeProperties odeProperties) throws DdsRequestManagerException {

      this.odeProperties = odeProperties;

      try {
         ddsClient = new DdsClient<>(this.odeProperties.getDdsCasUrl(), this.odeProperties.getDdsCasUsername(),
               this.odeProperties.getDdsCasPassword(), this.odeProperties.getDdsWebsocketUrl(), null, null);

      } catch (Exception e) {
         throw new DdsRequestManagerException("Error initializing DdsRequestManager", e);
      }
   }

   // @SuppressWarnings("unchecked")
   public Session connect(WebSocketMessageHandler<T> messageHandler, Class<?> decoder)
         throws DdsRequestManagerException {

      try {
         wsClient = ddsClient.login((Class<? extends WebSocketMessageDecoder<T>>) decoder, messageHandler);

         session = wsClient.connect();

         connected = session != null;

         return session;
      } catch (Exception e) {
         throw new DdsRequestManagerException("Error connecting to DDS", e);
      }
   }

   public void sendRequest(OdeRequest odeRequest) throws DdsRequestManagerException {
      try {
         if (session == null) {
            logger.info("Connecting to DDS");
            session = wsClient.connect();
            connected = session != null;
         }

         if (session != null) {
            // Send the new request
            DdsRequest ddsRequest = buildDdsRequest(odeRequest);
            if (ddsRequest != null) {
               String sDdsRequest = ddsRequest.toString();

               String logMsg = "Sending request to DDS: " + sDdsRequest;
               logger.info(logMsg);
               EventLogger.logger.info(logMsg);
               wsClient.send(sDdsRequest);
            }
         } else {
            throw new DdsRequestManagerException("DDS Client Session is null, probably NOT CONNECTED.");
         }
      } catch (Exception e) {
         try {
            wsClient.close();
            connected = false;
         } catch (WebSocketException e1) {
            logger.error("Error Closing DDS Client.", e1);
         }
         throw new DdsRequestManagerException("Error sending Data Request: " + e);
      }
   }

   protected abstract DdsRequest buildDdsRequest(OdeRequest odeRequest) throws DdsRequestManagerException;

   public static SystemName systemName(OdeRequest odeRequest) {
      SystemName sysName;

      OdeRequest.DataSource dataSource;

      if (odeRequest.getDataSource() != null) {
         dataSource = odeRequest.getDataSource();
      } else {
         dataSource = defaultDataSource(odeRequest);
      }

      switch (dataSource) {
      case SDC:
      case DEPOSIT_SDC:
         sysName = SystemName.SDC;
         break;
      case SDW:
      case DEPOSIT_SDW:
         sysName = SystemName.SDW;
         break;
      case SDPC:
         sysName = SystemName.SDPC;
         break;
      default:
         sysName = defaultSystemName(odeRequest);
      }

      return sysName;
   }

   public static DataSource defaultDataSource(OdeRequest odeRequest) {
      DataSource dataSource;
      if (odeRequest.getRequestType() == OdeRequestType.Subscription)
         dataSource = DataSource.SDC;
      else
         dataSource = DataSource.SDW;
      return dataSource;
   }

   public static SystemName defaultSystemName(OdeRequest odeRequest) {
      SystemName sysName;
      if (odeRequest.getRequestType() == OdeRequestType.Subscription)
         sysName = SystemName.SDC;
      else
         sysName = SystemName.SDW;
      return sysName;
   }

   public DdsClient<T> getDdsClient() {
      return ddsClient;
   }

   public void close() throws DdsRequestManagerException {
      connected = false;
      session = null;

      if (wsClient != null) {
         try {
            logger.info("Closing WebSocket Client.");
            wsClient.close();
            wsClient = null;
         } catch (WebSocketException e) {
            throw new DdsRequestManagerException("Error closing DDS Client: ", e);
         }
      }
   }

   public Session getSession() {
      return session;
   }

   public boolean isConnected() {
      return connected;
   }

   public WebSocketEndpoint<T> getWsClient() {
      return wsClient;
   }

   public static class DdsRequestManagerException extends Exception {
      private static final long serialVersionUID = 1L;

      public DdsRequestManagerException(String message, Exception e) {
         super(message, e);
      }

      public DdsRequestManagerException(String message) {
         super(message);
      }
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }

   public void setDdsClient(DdsClient<T> pDdsClient) {
      this.ddsClient = pDdsClient;
   }

   public void setWsClient(WebSocketEndpoint<T> pWsClient) {
      this.wsClient = pWsClient;
   }

   public void setLogger(Logger newLogger) {
      this.logger = newLogger;
   }
}
