package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;

/**
 * Represents the payload of a message frame.
 */
public class OdeMessageFramePayload extends OdeMsgPayload<MessageFrame<?>> {

  /**
   * Constructs an OdeMessageFramePayload with the given payload.
   *
   * @param payload The payload of the message frame
   */
  @JsonCreator
  public OdeMessageFramePayload(@JsonProperty("data") MessageFrame<?> payload) {
    super(payload);
  }
}
