package us.dot.its.jpo.ode.security.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import us.dot.its.jpo.ode.util.CodecUtils;

/**
 * Represents the result of a message signing operation provided by a security
 * service. This model encapsulates the signed message and its expiration
 * information.
 *
 * <p>The signing operation is performed by the security service and the result
 * is returned in this structure. The {@code Result} inner class contains specific
 * details about the signed message.</p>
 */
@Data
@NoArgsConstructor
public class SignatureResultModel {
  private String messageSigned;
  // messageExpiry is in seconds
  private Long messageExpiry;

  public String getHexEncodedMessageSigned() {
    return CodecUtils.toHex(
        CodecUtils.fromBase64(messageSigned));
  }
}
