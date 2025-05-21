package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Represents the data of a message frame.
 */
public class OdeMessageFrameData extends OdeData<OdeMsgMetadata, OdeMessageFramePayload> {

  public OdeMessageFrameData(OdeMsgMetadata metadata, OdeMessageFramePayload payload) {
    super(metadata, payload);
  }

  @Override
  @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY)
  public void setMetadata(OdeMsgMetadata metadata) {
    super.setMetadata(metadata);
  }

  @Override
  @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY)
  public void setPayload(OdeMessageFramePayload payload) {
    super.setPayload(payload);
  }
}
