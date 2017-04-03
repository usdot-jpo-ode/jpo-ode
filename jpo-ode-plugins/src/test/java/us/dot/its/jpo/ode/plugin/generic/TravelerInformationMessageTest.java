package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.TravelerInformationMessage;

public class TravelerInformationMessageTest {
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<TravelerInformationMessage > constructor = TravelerInformationMessage.class.getDeclaredConstructor();
     assertTrue(Modifier.isPrivate(constructor.getModifiers()));
     constructor.setAccessible(true);
     try {
       constructor.newInstance();
       fail("Expected IllegalAccessException.class");
     } catch (Exception e) {
       assertEquals(InvocationTargetException.class, e.getClass());
     }
   }
}
