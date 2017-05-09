package gov.usdot.asn1.j2735;

import java.util.Enumeration;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class UnitTestHelper {
	
	public static void initLog4j(boolean isDebugOutput) {
		initLog4j(isDebugOutput ? Level.DEBUG : Level.INFO);
	}
	
	public static void initLog4j(Level level) {
	    Logger rootLogger = Logger.getRootLogger();
	    @SuppressWarnings("rawtypes")
		Enumeration appenders = rootLogger.getAllAppenders();
	    if ( appenders == null || !appenders.hasMoreElements() ) {
		    rootLogger.setLevel(level);
		    rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%-6r [%p] %c - %m%n")));
	    }
	}
}
