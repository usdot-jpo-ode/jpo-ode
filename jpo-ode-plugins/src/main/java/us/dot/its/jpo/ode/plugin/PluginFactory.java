package us.dot.its.jpo.ode.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Return concrete implementations for specific, known interfaces. */
public final class PluginFactory {

	private static Logger logger = LoggerFactory.getLogger(PluginFactory.class);
	

	/**
	 * Read in configuration data that maps names of interfaces to names of
	 * corresponding concrete implementation classes. Called early upon startup,
	 * before any implementations are needed by the rest of the program.
	 * 
	 * <P>
	 * Example of a possible entry in such a config file : myapp.TimeSource =
	 * myapp.TimeSourceOneDayAdvance
	 */
	public static void init() {
		//TODO
		// perhaps a properties file is read, perhaps some other source is used
	}

	/*
	 * Another variation: allow the caller to swap in different implementation
	 * classes at runtime, after calling init. This allows testing code to swap
	 * in various implementations.
	 */

	/**
	 * Return the concrete implementation of a OdePlugin interface.
	 * @param coderClassName 
	 */
	public static OdePlugin getPluginByName(String coderClassName) {
		logger.info("Getting Plugin: {}", coderClassName);
		OdePlugin result = (OdePlugin) buildObject(coderClassName);
		
		logger.info("Got Plugin: {}", result.toString());
		return result;
	}

	private static Object buildObject(String aClassName) {
		Object result = null;
		try {
//        	ClassLoader cl = ClassLoader.getSystemClassLoader();
//
//        	URL[] urls = ((URLClassLoader)cl).getURLs();
//
//        	for(URL url: urls){
//        		logger.info(url.getFile());
//        	}
// 
//			logger.info("Getting class: {}", aClassName);
			// note that, with this style, the implementation needs to have a
			// no-argument constructor!
			Class<?> implClass = Class.forName(aClassName);
			
        	logger.info("creating an instance of: {} \n {}", 
					aClassName, implClass);
			result = implClass.newInstance();
		} catch (Exception ex) {
			logger.error("Error instantiating an object of " + aClassName, ex);
		}
		return result;
	}
}
