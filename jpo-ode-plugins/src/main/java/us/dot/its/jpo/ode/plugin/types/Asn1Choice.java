package us.dot.its.jpo.ode.plugin.types;

import us.dot.its.jpo.ode.plugin.serialization.SerializationUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public abstract class Asn1Choice implements Asn1Type {

    @JsonIgnore
    final boolean hasExtensionMarker;

    public Asn1Choice(boolean hasExtensionMarker) {
        this.hasExtensionMarker = hasExtensionMarker;
    }

    /**
     * Validates if one and only one option is selected for the choice
     */
    protected boolean isValid() {
        var types = listTypes();
        long numChosen = types.stream().filter(Optional::isPresent).count();
        return numChosen == 1;
    }

    /**
     * Ref ITU-T X.691 (02/2021) Section 23
     * @return the index of the chosen alternative
     */
    protected int chosenIndex() {
        List<Optional<Asn1Type>> types = listTypes();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).isPresent()) return i;
        }
        return -1;
    }

    protected int maxIndex() {
        return listTypes().size() - 1;
    }

    abstract protected List<Optional<Asn1Type>> listTypes();




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
