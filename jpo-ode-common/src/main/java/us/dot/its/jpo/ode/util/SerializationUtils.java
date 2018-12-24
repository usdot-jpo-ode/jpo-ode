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
package us.dot.its.jpo.ode.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerializationUtils<T> {
   private Kryo kryo = new Kryo();
   
   public byte[] serialize(T object) {
      if (object == null) {
         return null;
      }
      
      Output output = new Output(1024, -1);
      kryo.writeClassAndObject(output, object);
      byte[] bytes = output.getBuffer();
      output.close();
      return bytes;
   }

   public T deserialize(byte[] buffer) {
      Input input = new Input(buffer);
      @SuppressWarnings("unchecked")
      T object = (T) kryo.readClassAndObject(input);
      input.close();
      return object;
   }

}
