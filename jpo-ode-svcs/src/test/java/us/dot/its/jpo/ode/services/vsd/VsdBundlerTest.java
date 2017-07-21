package us.dot.its.jpo.ode.services.vsd;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssVehicleSituationRecord;
import us.dot.its.jpo.ode.util.CodecUtils;

public class VsdBundlerTest {

   @Tested
   VsdBundler testVsdBundler;

   @Mocked
   J2735Bsm mockJ2735Bsm;

   @Test
   public void shouldReturnNullNotFull() {
      new Expectations() {
         {
            mockJ2735Bsm.getCoreData().getId();
            result = "testID";
         }
      };

      assertNull(testVsdBundler.addToVsdBundle(mockJ2735Bsm));
   }

   @Test
   public void shouldReturnVSDAfter10Bsms(@Capturing OssVehicleSituationRecord capturingOssVehicleSituationRecord,
         @Capturing CodecUtils capturingCodecUtils) {
      new Expectations() {
         {
            mockJ2735Bsm.getCoreData().getId();
            result = "idTest";
         }
      };

      for (int i = 0; i < 9; i++) {
         assertNull(testVsdBundler.addToVsdBundle(mockJ2735Bsm));
      }

      assertTrue(testVsdBundler.addToVsdBundle(mockJ2735Bsm) instanceof VehSitDataMessage);
   }

}
