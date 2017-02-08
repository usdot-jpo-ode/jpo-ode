package us.dot.its.jpo.ode.traveler;

import com.oss.asn1.AbstractData;
import com.oss.asn1.PERUnalignedCoder;
import com.oss.util.HexTool;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


@Controller
public class TravelerMessageController {


    private static final String MESSAGE_DIR = "./src/test/resources/CVMessages/";

    @RequestMapping(value = "/travelerMessage", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public static String timeMessage(@RequestBody String jsonString ) throws Exception {

        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

        byte[] output = new byte[0 ];

        System.out.print(jsonString);

        JSONObject obj = new JSONObject(jsonString);
        String timeToLive = obj.getJSONObject("deposit").getString("timeToLive");

//        MsgCount msgCnt = new MsgCount(1);
//        MinuteOfTheYear moty = new MinuteOfTheYear(1);
//
//        int var = 1;
//        byte[] myvar = var.getBytes();
//        UniqueMSGID msdId = new UniqueMSGID(myvar);
//
//        URL_Base url = new URL_Base("www.google.con");
//
////        TravelerDataFrame(SSPindex sspTimRights,
////                TravelerInfoType frameType, MsgId msgId, DYear startYear,
////                MinuteOfTheYear startTime, MinutesDuration duratonTime,
////                SignPrority priority, SSPindex sspLocationRights,
////                Regions regions, SSPindex sspMsgRights1,
////                SSPindex sspMsgRights2, Content content, URL_Short url)
////        {
//
//        TravelerInformation decoder = new TravelerInformation();
//        TravelerInformation travelerContent = new TravelerInformation();
//
//        TravelerDataFrame dataFrame = new TravelerDataFrame();
//
//        TravelerDataFrameList dataFrameList = new TravelerDataFrameList();
//        TravelerInformation.Regional regional = new TravelerInformation.Regional();
//
//
//
//        AbstractData abstractTim = new TravelerInformation(msgCnt, moty, msdId, url, dataFrameList, regional);
//
//        System.out.print(coder.encode(abstractTim));

        String tim = "c44000000000001869f0001869f2a7709e801ce48895880029dc2251e73928468200000000ad9a0108c402000000000046544d52104e53b84a9a4e7249fa05cc0000c9f843669c000d1000480007253b84a9a4e7249fa05cc033ff3c010a0101ffe4ffb2ff707fe7804e10400188c800";
        byte [] tim_ba = HexTool.parseHex(tim, false);
        InputStream ins = new ByteArrayInputStream(tim_ba);

        AbstractData bsm = new TravelerDataFrame();

        coder.decode(ins, bsm);

        System.out.print(bsm);


        return timeToLive;
    }
    
}