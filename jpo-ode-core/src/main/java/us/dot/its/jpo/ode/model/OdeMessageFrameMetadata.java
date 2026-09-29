package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import us.dot.its.jpo.ode.plugin.ServiceRequest;

/**
 * Represents the metadata of a message frame.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OdeMessageFrameMetadata extends OdeLogMetadata {

  /**
   * Enum representing the source of a message frame.
   */
  public enum Source {
    RSU, V2X, MMITSS, EV, RV, SAT, SNMP, NA, UNKNOWN
  }

  private Source source;
  private String originIp;

  // Only used for messages created through the TIM deposit endpoint
  @JsonProperty("request")
  private ServiceRequest request;

  // otherwise it will deserialize as "certPresent"
  @JsonProperty("isCertPresent")
  private boolean isCertPresent;

  /**
   * Returns whether the message contains a certificate.
   *
   * <p>The explicit annotation keeps the historical JSON field name when Jackson inspects both
   * fields and JavaBean accessors.
   *
   * @return true when a certificate is present
   */
  @JsonProperty("isCertPresent")
  public boolean isCertPresent() {
    return isCertPresent;
  }

  /**
   * Sets whether the message contains a certificate.
   *
   * @param certPresent whether a certificate is present
   */
  @JsonProperty("isCertPresent")
  public void setCertPresent(boolean certPresent) {
    isCertPresent = certPresent;
  }

  public OdeMessageFrameMetadata(OdeMsgPayload<?> payload) {
    super(payload);
  }

  /**
   * Same as {@link #OdeMessageFrameMetadata(OdeMsgPayload)} but sets {@code asn1} to the given hex
   * string (for example the full UDP datagram payload before 1609.3 header stripping).
   */
  public OdeMessageFrameMetadata(OdeMsgPayload<?> payload, String asn1Hex) {
    super(payload, asn1Hex);
  }

  public OdeMessageFrameMetadata(Source source) {
    this.source = source;
  }
}
