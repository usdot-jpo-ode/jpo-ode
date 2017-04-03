package us.dot.its.jpo.ode;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;

import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.j2735.pdm.PDM;
import us.dot.its.jpo.ode.snmp.PdmManagerService;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.snmp.TimManagerService;
import us.dot.its.jpo.ode.snmp.TimParameters;

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

	public static SnmpSession initialize(Object params, SnmpProperties props, String classType) {
		String logMsg;
		String serviceType = classType.endsWith("PDM") ? "PDM" : "TIM";

		if (null == params || null == props) {
			logMsg = serviceType + " SERVICE - Received null object";
			logger.error(logMsg);
			return null;
		}

		// Initialize the SNMP session
		try {
			return new SnmpSession(props);
		} catch (IOException e) {
			logMsg = serviceType + " SERVICE - Failed to create SNMP session: {}";
			logger.error(logMsg, e);
			return null;
		}
	}

	public static ResponseEvent createAndSend(PDM pdmParams, SnmpProperties props) {
		SnmpSession session = null;
		if (pdmParams != null)
			session = initialize(pdmParams, props, pdmParams.getClass().getName());

		if (session == null)
			return null;

		// Send the PDU
		ResponseEvent response = null;
		ScopedPDU pdu = PdmManagerService.createPDU(pdmParams);
		try {
			response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
		} catch (IOException | NullPointerException e) {
			logger.error("PDM SERVICE - Error while sending PDU: {}", e);
			return null;
		}
		return response;
	}

	/**
	 * Create an SNMP session given the values in
	 * 
	 * @param tim
	 *            - The TIM parameters (payload, channel, mode, etc)
	 * @param props
	 *            - The SNMP properties (ip, username, password, etc)
	 * @return ResponseEvent
	 */
	public static ResponseEvent createAndSend(TimParameters timParams, SnmpProperties props) {
		SnmpSession session = null;
		if (timParams != null)
			session = initialize(timParams, props, timParams.getClass().getName());

		if (session == null)
			return null;

		// Send the PDU
		ResponseEvent response = null;
		ScopedPDU pdu = TimManagerService.createPDU(timParams);
		try {
			response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
		} catch (IOException | NullPointerException e) {
			logger.error("TIM SERVICE - Error while sending PDU: {}", e);
			return null;
		}
		return response;

	}
}
