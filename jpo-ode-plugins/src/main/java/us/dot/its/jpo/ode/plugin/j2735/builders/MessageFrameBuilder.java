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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;

public class MessageFrameBuilder {

    private MessageFrameBuilder() {
       throw new UnsupportedOperationException();
    }

    // Custom exception
    public static class MessageFrameBuilderException extends Exception {

        private static final long serialVersionUID = -952945144250120705L;

        public MessageFrameBuilderException(String msg) {
            super(msg);
        }

        public MessageFrameBuilderException(String msg, Exception e) {
            super(msg, e);
        }

    }

    // Convert a message frame
    public static J2735MessageFrame genericMessageFrame(JsonNode mf) throws BsmPart2ContentBuilderException, MessageFrameBuilderException {

        J2735MessageFrame genericMessageFrame = new J2735MessageFrame();
        
        genericMessageFrame.setMessageId( J2735DSRCmsgID.valueOf( mf.get("messageId").asInt() ));

//        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

        if (genericMessageFrame.getMessageId() == J2735DSRCmsgID.BasicSafetyMessage) {

            // If basicSafetyMessage
//            BasicSafetyMessage bsm;
//            if (mf.value.getDecodedValue() != null) {
//                bsm = (BasicSafetyMessage) mf.value.getDecodedValue();
//            } else if (mf.value.getEncodedValueAsStream() != null) {
//                bsm = new BasicSafetyMessage();
//                try {
//                    coder.decode(mf.value.getEncodedValueAsStream(), bsm);
//                } catch (DecodeFailedException | DecodeNotSupportedException e) {
//                    throw new OssMessageFrameException("Error decoding OpenType value", e);
//                }
//            } else {
//                throw new OssMessageFrameException("No OpenType value");
//            }

            genericMessageFrame.setValue(BsmBuilder.genericBsm(mf.get("value")));

        } else {
            throw new MessageFrameBuilderException("Unknown message type: " + genericMessageFrame.getMessageId());
        }

        return genericMessageFrame;

    }

}
