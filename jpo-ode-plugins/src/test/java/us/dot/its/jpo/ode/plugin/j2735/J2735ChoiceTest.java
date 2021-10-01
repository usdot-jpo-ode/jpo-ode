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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mockit.*;

public class J2735ChoiceTest {

	@SuppressWarnings("serial")
	public class DerivedClass extends J2735Choice {

		private String childField;

		public DerivedClass() {
			super();
		}

		public String getChildField() {
			return childField;
		}
	}

	@Test
	public void testChosenFieldName() {
		DerivedClass derivedClass = new DerivedClass();
		derivedClass.setChosenFieldName("childField");
		assertEquals("childField", derivedClass.getChosenFieldName());
	}

	@Test
	public void testSetChosenField() {
		DerivedClass derivedClass = new DerivedClass();
		derivedClass.setChosenFieldName("childField");
		assertEquals("childField", derivedClass.getChosenFieldName());

		derivedClass.setChosenField("childField", "childFieldValue");
		assertEquals("childFieldValue", derivedClass.getChildField());
	}

	@Tested
	@Mocked
	J2735Choice j2735Choice;
	@Mocked//(stubOutClassInitialization = true)
	final LoggerFactory unused = null;

	@Test(expected = NoSuchFieldException.class)
	public void testSetChosenFieldException2(@Mocked Logger logger) throws NoSuchFieldException {
		new Expectations() {
			{
				logger.error(anyString, (Exception) any);
				;
				result = new NoSuchFieldException();
			}
		};
		j2735Choice.setChosenFieldName("childField");
		assertEquals("childField", j2735Choice.getChosenFieldName());

		j2735Choice.setChosenField("wrongField", "childFieldValue");
	}
}
