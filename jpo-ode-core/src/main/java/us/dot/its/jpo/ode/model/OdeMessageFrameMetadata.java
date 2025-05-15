package us.dot.its.jpo.ode.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class OdeMessageFrameMetadata extends OdeLogMetadata {

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