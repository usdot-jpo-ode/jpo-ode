package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.fail;

import java.time.ZonedDateTime;

import org.junit.Test;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class AsdMessageTest {

    @Tested
    AsdMessage testAsdMessage;

    @Injectable
    String startTime = "12:34";
    @Injectable
    String stopTime = "12:35";
    @Injectable
    String advisoryMessage = "message";
    @Injectable
    J2735GeoRegion serviceRegion;
    @Injectable
    SituationDataWarehouse.SDW.TimeToLive ttl = SituationDataWarehouse.SDW.TimeToLive.ONEMINUTE;

    @Mocked
    ZonedDateTime mockZonedDateTimeStart;
    @Mocked
    ZonedDateTime mockZonedDateTimeStop;

    @Test
    public void testEncodeHex(@Mocked final DateTimeUtils dateTimeUtils, @Mocked final CodecUtils codecUtils,
            @Mocked final J2735 mockJ2735) {

        try {
            testAsdMessage.encodeHex();
        } catch (Exception e) {
            fail("Unexpected exception in method .enodeHex(): " + e);
        }
    }

}
