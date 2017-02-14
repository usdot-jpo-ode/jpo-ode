package us.dot.its.jpo.ode.traveler;

import org.json.JSONObject;
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
        JSONObject obj = new JSONObject(jsonString);
//        String msgcnt = obj.getString("msgcnt");
//
//        // Standard Tim Message
//        String timHex = "3081C68001108109000000000000003714830101A481AE3081AB800102A11BA119A0108004194FBA1F8104CE45CE2382020A0681020006820102820207DE830301C17084027D00850102A6108004194FC1988104CE45DA4082020A008702016E880100A92430228002000EA21CA01AA31804040CE205A104040ADA04F70404068004D60404034D0704AA3AA0383006A004800235293006A0048002010C3006A004800231283006A004800222113006A0048002010C3006A004800231203006A0048002221185021001";
//        byte [] tim_ba = HexTool.parseHex(timHex, false);
//        InputStream ins = new ByteArrayInputStream(tim_ba);
//
//        TravelerInformation tim = new TravelerInformation();
//
//        System.out.print(tim);
//        try {
//            coder.decode(ins, tim);
//
//        } catch (Exception e) {
////            System.out.print( e);
//        } finally {
//            try {
//                ins.close();
//            } catch (IOException e) {
//            }
//        }
//
//        MsgCount cnt = new MsgCount(msgcnt);
//        tim.setMsgCnt(cnt);
        TravelerSerializer timObject = new TravelerSerializer(jsonString);
        System.out.print(timObject.getTravelerInformationObject());


        return jsonString;
    }
    
}