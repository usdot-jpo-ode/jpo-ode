package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.COERCoder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import gov.usdot.asn1.generated.ieee1609dot2.Ieee1609dot2;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.plugin.OdePlugin;

public class Oss1609dot2Coder {

    private static final Logger logger = LoggerFactory.getLogger(Oss1609dot2Coder.class);

    private COERCoder coder;

    public Oss1609dot2Coder() {
        coder = Ieee1609dot2.getCOERCoder();
    }

    public Ieee1609Dot2Data decodeIeee1609Dot2DataHex(String hexMsg) {
        return decodeIeee1609Dot2DataBytes(DatatypeConverter.parseHexBinary(hexMsg));
    }

    public Ieee1609Dot2Data decodeIeee1609Dot2DataBytes(byte[] byteArrayMsg) {

        InputStream ins = new ByteArrayInputStream(byteArrayMsg);

        Ieee1609Dot2Data returnValue = null;

        try {
            returnValue = (Ieee1609Dot2Data) coder.decode(ins, new Ieee1609Dot2Data());
        } catch (Exception e) {
            logger.error("Error decoding ", e);
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                logger.warn("Error closing input stream: ", e);
            }
        }

        return returnValue;
    }

    public Ieee1609Dot2Data decodeIeee1609Dot2DataStream(BufferedInputStream bis) {
        Ieee1609Dot2Data returnValue = null;

        try {
            if (bis.available() > 0) {
                if (bis.markSupported()) {
                    bis.mark(OdePlugin.INPUT_STREAM_BUFFER_SIZE);
                }
                returnValue = (Ieee1609Dot2Data) coder.decode(bis, new Ieee1609Dot2Data());
                logger.debug("Decoded as {}: {}", Ieee1609Dot2Data.class.getSimpleName(), returnValue);
            }
        } catch (Exception e) {
           logger.debug("Exception occured while decoding as {}", Ieee1609Dot2Data.class.getSimpleName());
            if (bis.markSupported()) {
                try {
                    bis.reset();
                } catch (IOException ioe) {
                    logger.error("Error reseting Input Stream to marked position", ioe);
                    handleDecodeException(ioe);
                }
            }
            handleDecodeException(e);
        }

        return returnValue;
    }
    
    public void handleDecodeException(Exception e) {
        
        if (e instanceof DecodeFailedException) {
            AbstractData partialDecodedMessage = ((DecodeFailedException) e).getDecodedData();
            if (partialDecodedMessage != null) {
                logger.error("Error, message only partially decoded: {}", partialDecodedMessage);
            } else {
                logger.debug("Ignoring extraneous bytes at the end of the input stream.");
            }
        } else if (e instanceof DecodeNotSupportedException) {
            logger.error("Error decoding, data does not represent valid message", e);
        } else if (e instanceof IOException) {
            logger.error("Error decoding", e);
        } else {
            logger.error("Unknown error", e);
        }
    }

}
