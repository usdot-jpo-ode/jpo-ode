package us.dot.its.jpo.ode.inet;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import mockit.Capturing;
import mockit.Mocked;

public class InetPacketTest {
   
   @Capturing
   InetAddress capturingInetAddress;
   
   @Mocked
   DatagramPacket mockDatagramPacket;

   @Test
   public void testStringConstructorCallsPointConstructor() {
      try {
         new InetPacket("testHost", 5, new byte[]{1,2,3});
      } catch (UnknownHostException e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void testDatagramPacketConstructor() {
      new InetPacket(mockDatagramPacket);
   }
   
   @Test
   public void testByteConstructor() {
      InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
      testPacket.getPoint();
      testPacket.getPayload();
      testPacket.getBundle();
      testPacket.toHexString();
   }

}
