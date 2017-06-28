package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.udp.TrustManager;
import us.dot.its.jpo.ode.udp.TrustManager.TrustManagerException;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public abstract class AbstractSubscriberDepositor<K, V> extends MessageProcessor<K, V> {

   protected Logger logger = LoggerFactory.getLogger(this.getClass());
   protected OdeProperties odeProperties;
   protected int depositorPort;
   protected DatagramSocket socket = null;
   protected TrustManager trustMgr;
   protected TemporaryID requestId;
   protected SemiDialogID dialogId;
   protected GroupID groupId;
   protected int messagesSent;
   protected Coder coder;
   protected ExecutorService pool;

   public AbstractSubscriberDepositor(OdeProperties odeProps, int port, SemiDialogID dialogId) {
       //TODO ODE-314 isd debug fail: requestId and groupId are not initialized in the constructor
      this.odeProperties = odeProps;
      this.depositorPort = port;
      this.dialogId = dialogId;
      this.messagesSent = 0;
      this.coder = J2735.getPERUnalignedCoder();

      try {
         logger.debug("Creating depositor socket on port {}", port);
         socket = new DatagramSocket(port);
         trustMgr = new TrustManager(odeProps, socket);
         pool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }

   /**
    * Starts a Kafka listener that runs call() every time a new msg arrives
    * 
    * @param consumer
    * @param topics
    */
   public void subscribe(MessageConsumer<K, V> consumer, String... topics) {
      Executors.newSingleThreadExecutor().submit(new Runnable() {
         @Override
         public void run() {
            consumer.subscribe(topics);
         }
      });
   }

   @Override
   public Object call() {
      logger.debug("Subscriber received data.");
      byte[] encodedMsg = null;

      // Verify trust before depositing, else establish trust
      if (trustMgr.isTrustEstablished() && !trustMgr.isEstablishingTrust()) {
         encodedMsg = deposit();
      } else if (!trustMgr.isEstablishingTrust()) {
         logger.info("Starting trust establishment...");
         messagesSent = 0;

         try {
             Boolean trustEst = trustMgr.establishTrust(depositorPort,
             odeProperties.getSdcIp(),
             odeProperties.getSdcPort(), requestId, dialogId, groupId);
            logger.debug("Trust established: {}", trustEst);
            trustMgr.setTrustEstablished(trustEst);
         } catch (SocketException | TrustManagerException e) {
            logger.error("Error establishing trust: {}", e);
         }
      } else {
         logger.info("Not depositing message, trust establishment in progress.");
      }

      return encodedMsg;
   }

   public int getDepositorPort() {
      return depositorPort;
   }

   public void setDepositorPort(int depositorPort) {
      this.depositorPort = depositorPort;
   }

   public DatagramSocket getSocket() {
      return socket;
   }

   public void setSocket(DatagramSocket socket) {
      this.socket = socket;
   }

   public SemiDialogID getDialogId() {
      return dialogId;
   }

   public void setDialogId(SemiDialogID dialogId) {
      this.dialogId = dialogId;
   }

   protected abstract byte[] deposit();

}
