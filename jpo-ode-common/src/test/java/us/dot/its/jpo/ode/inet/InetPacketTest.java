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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import us.dot.its.jpo.ode.util.CrcCccitt;

public class InetPacketTest {


  @Mocked
  DatagramPacket mockDatagramPacket;
  byte[] mockPayload;

  @Mocked InetAddress mockAddress;
  
  @Before
  public void setup() {
    new MockUp<InetAddress>() {
      @Mock InetAddress getByName(String host) {
          return mockAddress;
      }
    };
  }


  @Test
  public void testStringConstructorCallsPointConstructor() {
    try {
      new InetPacket("testHost", 5, new byte[] { 1, 2, 3 });
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
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    testPacket.getPoint();
    testPacket.getPayload();
    testPacket.getBundle();
    testPacket.toHexString();

  }

  /*
   * @Test public void testEvenNum() { boolean ans = false; boolean val; byte[]
   * bundle = null;
   * 
   * 
   * val = InetPacket.parseBundle(bundle); assertEquals(ans,val); }
   */

  @Test
  public void parseBundleNull() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = null;

    assertFalse(testPacket.parseBundle(bundle));
  }

  @Test
  public void parseBundleNotMagic() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 1, 2, 3, 4, 5, 6 };
    assertFalse(testPacket.parseBundle(bundle));
  }

  @Test
  public void parseBundleMagic() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 };
    assertFalse(testPacket.parseBundle(bundle));
  }

  @Test
  public void parseBundleMagicother() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 1, 1, 1, 1, 1 };
    assertFalse(testPacket.parseBundle(bundle));
  }

  @Test
  public void parseBundleMaxLength() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1 };
    assertFalse(testPacket.parseBundle(bundle));
  }

  @Test
  public void parseBundleMaxMaxLength() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 9, 8, 2, 4, 5, 1, 6, 5, 3 };
    assertFalse(testPacket.parseBundle(bundle));
  }

  @Test
  public void setByteBuffer() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 58, (byte) 143, 5, (byte) 197, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    assertFalse(testPacket.parseBundle(bundle));

  }

  @Test
  public void parseBundleAddressLengthLessThanRemaining() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 58, (byte) 143, 5, (byte) 197, 1, 2, 3, 4, 9, 1, 1, 1, 1, 1, 1, 1, 1 };

    assertFalse(testPacket.parseBundle(bundle));

  }

  @Test
  public void parseBundleCrcCccittReturnsTrue() {
    InetPacket testPacket = new InetPacket(new byte[] { 1, 2, 3 });
    byte[] bundle = new byte[] { 58, (byte) 143, 5, (byte) 197, 1, 2, 3, 4, 9, 1, 1, 1, 1, 1, 1, 1, 1 };

    new Expectations() {
      {
        CrcCccitt.isValidMsgCRC((byte[]) any, anyInt, anyInt);
        result = true;
      }
    };
    assertFalse(testPacket.parseBundle(bundle));

  }

}
