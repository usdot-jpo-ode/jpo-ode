package us.dot.its.jpo.ode.eventlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;

public class EventLogger {
	public final static Logger logger;
	
	static {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.reset();
		ContextInitializer ci = new ContextInitializer(loggerContext);
		try {
			ci.autoConfig();
		} catch (JoranException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger = loggerContext.getLogger(EventLogger.class);
	}
}

