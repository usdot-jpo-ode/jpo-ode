package us.dot.its.jpo.ode.common.asn1;

import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.oss.asn1.AbstractData;

public class DialogIDHelper {
	
	private static final Logger log = Logger.getLogger(DialogIDHelper.class);

	static public SemiDialogID getDialogID(AbstractData pdu) {
		try {
			return (SemiDialogID)pdu.getClass().getMethod("getDialogID").invoke(pdu);
		} catch (SecurityException e) {
			log.warn(String.format("Couldn't detect message dialog ID via reflection due to SecurityException for message: '%s'", pdu));
		} catch (NoSuchMethodException e) {
			log.warn(String.format("Couldn't detect message dialog ID via reflection due to NoSuchMethodException for message: '%s'", pdu));
		} catch (IllegalArgumentException e) {
			log.warn(String.format("Couldn't detect message dialog ID via reflection due to IllegalArgumentException for message: '%s'", pdu));
		} catch (IllegalAccessException e) {
			log.warn(String.format("Couldn't detect message dialog ID via reflection due to IllegalAccessException for message: '%s'", pdu));
		} catch (InvocationTargetException e) {
			log.warn(String.format("Couldn't detect message dialog ID via reflection due to InvocationTargetException for message: '%s'", pdu));
		}
		return null;
	}
	
	public static SemiDialogID getDialogID(String dialogID) {
		if ( dialogID != null ) {
			try {
				return (SemiDialogID)SemiDialogID.class.getField(dialogID).get(null);
			} catch (IllegalArgumentException e) {
				log.warn(String.format("Couldn't get message dialog ID via reflection due to IllegalArgumentException for string dialogID: '%s'", dialogID));
			} catch (SecurityException e) {
				log.warn(String.format("Couldn't get message dialog ID via reflection due to SecurityException for string dialogID: '%s'", dialogID));
			} catch (IllegalAccessException e) {
				log.warn(String.format("Couldn't get message dialog ID via reflection due to IllegalAccessException for string dialogID: '%s'", dialogID));
			} catch (NoSuchFieldException e) {
				log.warn(String.format("Couldn't get message dialog ID via reflection due to NoSuchFieldException for string dialogID: '%s'", dialogID));
			}
		}
		return null;
	}
	
	static public String getDialogID(SemiDialogID dialogID) {
		return dialogID != null ? dialogID.toString().replaceAll("^(value\\s+SemiDialogID\\s*::=\\s*)(\\w+)\\s*$", "$2") : null;
	}
}
