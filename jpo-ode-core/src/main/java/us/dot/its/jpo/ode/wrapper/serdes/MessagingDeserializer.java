/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.wrapper.serdes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * MessagingDeserializer is a generic base class implementing the Kafka Deserializer interface to
 * provide deserialization of objects for use in Kafka messages.
 *
 * <p>This class uses a generic type parameter, allowing it to handle deserialization
 * of various types. Internal deserialization is performed using an instance of the
 * {@link Kryo} class for efficient object deserialization.</p>
 *
 * @param <T> the type of data to be deserialized
 */
@Slf4j
public final class MessagingDeserializer<T> implements Deserializer<T> {

  private final Kryo kryo = new Kryo();

  @Override
  public T deserialize(String topic, byte[] data) {
    if (data == null || data.length == 0) {
      log.debug("Null or empty data passed to deserializer for topic {}. Returning null.", topic);
      return null;
    }

    Input input = new Input(data);
    // We are certain that the return value can be cast to the generic type T, so we can safely suppress the unchecked warning
    @SuppressWarnings("unchecked")
    T object = (T) kryo.readClassAndObject(input);
    input.close();

    log.debug("Deserialized data of type {} from {} bytes.", object.getClass().getName(), data.length);
    return object;
  }
}
