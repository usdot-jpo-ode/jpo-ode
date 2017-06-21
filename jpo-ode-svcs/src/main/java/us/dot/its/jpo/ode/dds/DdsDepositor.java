package us.dot.its.jpo.ode.dds;

import java.text.ParseException;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.AbstractWebSocketClient;
import us.dot.its.jpo.ode.model.OdeDataType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.traveler.AsdMessage;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public class DdsDepositor<MessageType> extends AbstractWebSocketClient { // NOSONAR

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private OdeProperties odeProperties;
   private DdsRequestManager<MessageType> requestManager;
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

   // @SuppressWarnings("unchecked")
   public void deposit(AsdMessage asdMsg) throws DdsRequestManagerException, DdsClientException, WebSocketException,
         ParseException, EncodeFailedException, EncodeNotSupportedException {

      if (this.requestManager == null) {

         this.requestManager = (DdsRequestManager<MessageType>) new DdsDepositRequestManager(odeProperties);
      }

      if (!this.requestManager.isConnected()) {
         this.requestManager.connect((WebSocketMessageHandler<MessageType>) new StatusMessageHandler(this),
               DepositResponseDecoder.class);
      }

      depRequest.setData(asdMsg.encodeHex());

      this.requestManager.sendRequest(depRequest);
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

   public void setRequestManager(DdsRequestManager<MessageType> requestManager) {
      this.requestManager = requestManager;
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }

   public void setLogger(Logger logger) {
      this.logger = logger;
   }

}
