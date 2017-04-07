package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mockit.Mocked;
import mockit.Verifications;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;

public class PluginFactoryTest {

	@Mocked(stubOutClassInitialization = true)
	final LoggerFactory unused = null;

	@Test
	public void testGetPluginByName(@Mocked Logger logger)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		String coderClassName = "us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder";

		OdePlugin result = PluginFactory.getPluginByName(coderClassName);
		assertNotNull(result);
		assertTrue(result instanceof OssAsn1Coder);
		new Verifications() {
			{
				logger.info("Getting Plugin: {}", coderClassName);
				logger.info("Classpath: {}", anyString);
				logger.info("Getting class: {}", anyString);
				logger.info("creating an instance of: {}", any);
			}
		};
	}

	@Test
	public void testException(@Mocked Logger logger, @Mocked(stubOutClassInitialization = true) final Thread thread)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		String coderClassName = "us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder";

		OdePlugin result = PluginFactory.getPluginByName(coderClassName);
		assertNotNull(result);
		assertTrue(result instanceof OssAsn1Coder);
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
