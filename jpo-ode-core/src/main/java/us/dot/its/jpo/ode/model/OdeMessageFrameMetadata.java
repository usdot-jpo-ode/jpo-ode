package us.dot.its.jpo.ode.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents the metadata of a message frame.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OdeMessageFrameMetadata extends OdeLogMetadata {

  /**
   * Enum representing the source of a message frame.
   */
  public enum Source {
    RSU, V2X, MMITSS, EV, RV, unknown
  }

  private Source source;
  private String originIp;
  private boolean isCertPresent;

  public OdeMessageFrameMetadata() {}

  public OdeMessageFrameMetadata(OdeMsgPayload<?> payload) {
    super(payload);
  }

  public OdeMessageFrameMetadata(Source source) {
    this.source = source;
  }
}
