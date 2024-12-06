package us.dot.its.jpo.ode.plugin.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

/**
 * A constrained integer type with lower bound and upper bound.
 * Unconstrained integer types and extensibility markers in integer constraints
 * are not supported.
 */
public class Asn1Integer implements Asn1Type, Comparable<Asn1Integer> {

  protected long value;

  @JsonIgnore
  final long lowerBound;

  @JsonIgnore
  final long upperBound;

  public Asn1Integer(long lowerBound, long upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @JsonValue
  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public int intValue() {
    return (int) value;
  }

  @Override
  public int compareTo(Asn1Integer other) {
    if (other == null) {
      return -1;
    }
    return Long.compare(value, other.value);
  }

  public long getLowerBound() {
    return lowerBound;
  }

  public long getUpperBound() {
    return upperBound;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Asn1Integer that = (Asn1Integer) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
