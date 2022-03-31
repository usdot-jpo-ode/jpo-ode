package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitStringBuilder {

   private static final Logger logger = LoggerFactory.getLogger(BitStringBuilder.class);

   private BitStringBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735BitString genericBitString(JsonNode jsonNode, Enum<?>[] enumValues) {

      J2735BitString bitString = new J2735BitString();

      char[] chars = jsonNode.asText().trim().toCharArray();

      for (char i = 0; i < chars.length; i++) {
         if (i >= enumValues.length) {
            logger.debug(
                  "Invalid genericBitString. Provided bit string is longer than known enum values. Enum attempted: {}",
                  enumValues.getClass().getName());
            break;
         }
         String bitName = enumValues[i].name();
         Boolean bitStatus = (chars[i] == '1');
         bitString.put(bitName, bitStatus);
      }

      return bitString;
   }

}