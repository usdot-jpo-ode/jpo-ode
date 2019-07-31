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

public class J2735RTCMPackage extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private List<String> msgs = new ArrayList<>();
    private J2735RTCMheader rtcmHeader;

    public List<String> getMsgs() {
        return msgs;
    }

    public J2735RTCMPackage setMsgs(List<String> msgs) {
        this.msgs = msgs;
        return this;
    }

    public J2735RTCMheader getRtcmHeader() {
        return rtcmHeader;
    }

    public J2735RTCMPackage setRtcmHeader(J2735RTCMheader rtcmHeader) {
        this.rtcmHeader = rtcmHeader;
        return this;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((msgs == null) ? 0 : msgs.hashCode());
      result = prime * result + ((rtcmHeader == null) ? 0 : rtcmHeader.hashCode());
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
      J2735RTCMPackage other = (J2735RTCMPackage) obj;
      if (msgs == null) {
        if (other.msgs != null)
          return false;
      } else if (!msgs.equals(other.msgs))
        return false;
      if (rtcmHeader == null) {
        if (other.rtcmHeader != null)
          return false;
      } else if (!rtcmHeader.equals(other.rtcmHeader))
        return false;
      return true;
    }

}
