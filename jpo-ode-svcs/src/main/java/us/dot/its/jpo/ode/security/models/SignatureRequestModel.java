package us.dot.its.jpo.ode.security.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request model for a message signing operation in a security service.
 * This model is used to encapsulate the message to be signed and an optional
 * validity period override for the signed message.
 *
 * <p>The {@code message} field is the text message that needs to be signed by a
 * security service. The {@code sigValidityOverride} field provides an optional
 * override for the signature validity period, expressed in seconds since epoch.</p>
 */
@Data
@NoArgsConstructor
public class SignatureRequestModel {
  private String message;
  private Integer sigValidityOverride;
}
