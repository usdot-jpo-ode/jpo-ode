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
package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SpeedProfile extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private List<Integer> speedReports = new ArrayList<>();

    public List<Integer> getSpeedReports() {
        return speedReports;
    }

    public J2735SpeedProfile setSpeedReports(List<Integer> speedReports) {
        this.speedReports = speedReports;
        return this;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((speedReports == null) ? 0 : speedReports.hashCode());
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
      J2735SpeedProfile other = (J2735SpeedProfile) obj;
      if (speedReports == null) {
        if (other.speedReports != null)
          return false;
      } else if (!speedReports.equals(other.speedReports))
        return false;
      return true;
    }

}
