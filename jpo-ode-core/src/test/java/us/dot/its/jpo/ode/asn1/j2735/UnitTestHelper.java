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
package us.dot.its.jpo.ode.asn1.j2735;

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
