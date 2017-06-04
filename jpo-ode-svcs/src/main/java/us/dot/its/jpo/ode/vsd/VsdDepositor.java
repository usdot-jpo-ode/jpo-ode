package us.dot.its.jpo.ode.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.dds.TrustManager;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

/* 
 * ODE-314
 * The MessageProcessor value type changed to String 
 */
public class VsdDepositor extends MessageProcessor<String, String> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OdeProperties odeProperties;
	private DatagramSocket socket = null;
	private TrustManager trustMgr;
    private static Coder coder = J2735.getPERUnalignedCoder();

	public VsdDepositor(OdeProperties odeProps) {
		this.odeProperties = odeProps;
		
		try {
            logger.debug("Creating VSD depositor Socket with port {}", odeProps.getVsdDepositorPort());
			socket = new DatagramSocket(odeProps.getVsdDepositorPort());
			trustMgr = new TrustManager(odeProps, socket);
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + odeProps.getVsdDepositorPort(), e);
		}
	}

    public void subscribe(String... topics) {
        /* 
         * ODE-314
         * Changed to MessageConsumer.defaultStringMessageConsumer() method 
         */
        MessageConsumer<String, String> consumer = 
                MessageConsumer.defaultStringMessageConsumer(
                        odeProperties.getKafkaBrokers(), 
                        odeProperties.getHostId() + this.getClass().getSimpleName(),
                        this);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                /* 
                 * ODE-314
                 * The argument to subscribe method changed to 
                 * odeProps.getKafkaTopicBsmFilteredJson()
                 */
                consumer.subscribe(topics);
            }
        });
    }

    @Override
    public Object call() throws Exception {
        byte[] encodedMsg = null;
        
        if (!trustMgr.isTrustEstablished()) {
            trustMgr.establishTrust(
                    odeProperties.getSdcIp(), 
                    odeProperties.getSdcPort(),
                    SemiDialogID.vehSitData);
        }

        encodedMsg = depositVsdToSdc();

        return encodedMsg;
    }

    private byte[] depositVsdToSdc() {
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
                VehSitDataMessage vsd = addToVsdBundle(j2735Bsm);
                
                if (vsd != null) {
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
        // TODO Auto-generated method stub
        // ODE-314
        return null;
    }

}
