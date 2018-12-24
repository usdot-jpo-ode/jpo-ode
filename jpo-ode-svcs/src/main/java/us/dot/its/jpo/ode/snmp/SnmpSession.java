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

import java.io.IOException;

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
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.traveler.TimPduCreator;
import us.dot.its.jpo.ode.traveler.TimPduCreator.TimPduCreatorException;

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
    * @param props
    *           SnmpProperties for the session (target address, retries,
    *           timeout, etc)
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
        target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
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
              AuthSHA.ID, new OctetString(rsu.getRsuPassword()), null, null));
      }
      
      // Assert the ready flag so the user can begin sending messages
      ready = true;

   }

   /**
    * Sends a SET-type PDU to the target specified by the constructor.
    * 
    * @param pdu
    *           The message content to be sent to the target
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
      ResponseEvent responseEvent = null;
      try {
         responseEvent = snmpob.set(pdu, targetob);
         if (!keepOpen) {
            snmpob.close();
         }
      } catch (IOException e) {
         throw new IOException("Failed to send SNMP request: " + e);
      }

      return responseEvent;
   }

   /**
    * Sends a SET-type PDU to the target specified by the constructor.
    * 
    * @param pdu
    *           The message content to be sent to the target
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
    * @param tim
    *           - The TIM parameters (payload, channel, mode, etc)
    * @param props
    *           - The SNMP properties (ip, username, password, etc)
    * @return ResponseEvent
    * @throws TimPduCreatorException
    * @throws IOException
    */
   public static ResponseEvent createAndSend(SNMP snmp, RSU rsu, String payload, RequestVerb requestVerb)
         throws IOException, TimPduCreatorException {

      SnmpSession session = new SnmpSession(rsu);

      // Send the PDU
      ResponseEvent response = null;
      ScopedPDU pdu = TimPduCreator.createPDU(snmp, payload, rsu.getRsuIndex(), requestVerb);
      response = session.set(pdu, session.getSnmp(), session.getTarget(), false);
      String msg = "Message Sent to {}, index {}: {}";
      EventLogger.logger.info(msg, rsu.getRsuTarget(), rsu.getRsuIndex(), payload);
      logger.info(msg, rsu.getRsuTarget(), rsu.getRsuIndex(), payload);
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
}
