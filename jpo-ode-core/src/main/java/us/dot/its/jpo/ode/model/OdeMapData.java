package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

public class OdeMapData extends OdeData {

	private static final long serialVersionUID = 4944935387116447760L;

	public OdeMapData() {
		super();
	}

	public OdeMapData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
		super(metadata, payload);
	}

	@Override
	@JsonTypeInfo(use = Id.CLASS, defaultImpl = OdeMapMetadata.class)
	public void setMetadata(OdeMsgMetadata metadata) {
		super.setMetadata(metadata);
	}

	@Override
	@JsonTypeInfo(use = Id.CLASS, defaultImpl = OdeMapPayload.class)
	public void setPayload(OdeMsgPayload payload) {
		super.setPayload(payload);
	}
}
