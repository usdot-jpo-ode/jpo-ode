package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class BsmBuilderTest {

   @Test
   public void shouldTranslateBsm() throws BsmPart2ContentBuilderException {

      JsonNode jsonBsm = null;
      try {
         jsonBsm = (JsonNode) XmlUtils.fromXmlS(
               "<BasicSafetyMessage><coreData><msgCnt>88</msgCnt><id>BEA10000</id><secMark>59299</secMark><lat>411642143</lat><long>-1048434120</long><elev>18822</elev><accuracy><semiMajor>255</semiMajor><semiMinor>255</semiMinor><orientation>65535</orientation></accuracy><transmission><unavailable/></transmission><speed>0</speed><heading>15290</heading><angle>127</angle><accelSet><long>2001</long><lat>2001</lat><vert>-127</vert><yaw>0</yaw></accelSet><brakes><wheelBrakes>10000</wheelBrakes><traction><unavailable/></traction><abs><unavailable/></abs><scs><unavailable/></scs><brakeBoost><unavailable/></brakeBoost><auxBrakes><unavailable/></auxBrakes></brakes><size><width>0</width><length>0</length></size></coreData><partII><PartIIcontent><partII-Id>0</partII-Id><partII-Value><VehicleSafetyExtensions><pathHistory><crumbData><PathHistoryPoint><latOffset>130</latOffset><lonOffset>131071</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>16680</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>327</latOffset><lonOffset>131071</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>17570</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1032</latOffset><lonOffset>131071</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>18710</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1236</latOffset><lonOffset>161</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>19180</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>2118</latOffset><lonOffset>747</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>21420</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1605</latOffset><lonOffset>716</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>22270</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1827</latOffset><lonOffset>858</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>23540</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1942</latOffset><lonOffset>1281</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>26890</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1655</latOffset><lonOffset>1110</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>27860</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1488</latOffset><lonOffset>1138</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>28440</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>1403</latOffset><lonOffset>1090</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>28550</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>23</latOffset><lonOffset>205</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>49050</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>131071</latOffset><lonOffset>46</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>58030</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>131071</latOffset><lonOffset>131071</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>61070</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>128</latOffset><lonOffset>131071</lonOffset><elevationOffset>2047</elevationOffset><timeOffset>61650</timeOffset></PathHistoryPoint></crumbData></pathHistory><pathPrediction><radiusOfCurve>32767</radiusOfCurve><confidence>0</confidence></pathPrediction></VehicleSafetyExtensions></partII-Value></PartIIcontent></partII></BasicSafetyMessage>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735Bsm actualBsm = BsmBuilder.genericBsm(jsonBsm);

      //assertEquals("string", actualBsm.toJson());
      assertNotNull(actualBsm);
   }

}
