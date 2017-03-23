package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MessageFrame;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssMessageFrame.OssMessageFrameException;

public class OssAsn1Coder implements Asn1Plugin {

    private static Logger logger = LoggerFactory.getLogger(OssAsn1Coder.class);

    private PERUnalignedCoder coder;

    public OssAsn1Coder() {
        coder = J2735.getPERUnalignedCoder();
    }

    @Override
    public Asn1Object decodeUPERBase64(String base64Msg) {
        return decodeUPERMessageFrameBytes(DatatypeConverter.parseBase64Binary(base64Msg));
    }

    @Override
    public Asn1Object decodeUPERMessageFrameHex(String hexMsg) {
        return decodeUPERMessageFrameBytes(DatatypeConverter.parseHexBinary(hexMsg));
    }

    @Override
    public Asn1Object decodeUPERMessageFrameBytes(byte[] byteArrayMsg) {

        InputStream ins = new ByteArrayInputStream(byteArrayMsg);

        MessageFrame mf = new MessageFrame();

        J2735Bsm returnValue = null;

        try {
            coder.decode(ins, mf);
            returnValue = OssMessageFrame.genericMessageFrame(mf).getValue();
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

    @Override
    public Asn1Object decodeUPERBsmBytes(byte[] byteArrayMsg) {
        InputStream ins = new ByteArrayInputStream(byteArrayMsg);

        BasicSafetyMessage bsm = new BasicSafetyMessage();
        J2735Bsm gbsm = null;

        try {
            coder.decode(ins, bsm);
            gbsm = OssBsm.genericBsm(bsm);
        } catch (Exception e) {
            logger.error("Error decoding ", e);
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                logger.warn("Error closing input stream: ", e);
            }
        }

        return gbsm;
    }

    @Override
    public Asn1Object decodeUPERBsmStream(InputStream ins) {
        BasicSafetyMessage bsm = new BasicSafetyMessage();
        J2735Bsm gbsm = null;

        try {
            if (ins.available() > 0) {
                coder.decode(ins, bsm);
                gbsm = OssBsm.genericBsm(bsm);
            }
        } catch (DecodeFailedException e) { //NOSONAR
            AbstractData partialDecodedMessage = e.getDecodedData();
            if (partialDecodedMessage != null) {
                logger.error("DECODER - Error, message only partially decoded: {}", 
                      partialDecodedMessage.toString());
            } else {
                logger.debug("DECODER - Ignoring extraneous bytes at the end of the input stream.");
            }
        } catch (DecodeNotSupportedException e) {
            logger.error("DECODER - Error decoding, data does not represent valid message", e);
        } catch (IOException e) {
            logger.error("DECODER - Error decoding, general error: {}", e);
        } catch (OssBsmPart2Exception e) {
            logger.error("DECODER - Error decoding, unable to parse BSM part 2: {}", e);
        }
        
        return gbsm;
    }

    @Override
    public Asn1Object decodeUPERMessageFrameStream(InputStream ins) {
        MessageFrame mf = new MessageFrame();
        J2735MessageFrame gmf = null;

        try {
            if (ins.available() > 0) {
                coder.decode(ins, mf);
                gmf = OssMessageFrame.genericMessageFrame(mf);
            }
        } catch (DecodeFailedException e) {//NOSONAR
            AbstractData partialDecodedMessage = e.getDecodedData();
            if (partialDecodedMessage != null) {
                logger.error("DECODER - Error, message only partially decoded: {}", partialDecodedMessage.toString());
            } else {
                logger.debug("DECODER - Ignoring extraneous bytes at the end of the input stream.");
            }
        } catch (DecodeNotSupportedException e) {
            logger.error("DECODER - Error decoding, data does not represent valid message", e);
        } catch (IOException e) {
            logger.error("DECODER - Error decoding, general error: {}", e);
        } catch (OssBsmPart2Exception e) {
            logger.error("DECODER - Error decoding, unable to parse BSM part 2: {}", e);
        } catch (OssMessageFrameException e) {
            logger.error("DECODER - Error decoding, message frame exception: {}", e);
        }

        return gmf;
    }

    @Override
    public String encodeUPERBase64(Asn1Object asn1Object) {
        return DatatypeConverter.printBase64Binary(encodeUPERBytes(asn1Object));
    }

    @Override
    public String encodeUPERHex(Asn1Object asn1Object) {
        return DatatypeConverter.printHexBinary(encodeUPERBytes(asn1Object));
    }

    @Override
    public byte[] encodeUPERBytes(Asn1Object asn1Object) {
        if (asn1Object instanceof J2735Bsm) {
            J2735Bsm genericBsm = (J2735Bsm) asn1Object;
            try {
                ByteBuffer bytes = coder.encode(OssBsm.basicSafetyMessage(genericBsm));
                return bytes.array();
            } catch (Exception e) {
                logger.warn("Error encoding data.", e);
            }
        }

        return null;
    }

    @Override
    public Asn1Object decodeUPERBsmHex(String hexMsg) {
        return decodeUPERBsmBytes(DatatypeConverter.parseHexBinary(hexMsg));
    }

}
