package us.dot.its.jpo.ode.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

/* 
 * ODE-314
 * The MessageProcessor value type is String 
 */
public class VsdDepositor extends AbstractSubscriberDepositor<String, String> {
    
    private static Coder coder = J2735.getPERUnalignedCoder();

	public VsdDepositor(OdeProperties odeProps) {
	    super(odeProps, odeProps.getVsdDepositorPort(), SemiDialogID.vehSitData);
	}

   @Override
    protected byte[] deposit() {
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
