package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

public class OdeSrmData extends OdeData {

    private static final long serialVersionUID = 4944933827116447760L;

    public OdeSrmData() {
        super();
    }

    public OdeSrmData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
        super(metadata, payload);
    }

    @Override
    @JsonTypeInfo(use = Id.CLASS, defaultImpl = OdeSrmMetadata.class)
    public void setMetadata(OdeMsgMetadata metadata) {
        super.setMetadata(metadata);
    }

    @Override
    @JsonTypeInfo(use = Id.CLASS, defaultImpl = OdeSrmPayload.class)
    public void setPayload(OdeMsgPayload payload) {
        super.setPayload(payload);
    }

}
