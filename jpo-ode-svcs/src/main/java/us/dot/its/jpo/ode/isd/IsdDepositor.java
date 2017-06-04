package us.dot.its.jpo.ode.isd;

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
            socket = new DatagramSocket(odeProps.getIsdDepositorPort());
            logger.debug("Created Isd depositor Socket with port {}", odeProps.getIsdDepositorPort());
        } catch (SocketException e) {
            logger.error("Error creating socket with port {}", odeProps.getIsdDepositorPort(),
                    e);
        }
    }

    @Override
    public void run() {
//        String isd = "2088260186c577ea33c00d84ee07b0039c7dd1113b7daeb0e72df8fce000c0008061b0053befe314e695def062600b70396008a10010100002639a5756e9df7e18e639a5767a9df7a868639a577519df76ef20800022c021420020200004c734aee373befc31cc734af04f3bef5080c734af1fd3beedde4100004580628400404000098e695e3fa77df87718e695e75677dea1a18e695eb1a77ddbb28200008b01050800808000131cd2bd3b4efbf0ee31cd2bd98cefbd46f31cd2be044efbb78c04000116028a10010040002639a57c739df7e204639a57cde9df7b15c639a57d4a9df780b40800022c062420020100010c734ae4293befe740c734adf213befe7e0c734adc333befeb44c734ad8a33beff34cc734ad7613beffbf4c734ad5133bf075a8100008580e68400406000198e695c77a77dfc0498e695beae77dfc1898e695b64a77dfc7198e695b06e77dfd1998e695aec277dfdd58200020b020d0800808000131cd2bab74efbf0c731cd2bb3d4efbd43431cd2bba8cefbb7790400047e7e1610ea6590000c3602000004004086800df00df004045400838083804022a0041c041c";

        try {
            logger.debug("\nISD in hex: \n{}\n", encodedIsd);
            logger.debug("Sending ISD to SDC IP: {} Port: {}", odeProps.getSdcIp(),
                    odeProps.getSdcPort());
            socket.send(new DatagramPacket(encodedIsd, encodedIsd.length,
                    new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
        } catch (IOException e) {
            logger.error("Error Sending Isd to SDC {}", e);
        }
        if (this.socket != null) {
            logger.debug("Closing isd depositor socket with port {}", odeProps.getIsdDepositorPort());
            socket.close();
        }
    }

}
