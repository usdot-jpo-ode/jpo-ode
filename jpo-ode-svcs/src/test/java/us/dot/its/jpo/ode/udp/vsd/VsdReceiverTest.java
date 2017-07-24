package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher.UdpReceiverException;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class VsdReceiverTest {

   @Injectable
   OdeProperties injectableOdeProperties;

   @Test(timeout = 4000) // catch runaway while loop
   public void shouldNotRunWhenStopped(@Capturing DatagramPacket capturingDatagramPacket,
         @Mocked DatagramPacket mockDatagramPacket,
         @SuppressWarnings("rawtypes") @Capturing MessageProducer<?, ?> capturingMessageProducer) {
      new Expectations() {
         {
            new DatagramPacket((byte[]) any, anyInt);
            times = 1;
         }
      };

      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      testVsdReceiver.setStopped(true);
      testVsdReceiver.run();
   }

   @Test
   public void testGetPacketThrowsError(@Capturing DatagramPacket capturingDatagramPacket,
         @Mocked DatagramPacket mockDatagramPacket, @Capturing DatagramSocket capturingDatagramSocket,
         @Mocked DatagramSocket mockDatagramSocket,
         @SuppressWarnings("rawtypes") @Capturing MessageProducer<?, ?> capturingMessageProducer) {
      try {
         new Expectations() {
            {
               new DatagramSocket(anyInt);
               result = mockDatagramSocket;
               mockDatagramSocket.receive((DatagramPacket) any);
               mockDatagramPacket.getLength();
               returns(1, 0);

               mockDatagramPacket.getData();
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      testVsdReceiver.getPacket(mockDatagramPacket);
   }

   @Test
   public void testProcessPacketServiceRequest(@Capturing MessageProducer<?, ?> doNotProduceMessages,
         @Capturing J2735Util capturingJ2735Util) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = new ServiceRequest();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      try {
         testVsdReceiver.processPacket(new byte[] { 0 });
      } catch (UdpReceiverException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testProcessPacketServiceRequestWithDestination(@Capturing MessageProducer<?, ?> doNotProduceMessages,
         @Capturing J2735Util capturingJ2735Util, @Mocked ServiceRequest mockServiceRequest) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = mockServiceRequest;
               mockServiceRequest.getDestination();
               result = new ConnectionPoint();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      try {
         testVsdReceiver.processPacket(new byte[] { 0 });
      } catch (UdpReceiverException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testProcessPacketServiceRequestWithDestinationAndAddressAndPort(
         @Capturing MessageProducer<?, ?> doNotProduceMessages, @Capturing J2735Util capturingJ2735Util,
         @Mocked ServiceRequest mockServiceRequest, @Mocked ConnectionPoint mockConnectionPoint,
         @Capturing UdpUtil capturingUdpUtil) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = mockServiceRequest;
               mockServiceRequest.getDestination();
               result = mockConnectionPoint;
               mockConnectionPoint.getAddress();
               result = new IpAddress();

               mockConnectionPoint.getPort();
               result = new PortNumber();

               UdpUtil.send((DatagramSocket) any, (AbstractData) any, anyString, anyInt);
               times = 1;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException | UdpUtilException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      try {
         testVsdReceiver.processPacket(new byte[] { 0 });
      } catch (UdpReceiverException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testProcessPacketVSD(@Capturing MessageProducer<?, ?> doNotProduceMessages,
         @Capturing J2735Util capturingJ2735Util) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = new VehSitDataMessage();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      try {
         testVsdReceiver.processPacket(new byte[] { 0 });
      } catch (UdpReceiverException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testProcessPacketUnknownMessageType(@Capturing MessageProducer<?, ?> doNotProduceMessages,
         @Capturing J2735Util capturingJ2735Util) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = new ServiceResponse();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      try {
         testVsdReceiver.processPacket(new byte[] { 0 });
      } catch (UdpReceiverException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testExtractAndPublishBsms(@Capturing MessageProducer<?, ?> doNotProduceMessages,
         @Capturing VsdToBsmConverter capturingVsdToBsmConverter) {
      new Expectations() {
         {
            VsdToBsmConverter.convert((VehSitDataMessage) any);
            times = 1;
            result = new ArrayList<BasicSafetyMessage>();
         }
      };
      VsdReceiver testVsdReceiver = new VsdReceiver(injectableOdeProperties);
      try {
         testVsdReceiver.extractAndPublishBsms(new VehSitDataMessage());
      } catch (OssBsmPart2Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
