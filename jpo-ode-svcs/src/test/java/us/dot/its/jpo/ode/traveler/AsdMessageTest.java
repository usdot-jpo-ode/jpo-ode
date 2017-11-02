package us.dot.its.jpo.ode.traveler;

import java.time.ZonedDateTime;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.DdsGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;

public class AsdMessageTest {

    @Tested
    DdsAdvisorySituationData testAsdMessage;

    @Injectable
    String startTime = "12:34";
    @Injectable
    String stopTime = "12:35";
    @Injectable
    J2735MessageFrame advisoryMessage;
    @Injectable
    DdsGeoRegion serviceRegion;
    @Injectable
    SituationDataWarehouse.SDW.TimeToLive ttl = 
    SituationDataWarehouse.SDW.TimeToLive.oneminute;

    @Mocked
    ZonedDateTime mockZonedDateTimeStart;
    @Mocked
    ZonedDateTime mockZonedDateTimeStop;

    //TODO open-ode
//    @Test
//    public void testEncodeHex(@Mocked final DateTimeUtils dateTimeUtils, @Mocked final CodecUtils codecUtils,
//            @Mocked final J2735 mockJ2735) {
//
//        try {
//            testAsdMessage.getAsdmDetails().getAdvisoryMessage();
//        } catch (Exception e) {
//            fail("Unexpected exception in method .enodeHex(): " + e);
//        }
//    }

}
