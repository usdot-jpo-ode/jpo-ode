/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
