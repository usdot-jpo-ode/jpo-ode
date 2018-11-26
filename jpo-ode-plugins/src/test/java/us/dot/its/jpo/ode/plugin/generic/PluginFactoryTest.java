package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;



@RunWith(JMockit.class)
public class PluginFactoryTest {


  @Mocked(stubOutClassInitialization = true)
	final LoggerFactory unused = null;

	@Test
	public void testGetPluginByName(@Mocked Logger logger)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		String coderClassName = "us.dot.its.jpo.ode.plugin.generic.TestPlugin";

		OdePlugin result = PluginFactory.getPluginByName(coderClassName);
		assertNotNull(result);
		assertTrue(result instanceof TestPlugin);
		new Verifications() {
			{
				logger.info("Getting Plugin: {}", coderClassName);
				logger.info("Classpath: {}", anyString);
				logger.info("Getting class: {}", anyString);
				logger.info("creating an instance of: {}", any);
			}
		};
	}


	@Test(expected = ClassNotFoundException.class)
	public void testException(@Mocked Logger logger)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		String coderClassName = "bogus.BogusClass";

		OdePlugin result = PluginFactory.getPluginByName(coderClassName);
		assertNotNull(result);
		assertTrue(result instanceof J2735Bsm);
		new Verifications() {
			{
				logger.info("Getting Plugin: {}", coderClassName);
				logger.error(anyString, (Exception) any);
				logger.info("Classpath: {}", anyString);
				logger.info("Getting class: {}", anyString);
				logger.info("creating an instance of: {}", any);
			}
		};
	}
}
