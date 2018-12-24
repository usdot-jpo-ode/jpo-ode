/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

 // TODO open-ode
//    /**
//     * Returns true if the the signed message has valid generation time.
//     * @param signedMsg
//     * @return
//     * @throws SecurityManagerException 
//     */
//    public static void validateGenerationTime(IEEE1609p2Message signedMsg) 
//            throws SecurityManagerException {
//        // decode and validate message
//        // check that generation time is not in the future
//        // if we change generation time to expiration time as per standard then we reverse the check
//        long generationTime = signedMsg.getGenerationTime().getTime();
//        logger.debug("Message generation time: {}", signedMsg.getGenerationTime());
//        long now = new Date().getTime();
//        if (generationTime >= now) {
//            throw new SecurityManagerException("Generation time ("
//                    + generationTime
//                    + ") is in the future (now = "
//                    + now 
//                    + ")");
//        }
//    }
//
//    /**
//     * This method returns a message if it is signed.
//     * 
//     * @param signedMsgBytes
//     * @return
//     * @throws MessageException
//     * @throws CertificateException
//     * @throws CryptoException
//     * @throws EncodeFailedException
//     * @throws EncodeNotSupportedException
//     */
//    public static IEEE1609p2Message decodeSignedMessage(byte[] signedMsgBytes) throws MessageException,
//            CertificateException, CryptoException, EncodeFailedException, EncodeNotSupportedException {
//       return IEEE1609p2Message.parse(signedMsgBytes);
//    }
//    
//    /**
//     * If you are not sure if the message is signed or not, use this method. 
//     * The method tries to decode it as a signed message. 
//     * If it fails to decode as a valid signed message, it assumes
//     * the message is unsigned and it returns the message raw. 
//     *  
//     * @param signedOrUnsignedMsgBytes
//     * @return
//     * @throws SecurityManagerException
//     */
//    public static byte[] getMessagePayload(byte[] signedOrUnsignedMsgBytes) throws SecurityManagerException  {
//        byte[] payload = null;
//        IEEE1609p2Message signedMsg;
//        try {
//            signedMsg = decodeSignedMessage(signedOrUnsignedMsgBytes);
//            if (signedMsg != null) {
//                payload = signedMsg.getPayload();
//            }
//        } catch (EncodeFailedException | MessageException | EncodeNotSupportedException e1) {
//           logger.error("Error parsing 1609.2 message.", e1);
//            payload = signedOrUnsignedMsgBytes;
//        } catch (CertificateException | CryptoException e2) {
//            throw new SecurityManagerException("Security Error", e2);
//        }
//        
//        return payload;
//    }
}
