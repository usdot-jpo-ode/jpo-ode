package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.NoArgsConstructor;

/**
 * Represents the data of a message frame.
 */
@NoArgsConstructor
public class OdeMessageFrameData extends OdeData<OdeMessageFrameMetadata, OdeMessageFramePayload> {

  public OdeMessageFrameData(OdeMessageFrameMetadata metadata, OdeMessageFramePayload payload) {
    super(metadata, payload);
  }

  @Override
  @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY,
      defaultImpl = OdeMessageFrameMetadata.class)
  public void setMetadata(OdeMessageFrameMetadata metadata) {
    super.setMetadata(metadata);
  }

  @Override
  @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY,
      defaultImpl = OdeMessageFramePayload.class)
  public void setPayload(OdeMessageFramePayload payload) {
    super.setPayload(payload);
  }
}
