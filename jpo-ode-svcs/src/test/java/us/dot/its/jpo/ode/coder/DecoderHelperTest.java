package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.ZonedDateTime;

import org.junit.Ignore;
import org.junit.Test;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssVehicleSituationRecord;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.coder.BsmDecoderHelper;

public class DecoderHelperTest {

   @Test
   @Ignore
   public void test() {
      fail("Not yet implemented");
   }

   @Ignore
   @Test
   public void decodeBsmTest() {

      InputStream testInput = null;

      J2735MessageFrame expectedValue = new J2735MessageFrame();
      expectedValue = null;
      
      
      // OdeObject actualValue = BsmDecoderHelper.decodeBsm(testInput);

      // assertEquals(expectedValue, actualValue);
   }
//
//   @Test
//   public void decodeBsmByteTest() {
//
//      byte[] testInput = { 6, 1, 1, 4, 6, 1, 1, 1, 6, 1 };
//
//      J2735MessageFrame expectedValue = new J2735MessageFrame();
//      expectedValue = null;
//      OdeObject actualValue;
//      
//      actualValue = decodeBsm((BufferedInputStream) testInput);
//
//      assertEquals(expectedValue, actualValue);
//   }
//
//   public void decodeBsmByteTestTwo() {
//
//      byte[] testInput = { 6, 1, 1, 4, 6, 1, 1, 1, 6, 1 };
//
//      J2735MessageFrame expectedValue = new J2735MessageFrame();
//      expectedValue = null;
//      OdeObject actualValue = decodeBsm(testInput);
//
//      assertEquals(expectedValue, actualValue);
//   }

}
