package us.dot.its.jpo.ode.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;

public class IsdDepositor extends AbstractSubscriberDepositor<String, byte[]> {
    
	public IsdDepositor(OdeProperties odeProps) {
        super(odeProps, odeProps.getIsdDepositorPort(), SemiDialogID.intersectionSitDataDep);
	}

    @Override
    protected byte[] deposit() {
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
