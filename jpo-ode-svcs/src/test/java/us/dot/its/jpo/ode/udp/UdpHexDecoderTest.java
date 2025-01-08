package us.dot.its.jpo.ode.udp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.DatagramPacket;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

class UdpHexDecoderTest {

  @Test
  void getPayloadHexString_BSM_VerifyNoMissingBytes() throws InvalidPayloadException {
    // prepare
    String hexString = "001480ADDA7CDE5517E962C66947240CB711E804C8B106B7DB7B12B3056B8AA1AA4E838D00400F86822A3CD398D89E1BB8405B72C3C7A398C3CAFF63338526C646F4FFF524AD9E404039D5DA2FA62FEB57E305B552C7BE088B61E52A6BFC8CAF5AF64414F3E4513FEC189F8B5E1138B824A48B29BA1F43CB12CE296BCA3DFA8F651AB44AB1B81B633B797D5645DAA4EDADAB4AC22A0BC38AB361443395BAA2C81CC4538E7413E9C8C3F696BB2C9B6B0000";
    byte[] bytes = HexUtils.fromHexString(hexString);
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
    String expectedOutput = "001480ADDA7CDE5517E962C66947240CB711E804C8B106B7DB7B12B3056B8AA1AA4E838D00400F86822A3CD398D89E1BB8405B72C3C7A398C3CAFF63338526C646F4FFF524AD9E404039D5DA2FA62FEB57E305B552C7BE088B61E52A6BFC8CAF5AF64414F3E4513FEC189F8B5E1138B824A48B29BA1F43CB12CE296BCA3DFA8F651AB44AB1B81B633B797D5645DAA4EDADAB4AC22A0BC38AB361443395BAA2C81CC4538E7413E9C8C3F696BB2C9B6B0000";

    // execute
    OdeAsn1Payload result = UdpHexDecoder.getPayloadHexString(packet, SupportedMessageType.BSM);

    // verify
    String actualOutput = ((OdeHexByteArray) result.getData()).getBytes();
    assertEquals(expectedOutput, actualOutput);
  }
}