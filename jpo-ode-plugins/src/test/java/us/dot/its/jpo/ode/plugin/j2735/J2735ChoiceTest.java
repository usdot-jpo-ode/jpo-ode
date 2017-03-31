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
		assertEquals(derivedClass.getChosenFieldName(), "childField");
	}

	@Test
	public void testSetChosenField() {
		DerivedClass derivedClass = new DerivedClass();
		derivedClass.setChosenFieldName("childField");
		assertEquals(derivedClass.getChosenFieldName(), "childField");

		derivedClass.setChosenField("childField", "childFieldValue");
		assertEquals(derivedClass.getChildField(), "childFieldValue");
	}

	@Tested
	@Mocked
	J2735Choice j2735Choice;
	@Mocked(stubOutClassInitialization = true)
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
		assertEquals(j2735Choice.getChosenFieldName(), "childField");

		j2735Choice.setChosenField("wrongField", "childFieldValue");
	}
}
