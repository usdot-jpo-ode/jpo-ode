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
package us.dot.its.jpo.ode.wrapper.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import us.dot.its.jpo.ode.util.SerializationUtils;

public class MessagingSerializer<T> implements Serializer<T> {

    SerializationUtils<T> serializer = new SerializationUtils<T>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return serializer.serialize(data);
    }

    @Override
    public void close() {
        // nothing to do
    }

}
