package us.dot.its.jpo.ode.asn1.j2735;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.util.HexTool;

import us.dot.its.jpo.ode.asn1.j2735.J2735Util;

public class CoderHelper {
	
	private static final Logger log = Logger.getLogger(CoderHelper.class);

	private Coder coder;
	private String messageDir;
	
	public CoderHelper(Coder coder, String messageDir) {
		this.coder = coder;
		this.messageDir = messageDir;
	}

	public void encodeDecodeMessage(AbstractData message, String fileNameToWrite) throws EncodeFailedException,
			EncodeNotSupportedException, FileNotFoundException, DecodeFailedException, DecodeNotSupportedException {
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		coder.encode(message, sink);

		if (fileNameToWrite != null) {
			// write out to file
			FileOutputStream fos = new FileOutputStream(new File(messageDir + fileNameToWrite));
			coder.encode(message, fos);
		}

		// Extract the encoding from the sink stream
		byte[] encoding = sink.toByteArray();

		// Print the encoding using the HexTool utility
		if ( log.isDebugEnabled() )
			HexTool.printHex(encoding);

		AbstractData message2 = J2735Util.decode(coder, encoding);
		if ( message2 == null ) {
			log.warn("Got null message!");
		}

		assertEquals(message, message2);
	}

}
