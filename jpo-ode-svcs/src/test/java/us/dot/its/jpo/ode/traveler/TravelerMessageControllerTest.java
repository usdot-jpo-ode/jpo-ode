package us.dot.its.jpo.ode.traveler;

import org.junit.Test;

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





}
