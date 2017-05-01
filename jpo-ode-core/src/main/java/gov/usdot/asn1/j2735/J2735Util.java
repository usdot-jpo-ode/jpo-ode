package gov.usdot.asn1.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DFullTime;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import gov.usdot.asn1.j2735.msg.ids.ConnectedVehicleMessageID;
import gov.usdot.asn1.j2735.msg.ids.ConnectedVehicleMessageLookup;
import gov.usdot.asn1.j2735.msg.ids.SEMIMessageID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.coders.DecoderException;

public class J2735Util {
	
	private static final Logger logger = Logger.getLogger(J2735Util.class);
	
	private final static int LAT_LONG_CONVERSION_FACTOR = 10000000;
	private final static int LAT_LONG_DECIMAL_PRECISION = 7;
	
	public static byte[] shortsToBytes(short[] shorts) {
		ByteBuffer buffer = ByteBuffer.allocate(shorts.length*2).order(ByteOrder.BIG_ENDIAN);
		for (short num: shorts) {
			buffer.putShort(num);
		}
		return buffer.array();
	}
	
	public static byte[] shortToBytes(short number) {
		short[] shorts = new short[] { number };
		return shortsToBytes(shorts);
	}
	
	public static short bytesToShort(byte[] bytes) {
		return bytesToShorts(bytes)[0];
	}
	
	public static short[] bytesToShorts(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length).order(ByteOrder.BIG_ENDIAN);
		buffer.put(bytes);
		buffer.flip();
		int numberOfShorts = bytes.length / 2;
		short[] shorts = new short[numberOfShorts];
		for (int i = 0; i < numberOfShorts; i++) {
			shorts[i] = buffer.getShort();
		}
		return shorts;
	}
	
	public static byte[] mergeBytes(byte[]... bytes) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (byte[] bArray: bytes) {
			outputStream.write(bArray);
		}
		return outputStream.toByteArray();
	}
	
	public static AbstractData decode(Coder coder, byte[] message) throws DecodeFailedException, DecodeNotSupportedException {
		AbstractData abstractData = null;
		for( int preambleSize = 0; abstractData == null && preambleSize <= 2; preambleSize++ ) {
			ConnectedVehicleMessageID msgID = getMessageID(message, preambleSize);
			try {
				abstractData = decode(coder, message, msgID);
			} catch( DecoderException ex ) {
			}
		}
		return abstractData;
	}
	
	private static final SemiDialogID[] semiDialogIDs = (SemiDialogID[])SemiDialogID.vehSitData.getNamedNumbers();
	private static final SemiSequenceID[] semiSvcIDs = (SemiSequenceID[])SemiSequenceID.svcReq.getNamedNumbers();
		
	private static ConnectedVehicleMessageID getMessageID(final byte[] message, final int uperPreambleSize) {
		if (message == null || message.length < 2) 
			return null;
		
		// UPER messages in the beginning have an extra preamble bit for every optional high level element (if any)
		// So the format is: [preamble] extension_range (1 bit) dialogID (4 bits) extension_range (1 bit) seqID (4 bits)
		// So far all our messages have at most two top level optional elements
		
		final int b0 = message[0] & 0xff;
		final int b1 = message[1] & 0xff;

		final int dlgID, sID;
		
		switch(uperPreambleSize) {
		case 0: 
			// no preamble
			// dialogID --extension range: <.0> --contents: <0001>
			// seqID    --extension range: <0>  --contents: <01.11>
			dlgID = (b0 >> 3) & 0xf;
			sID = ((b0 & 3) << 2) + (b1 >> 6);		
			break;
		case 1:
			// --preamble: <.1>
			// dialogID --extension range: <0> --contents: <0001>
			// seqID    --extension range: <0> --contents: <0.111>
			dlgID = (b0 >> 2) & 0xf;
			sID = ((b0 & 1) << 3) + (b1 >> 5);
			break;
		case 2:
			// --preamble: <.00>
			// dialogID --extension range: <0> --contents: <0000>
			// seqID    --extension range: <0> --contents: <.0000>  
			dlgID = (b0 >> 1) & 0xf;
			sID =   (b1 >> 4) & 0xf;			
			break;
		default:
			logger.error(String.format("Message to decode with unsepported UPER preamble size of %d, cannot decode!", uperPreambleSize));
			return null;
		}

		
		if ( dlgID < 0 || dlgID >= semiDialogIDs.length || sID < 0 || sID >= semiSvcIDs.length )
			return null;

		final SemiDialogID dialogID = semiDialogIDs[dlgID];
		final SemiSequenceID seqID  = semiSvcIDs[sID];
		
		return new SEMIMessageID(dialogID.longValue(), seqID.longValue(), uperPreambleSize);
	}
	
	private static AbstractData decode(Coder coder, byte[] message, ConnectedVehicleMessageID msgID) throws DecodeFailedException, DecodeNotSupportedException {
		AbstractData typeMessage = ConnectedVehicleMessageLookup.lookupMessage(msgID);
		if (typeMessage == null)
			return null;
		ByteArrayInputStream source = new ByteArrayInputStream(message);
		return coder.decode(source, typeMessage);
	}
	
	/**
	 * Decode bytes to the object specified by name
	 * @param coder coder to use for decoding
	 * @param message bytes to decode
	 * @param objectName name of the encoded object 
	 * @return decoded object
	 * @throws DecodeFailedException if decoding fails
	 * @throws DecodeNotSupportedException if decoding is not supported
	 */
	public static AbstractData decode(Coder coder, byte[] message, String objectName) throws DecodeFailedException, DecodeNotSupportedException {
		ByteArrayInputStream source = new ByteArrayInputStream(message);
		return decode(coder, source, objectName);
	}
	
	/**
	 * Decode input stream to the object specified by name
	 * @param coder coder to use for decoding
	 * @param message bytes to decode
	 * @param objectName name of the encoded object 
	 * @return decoded object
	 * @throws DecodeFailedException if decoding fails
	 * @throws DecodeNotSupportedException if decoding is not supported
	 */
	public static AbstractData decode(Coder coder, InputStream message, String objectName) throws DecodeFailedException, DecodeNotSupportedException {
		AbstractData typeMessage = ConnectedVehicleMessageLookup.lookupMessage(objectName);
		return typeMessage != null ? coder.decode(message, typeMessage) : null;
	}
	
	/**
	 * Takes a Lat or Long as a double and converts to an int.
	 * @param point
	 * @return
	 */
	public static int convertGeoCoordinateToInt(double point) {
		double convertedPoint = point * LAT_LONG_CONVERSION_FACTOR;
		return (int)Math.round(convertedPoint);
	}

	/**
	 * Takes a Lat or Long as an int and converts to a double.
	 * @param point
	 * @return
	 */
	public static double convertGeoCoordinateToDouble(int point) {
		double convertedPoint = ((double)point) / LAT_LONG_CONVERSION_FACTOR;
		BigDecimal bd = new BigDecimal(convertedPoint);
		bd.setScale(LAT_LONG_DECIMAL_PRECISION, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	public static String ipToString(byte[] ipBytes) {
		String ip = "";
		InetAddress ia = null;
		try {
			ia = (InetAddress.getByAddress(ipBytes));
			ip = ia.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("Failed to decode IP Address: " + ipBytes, e);
		}
		return ip;
	}
	
	public static byte[] ipToBytes(String ipAddress) {
		InetAddress ia = null;
		try {
			ia = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			logger.error("Failed to encode IP Address: " + ipAddress, e);
		}
		if (ia == null) {
			return null;
		} else {
			return ia.getAddress();
		}
	}
	
	public static Date toDate(DFullTime fullTime) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.set(fullTime.getYear().intValue(), fullTime.getMonth().intValue()-1, fullTime.getDay().intValue(),
				fullTime.getHour().intValue(), fullTime.getMinute().intValue(), 0);
		return cal.getTime();
	}
	
	public static Date toDate(DDateTime dateTime) {
		Calendar cal = Calendar.getInstance();
		cal.set(dateTime.getYear().intValue(), dateTime.getMonth().intValue()-1, dateTime.getDay().intValue(),
				dateTime.getHour().intValue(), dateTime.getMinute().intValue(), dateTime.getSecond().intValue());
		return cal.getTime();
	}
	
	public static String toFormattedDateString(DFullTime fullTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(toDate(fullTime));
	}
	
	public static String toFormattedDateString(DDateTime dateTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(toDate(dateTime));
	}
	
	// usage: byte[] encodedVsdm  = J2735Util.<VehSitDataMessage>createEncodedCvMessage(coder, msgBerFolder, "VehSitDataMessage.uper");
	public static <T extends AbstractData> byte[] createEncodedCvMessage(Coder coder, String msgBerFolder, String msgBerFileName) {
		final String msgTypeName = FilenameUtils.removeExtension(msgBerFileName);
		try {
			T cvMsg = J2735Util.<T>createCvMessage(coder, msgBerFolder, msgBerFileName);
			ByteArrayOutputStream sink = new ByteArrayOutputStream();
			coder.encode(cvMsg, sink);
			byte[] responseBytes = sink.toByteArray();
			return responseBytes;
		} catch (EncodeFailedException ex) {
			logger.error(String.format("Couldn't encode %s message because encoding failed", msgTypeName), ex);
		} catch (EncodeNotSupportedException ex) {
			logger.error(String.format("Couldn't encode %s message because encoding is not supported", msgTypeName), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	// usage: VehSitDataMessage vsdm = J2735Util.<VehSitDataMessage>createCvMessage(coder, msgFolder, "VehSitDataMessage.uper");
	public static <T extends AbstractData> T createCvMessage(Coder coder, String msgBerFolder, String msgBerFileName) {
		final File msgFile = new File(msgBerFolder, msgBerFileName);
		final String msgTypeName = FilenameUtils.removeExtension(msgBerFileName);
		try {
			byte[] msgBytes = FileUtils.readFileToByteArray(msgFile);
			AbstractData pdu = J2735Util.decode(coder, msgBytes);
			return (T)pdu;
		} catch (IOException ex) {
			logger.error(String.format("Couldn't create %s message", msgTypeName), ex);
		} catch (DecodeFailedException ex) {
			logger.error(String.format("Couldn't create %s message because decoding failed", msgTypeName), ex);
		} catch (DecodeNotSupportedException ex) {
			logger.error(String.format("Couldn't create %s message because decoding is not supported", msgTypeName), ex);					
		}
		return null;
	}
	
	public static TemporaryID createTemporaryID() {
		byte[] id = new byte[4];
		new Random().nextBytes(id);
		return new TemporaryID(id);
	}

	public static DDateTime expireInMin(int expirationIntervalInMin) {
		Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		now.add(Calendar.MINUTE, expirationIntervalInMin);
		return new DDateTime(
				new DYear(now.get(Calendar.YEAR)), 
				new DMonth(now.get(Calendar.MONTH)+1), 
				new DDay(now.get(Calendar.DAY_OF_MONTH)), 
				new DHour(now.get(Calendar.HOUR_OF_DAY)), 
				new DMinute(now.get(Calendar.MINUTE)), 
				new DSecond(now.get(Calendar.SECOND)),
				new DOffset(0));
	}
	
	public static boolean isExpired(DDateTime exp) {
		Calendar expire = J2735Util.DDateTimeToCalendar(exp);
		Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		return expire.before(now);
	}
	
	public static String formatCalendar(Calendar calendar) {
		final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS z";
		SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(calendar.getTime());
	}
	
	public static Calendar DDateTimeToCalendar(DDateTime ddt) {
		Calendar calendar = new GregorianCalendar(ddt.getYear().intValue(), ddt.getMonth().intValue()-1, ddt.getDay().intValue(), 
				ddt.getHour().intValue(), ddt.getMinute().intValue(), ddt.getSecond().intValue());
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		return calendar;
	}

}
