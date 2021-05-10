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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import us.dot.its.jpo.ode.util.CodecUtils;

public class InetPacketFunctionalTest {
   
	private static boolean isVerbose = false;
	
  @Test
	public void testConstrcutor() throws UnknownHostException {
	   InetPacket pkt = new InetPacket("bah.com", 1111, null);
	   
	   assertNull(pkt.getPayload());
	}
	
	@Test @Ignore
	public void test() throws UnknownHostException {
		test("127.0.0.1", 12, "01234567890".getBytes());
		test("::1", 47561, "0123456789001234567890".getBytes());
		test("1080:0:0:0:8:800:200C:417A", 345, "".getBytes());
		test("1080:0:0:0:8:800:200C:417A", 345, null);
		test("1.0.0", 47561, "".getBytes());
		test("::FFFF:129.144.52.38", 4756, "0".getBytes());
		test("1080::8:800:200C:417A", 4756, new byte[] { (byte)0, (byte)1 });
		test("2001:0:9d38:90d7:3ce3:339d:f5c3:c42b", 11111, new byte[] { (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef });
		test(null, 22222, new byte[] { (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef });
		test("fdf8:f53b:82e4::53", 11111, new byte[] { (byte)0xca, (byte)0xfe, (byte)0xba, (byte)0xbe });
	}
	
	public void test(String address, int port, byte[] payload) throws UnknownHostException {
		if ( payload == null )
			return;
		if ( isVerbose ) 
			System.out.println("---------- Test ----------");		
		DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName(address), port);
		InetPacket pbOut = new InetPacket(packet);
		test(address, port, payload, pbOut);
	}

	public void test(String address, int port, byte[] payload, InetPacket pbOut) throws UnknownHostException {
		InetPacket pbIn = new InetPacket(pbOut.getBundle());
		if ( isVerbose ) {
			print("From params", pbOut);
			print("From bundle", pbIn);
		}
		InetPoint pointIn = pbIn.getPoint();
		assertNotNull(pointIn);
		InetPoint pointOut = pbOut.getPoint();
		assertEquals(pointOut.port, pointIn.port);
		assertArrayEquals(pointOut.address, pointIn.address);
		byte[] pbOutPayload = pbOut.getPayload();
		byte[] pyInPayload = pbIn.getPayload();
		if ( pbOutPayload != null && pbOutPayload != null )
			assertTrue(Arrays.equals(pbOutPayload, pyInPayload));
		else if (pbOutPayload != null || pbOutPayload != null)
			assertTrue(false);
		assertTrue(Arrays.equals(pbOut.getBundle(), pbIn.getBundle()));		
	}
	
	void print(String header, InetPacket pb) throws UnknownHostException {
		assertNotNull(pb);
		InetPoint point = pb.getPoint();
		if( point != null ) {
			System.out.printf("%s: port:  %d (0x%x)\n", header, point.port, point.port );
			System.out.printf("%s: address size: %d value: %s ip: %s\n", header, point.address.length, 
					CodecUtils.toHex(point.address), InetAddress.getByAddress(point.address).getHostAddress() );
			System.out.printf("%s: forward:  %s\n", header, point.forward ? "true" : "false" );
		} else {
			System.out.printf("%s: Inet point is null\n", header);
		}
		byte[] p = pb.getPayload();
		System.out.printf("%s payload: %s\n", header, p != null && p.length > 0 ? CodecUtils.toHex(p) : "<empty>");
		System.out.printf("%s bundle: %s\n", header, pb.toHexString());
	}

}
