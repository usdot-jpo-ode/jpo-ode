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
package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.*;

import org.junit.Test;

public class J2735RegionalContentTest {

	@Test
	public void testId() {
		Integer id = 2;
		J2735RegionalContent j2735RegionalContent = new J2735RegionalContent();
		j2735RegionalContent.setId(id);
		assertEquals(j2735RegionalContent.getId(), id);
	}
	
	@Test
	public void testRegionId() {
		Integer id = 2;
		J2735RegionalContent j2735RegionalContent = new J2735RegionalContent();
		j2735RegionalContent.setId(id);
		assertEquals(j2735RegionalContent.getId(), id);
	}
	
	@Test
	public void testValue() {
		byte[] value = new byte[] {0x00, 0x01, 0x02};
		J2735RegionalContent j2735RegionalContent = new J2735RegionalContent();
		j2735RegionalContent.setValue(value);
		assertEquals(j2735RegionalContent.getValue(), value);
	}
	
	@Test
	public void testHashCode() {
		Integer id1 = 2;
		Integer id2 = 2;
		byte[] value1 = new byte[] {0x00, 0x01, 0x02};
		byte[] value2 = new byte[] {0x00, 0x01, 0x02};
		
		J2735RegionalContent j2735RegionalContent1 = new J2735RegionalContent();
		J2735RegionalContent j2735RegionalContent2 = new J2735RegionalContent();
		assertEquals(j2735RegionalContent1.hashCode(), j2735RegionalContent2.hashCode());
		
		j2735RegionalContent1.setId(id1);
		assertNotEquals(j2735RegionalContent1.hashCode(), j2735RegionalContent2.hashCode());
		
		j2735RegionalContent2.setId(id2);
		assertEquals(j2735RegionalContent1.hashCode(), j2735RegionalContent2.hashCode());
		
		j2735RegionalContent1.setValue(value1);
		assertNotEquals(j2735RegionalContent1.hashCode(), j2735RegionalContent2.hashCode());
		
		j2735RegionalContent2.setValue(value2);
		assertEquals(j2735RegionalContent1.hashCode(), j2735RegionalContent2.hashCode());
	}
	
	@Test
	public void testEquals() {
		Integer id = 2;
		Integer id2 = 3;
		byte[] value = new byte[] {0x00, 0x01, 0x02};
		
		J2735RegionalContent j2735RegionalContent1 = new J2735RegionalContent();
		J2735RegionalContent j2735RegionalContent2 = new J2735RegionalContent();
		assertTrue(j2735RegionalContent1.equals(j2735RegionalContent1));
		assertFalse(j2735RegionalContent1.equals(null));
		assertFalse(j2735RegionalContent1.equals(new Object()));
		assertTrue(j2735RegionalContent1.equals(j2735RegionalContent2));
		
		j2735RegionalContent1.setId(null);
		j2735RegionalContent2.setId(id2);
		assertFalse(j2735RegionalContent1.equals(j2735RegionalContent2));
		
		j2735RegionalContent1.setId(id);
		assertFalse(j2735RegionalContent1.equals(j2735RegionalContent2));
		
		j2735RegionalContent2.setId(id);
		assertTrue(j2735RegionalContent1.equals(j2735RegionalContent2));
		
		j2735RegionalContent1.setValue(value);
		assertFalse(j2735RegionalContent1.equals(j2735RegionalContent2));
		
		j2735RegionalContent2.setValue(value);
		assertTrue(j2735RegionalContent1.equals(j2735RegionalContent2));
	}
}
