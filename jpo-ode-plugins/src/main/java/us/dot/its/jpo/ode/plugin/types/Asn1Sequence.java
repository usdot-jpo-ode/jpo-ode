package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import us.dot.its.jpo.ode.plugin.serialization.SerializationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Asn1Sequence implements Asn1Type {

    @JsonIgnore
    final boolean extensionMarker;

    public Asn1Sequence(boolean hasExtensionMarker) {
        this.extensionMarker = hasExtensionMarker;
    }

    @JsonIgnore
    public boolean hasExtensionMarker() {
        return extensionMarker;
    }



    @Override
    public String toString() {
        ObjectMapper mapper = SerializationUtil.jsonMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }
}
