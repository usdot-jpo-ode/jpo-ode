package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmCoreDataBuilderTest {

   @Capturing
   AccelerationSet4WayBuilder capturingAccelerationSet4WayBuilder;
   @Capturing
   PositionalAccuracyBuilder capturingPositionalAccuracyBuilder;
   @Capturing
   SpeedOrVelocityBuilder capturingSpeedOrVelocityBuilder;
   @Capturing
   BrakeSystemStatusBuilder capturingBrakeSystemStatusBuilder;
   @Capturing
   VehicleSizeBuilder capturingVehicleSizeBuilder;

   @Test
   public void testRequiredElements() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 4567);

      testInput.put("lat", 40741895);
      testInput.put("long", -73989308);
      testInput.put("elev", 3456);

      assertNotNull(BsmCoreDataBuilder.genericBsmCoreData(testInput));
   }

   @Test
   public void testFlagValues() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 65535);

      testInput.put("lat", 50741895);
      testInput.put("long", -63989308);
      testInput.put("elev", 3456);

      testInput.put("angle", 0x7F);
      testInput.put("transmission", 7); // unavailable flag

      assertNotNull(BsmCoreDataBuilder.genericBsmCoreData(testInput));
   }

   @Test
   public void testOptionalElements() {

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("msgCnt", 88);
      testInput.put("id", "A0F1");
      testInput.put("secMark", 65535);
      testInput.put("heading", 21000);
      testInput.put("angle", 55);

      testInput.put("lat", 50741895);
      testInput.put("long", -63989308);
      testInput.put("elev", 3456);

      testInput.put("transmission", 5); // unavailable flag

      assertNotNull(BsmCoreDataBuilder.genericBsmCoreData(testInput));
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BsmCoreDataBuilder> constructor = BsmCoreDataBuilder.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
         assertEquals(InvocationTargetException.class, e.getClass());
      }
   }

}
