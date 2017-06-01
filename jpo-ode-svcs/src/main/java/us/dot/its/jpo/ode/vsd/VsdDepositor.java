package us.dot.its.jpo.ode.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

/*
 * This class receives VSD from the OBU and forwards it to the SDC.
 */
/* 
 * TODO ODE-314
 * The MessageProcessor value type will be String 
 */
public class VsdDepositor extends MessageProcessor<String, byte[]> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private OdeProperties odeProps;
	private DatagramSocket socket = null;

	public VsdDepositor(OdeProperties odeProps) {
		this.odeProps = odeProps;
		
		try {
            logger.debug("Creating VSD depositor Socket with port {}", odeProps.getVsdSenderPort());
			socket = new DatagramSocket(odeProps.getForwarderPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + odeProps.getForwarderPort(), e);
		}
	}

    @Override
    public Object call() throws Exception {
        /* 
         * TODO ODE-314
         * The record.value() will return a J2735Bsm JSON string 
         */
        byte[] encodedVsd = record.value();

        try {
            /* 
             * TODO ODE-314
             * The record.value() will return a J2735Bsm JSON string
             * Adjust code accordingly 
             */
            logger.debug("\nConsuming VSD (in hex): \n{}\n", Hex.encodeHexString(encodedVsd));

            if (odeProps.getDepositSanitizedBsmToSdc()) {
                logger.debug(
                        "Sending VSD to SDC IP: {} Port: {}",
                        odeProps.getSdcIp(),
                        odeProps.getSdcPort());
                /* 
                 * TODO ODE-314
                 * The record.value() will return a J2735Bsm JSON string
                 * Adjust code accordingly 
                 */
                socket.send(new DatagramPacket(encodedVsd, encodedVsd.length,
                        new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
            }
        } catch (IOException e) {
            logger.error("Error Sending VSD to SDC", e);
        }

//        if (this.socket != null) {
//            logger.debug("Closing vsd depositor socket with port {}", odeProps.getVsdmSenderPort());
//            socket.close();
//        }
        return encodedVsd;
    }

}
