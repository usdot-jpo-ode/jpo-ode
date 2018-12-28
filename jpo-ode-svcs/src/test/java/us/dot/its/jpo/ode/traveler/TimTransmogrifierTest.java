package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class TimTransmogrifierTest {

   @Injectable
   OdeProperties injectableOdeProperties;

   @Test
   public void testGetRsu() throws IOException {
      new Expectations() {
         {
            injectableOdeProperties.getRsuUsername();
            result = "v3user";

            injectableOdeProperties.getRsuPassword();
            result = "password";
         }
      };

      RSU expected = new RSU("127.0.0.1", "v3user", "password", 1, 2000);

      // rsuUsername and rsuPassword are null
      RSU actual1 = new RSU("127.0.0.1", null, null, 1, 2000);
      TimTransmogrifier.updateRsuCreds(actual1, injectableOdeProperties);
      assertEquals(expected, actual1);

      // rsuUsername and rsuPassword are not-null
      RSU actual2 = new RSU("127.0.0.1", "v3user", "password", 1, 2000);
      TimTransmogrifier.updateRsuCreds(actual2, injectableOdeProperties);
      assertEquals(expected, actual2);

      // rsuUsername and rsuPassword are blank
      RSU actual3 = new RSU("127.0.0.1", "", "", 1, 2000);
      TimTransmogrifier.updateRsuCreds(actual3, injectableOdeProperties);
      assertEquals(expected, actual3);
   }

   @Test
   public void testObfuscateRsuPassword() {
      String actual = TimTransmogrifier.obfuscateRsuPassword(
            "{\"metadata\":{\"request\":{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"sdw\":null,\"rsus\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\": \"password\",\"rsuRetries\":0,\"rsuTimeout\":2000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\": \"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\": \"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeMsgPayload\",\"serialId\":{\"streamId\":\"59651ecc-240c-4440-9011-4a43c926817b\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2018-11-16T19:21:22.568Z\",\"schemaVersion\":6,\"recordGeneratedAt\":\"2017-03-13T06:07:11Z\",\"recordGeneratedBy\":\"TMC\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage\",\"data\":{\"msgCnt\":13,\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":0,\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000000000000000\"},\"furtherInfoID\":null},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":22,\"priority\":0,\"sspLocationRights\":3,\"regions\":[{\"name\":\"bob\",\"regulatorID\":23,\"segmentID\":33,\"anchorPosition\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"laneWidth\":7,\"directionality\":3,\"closedPath\":false,\"direction\":\"1010101010101010\",\"description\":\"geometry\",\"path\":null,\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":1,\"laneWidth\":33,\"circle\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"radius\":15,\"units\":7}},\"oldRegion\":null}],\"sspMsgTypes\":2,\"sspMsgContent\":3,\"content\":\"Advisory\",\"items\":[\"125\",\"some text\",\"250\",\"\\u002798765\"],\"url\":\"null\"}],\"asnDataFrames\":null}}}");
      assertEquals(
            "{\"metadata\":{\"request\":{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"sdw\":null,\"rsus\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"*\",\"rsuRetries\":0,\"rsuTimeout\":2000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"*\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"*\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeMsgPayload\",\"serialId\":{\"streamId\":\"59651ecc-240c-4440-9011-4a43c926817b\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2018-11-16T19:21:22.568Z\",\"schemaVersion\":6,\"recordGeneratedAt\":\"2017-03-13T06:07:11Z\",\"recordGeneratedBy\":\"TMC\",\"sanitized\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage\",\"data\":{\"msgCnt\":13,\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":0,\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000000000000000\"},\"furtherInfoID\":null},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":22,\"priority\":0,\"sspLocationRights\":3,\"regions\":[{\"name\":\"bob\",\"regulatorID\":23,\"segmentID\":33,\"anchorPosition\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"laneWidth\":7,\"directionality\":3,\"closedPath\":false,\"direction\":\"1010101010101010\",\"description\":\"geometry\",\"path\":null,\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":1,\"laneWidth\":33,\"circle\":{\"position\":{\"latitude\":41.678473,\"longitude\":-108.782775,\"elevation\":917.1432},\"radius\":15,\"units\":7}},\"oldRegion\":null}],\"sspMsgTypes\":2,\"sspMsgContent\":3,\"content\":\"Advisory\",\"items\":[\"125\",\"some text\",\"250\",\"\\u002798765\"],\"url\":\"null\"}],\"asnDataFrames\":null}}}",
            actual);
   }

   public void assertConvertArray(String array, String arrayKey, String elementKey, Object expectedXml)
         throws JsonUtilsException, XmlUtilsException {
      JsonNode obj = JsonUtils.toObjectNode(array);
      JsonNode oldObj = obj.get(arrayKey);

      JsonNode newObj = XmlUtils.createEmbeddedJsonArrayForXmlConversion(elementKey, oldObj);
      String actualXml = XmlUtils.toXmlStatic(newObj);

      assertEquals(expectedXml, actualXml);
   }

   @Test
   public void testConvertRsusArray() throws JsonUtilsException, XmlUtilsException {
      String single = "{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"rsus\":{\"rsu_\":[{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}]},\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}}";
      String singleXmlExpected = "<ObjectNode><rsus><rsu_><rsuTarget>127.0.0.3</rsuTarget><rsuUsername>v3user</rsuUsername><rsuPassword>password</rsuPassword><rsuRetries>1</rsuRetries><rsuTimeout>1000</rsuTimeout><rsuIndex>10</rsuIndex></rsu_></rsus></ObjectNode>";
      assertConvertArray(single, TimTransmogrifier.RSUS_STRING, TimTransmogrifier.RSUS_STRING, singleXmlExpected);

      String multi = "{\"ode\":{\"version\":3,\"verb\":\"POST\"},\"rsus\":{\"rsu_\":[{\"rsuTarget\":\"127.0.0.1\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":0,\"rsuTimeout\":2000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10},{\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":1,\"rsuTimeout\":1000,\"rsuIndex\":10}]},\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":31,\"mode\":1,\"channel\":178,\"interval\":2,\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":1,\"status\":4}}";
      String multiXmlExpected = "<ObjectNode><rsus><rsu_><rsuTarget>127.0.0.1</rsuTarget><rsuUsername>v3user</rsuUsername><rsuPassword>password</rsuPassword><rsuRetries>0</rsuRetries><rsuTimeout>2000</rsuTimeout><rsuIndex>10</rsuIndex></rsu_><rsu_><rsuTarget>127.0.0.2</rsuTarget><rsuUsername>v3user</rsuUsername><rsuPassword>password</rsuPassword><rsuRetries>1</rsuRetries><rsuTimeout>1000</rsuTimeout><rsuIndex>10</rsuIndex></rsu_><rsu_><rsuTarget>127.0.0.3</rsuTarget><rsuUsername>v3user</rsuUsername><rsuPassword>password</rsuPassword><rsuRetries>1</rsuRetries><rsuTimeout>1000</rsuTimeout><rsuIndex>10</rsuIndex></rsu_></rsus></ObjectNode>";
      assertConvertArray(multi, TimTransmogrifier.RSUS_STRING, TimTransmogrifier.RSUS_STRING, multiXmlExpected);
   }

}
