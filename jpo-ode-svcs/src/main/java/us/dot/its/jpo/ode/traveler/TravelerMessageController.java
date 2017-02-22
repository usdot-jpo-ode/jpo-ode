package us.dot.its.jpo.ode.traveler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class TravelerMessageController {


    private static final String MESSAGE_DIR = "./src/test/resources/CVMessages/";

    @RequestMapping(value = "/travelerMessage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timMessage(@RequestBody String jsonString ) throws Exception {

        if (jsonString == null) {
            throw new IllegalArgumentException("[ERROR] Endpoint received null TIM");
        }

        TravelerSerializer timObject = new TravelerSerializer(jsonString);
        System.out.println(timObject.getTravelerInformationObject());

        System.out.print(timObject.getHexTravelerInformation());


        return jsonString;
    }
    
}