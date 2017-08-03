package us.dot.its.jpo.ode.udp.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmReceiverTest {

   @Injectable
   OdeProperties mockOdeProperties;

   @Capturing
   OssJ2735Coder capturingOssJ2735Coder;

   @Mocked
   DatagramSocket mockedDatagramSocket;
   @SuppressWarnings("rawtypes")
   @Mocked
   SerializableMessageProducerPool mockedSMPP;
   @SuppressWarnings("rawtypes")
   @Mocked
   MessageProducer mockedMessageProducer;

   private BsmReceiver testBsmReceiver;

   @Before
   public void createTestObject() throws SocketException {
      testBsmReceiver = new BsmReceiver(mockOdeProperties);
      testBsmReceiver.setStopped(true);
   }

   @Test
   public void testRun(@Mocked DatagramPacket mockedDatagramPacket, @Mocked J2735Bsm mockedJ2735Bsm)
         throws IOException, DecoderException {
      new Expectations() {
         {
            new DatagramPacket((byte[]) any, anyInt);
            result = mockedDatagramPacket;

            String sampleBsmPacketStr = "030000ac000c001700000000000000200026ad01f13c00b1001480ad5f7bf1400014ff277c5e951cc867008d1d7ffffffff0017080fdfa1fa1007fff8000000000020214c1c0ffc7bffc2f963d160ffab401cef8261ba0ffedc0142fca96c61002dbff6efaeb3d61001ebff4afb8d6860ffe3bffc2fc4ec8e0ff7fc00e0fb50bce0ffe2bffe4fe347e20ffc5c0048fa5887e0ffb5c00dcfbdc87a0ffdb3feccfbe11ae0ffe03fe9cfde5e520ffc23ff90fd078060ffff3ff38fdea88a0ffe83ff84fb8f5a6fffe00000000";
            byte[] sampleBsmPacketByte = Hex.decodeHex(sampleBsmPacketStr.toCharArray());

            mockedDatagramPacket.getLength();
            result = sampleBsmPacketByte.length;

            mockedDatagramPacket.getData();
            result = sampleBsmPacketByte;

            mockedDatagramPacket.getLength();
            result = sampleBsmPacketByte.length;

            // capturingOssJ2735Coder.decodeUPERBsmBytes((byte[]) any);
            // result = mockedJ2735Bsm;
            // mockedMessageProducer.send(anyString, null, (Byte[]) any);
         }
      };

      testBsmReceiver.run();
   }

   @Test
   public void testRunException(@Mocked DatagramPacket mockedDatagramPacket, @Mocked J2735Bsm mockedJ2735Bsm)
         throws IOException {
      new Expectations() {
         {
            new DatagramPacket((byte[]) any, anyInt);
            result = mockedDatagramPacket;

            new DatagramPacket((byte[]) any, anyInt);
            result = mockedDatagramPacket;

            mockedDatagramSocket.receive(mockedDatagramPacket);
            result = new SocketException();
         }
      };

      testBsmReceiver.run();
   }

}
