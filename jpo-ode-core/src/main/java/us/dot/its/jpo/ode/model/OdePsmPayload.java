package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;

import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessage;

public class OdePsmPayload extends OdeMsgPayload<PersonalSafetyMessage> {

	private static final long serialVersionUID = 1L;

	public OdePsmPayload() {
	  this(new PersonalSafetyMessage());
	}
  
	@JsonCreator
	public OdePsmPayload(@JsonProperty("data") PersonalSafetyMessage psm) {
	  super(psm);
	  this.setData(psm);
	}
}
