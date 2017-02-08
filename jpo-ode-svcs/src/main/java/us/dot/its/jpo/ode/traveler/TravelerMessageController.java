package us.dot.its.jpo.ode.traveler;

import com.oss.asn1.Coder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.json.*;
@Controller
public class TravelerMessageController {

    private static Coder coder;
    private static final String MESSAGE_DIR = "./src/test/resources/CVMessages/";

    @RequestMapping(value = "/travelerMessage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timeMessage(@RequestBody String jsonString ) throws Exception {
        

        System.out.print(jsonString);

        JSONObject obj = new JSONObject(jsonString);
        String timeToLive = obj.getJSONObject("deposit").getString("timeToLive");



        return timeToLive;
    }
    
}