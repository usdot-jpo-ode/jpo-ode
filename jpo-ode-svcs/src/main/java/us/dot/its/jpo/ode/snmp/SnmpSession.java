package us.dot.its.jpo.ode.snmp;

import java.io.IOException;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * This object is used to abstract away the complexities of SNMP calls and allow
 * a user to more quickly and easily send SNMP requests. Note that the
 * "connection" aspect of this class is an abstraction meant to reinforce that
 * these objects correspond 1-to-1 with a destination server, while SNMP is sent
 * over UDP and is actually connection-less.
 */
public class SnmpSession {

    private Snmp snmp;
    private TransportMapping transport;
    private UserTarget target;

    private boolean ready = false;

    /**
     * Constructor for SnmpSession
     * 
     * @param props
     *            SnmpProperties for the session (target address, retries,
     *            timeout, etc)
     * @throws IOException
     */
    public SnmpSession(SnmpProperties props) throws IOException {
        
        // Verify props object is not null
        if (props == null) {
            throw new IllegalArgumentException("SnmpProperties object is null");
        }

        // Create a "target" to which a request is sent
        target = new UserTarget();
        target.setAddress(props.getTarget());
        target.setRetries(props.getRetries());
        target.setTimeout(props.getTimeout());
        target.setVersion(props.getVersion());
        target.setSecurityLevel(props.getSecurityLevel());
        target.setSecurityName(new OctetString(props.getUsername()));

        // Set up the UDP transport mapping over which requests are sent
        transport = null;
        try {
            transport = new DefaultUdpTransportMapping();
        } catch (IOException e) {
            throw new IOException("Failed to create UDP transport mapping: {}", e);
        }

        // Instantiate the SNMP instance
        snmp = new Snmp(transport);

        // Register the security options and create an SNMP "user"
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        snmp.getUSM().addUser(new OctetString(props.getUsername()), new UsmUser(new OctetString(props.getUsername()),
                AuthMD5.ID, new OctetString(props.getPassword()), null, null));

        // Assert the ready flag so the user can begin sending messages
        ready = true;

    }

    /**
     * Sends a SET-type PDU to the target specified by the constructor.
     * 
     * @param pdu
     *            The message content to be sent to the target
     * @return ResponseEvent
     * @throws IOException
     */
    public ResponseEvent set(PDU pdu, Snmp snmpob, TransportMapping transportob, UserTarget targetob) throws IOException {

        // Ensure the object has been instantiated
        if (!ready) {
            throw new IOException("Tried to send PDU before SNMP sending service is ready.");
        }

        // Start listening on UDP
        try {
            transportob.listen();
        } catch (IOException e) {
            throw new IOException("Unable to start UDP listener: " + e);
        }

        // Try to send the SNMP request (synchronously)
        ResponseEvent responseEvent = null;
        try {
            responseEvent = snmpob.set(pdu, targetob);
            snmpob.close();
        } catch (IOException e) {
            throw new IOException("Failed to send SNMP request: " + e);
        }

        return responseEvent;
    }

    public Snmp getSnmp() {
        return snmp;
    }

    public void setSnmp(Snmp snmp) {
        this.snmp = snmp;
    }

    public TransportMapping getTransport() {
        return transport;
    }

    public void setTransport(TransportMapping transport) {
        this.transport = transport;
    }

    public UserTarget getTarget() {
        return target;
    }

    public void setTarget(UserTarget target) {
        this.target = target;
    }
}
