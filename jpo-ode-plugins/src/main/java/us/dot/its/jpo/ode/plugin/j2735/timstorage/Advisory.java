package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Advisory extends Asn1Object {
  private static final long serialVersionUID = 1L;
  @JsonProperty("SEQUENCE")
  private Items[] SEQUENCE;
  public Items[] getSEQUENCE() {
    return SEQUENCE;
  }
  public void setSEQUENCE(Items[] sEQUENCE) {
    SEQUENCE = sEQUENCE;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(SEQUENCE);
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Advisory other = (Advisory) obj;
    if (!Arrays.equals(SEQUENCE, other.SEQUENCE))
      return false;
    return true;
  }
}
