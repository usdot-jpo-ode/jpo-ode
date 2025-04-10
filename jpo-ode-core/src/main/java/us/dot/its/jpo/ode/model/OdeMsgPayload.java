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
package us.dot.its.jpo.ode.model;

import lombok.Data;
import us.dot.its.jpo.ode.util.JsonUtils;

@Data
public class OdeMsgPayload<T> {
    public static final String PAYLOAD_STRING = "payload";
    public static final String DATA_STRING = "data";

    private String dataType;
    private T data;
    
    public OdeMsgPayload() {
       super();
    }

    public OdeMsgPayload(T data) {
        super();
        this.dataType = data.getClass().getName();
        this.data = data;
    }

    public OdeMsgPayload(String dataType, T data) {
        super();
        this.dataType = dataType;
        this.data = data;
    }

    public String toJson() {
        return JsonUtils.toJson(this, false);
     }
}
