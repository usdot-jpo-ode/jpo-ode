package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;
import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessageMessageFrame;

public class OdePsmPayload extends OdeMessageFramePayload {

	private static final long serialVersionUID = 1L;

	public OdePsmPayload() {
		this(new PersonalSafetyMessageMessageFrame());
	}
  
	@JsonCreator
	public OdePsmPayload(@JsonProperty("data") PersonalSafetyMessageMessageFrame psm) {
		super();
		this.setData(psm);
	}
}
