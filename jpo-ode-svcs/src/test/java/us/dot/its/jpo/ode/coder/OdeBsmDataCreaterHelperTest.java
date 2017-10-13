package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Capturing;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class OdeBsmDataCreaterHelperTest {
   @Capturing
   J2735Bsm capturingJ2735Bsm;
   @Capturing
   IEEE1609p2Message capturingIEEE1609p2Message;
   @Capturing
   BsmLogFileParser capturingBsmFileParser;
   
   
   @Test
   public void notNullTest() {
      
      assertNotNull(OdeBsmDataCreatorHelper.createOdeBsmData(
         capturingJ2735Bsm, capturingIEEE1609p2Message, capturingBsmFileParser));
   }
   @Test
   public void nullTest() {
      capturingIEEE1609p2Message = null;
      assertNotNull(OdeBsmDataCreatorHelper.createOdeBsmData(
         capturingJ2735Bsm, capturingIEEE1609p2Message, capturingBsmFileParser));
   }


}
