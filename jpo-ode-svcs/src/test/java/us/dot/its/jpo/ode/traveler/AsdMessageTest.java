package us.dot.its.jpo.ode.traveler;

import java.time.ZonedDateTime;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.DdsGeoRegion;

public class AsdMessageTest {

    @Tested
    DdsAdvisorySituationData testAsdMessage;

    @Injectable
    String startTime = "12:34";
    @Injectable
    String stopTime = "12:35";
    @Injectable
    Ieee1609Dot2DataTag advisoryMessage;
    @Injectable
    DdsGeoRegion serviceRegion;
    @Injectable
    SituationDataWarehouse.SDW.TimeToLive ttl = SituationDataWarehouse.SDW.TimeToLive.oneminute;
    @Injectable
    String groupID = "01234567";
    @Injectable
    byte distroType = DdsAdvisorySituationData.NONE; 
    @Injectable
    String recordID = "76543210";

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
