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
package us.dot.its.jpo.ode.plugin;

import java.util.Arrays;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

public class ServiceRequest extends OdeObject {

   public static class OdeInternal extends OdeObject {
     
    private static final long serialVersionUID = 1L;

    public enum RequestVerb {
       POST, PUT, DELETE, GET
     }

     public static final int LATEST_VERSION = 3;

     private int version = LATEST_VERSION;
     private RequestVerb verb;

    public OdeInternal() {
      super();
      setVersion(LATEST_VERSION);
    }

    public OdeInternal(RequestVerb verb) {
       this();
       this.verb = verb;
     }

    public OdeInternal(int version, RequestVerb verb) {
      super();
      this.version = version;
      this.verb = verb;
    }

    public int getVersion() {
        return version;
     }

     public OdeInternal setVersion(int version) {
        this.version = version;
        return this;
     }

    public RequestVerb getVerb() {
      return verb;
    }

    public OdeInternal setVerb(RequestVerb verb) {
      this.verb = verb;
      return this;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((verb == null) ? 0 : verb.hashCode());
      result = prime * result + version;
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
      OdeInternal other = (OdeInternal) obj;
      if (verb != other.verb)
        return false;
      if (version != other.version)
        return false;
      return true;
    }

  }

  private static final long serialVersionUID = 1L;
   private OdeInternal ode;
   private SituationDataWarehouse.SDW sdw;
   private RSU[] rsus;
   private SNMP snmp;

   public RSU[] getRsus() {
      return rsus;
   }

   public void setRsus(RSU[] rsus) {
      this.rsus = rsus;
   }

   public SNMP getSnmp() {
      return snmp;
   }

   public void setSnmp(SNMP snmp) {
      this.snmp = snmp;
   }

   public OdeInternal getOde() {
     return ode;
   }

   public void setOde(OdeInternal ode) {
     this.ode = ode;
   }

   public SituationDataWarehouse.SDW getSdw() {
     return sdw;
   }

   public void setSdw(SituationDataWarehouse.SDW sdw) {
     this.sdw = sdw;
   }

  @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((ode == null) ? 0 : ode.hashCode());
      result = prime * result + Arrays.hashCode(rsus);
      result = prime * result + ((sdw == null) ? 0 : sdw.hashCode());
      result = prime * result + ((snmp == null) ? 0 : snmp.hashCode());
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
      ServiceRequest other = (ServiceRequest) obj;
      if (ode == null) {
         if (other.ode != null)
            return false;
      } else if (!ode.equals(other.ode))
         return false;
      if (!Arrays.equals(rsus, other.rsus))
         return false;
      if (sdw == null) {
         if (other.sdw != null)
            return false;
      } else if (!sdw.equals(other.sdw))
         return false;
      if (snmp == null) {
         if (other.snmp != null)
            return false;
      } else if (!snmp.equals(other.snmp))
         return false;
      return true;
   }

}
