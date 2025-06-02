package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import us.dot.its.jpo.ode.plugin.j2735.J2735SSM;

/**
 * Represents the payload of an SSM message.
 */
public class OdeSsmPayload extends OdeMsgPayload<OdeObject> {

  public OdeSsmPayload() {
    this(new J2735SSM());
  }

  @JsonCreator
  public OdeSsmPayload(@JsonProperty("data") J2735SSM ssm) {
    super(ssm);
    this.setData(ssm);
  }

  @JsonProperty("data")
  public J2735SSM getSsm() {
    return (J2735SSM) getData();
  }

  public void setSsm(J2735SSM ssm) {
    setData(ssm);
  }
}
