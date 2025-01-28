package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

/**
 * Base class for an ASN.1 sequence-of.
 */
public abstract class Asn1SequenceOf<T extends Asn1Type>
    extends ArrayList<T>
    implements Asn1Type {

  final Class<T> itemClass;
  final long sizeLowerBound;
  final long sizeUpperBound;

  /**
   * Creates a Asn1SequenceOf with the specified generic object type along with 
   * an upper and lower number of items in the sequence-of array.
   *
   * @param itemClass The type of object the SequenceOf consists of.
   * @param sizeLowerBound The lowest allowed number of items inside the Asn1SequenceOf array.
   * @param sizeUpperBound The highest allowed number of items inside the Asn1SequenceOf array.
   */
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
    return super.add((T) item);
  }
}
