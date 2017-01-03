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
