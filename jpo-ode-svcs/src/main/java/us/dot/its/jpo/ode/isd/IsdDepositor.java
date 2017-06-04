package us.dot.its.jpo.ode.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.TrustManager;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class IsdDepositor extends MessageProcessor<String, byte[]> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OdeProperties odeProperties;
	private DatagramSocket socket = null;
	private TrustManager trustMgr;

	public IsdDepositor(OdeProperties odeProps) {
		this.odeProperties = odeProps;
		
		try {
            logger.debug("Creating ISD depositor Socket with port {}", odeProps.getIsdDepositorPort());
			socket = new DatagramSocket(odeProps.getIsdDepositorPort());
			trustMgr = new TrustManager(odeProps, socket);
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + odeProps.getIsdDepositorPort(), e);
		}
	}

    public void subscribe(String... topics) {
        MessageConsumer<String, byte[]> consumer = 
                MessageConsumer.defaultByteArrayMessageConsumer(
                        odeProperties.getKafkaBrokers(), 
                        odeProperties.getHostId() + this.getClass().getSimpleName(),
                        this);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
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
                    SemiDialogID.intersectionSitDataDep);
        }

        encodedMsg = depositIsdToSdc();

        return encodedMsg;
    }

    private byte[] depositIsdToSdc() {
        /* 
         * The record.value() will return an encoded ISD 
         */
        byte[] encodedIsd = record.value();

        try {
            logger.debug(
                    "Sending Isd to SDC IP: {} Port: {}",
                    odeProperties.getSdcIp(),
                    odeProperties.getSdcPort());
            socket.send(new DatagramPacket(
                    encodedIsd,
                    encodedIsd.length,
                        new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
        } catch (IOException e) {
            logger.error("Error Sending Isd to SDC", e);
        }
        return encodedIsd;
    }

}
