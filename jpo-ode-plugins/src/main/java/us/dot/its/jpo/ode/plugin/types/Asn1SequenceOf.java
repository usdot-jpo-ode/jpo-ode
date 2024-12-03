package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;


public abstract class Asn1SequenceOf<T extends Asn1Type>
    extends ArrayList<T>
    implements Asn1Type {

    final Class<T> itemClass;
    final long sizeLowerBound;
    final long sizeUpperBound;

    public Asn1SequenceOf(Class<T> itemClass, long sizeLowerBound, long sizeUpperBound) {
        this.itemClass = itemClass;
        this.sizeLowerBound = sizeLowerBound;
        this.sizeUpperBound = sizeUpperBound;
    }

    @JsonIgnore
    public Class<T> getItemClass() {
        return itemClass;
    }

    @JsonIgnore
    public long getSizeLowerBound() {
        return sizeLowerBound;
    }

    @JsonIgnore
    public long getSizeUpperBound() {
        return sizeUpperBound;
    }

    @SuppressWarnings("unchecked")
    public boolean add(Asn1Type item) {
        return super.add((T)item);
    }
}
