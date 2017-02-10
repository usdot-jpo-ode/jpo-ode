package us.dot.its.jpo.ode.plugin.j2735.oss;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;

public class OssMessageFrame {

    private OssMessageFrame() {
        // never used (utility class)
    }

    // Custom exception
    public static class OssMessageFrameException extends Exception {

        private static final long serialVersionUID = -952945144250120705L;

        public OssMessageFrameException(String msg) {
            super(msg);
        }

        public OssMessageFrameException(String msg, Exception e) {
            super(msg, e);
        }

    }

    // Convert a message frame
    public static J2735MessageFrame genericMessageFrame(MessageFrame mf)
            throws OssMessageFrameException, OssBsmPart2Exception {

        J2735MessageFrame genericMessageFrame = new J2735MessageFrame();
        
        genericMessageFrame.setMessageId( J2735DSRCmsgID.valueOf( mf.messageId.intValue() ));

        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

        if (genericMessageFrame.getMessageId() == J2735DSRCmsgID.basicSafetyMessage) {

            // If basicSafetyMessage
            BasicSafetyMessage bsm;
            if (mf.value.getDecodedValue() != null) {
                bsm = (BasicSafetyMessage) mf.value.getDecodedValue();
            } else if (mf.value.getEncodedValueAsStream() != null) {
                bsm = new BasicSafetyMessage();
                try {
                    coder.decode(mf.value.getEncodedValueAsStream(), bsm);
                } catch (DecodeFailedException | DecodeNotSupportedException e) {
                    throw new OssMessageFrameException("Error decoding OpenType value", e);
                }
            } else {
                throw new OssMessageFrameException("No OpenType value");
            }

            genericMessageFrame.setValue(OssBsm.genericBsm(bsm));

        } else {
            throw new OssMessageFrameException("Unknown message type");
        }

        return genericMessageFrame;

    }

}
