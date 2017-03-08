package us.dot.its.jpo.ode.traveler;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
/**
 * Created by anthonychen on 2/10/17.
 */
public class TravelerMessageControllerTest {


    /**
     * Created by anthonychen on 2/10/17.
     */

    @Test
    public void shouldRefuseConnectionNullIp() {

        String jsonString = null;


        String response = null;

        try {
            response = TravelerMessageController.timMessage(jsonString);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void testBuildTravelerInformation() throws IOException, DecodeFailedException, DecodeNotSupportedException {
        testBuildTravelerInformation("tim2.json");	// lane
        testBuildTravelerInformation("tim3.json");	// region
    }
    public void testBuildTravelerInformation(String timName) throws IOException, DecodeFailedException, DecodeNotSupportedException {
//        TravelerMessageController timBuilder = new TravelerMessageController();
//        String json = FileUtils.readFileToString(new File("src/test/resources/" + timName));
//        TravelerInformationMessage timMessage = (TravelerInformationMessage)timBuilder.build(json);
//
//        assertEquals(TravelerInformationMessage.class.getSimpleName(), timMessage.getMessageName());
//
//        TravelerInformation tim = J2735Helper.decodeMessage(timMessage.getHexString(), new TravelerInformation());
//        System.out.println(tim);

    }






}
