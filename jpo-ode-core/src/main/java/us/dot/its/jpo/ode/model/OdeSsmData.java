package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

public class OdeSsmData extends OdeData {

    private static final long serialVersionUID = 2057222204896561615L;

    public OdeSsmData() {
        super();
    }

    public OdeSsmData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
        super(metadata, payload);
    }

    @Override
    @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, defaultImpl = OdeSsmMetadata.class)
    public void setMetadata(OdeMsgMetadata metadata) {
        super.setMetadata(metadata);
    }

    @Override
    @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, defaultImpl = OdeSsmPayload.class)
    public void setPayload(OdeMsgPayload payload) {
        super.setPayload(payload);
    }
}
