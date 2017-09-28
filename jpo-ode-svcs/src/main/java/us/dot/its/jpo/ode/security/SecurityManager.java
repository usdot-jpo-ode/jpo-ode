package us.dot.its.jpo.ode.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import gov.usdot.cv.security.msg.MessageException;

public class SecurityManager {
    
    private static Logger logger = LoggerFactory.getLogger(SecurityManager.class);

    public static class SecurityManagerException extends Exception {

        private static final long serialVersionUID = 1L;

        public SecurityManagerException(String msg, Exception e2) {
            super (msg, e2);
        }

        public SecurityManagerException(String string) {
            super(string);
        }

    }

    /**
     * Returns true if the the signed message has valid generation time.
     * @param signedMsg
     * @return
     * @throws SecurityManagerException 
     */
    public static void validateGenerationTime(IEEE1609p2Message signedMsg) 
            throws SecurityManagerException {
        // decode and validate message
        // check that generation time is not in the future
        // if we change generation time to expiration time as per standard then we reverse the check
        long generationTime = signedMsg.getGenerationTime().getTime();
        logger.debug("Message generation time: {}", signedMsg.getGenerationTime());
        long now = new Date().getTime();
        if (generationTime >= now) {
            throw new SecurityManagerException("Generation time ("
                    + generationTime
                    + ") is in the future (now = "
                    + now 
                    + ")");
        }
    }

    /**
     * This method returns a message if it is signed.
     * 
     * @param signedMsgBytes
     * @return
     * @throws MessageException
     * @throws CertificateException
     * @throws CryptoException
     * @throws EncodeFailedException
     * @throws EncodeNotSupportedException
     */
    public static IEEE1609p2Message decodeSignedMessage(byte[] signedMsgBytes) throws MessageException,
            CertificateException, CryptoException, EncodeFailedException, EncodeNotSupportedException {
       return IEEE1609p2Message.parse(signedMsgBytes);
    }
    
    /**
     * If you are not sure if the message is signed or not, use this method. 
     * The method tries to decode it as a signed message. 
     * If it fails to decode as a valid signed message, it assumes
     * the message is unsigned and it returns the message raw. 
     *  
     * @param signedOrUnsignedMsgBytes
     * @return
     * @throws SecurityManagerException
     */
    public static byte[] getMessagePayload(byte[] signedOrUnsignedMsgBytes) throws SecurityManagerException  {
        byte[] payload = null;
        IEEE1609p2Message signedMsg;
        try {
            signedMsg = decodeSignedMessage(signedOrUnsignedMsgBytes);
            if (signedMsg != null) {
                payload = signedMsg.getPayload();
            }
        } catch (EncodeFailedException | MessageException | EncodeNotSupportedException e1) {
           logger.error("Error parsing 1609.2 message.", e1);
            payload = signedOrUnsignedMsgBytes;
        } catch (CertificateException | CryptoException e2) {
            throw new SecurityManagerException("Security Error", e2);
        }
        
        return payload;
    }
}
