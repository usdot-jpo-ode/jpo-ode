package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long psid;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String generationTime;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String expiryTime;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String certificateStartTime;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String certificateExpiryTime;

  // Only used for messages created through the TIM deposit endpoint
  private ServiceRequest request;

  // otherwise it will deserialize as "certPresent"
  private boolean isCertPresent;

  /**
   * Returns whether the message contains a certificate.
   *
   * <p>The explicit annotation keeps the historical JSON field name when Jackson inspects both
   * fields and JavaBean accessors. Writing the accessor explicitly also avoids Lombok copying
   * {@code @JsonProperty} to both generated accessors when annotation copying is enabled in
   * {@code lombok.config}.
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
