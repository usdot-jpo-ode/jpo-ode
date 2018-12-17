package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.model.OdeObject;

public class DataFrames extends OdeObject {

  private static final long serialVersionUID = 1L;

  @JsonProperty("TravelerDataFrame")
  private TravelerDataFrame[] TravelerDataFrame;

  public TravelerDataFrame[] getTravelerDataFrame() {
    return TravelerDataFrame;
  }

  public void setTravelerDataFrame(TravelerDataFrame[] travelerDataFrame) {
    TravelerDataFrame = travelerDataFrame;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(TravelerDataFrame);
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
    DataFrames other = (DataFrames) obj;
    if (!Arrays.equals(TravelerDataFrame, other.TravelerDataFrame))
      return false;
    return true;
  }

}
