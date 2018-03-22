package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.fail;

import java.time.ZonedDateTime;

import org.junit.Test;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.DdsGeoRegion;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

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
    String recordID = "76543210";

    @Mocked
    ZonedDateTime mockZonedDateTimeStart;
    @Mocked
    ZonedDateTime mockZonedDateTimeStop;

    @Test
    public void testEncodeHex(@Mocked final DateTimeUtils dateTimeUtils, @Mocked final CodecUtils codecUtils,
            @Mocked final J2735 mockJ2735) {

        try {
            testAsdMessage.getAsdmDetails().getAdvisoryMessage();
        } catch (Exception e) {
            fail("Unexpected exception in method .encodeHex(): " + e);
        }
    }

}
