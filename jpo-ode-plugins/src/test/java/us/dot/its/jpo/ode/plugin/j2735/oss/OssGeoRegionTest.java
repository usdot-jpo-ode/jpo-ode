package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class OssGeoRegionTest {

   @Capturing
   J2735GeoRegion capturingJ2735GeoRegion;
   @Capturing
   OssPosition3D capturingOssPosition3D;

   @Injectable
   GeoRegion injectableGeoRegion;
   @Injectable
   J2735GeoRegion injectableJ2735GeoRegion;

   @Mocked
   J2735Position3D mockJ2735Position3D;

   @Mocked
   Position3D mockPosition3D;

   @Test
   public void testGenericGeoRegionReturnsJ2735GeoRegion() {
      new Expectations() {
         {
            OssPosition3D.genericPosition3D((Position3D) any);
            result = mockJ2735Position3D;
         }
      };

      J2735GeoRegion actualRegion = OssGeoRegion.genericGeoRegion(injectableGeoRegion);
      assertEquals("NW corner incorrect", mockJ2735Position3D, actualRegion.getNwCorner());
      assertEquals("SE corner incorrect", mockJ2735Position3D, actualRegion.getSeCorner());
   }

   @Test
   public void testGeoRegionReturnsGeoRegion() {
      new Expectations() {
         {
            OssPosition3D.position3D((J2735Position3D) any);
            result = mockPosition3D;
         }
      };

      GeoRegion actualRegion = OssGeoRegion.geoRegion(injectableJ2735GeoRegion);
      assertEquals("NW corner incorrect", mockPosition3D, actualRegion.getNwCorner());
      assertEquals("SE corner incorrect", mockPosition3D, actualRegion.getSeCorner());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssGeoRegion> constructor = OssGeoRegion.class.getDeclaredConstructor();
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
