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

import org.apache.kafka.common.serialization.Serializer;
import us.dot.its.jpo.ode.util.SerializationUtils;

/**
 * MessagingSerializer is a generic base class implementing the Kafka Serializer interface to
 * provide serialization of objects for use in Kafka messages.
 *
 * <p>This class uses a generic type parameter, allowing it to handle serialization of various types.
 * Internal serialization is performed using an instance of the SerializationUtils class, which
 * leverages Kryo for efficient object serialization.</p>
 *
 * <p>The class is declared as sealed, restricting which other classes can directly extend it. The
 * class is declared as sealed, restricting which other classes can directly extend it. It will soon
 * be marked as final to prevent incorrect usage through unnecessary subtyping</p>
 *
 * @param <T> the type of data to be serialized
 */
public sealed class MessagingSerializer<T> implements Serializer<T>
    permits OdeBsmSerializer {

  SerializationUtils<T> serializer = new SerializationUtils<>();

  @Override
  public byte[] serialize(String topic, T data) {
    return serializer.serialize(data);
  }

}
