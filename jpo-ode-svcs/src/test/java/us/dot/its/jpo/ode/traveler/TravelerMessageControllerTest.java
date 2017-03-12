package us.dot.its.jpo.ode.traveler;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
/**
 * Created by anthonychen on 2/10/17.
 */
@RunWith(JMockit.class)
public class TravelerMessageControllerTest {

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

   @Mocked
   private OdeProperties odeProperties;
   
   @Test
   public void shouldRefuseConnectionNullIp() {

      String jsonString = null;

      try {
         new TravelerMessageController(odeProperties).timMessage(jsonString);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertEquals(TimMessageException.class, e.getClass());
      }
   }

}
