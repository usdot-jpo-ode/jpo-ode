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
package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.util.CodecUtils;

public class PayloadParser extends LogFileParser {

   private static Logger logger = LoggerFactory.getLogger(PayloadParser.class);
   private static HashMap<String, String> msgStartFlags = new HashMap<String, String>();

   private static final int HEADER_SIZE = 20; 
   public static final int PAYLOAD_LENGTH_LENGTH = 2;
   
   protected short payloadLength;
   protected byte[] payload;
   protected String payloadType;

   public PayloadParser() {
      super();
      msgStartFlags.put("BSM", "0014");
      msgStartFlags.put("TIM", "001f");
      msgStartFlags.put("MAP", "0012");
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      ParserStatus status = ParserStatus.INIT;
      try {
         // parse payload length
         if (getStep() == 0) {
            status = parseStep(bis, PAYLOAD_LENGTH_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            short length = CodecUtils.bytesToShort(readBuffer, 0, PAYLOAD_LENGTH_LENGTH, ByteOrder.LITTLE_ENDIAN);
            logger.debug("Payload length is: " + length);
            setPayloadLength(length);
         }

         // Step 10 - copy payload bytes
         if (getStep() == 1) {
            status = parseStep(bis, getPayloadLength());
            if (status != ParserStatus.COMPLETE)
               return status;
            setPayload(removeHeader(Arrays.copyOf(readBuffer, getPayloadLength())));
         }
         
         resetStep();
         status = ParserStatus.COMPLETE;

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      return status;

   }
   
   public short getPayloadLength() {
      return payloadLength;
   }

   public LogFileParser setPayloadLength(short length) {
      this.payloadLength = length;
      return this;
   }

   public byte[] getPayload() {
      return payload;
   }

   public LogFileParser setPayload(byte[] payload) {
      this.payload = payload;
      return this;
   }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write(CodecUtils.shortToBytes(payloadLength, ByteOrder.LITTLE_ENDIAN));
    os.write(payload, 0, payloadLength);
  }



   public byte[] removeHeader(byte[] packet) {
      String hexPacket = HexUtils.toHexString(packet);
      String hexPacketParsed = "";
      for (String key : msgStartFlags.keySet()) {
         String startFlag = msgStartFlags.get(key);
         int startIndex = hexPacket.toLowerCase().indexOf(startFlag);
         if (hexPacketParsed == ""){
            logger.debug("Start index for: " + key + " is: " + startIndex);
            if (startIndex == -1) {
               logger.debug("Message does not have header for: " + key);
               break;
            } else if (startIndex <= HEADER_SIZE) {
               logger.debug("Message has supported header. startIndex: " + startIndex + " msgFlag: " + startFlag);
               hexPacketParsed = hexPacket;
            // Using a value of 35 as the largest index from preliminary testing data.
            } else if (startIndex > HEADER_SIZE && startIndex < 35) {
               int trueStartIndex = HEADER_SIZE
                     + hexPacket.substring(HEADER_SIZE, hexPacket.length()).indexOf(startFlag);
               logger.debug("Found payload start at: " + trueStartIndex);
               hexPacketParsed = hexPacket.substring(trueStartIndex, hexPacket.length());
            }
         }
      }
      if (hexPacketParsed == ""){
         hexPacketParsed = hexPacket;
         logger.debug("Could not identify a Header in the following packet: " + hexPacketParsed);
      } else {
         logger.debug("Payload hex: " + hexPacketParsed);
      }
      return HexUtils.fromHexString(hexPacketParsed);
   }
}
