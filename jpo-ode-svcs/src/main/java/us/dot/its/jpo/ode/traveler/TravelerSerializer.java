package us.dot.its.jpo.ode.traveler;

import com.oss.asn1.PERUnalignedCoder;
import com.oss.util.HexTool;
import org.json.JSONObject;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrameList;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by anthonychen on 2/8/17.
 */
public class TravelerSerializer {

    public TravelerInformation travelerInfo;
    public TravelerSerializer(String jsonInfo){
        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();
//      MsgCount msgCnt = new MsgCount(1);
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

        JSONObject obj = new JSONObject(jsonInfo);
        String msgcnt = obj.getJSONObject("timContent").getString("msgcnt");

        // Standard Tim Message
        String timHex = "3081C68001108109000000000000003714830101A481AE3081AB800102A11BA119A0108004194FBA1F8104CE45CE2382020A0681020006820102820207DE830301C17084027D00850102A6108004194FC1988104CE45DA4082020A008702016E880100A92430228002000EA21CA01AA31804040CE205A104040ADA04F70404068004D60404034D0704AA3AA0383006A004800235293006A0048002010C3006A004800231283006A004800222113006A0048002010C3006A004800231203006A0048002221185021001";
        byte [] tim_ba = HexTool.parseHex(timHex, false);
        InputStream ins = new ByteArrayInputStream(tim_ba);

        travelerInfo = new TravelerInformation();

        System.out.print(travelerInfo);
        try {
            coder.decode(ins, travelerInfo);

        } catch (Exception e) {
//            System.out.print( e);
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
            }
        }

        MsgCount cnt = new MsgCount(Integer.parseInt(msgcnt));
        travelerInfo.setMsgCnt(cnt);
    }

    private TravelerInformation.Regional setRegional(JSONObject dataFrame){
        return null;
    }
    private TravelerDataFrameList setDataFrames(JSONObject dataFrame){
        return null ;
    }

    public TravelerInformation getTravelerInformationObject(){
        return travelerInfo;
    }

}
