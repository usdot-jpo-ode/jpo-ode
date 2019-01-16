package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;

public class BitStringBuilder {

   private BitStringBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735BitString genericBitString(JsonNode jsonNode, Enum<?>[] enumValues) {

      J2735BitString bitString = new J2735BitString();

      char[] chars = jsonNode.asText().trim().toCharArray();

      for (char i = 0; i < chars.length; i++) {
         String bitName = enumValues[i].name();
         Boolean bitStatus = (chars[i] == '1');
         bitString.put(bitName, bitStatus);
      }

      return bitString;
   }

}