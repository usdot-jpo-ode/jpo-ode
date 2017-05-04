package us.dot.its.jpo.ode;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;

public class ManagerAndControllerServices {
	private static Logger logger = LoggerFactory.getLogger(ManagerAndControllerServices.class);

	private ManagerAndControllerServices() {
	}

	public static String log(boolean success, String msg, Throwable t) {
		if (success) {
			EventLogger.logger.info(msg);
			String myMsg = String.format("{success: true, message:\"%1$s\"}", msg);
			logger.info(myMsg);
			return myMsg;
		} else {
			if (Objects.nonNull(t)) {
				EventLogger.logger.error(msg, t);
				logger.error(msg, t);
			} else {
				EventLogger.logger.error(msg);
				logger.error(msg);
			}
			return "{success: false, message: \"" + msg + "\"}";
		}
	}

	public static SnmpSession createSnmpSession(RSU rsu) {
		// Initialize the SNMP session
		try {
			return new SnmpSession(rsu);
		} catch (IOException e) {
			log(false, "Failed to create SNMP session: {}", e);
			return null;
		}
	}

}
