package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Regions extends Asn1Object {
  private static final long serialVersionUID = 1L;

  @JsonProperty("GeographicalPath")
  private GeographicalPath[] GeographicalPath;

  public GeographicalPath[] getGeographicalPath() {
    return GeographicalPath;
  }

  public void setGeographicalPath(GeographicalPath[] geographicalPath) {
    GeographicalPath = geographicalPath;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(GeographicalPath);
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
    Regions other = (Regions) obj;
    if (!Arrays.equals(GeographicalPath, other.GeographicalPath))
      return false;
    return true;
  }

}