package us.dot.its.jpo.ode.isdm;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.ode.OdeProperties;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


/*
 * This class receives VSD from the OBU and forwards it to the SDC.
 */
public class IsdDepositor implements Runnable {
    private OdeProperties odeProps;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private DatagramSocket socket = null;
    private byte[] encodedIsd;

    public IsdDepositor(OdeProperties odeProps, byte[] encodedIsd) {
        this.encodedIsd = encodedIsd;
        this.odeProps = odeProps;
        try {
            socket = new DatagramSocket(odeProps.getForwarderPort());
            logger.debug("Created VSD depositor Socket with port {}", odeProps.getIsdmSenderPort());
        } catch (SocketException e) {
            logger.error("Error creating socket with port {}", odeProps.getForwarderPort(),
                    e);
        }
    }

    @Override
    public void run() {
        try {
            logger.debug("\nVSD in hex: \n{}\n", Hex.encodeHexString(encodedIsd));
            logger.debug("Sending ISD to SDC IP: {} Port: {}", odeProps.getSdcIp(),
                    odeProps.getSdcPort());
            socket.send(new DatagramPacket(encodedIsd, encodedIsd.length,
                    new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
        } catch (IOException e) {
            logger.error("Error Sending Isd to SDC {}", e);
        }

        if (this.socket != null) {
            logger.debug("Closing isd depositor socket with port {}", odeProps.getIsdmSenderPort());
            socket.close();
        }
    }

}
