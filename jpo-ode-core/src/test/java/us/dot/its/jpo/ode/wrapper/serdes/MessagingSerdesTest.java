package us.dot.its.jpo.ode.wrapper.serdes;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.esotericsoftware.kryo.KryoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MessagingSerdesTest {

  @Test
  void serdes() {
    TestData td = new TestData(1, "string", BigDecimal.valueOf(2.3));

    try (var serializer = new MessagingSerializer<TestData>(); var deserializer = new MessagingDeserializer<TestData>()) {
      byte[] actual = serializer.serialize("test", td);
      Assertions.assertEquals(td, deserializer.deserialize("test", actual));
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  void deserializeNull() {
    try (var deserializer = new MessagingDeserializer<TestData>()) {
      Assertions.assertNull(deserializer.deserialize("test", null));
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  void serializeNull() {
    try (var serializer = new MessagingSerializer<TestData>()) {
      Assertions.assertArrayEquals(new byte[0], serializer.serialize("test", null));
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  void failWhenNoPublicConstructor() {
    try (var serializer = new MessagingDeserializer<NoPublicConstructor>()) {
      var bytes = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
      assertThrows(KryoException.class, () -> serializer.deserialize("test", bytes));
    }
  }

  public static class NoPublicConstructor {

    private NoPublicConstructor() {
    }
  }
}