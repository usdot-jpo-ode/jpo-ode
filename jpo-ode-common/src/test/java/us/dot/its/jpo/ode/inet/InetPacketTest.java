package us.dot.its.jpo.ode.inet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.util.CrcCccitt;

public class InetPacketTest {
   
   @Capturing
   DatagramSocket capturingDatagramSocket;
   
   @Capturing
   DatagramPacket capturingDatagramPacket;
   
   @Capturing
   Thread capturingThread;
 
   
   
   @Mocked
   DatagramPacket mockDatagramPacket;
   byte[] mockPayload;
   
   @Test @Ignore
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
   

/*
    @Test
    public void testEvenNum() {
        boolean ans = false;
        boolean val;
        byte[] bundle = null;
        

        val = InetPacket.parseBundle(bundle);
        assertEquals(ans,val);
    }
*/
   

@Test
   public void parseBundleNulll() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = null; 
	   
	   assertFalse(testPacket.parseBundle(bundle));
   }

@Test
public void parseBundleNotMagic() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{1,2,3,4,5,6}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	 //  System.out.println("Buffer: " + buffer);
	 //  System.out.println("Buffer.getInt: " + buffer.getInt());
	 //  System.out.println("Buffer.get: " + buffer.get());
	  // System.out.println("Buffer.remaining: " + buffer.remaining());
	   assertFalse(testPacket.parseBundle(bundle));
}

@Test
public void parseBundleMagic() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{9,9,9,9,9,9,9,9,9,9}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	 //  System.out.println("Buffer: " + buffer);
	 //  System.out.println("Buffer.getInt: " + buffer.getInt());
	 //  System.out.println("Buffer.get: " + buffer.get());
	 //  System.out.println("Buffer.remaining: " + buffer.remaining());
	   assertFalse(testPacket.parseBundle(bundle));
}
@Test
public void parseBundleMagicother() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{1,1,1,1,1}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	//   System.out.println("Buffer: " + buffer);
	 //  System.out.println("Buffer.getInt: " + buffer.getInt());
	  // System.out.println("Buffer.get: " + buffer.get());
	 //  System.out.println("Buffer.remaining: " + buffer.remaining());
	   assertFalse(testPacket.parseBundle(bundle));
}
@Test
public void parseBundleMaxLength() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	//   System.out.println("Buffer: " + buffer);
	 //  System.out.println("Buffer.getInt: " + buffer.getInt());
	 //  System.out.println("Buffer.get: " + buffer.get());
	 //  System.out.println("Buffer.remaining: " + buffer.remaining());
	   assertFalse(testPacket.parseBundle(bundle));
}
@Test
public void parseBundleMaxMaxLength() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{9,8,2,4,5,1,6,5,3}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	//   System.out.println("Buffer.getInt: " + buffer.getInt());
	//   System.out.println("Buffer.getInt: " + buffer.getInt());
	//   System.out.println("Buffer: " + buffer);

	//   System.out.println("Buffer.get: " + buffer.get());
	 //  System.out.println("Buffer.remaining: " + buffer.remaining());
	   assertFalse(testPacket.parseBundle(bundle));
}

@Test
public void setByteBuffer() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{58,(byte) 143,5,(byte) 197,1,1,1,1,1,1,1,1,1,1,1,1,1}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	  // System.out.println("Buffer.getInt1: " + buffer.getInt());
	  // System.out.println("Buffer.getInt2: " + buffer.getInt());
	  // System.out.println("Buffer: " + buffer.get());
	  // System.out.println("Buffer.remaining: " + buffer.remaining());
	   assertFalse(testPacket.parseBundle(bundle));
	   
}
@Test
public void parseBundleAddressLengthLessThanRemaining() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{58,(byte) 143,5,(byte) 197,1,2,3,4,9,1,1,1,1,1,1,1,1}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	  // System.out.println("Buffer.getInt1: " + buffer.getInt());
	  // System.out.println("Buffer.getInt2: " + buffer.getInt());
	  // System.out.println("Buffer: " + buffer.get());
	   //System.out.println("Buffer.remaining: " + buffer.remaining());
	 

	  
	   assertFalse(testPacket.parseBundle(bundle));
	   
}
@Test
public void parseBundleCrcCccittReturnsTrue() {
	   InetPacket testPacket = new InetPacket(new byte[]{1,2,3});
	   byte[] bundle = new byte[]{58,(byte) 143,5,(byte) 197,1,2,3,4,9,1,1,1,1,1,1,1,1}; 
	   ByteBuffer buffer = ByteBuffer.wrap(bundle);
	  // System.out.println("Buffer.getInt1: " + buffer.getInt());
	  // System.out.println("Buffer.getInt2: " + buffer.getInt());
	  // System.out.println("Buffer: " + buffer.get());
	   //System.out.println("Buffer.remaining: " + buffer.remaining());
	 

	   new Expectations(CrcCccitt.class) {{ CrcCccitt.isValidMsgCRC((byte[])any, anyInt, anyInt); result=true; }};
	   assertTrue(testPacket.parseBundle(bundle));
	   
}

   
}
