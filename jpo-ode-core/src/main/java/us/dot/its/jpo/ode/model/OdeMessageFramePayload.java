package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;


public class OdeMessageFramePayload extends OdeMsgPayload<MessageFrame<?>>{

  @JsonCreator
  public OdeMessageFramePayload(@JsonProperty("data") MessageFrame<?> payload) {
    super(payload);
  }
}