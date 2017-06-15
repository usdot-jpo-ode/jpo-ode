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

    public class SecurityManagerException extends Exception {

        private static final long serialVersionUID = 1L;

        public SecurityManagerException(String msg, Exception e2) {
            super (msg, e2);
        }

    }

    /**
     * Returns true if the the signed message has valid generation time.
     * @param signedMsg
     * @return
     */
    public boolean isValid(IEEE1609p2Message signedMsg) {
        boolean valid = false;
        if (signedMsg != null) {
            // decode and validate message
            try {
                // check that generation time is not in the future
                // if we change generation time to expiration time as per standard then we reverse the check
                long generationTime = signedMsg.getGenerationTime().getTime();
                valid = generationTime < new Date().getTime();
            } catch (Exception e ) {
                logger.error("Error parsing 1609.2 message.", e);
            }
        }        
        return valid;
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
    public IEEE1609p2Message getSignedMessage(byte[] signedMsgBytes) throws MessageException,
            CertificateException, CryptoException, EncodeFailedException, EncodeNotSupportedException {
        IEEE1609p2Message signedMsg = IEEE1609p2Message.parse(signedMsgBytes);
        return signedMsg;
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
    public byte[] getMessagePayload(byte[] signedOrUnsignedMsgBytes) throws SecurityManagerException  {
        byte[] payload = null;
        IEEE1609p2Message signedMsg;
        try {
            signedMsg = getSignedMessage(signedOrUnsignedMsgBytes);
            if (isValid(signedMsg)) {
                payload = signedMsg.getPayload();
            }
        } catch (EncodeFailedException | MessageException | EncodeNotSupportedException e1) {
            payload = signedOrUnsignedMsgBytes;
        } catch (CertificateException | CryptoException e2) {
            throw new SecurityManagerException("Security Error", e2);
        }
        
        return payload;
    }
}
