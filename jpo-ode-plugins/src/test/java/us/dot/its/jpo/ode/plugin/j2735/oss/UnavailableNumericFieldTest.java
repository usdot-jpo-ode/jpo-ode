package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

public class UnavailableNumericFieldTest {

   private OssAsn1Coder coder = new OssAsn1Coder();
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void test() throws EncodeFailedException, EncodeNotSupportedException {
      String hBsm = "004C4C8CCD00005AD27494B5A4E8CB8BB40000000000050000FD7D07D07F7FFF0000050050000000000000000000000000000000000000000000000000000000";
      
      J2735Bsm bsm = (J2735Bsm) coder.UPER_DecodeBsmHex(hBsm);
      assertEquals(0, bsm.getCoreData().getSpeed().compareTo(BigDecimal.valueOf(0.2)));
      assertNull(bsm.getCoreData().getAngle());
      
      hBsm = "004C4C8CCD00005AD27494B5A4E8CB8BB4000000000FFF8000FC7D07D07F7FFF0000050050000000000000000000000000000000000000000000000000000000";
      
//      bsm.coreData.speed = new Speed(8191);
//      bsm.coreData.angle = new SteeringWheelAngle(126);

      bsm = (J2735Bsm) coder.UPER_DecodeBsmHex(hBsm);
      assertNull(bsm.getCoreData().getSpeed());
      assertEquals(0, bsm.getCoreData().getAngle().compareTo(BigDecimal.valueOf(189.0)));
   }

}
