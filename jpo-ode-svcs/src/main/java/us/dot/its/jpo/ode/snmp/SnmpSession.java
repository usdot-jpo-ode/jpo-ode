/*******************************************************************************
 * Copyright 2018 572682
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.snmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.plugin.SnmpProtocol;

import java.io.IOException;
import java.text.ParseException;

/**
 * This object is used to abstract away the complexities of SNMP calls and allow
 * a user to more quickly and easily send SNMP requests. Note that the
 * "connection" aspect of this class is an abstraction meant to reinforce that
 * these objects correspond 1-to-1 with a destination server, while SNMP is sent
 * over UDP and is actually connection-less.
 */
public class SnmpSession {

    private static final Logger logger = LoggerFactory.getLogger(SnmpSession.class);

    private Snmp snmp;
    private TransportMapping transport;
    private UserTarget target;

    private boolean ready = false;
    private boolean listening;

    /**
     * Constructor for SnmpSession
     *
     * @param props SnmpProperties for the session (target address, retries,
     *              timeout, etc)
     * @throws IOException
     */
    public SnmpSession(RSU rsu) throws IOException {
        Address addr = GenericAddress.parse(rsu.getRsuTarget() + "/161");

        // Create a "target" to which a request is sent
        target = new UserTarget();
        target.setAddress(addr);
        target.setRetries(rsu.getRsuRetries());
        target.setTimeout(rsu.getRsuTimeout());
        target.setVersion(SnmpConstants.version3);
        if (rsu.getRsuUsername() != null) {
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString(rsu.getRsuUsername()));
        } else {
            target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
        }

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
        if (rsu.getRsuUsername() != null) {
            snmp.getUSM().addUser(new OctetString(rsu.getRsuUsername()), new UsmUser(new OctetString(rsu.getRsuUsername()),
                    AuthSHA.ID, new OctetString(rsu.getRsuPassword()), PrivAES128.ID, new OctetString(rsu.getRsuPassword())));
        }

        // Assert the ready flag so the user can begin sending messages
        ready = true;
    }

    /**
     * Sends a SET-type PDU to the target specified by the constructor.
     *
     * @param pdu The message content to be sent to the target
     * @return ResponseEvent
     * @throws IOException
     */
    public ResponseEvent set(PDU pdu, Snmp snmpob, UserTarget targetob, Boolean keepOpen) throws IOException {

        // Ensure the object has been instantiated
        if (!ready) {
            throw new IOException("Tried to send PDU before SNMP sending service is ready.");
        }

        if (!listening) {
            startListen();
        }

        // Try to send the SNMP request (synchronously)
        ResponseEvent<Address> responseEvent;
        try {
            // attempt to discover & set authoritative engine ID
            byte[] authEngineID = snmpob.discoverAuthoritativeEngineID(targetob.getAddress(), 1000);
            if (authEngineID != null && authEngineID.length > 0) {
                targetob.setAuthoritativeEngineID(authEngineID);
            }

            // send request
            responseEvent = snmpob.set(pdu, targetob);
            if (!keepOpen) {
                snmpob.close();
            }

            // if RSU responded and we didn't get an authEngineID, log a warning
            if (responseEvent != null && responseEvent.getResponse() != null && authEngineID == null) {
                logger.warn("Failed to discover authoritative engine ID for SNMP target: {}", targetob.getAddress());
            }
        } catch (IOException e) {
            throw new IOException("Failed to send SNMP request: " + e);
        }

        return responseEvent;
    }

    /**
     * Sends a SET-type PDU to the target specified by the constructor.
     *
     * @param pdu The message content to be sent to the target
     * @return ResponseEvent
     * @throws IOException
     */
    public ResponseEvent get(PDU pdu, Snmp snmpob, UserTarget targetob, Boolean keepOpen) throws IOException {

        // Ensure the object has been instantiated
        if (!ready) {
            throw new IOException("Tried to send PDU before SNMP sending service is ready.");
        }

        // Start listening on UDP
        if (!listening) {
            startListen();
        }

        // Try to send the SNMP request (synchronously)
        ResponseEvent responseEvent = null;
        try {
            responseEvent = snmpob.get(pdu, targetob);
            if (!keepOpen) {
                snmpob.close();
            }
        } catch (IOException e) {
            throw new IOException("Failed to send SNMP request: " + e);
        }

        return responseEvent;
    }

    /**
     * Start listening for responses
     *
     * @throws IOException
     */
    public void startListen() throws IOException {
        transport.listen();
        listening = true;
    }

    /**
     * Create an SNMP session given the values in
     *
     * @param tim   - The TIM parameters (payload, channel, mode, etc)
     * @param props - The SNMP properties (ip, username, password, etc)
     * @return ResponseEvent
     * @throws TimPduCreatorException
     * @throws IOException
     * @throws ParseException
     */
    public static ResponseEvent createAndSend(SNMP snmp, RSU rsu, String payload, RequestVerb requestVerb, boolean dataSigningEnabledRSU)
            throws ParseException, IOException {

        SnmpSession session = new SnmpSession(rsu);

        // Send the PDU
        ResponseEvent response = null;
        ScopedPDU pdu = SnmpSession.createPDU(snmp, payload, rsu.getRsuIndex(), requestVerb, rsu.getSnmpProtocol(), dataSigningEnabledRSU);
        response = session.set(pdu, session.getSnmp(), session.getTarget(), false);
        String msg = "Message Sent to {}, index {}: {}";
        EventLogger.logger.debug(msg, rsu.getRsuTarget(), rsu.getRsuIndex(), payload);
        logger.debug(msg, rsu.getRsuTarget(), rsu.getRsuIndex(), payload);
        return response;
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

    public void endSession() throws IOException {
        this.snmp.close();
    }

    /**
     * Assembles the various RSU elements of a TimParameters object into a usable
     * PDU.
     *
     * @param index  Storage index on the RSU
     * @param params TimParameters POJO that stores status, channel, payload, etc.
     * @return PDU
     * @throws ParseException
     */
    public static ScopedPDU createPDU(SNMP snmp, String payload, int index, RequestVerb verb, SnmpProtocol snmpProtocol, boolean dataSigningEnabledRSU) throws ParseException {
        switch (snmpProtocol) {
            case FOURDOT1:
                return createPDUWithFourDot1Protocol(snmp, payload, index, verb);
            case NTCIP1218:
                return createPDUWithNTCIP1218Protocol(snmp, payload, index, verb, dataSigningEnabledRSU);
            default:
                logger.error("Unknown SNMP protocol: {}", snmpProtocol);
                return null;
        }
    }

    public static VariableBinding getPEncodedVariableBinding(String oid, String val) {
        Integer intVal = Integer.parseInt(val, 16);
        Integer additionValue = null;

        if (intVal >= 0 && intVal <= 127) {
            // P = V
            // here we must instantiate the OctetString directly with the hex string to
            // avoid inadvertently creating the ASCII character codes
            // for instance OctetString.fromString("20", 16) produces the space character ("
            // ") rather than hex 20
            return new VariableBinding(new OID(oid), new OctetString(Integer.toHexString(intVal)));
        } else if (intVal >= 128 && intVal <= 16511) {
            // P = V + 0x7F80
            additionValue = 0x7F80;
        } else if (intVal >= 016512 && intVal <= 2113663) {
            // P = V + 0xBFBF80
            additionValue = 0xBFBF80;
        } else if (intVal >= 2113664 && intVal <= 270549119) {
            // P = V + 0xDFDFBF80
            additionValue = 0xDFDFBF80;
        }

        if (additionValue != null) {
            return new VariableBinding(new OID(oid),
                    OctetString.fromString(Integer.toHexString(intVal + additionValue), 16));
        }
        return null;
    }

    private static ScopedPDU createPDUWithFourDot1Protocol(SNMP snmp, String payload, int index, RequestVerb verb) throws ParseException {
        //////////////////////////////
        // - OID examples - //
        //////////////////////////////
        // rsuSRMStatus.3 = 4
        // --> 1.4.1.11.3 = 4
        // rsuSRMTxChannel.3 = 3
        // --> 1.4.1.5.3 = 178
        // rsuSRMTxMode.3 = 1
        // --> 1.4.1.4.3 = 1
        // rsuSRMPsid.3 x "8003"
        // --> 1.4.1.2.3 x "8003"
        // rsuSRMDsrcMsgId.3 = 31
        // --> 1.4.1.3.3 = 31
        // rsuSRMTxInterval.3 = 10
        // --> 1.4.1.6.3 = 10
        // rsuSRMDeliveryStart.3 x "07e7051f0c000000"
        // --> 1.4.1.7.3 = "07e7051f0c000000"
        // rsuSRMDeliveryStop.3 x "07e7060f0c000000"
        // --> 1.4.1.8.3 = "07e7060f0c000000"
        // rsuSRMPayload.3 x "001f6020100000000000de8f834082729de80d80734d37862d2187864fc2099f1f4028407e53bd01b00e69a6f0c5a409c46c3c300118e69a26fa77a0104b8e69a2e86779e21981414e39a68fd29de697d804fb38e69a50e27796151013d81080020290"
        // --> 1.4.1.9.3 = "001f6020100000000000de8f834082729de80d80734d37862d2187864fc2099f1f4028407e53bd01b00e69a6f0c5a409c46c3c300118e69a26fa77a0104b8e69a2e86779e21981414e39a68fd29de697d804fb38e69a50e27796151013d81080020290"
        // rsuSRMEnable.3 = 1
        // --> 1.4.1.10.3 = 1
        //////////////////////////////

        VariableBinding rsuSRMPsid = SnmpFourDot1Protocol.getVbRsuSrmPsid(index, snmp.getRsuid());
        VariableBinding rsuSRMDsrcMsgId = SnmpFourDot1Protocol.getVbRsuSrmDsrcMsgId(index, snmp.getMsgid());
        VariableBinding rsuSRMTxMode = SnmpFourDot1Protocol.getVbRsuSrmTxMode(index, snmp.getMode());
        VariableBinding rsuSRMTxChannel = SnmpFourDot1Protocol.getVbRsuSrmTxChannel(index, snmp.getChannel());
        VariableBinding rsuSRMTxInterval = SnmpFourDot1Protocol.getVbRsuSrmTxInterval(index, snmp.getInterval());
        VariableBinding rsuSRMDeliveryStart = SnmpFourDot1Protocol.getVbRsuSrmDeliveryStart(index, snmp.getDeliverystart());
        VariableBinding rsuSRMDeliveryStop = SnmpFourDot1Protocol.getVbRsuSrmDeliveryStop(index, snmp.getDeliverystop());
        VariableBinding rsuSRMPayload = SnmpFourDot1Protocol.getVbRsuSrmPayload(index, payload);
        VariableBinding rsuSRMEnable = SnmpFourDot1Protocol.getVbRsuSrmEnable(index, snmp.getEnable());
        VariableBinding rsuSRMStatus = SnmpFourDot1Protocol.getVbRsuSrmStatus(index, snmp.getStatus());

        ScopedPDU pdu = new ScopedPDU();
        pdu.add(rsuSRMPsid);
        pdu.add(rsuSRMDsrcMsgId);
        pdu.add(rsuSRMTxMode);
        pdu.add(rsuSRMTxChannel);
        pdu.add(rsuSRMTxInterval);
        pdu.add(rsuSRMDeliveryStart);
        pdu.add(rsuSRMDeliveryStop);
        pdu.add(rsuSRMPayload);
        pdu.add(rsuSRMEnable);
        if (verb == ServiceRequest.OdeInternal.RequestVerb.POST) {
            pdu.add(rsuSRMStatus);
        }
        pdu.setType(PDU.SET);

        return pdu;
    }

    private static ScopedPDU createPDUWithNTCIP1218Protocol(SNMP snmp, String payload, int index, RequestVerb verb, boolean dataSigningEnabledRSU) throws ParseException {
        //////////////////////////////
        // - OID examples - //
        //////////////////////////////
        // rsuMsgRepeatPsid.3 x "8003"
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.2.3 x "8003"
        // rsuMsgRepeatTxChannel.3 = 3
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.3.3 = 183
        // rsuMsgRepeatTxInterval.3 = 10
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.4.3 = 10
        // rsuMsgRepeatDeliveryStart.3 x "07e7051f0c000000"
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.5.3 = "07e7051f0c000000"
        // rsuMsgRepeatDeliveryStop.3 x "07e7060f0c000000"
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.6.3 = "07e7060f0c000000"
        // rsuMsgRepeatPayload.3 x "001f6020100000000000de8f834082729de80d80734d37862d2187864fc2099f1f4028407e53bd01b00e69a6f0c5a409c46c3c300118e69a26fa77a0104b8e69a2e86779e21981414e39a68fd29de697d804fb38e69a50e27796151013d81080020290"
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.7.3 = "001f6020100000000000de8f834082729de80d80734d37862d2187864fc2099f1f4028407e53bd01b00e69a6f0c5a409c46c3c300118e69a26fa77a0104b8e69a2e86779e21981414e39a68fd29de697d804fb38e69a50e27796151013d81080020290"
        // rsuMsgRepeatEnable.3 = 1
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.8.3 = 1
        // rsuMsgRepeatStatus.3 = 4
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.9.3 = 4
        // rsuMsgRepeatPriority.3 = 6
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.10.3 = 6
        // rsuMsgRepeatOptions.3 = "00"
        // --> 1.3.6.1.4.1.1206.4.2.18.3.2.1.11.3 = "00"
        //////////////////////////////

        VariableBinding rsuMsgRepeatPsid = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatPsid(index, snmp.getRsuid());
        // note: dsrc_msg_id is not in NTCIP 1218
        // note: tx_mode is not in NTCIP 1218
        VariableBinding rsuMsgRepeatTxChannel = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatTxChannel(index, snmp.getChannel());
        VariableBinding rsuMsgRepeatTxInterval = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatTxInterval(index, snmp.getInterval());
        VariableBinding rsuMsgRepeatDeliveryStart = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatDeliveryStart(index, snmp.getDeliverystart());
        VariableBinding rsuMsgRepeatDeliveryStop = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatDeliveryStop(index, snmp.getDeliverystop());
        VariableBinding rsuMsgRepeatPayload = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatPayload(index, payload);
        VariableBinding rsuMsgRepeatEnable = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatEnable(index, snmp.getEnable());
        VariableBinding rsuMsgRepeatStatus = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatStatus(index, snmp.getStatus());
        VariableBinding rsuMsgRepeatPriority = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatPriority(index);
        VariableBinding rsuMsgRepeatOptions;
        if (dataSigningEnabledRSU) {
            // set options to 0x00 to tell RSU to broadcast message without signing or attaching a 1609.2 header
            rsuMsgRepeatOptions = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatOptions(index, 0x00);
        } else {
            // set options to 0x80 to tell RSU to sign & attach a 1609.2 header before broadcasting
            rsuMsgRepeatOptions = SnmpNTCIP1218Protocol.getVbRsuMsgRepeatOptions(index, 0x80);
        }

        ScopedPDU pdu = new ScopedPDU();
        pdu.add(rsuMsgRepeatPsid);
        pdu.add(rsuMsgRepeatTxChannel);
        pdu.add(rsuMsgRepeatTxInterval);
        pdu.add(rsuMsgRepeatDeliveryStart);
        pdu.add(rsuMsgRepeatDeliveryStop);
        pdu.add(rsuMsgRepeatPayload);
        pdu.add(rsuMsgRepeatEnable);
        if (verb == ServiceRequest.OdeInternal.RequestVerb.POST) {
            pdu.add(rsuMsgRepeatStatus);
        }
        pdu.add(rsuMsgRepeatPriority);
        pdu.add(rsuMsgRepeatOptions);
        pdu.setType(PDU.SET);

        return pdu;
    }
}