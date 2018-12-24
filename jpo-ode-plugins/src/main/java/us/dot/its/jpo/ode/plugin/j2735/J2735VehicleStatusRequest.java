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

import us.dot.its.jpo.ode.model.OdeObject;

public class J2735VehicleStatusRequest extends OdeObject {

    private static final long serialVersionUID = 2137805895544104045L;
    
    private int tag;
    private int subTag;
    private int lessThenValue;
    private int moreThenValue;
    private int sendAll;
    private int status;

    public J2735VehicleStatusRequest() {
        // empty constructor
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getSubTag() {
        return subTag;
    }

    public void setSubTag(int subTag) {
        this.subTag = subTag;
    }

    public int getLessThenValue() {
        return lessThenValue;
    }

    public void setLessThenValue(int lessThenValue) {
        this.lessThenValue = lessThenValue;
    }

    public int getMoreThenValue() {
        return moreThenValue;
    }

    public void setMoreThenValue(int moreThenValue) {
        this.moreThenValue = moreThenValue;
    }

    public int getSendAll() {
        return sendAll;
    }

    public void setSendAll(int sendAll) {
        this.sendAll = sendAll;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
