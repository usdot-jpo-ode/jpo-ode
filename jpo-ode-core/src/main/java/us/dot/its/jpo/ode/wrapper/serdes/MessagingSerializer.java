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
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

/**
 * MessagingSerializer is a generic base class implementing the Kafka Serializer interface to
 * provide serialization of objects for use in Kafka messages.
 *
 * <p>This class uses a generic type parameter, allowing it to handle serialization of various types.
 * Internal serialization is performed using an instance of the {@link Kryo} class for efficient object serialization.</p>
 *
 * @param <T> the type of data to be serialized
 */
@Slf4j
public final class MessagingSerializer<T> implements Serializer<T> {

  private final Kryo kryo = new Kryo();

  @Override
  public byte[] serialize(String topic, T data) {
    if (data == null) {
      log.debug("null data passed to serializer for topic {}. Returning empty byte array.", topic);
      return new byte[0];
    }

    Output output = new Output(1024, -1);
    kryo.writeClassAndObject(output, data);
    byte[] bytes = output.toBytes();
    output.close();
    log.debug("Serialized data of type {} to {} bytes.", data.getClass().getName(), bytes.length);
    return bytes;
  }

}
