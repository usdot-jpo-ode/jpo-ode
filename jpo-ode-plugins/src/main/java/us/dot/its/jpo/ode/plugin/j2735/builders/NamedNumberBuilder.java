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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NamedNumber;

public class NamedNumberBuilder {

    private NamedNumberBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735NamedNumber genericNamedNumber(String name) {
       J2735NamedNumber gnn = new J2735NamedNumber();

       gnn.setName(name);
       // value not needed for ASN1c encoder
       gnn.setValue(null);
       return gnn;
   }

    public static J2735NamedNumber genericGenericLocations(JsonNode genericLocations) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(genericLocations.fieldNames().next());
        // value not needed for ASN1c encoder
        gnn.setValue(null);
        return gnn;
    }

    public static J2735NamedNumber genericIncidentResponseEquipment(JsonNode incidentResponseEquipment) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(incidentResponseEquipment.fieldNames().next());
        // value not needed for ASN1c encoder
        gnn.setValue(null);
        return gnn;
    }

    public static J2735NamedNumber genericVehicleGroupAffected(JsonNode vehicleGroupAffected) {
        J2735NamedNumber gnn = new J2735NamedNumber();

        gnn.setName(vehicleGroupAffected.fieldNames().next());
        // value not needed for ASN1c encoder
        gnn.setValue(null);
        return gnn;
    }

}
