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
package us.dot.its.jpo.ode.inet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mockit.Capturing;
import us.dot.its.jpo.ode.util.CodecUtils;

@Slf4j
public class InetPacketSenderTest {

   static final private boolean isDebugOutput = false;

   private static final int DEFAULT_MAX_PACKET_SIZE = 65535;

   private static final String TRANSPORT_HOST = "localhost";
   private static final int TRANSPORT_PORT = 46751;

   private static final String FORWARDER_HOST = "localhost";
   private static final int FORWARDER_PORT = 46752;

   private static final String CLIENT_HOST_IPV4 = "localhost";
   private static final String CLIENT_HOST_IPV6 = "::1";
   private static final int CLIENT_PORT = 46753;

   private static final byte[] PAYLOAD = new byte[] { (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, (byte) 0xca,
         (byte) 0xfe, (byte) 0xba, (byte) 0xbe };

   AssertionError assertionError = null;

   private enum TestCase {
      TestForwardPacketInbound, TestSendPacketOutbound, TestIPv6ForwardOutbound, TestIPv4ForwardOutboundForward, TestIPv4ForwardOutboundSend, TestIPv6SendOutbound, TestIPv4SendOutbound,
   };

   @BeforeAll
   public static void init() {
   }

   @Test
   public void testForwardPacketInboundIPV4() throws UnknownHostException, InetPacketException, InterruptedException {
      // Use case: Forwarder forwards incoming IPV4 packet to transport
      testForwardPacketInbound(CLIENT_HOST_IPV4);
   }

   @Test
   public void testForwardPacketInboundIPV6() throws UnknownHostException, InetPacketException, InterruptedException {
      // Use case: Forwarder forwards incoming IPV6 packet to transport
      testForwardPacketInbound(CLIENT_HOST_IPV6);
   }

   public void testForwardPacketInbound(String hostname)
         throws UnknownHostException, InetPacketException, InterruptedException {
      startUdpListener(TRANSPORT_PORT, TestCase.TestForwardPacketInbound);
      InetPoint transport = new InetPoint(getAddressBytes(TRANSPORT_HOST), TRANSPORT_PORT);
      InetPacketSender sender = new InetPacketSender(transport);
      DatagramPacket packet = new DatagramPacket(PAYLOAD, PAYLOAD.length, InetAddress.getByName(hostname), CLIENT_PORT);
      sender.forward(packet);
      checkBackgroundThreadAssertion();
   }

   @Test @Disabled
   public void testSendPacketOutboundIPv4() throws UnknownHostException, InterruptedException, InetPacketException {
      // Use case: Forwarder sends outgoing IPv4 packet out
      testSendPacketOutbound(CLIENT_HOST_IPV4);
   }

   @Test @Disabled
   public void testSendPacketOutboundIPv6() throws UnknownHostException, InterruptedException, InetPacketException {
      // Use case: Forwarder sends outgoing IPv6 packet out
      testSendPacketOutbound(CLIENT_HOST_IPV6);
   }

   public void testSendPacketOutbound(String hostname)
         throws UnknownHostException, InetPacketException, InterruptedException {
      startUdpListener(CLIENT_PORT, TestCase.TestSendPacketOutbound);
      InetPoint client = new InetPoint(getAddressBytes(hostname), CLIENT_PORT);
      byte[] bundle = new InetPacket(client, PAYLOAD).getBundle();
      InetPacketSender sender = new InetPacketSender();
      DatagramPacket packet = new DatagramPacket(bundle, bundle.length, InetAddress.getByName(FORWARDER_HOST),
            FORWARDER_PORT);
      sender.send(packet);
      checkBackgroundThreadAssertion();
   }

   @Test
   public void testIPv6ForwardOutbound() throws UnknownHostException, InetPacketException, InterruptedException {
      // Use case: Transport or Data Sink send IPv6 message out via forwarder
      startUdpListener(FORWARDER_PORT, TestCase.TestIPv6ForwardOutbound);
      InetPoint forwarder = new InetPoint(getAddressBytes(FORWARDER_HOST), FORWARDER_PORT);
      InetPacketSender sender = new InetPacketSender(forwarder);
      InetPoint client = new InetPoint(getAddressBytes(CLIENT_HOST_IPV6), CLIENT_PORT);
      sender.forward(client, PAYLOAD);
      checkBackgroundThreadAssertion();
   }

   @Test
   public void testIPv4ForwardOutboundForward() throws InterruptedException, UnknownHostException, InetPacketException {
      // Use case: Transport or Data Sink send IPv4 message out via forwarder
      startUdpListener(FORWARDER_PORT, TestCase.TestIPv4ForwardOutboundForward);
      InetPoint forwarder = new InetPoint(getAddressBytes(FORWARDER_HOST), FORWARDER_PORT);
      InetPacketSender sender = new InetPacketSender(forwarder);
      sender.setForwardAll(true);
      InetPoint client = new InetPoint(getAddressBytes(CLIENT_HOST_IPV4), CLIENT_PORT);
      sender.forward(client, PAYLOAD);
      checkBackgroundThreadAssertion();
   }

   @Test
   public void testIPv4ForwardOutboundSend() throws InterruptedException, UnknownHostException, InetPacketException {
      // Use case: Transport or Data Sink send IPv4 message out via forwarder
      // but it's being send out directly
      startUdpListener(CLIENT_PORT, TestCase.TestIPv4ForwardOutboundSend);
      InetPoint forwarder = new InetPoint(getAddressBytes(FORWARDER_HOST), FORWARDER_PORT);
      InetPacketSender sender = new InetPacketSender(forwarder);
      InetPoint client = new InetPoint(getAddressBytes(CLIENT_HOST_IPV4), CLIENT_PORT);
      sender.forward(client, PAYLOAD);
      checkBackgroundThreadAssertion();
   }

   @Test @Disabled
   public void testIPv6SendOutbound() throws InterruptedException, UnknownHostException, InetPacketException {
      // Use case: Transport or Data Sink send IPv6 message out directly
      startUdpListener(CLIENT_PORT, TestCase.TestIPv6SendOutbound);
      InetPacketSender sender = new InetPacketSender();
      InetPoint client = new InetPoint(getAddressBytes(CLIENT_HOST_IPV6), CLIENT_PORT);
      sender.send(client, PAYLOAD);
      checkBackgroundThreadAssertion();
   }

   @Test @Disabled
   public void testIPv4SendOutbound() throws InterruptedException, UnknownHostException, InetPacketException {
      // Use case: Transport or Data Sink send IPv4 message out directly
      startUdpListener(CLIENT_PORT, TestCase.TestIPv4SendOutbound);
      InetPacketSender sender = new InetPacketSender();
      InetPoint client = new InetPoint(getAddressBytes(CLIENT_HOST_IPV4), CLIENT_PORT);
      sender.send(client, PAYLOAD);
      checkBackgroundThreadAssertion();
   }

   private static byte[] getAddressBytes(String host) throws UnknownHostException {
      return InetAddress.getByName(host).getAddress();
   }

   private void startUdpListener(int port, TestCase tc) {
      final int listenPort = port;
      final TestCase testCase = tc;

      Thread listener = new Thread(new Runnable() {
         @Override
         public void run() {
            DatagramSocket socket = null;
            try {
               socket = new DatagramSocket(listenPort);
               DatagramPacket datagramPacket = new DatagramPacket(new byte[DEFAULT_MAX_PACKET_SIZE],
                     DEFAULT_MAX_PACKET_SIZE);
               socket.setSoTimeout(1000);
               socket.receive(datagramPacket);
               validatePacket(datagramPacket);
            } catch (SocketTimeoutException ex) {
               log.error(
                     String.format("Caught socket timeout exception while recieving message on port %d. Max size is %d",
                           listenPort, DEFAULT_MAX_PACKET_SIZE),
                     ex);
            } catch (SocketException ex) {
               log.error(String.format("Caught socket exception while recieving message on port %d. Max size is %d",
                     listenPort, DEFAULT_MAX_PACKET_SIZE), ex);
            } catch (IOException ex) {
               log.error(
                     String.format("Caught IO exception exception while recieving message on port %d. Max size is %d",
                           listenPort, DEFAULT_MAX_PACKET_SIZE),
                     ex);
            } finally {
               if (socket != null && !socket.isClosed()) {
                  socket.close();
                  socket = null;
               }
            }
         }

         private void validatePacket(DatagramPacket packet) throws UnknownHostException {
            assertNotNull(packet);

            final byte[] data = packet.getData();
            assertNotNull(data);

            final int length = packet.getLength();
            assertTrue(length > 0);

            final int offset = packet.getOffset();

            byte[] packetData = Arrays.copyOfRange(data, offset, length);

            try {
               switch (testCase) {
               case TestForwardPacketInbound:
                  validateForwardPacketInbound(packetData);
                  validateForwardPacketInbound(packet);
               break;
               case TestSendPacketOutbound:
                  validateSendPacketOutbound(packetData);
                  validateSendPacketOutbound(packet);
               break;
               case TestIPv6ForwardOutbound:
                  validateIPv6ForwardOutbound(packetData);
                  validateIPv6ForwardOutbound(packet);
               break;
               case TestIPv4ForwardOutboundForward:
                  validateIPv4ForwardOutboundForward(packetData);
                  validateIPv4ForwardOutboundForward(packet);
               break;
               case TestIPv4ForwardOutboundSend:
                  validateIPv4ForwardOutboundSend(packetData);
                  validateIPv4ForwardOutboundSend(packet);
               break;
               case TestIPv6SendOutbound:
                  validateIPv6SendOutbound(packetData);
                  validateIPv6SendOutbound(packet);
               break;
               case TestIPv4SendOutbound:
                  validateIPv4SendOutbound(packetData);
                  validateIPv4SendOutbound(packet);
               break;
               }
            } catch (AssertionError ex) {
               assertionError = ex;
            }
         }

         private void validateForwardPacketInbound(byte[] payload) throws UnknownHostException {
            validateForwardPacketInbound("Payload", new InetPacket(payload));
         }

         private void validateForwardPacketInbound(DatagramPacket packet) throws UnknownHostException {
            validateForwardPacketInbound("Packet", new InetPacket(packet));
         }

         private void validateForwardPacketInbound(String tail, InetPacket p) throws UnknownHostException {
            assertNotNull(p);
            if (isDebugOutput)
               print("ForwardPacketInbound", p);
            assertEquals(TRANSPORT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNotNull(point);
            byte[] srcAddress = point.address;
            assertNotNull(srcAddress);
            if (point.isIPv6Address())
               assertArrayEquals(getAddressBytes(CLIENT_HOST_IPV6), point.address);
            else
               assertArrayEquals(getAddressBytes(CLIENT_HOST_IPV4), point.address);
            assertEquals(CLIENT_PORT, point.port);
            assertTrue(point.forward);
            assertArrayEquals(PAYLOAD, p.getPayload());
         }

         private void validateSendPacketOutbound(byte[] payload) throws UnknownHostException {
            InetPacket p = new InetPacket(payload);
            assertNotNull(p);
            if (isDebugOutput)
               print("SendPacketOutbound Payload", p);
            assertEquals(CLIENT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNull(point);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateSendPacketOutbound(DatagramPacket packet) throws UnknownHostException {
            InetPacket p = new InetPacket(packet);
            assertNotNull(p);
            if (isDebugOutput)
               print("SendPacketOutbound Packet", p);
            assertEquals(CLIENT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNotNull(point);
            byte[] srcAddress = point.address;
            assertNotNull(srcAddress);
            if (point.isIPv6Address())
               assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV6));
            else
               assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV4));
            assertFalse(point.forward);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateIPv6ForwardOutbound(byte[] payload) throws UnknownHostException {
            validateIPv6ForwardOutbound("Payload", new InetPacket(payload));
         }

         private void validateIPv6ForwardOutbound(DatagramPacket packet) throws UnknownHostException {
            validateIPv6ForwardOutbound("Packet", new InetPacket(packet));
         }

         private void validateIPv6ForwardOutbound(String tail, InetPacket p) throws UnknownHostException {
            assertNotNull(p);
            if (isDebugOutput)
               print("IPv6ForwardOutbound " + tail, p);
            assertEquals(FORWARDER_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNotNull(point);
            byte[] srcAddress = point.address;
            assertNotNull(srcAddress);
            assertTrue(point.isIPv6Address());
            assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV6));
            assertEquals(CLIENT_PORT, point.port);
            assertTrue(point.forward);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateIPv4ForwardOutboundForward(byte[] payload) throws UnknownHostException {
            validateIPv4ForwardOutboundForward("Payload", new InetPacket(payload));
         }

         private void validateIPv4ForwardOutboundForward(DatagramPacket packet) throws UnknownHostException {
            validateIPv4ForwardOutboundForward("Packet", new InetPacket(packet));
         }

         private void validateIPv4ForwardOutboundForward(String tail, InetPacket p) throws UnknownHostException {
            assertNotNull(p);
            if (isDebugOutput)
               print("IPv4ForwardOutboundForward " + tail, p);
            assertEquals(FORWARDER_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNotNull(point);
            byte[] srcAddress = point.address;
            assertNotNull(srcAddress);
            assertFalse(point.isIPv6Address());
            assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV4));
            assertEquals(CLIENT_PORT, point.port);
            assertTrue(point.forward);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateIPv4ForwardOutboundSend(byte[] payload) throws UnknownHostException {
            InetPacket p = new InetPacket(payload);
            assertNotNull(p);
            if (isDebugOutput)
               print("IPv4ForwardOutboundSend Payload", p);
            assertEquals(CLIENT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNull(point);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateIPv4ForwardOutboundSend(DatagramPacket packet) throws UnknownHostException {
            InetPacket p = new InetPacket(packet);
            assertNotNull(p);
            if (isDebugOutput)
               print("IPv4ForwardOutboundSend Packet", p);
            assertEquals(CLIENT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNotNull(point);
            byte[] srcAddress = point.address;
            assertNotNull(srcAddress);
            if (point.isIPv6Address())
               assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV6));
            else
               assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV4));
            assertFalse(point.forward);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateIPv6SendOutbound(byte[] payload) throws UnknownHostException {
            validateIPvXSendOutboundPayload(new InetPacket(payload));
         }

         private void validateIPv4SendOutbound(byte[] payload) throws UnknownHostException {
            validateIPvXSendOutboundPayload(new InetPacket(payload));
         }

         private void validateIPv6SendOutbound(DatagramPacket packet) throws UnknownHostException {
            validateIPvXSendOutboundPacket(new InetPacket(packet));
         }

         private void validateIPv4SendOutbound(DatagramPacket packet) throws UnknownHostException {
            validateIPvXSendOutboundPacket(new InetPacket(packet));
         }

         private void validateIPvXSendOutboundPayload(InetPacket p) throws UnknownHostException {
            assertNotNull(p);
            if (isDebugOutput)
               print("IPvXSendOutbound Payload", p);
            assertEquals(CLIENT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNull(point);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void validateIPvXSendOutboundPacket(InetPacket p) throws UnknownHostException {
            assertNotNull(p);
            if (isDebugOutput)
               print("IPvXSendOutbound Packet", p);
            assertEquals(CLIENT_PORT, listenPort);
            InetPoint point = p.getPoint();
            assertNotNull(point);
            byte[] srcAddress = point.address;
            assertNotNull(srcAddress);
            if (point.isIPv6Address())
               assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV6));
            else
               assertArrayEquals(point.address, getAddressBytes(CLIENT_HOST_IPV4));
            assertFalse(point.forward);
            assertArrayEquals(p.getPayload(), PAYLOAD);
         }

         private void print(String header, InetPacket pb) throws UnknownHostException {
            assertNotNull(pb);
            InetPoint point = pb.getPoint();
            if (point != null) {
               System.out.printf("%s: port:  %d (0x%x)\n", header, point.port, point.port);
               System.out.printf("%s: address size: %d value: %s ip: %s\n", header, point.address.length,
                     CodecUtils.toHex(point.address), InetAddress.getByAddress(point.address).getHostAddress());
               System.out.printf("%s: forward:  %s\n", header, point.forward ? "true" : "false");
            } else {
               System.out.printf("%s: Inet point is null\n", header);
            }
            byte[] p = pb.getPayload();
            System.out.printf("%s: payload: %s\n", header, p != null && p.length > 0 ? CodecUtils.toHex(p) : "<empty>");
            System.out.printf("%s: bundle: %s\n", header, pb.toHexString());
         }
      });
      listener.start();
      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
      }
   }

   void checkBackgroundThreadAssertion() {
      try {
         Thread.sleep(1500);
      } catch (InterruptedException unused) {
      }

      if (assertionError != null)
         throw assertionError;
   }

}
