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

        //Get fully populated TIMcontent string
        JSONObject obj = new JSONObject(jsonInfo);
        
        //Populate pojo's for TIM
        String msgcnt = obj.getJSONObject("timContent").getString("msgcnt");
        validateMessageCount(msgcnt);
        
        //Populate pojo's for part1-header
        int sspindex = Integer.parseInt(obj.getJSONObject("timContent").getJSONObject("header").getString("sspindex"));
        validateHeaderIndex(sspindex);
        String travelerInfoType = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getString("FurtherInfoID");
        validateInfoType(travelerInfoType);
        
        if (travelerInfoType.equals(null)) //Choice for msgid was roadsign
        {
           boolean notChecked = true;
           String latitude = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("latitude");
           validateLat(latitude);
           String longitude = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("longitude");
           validateLong(longitude);
           //String elevation = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("elevation");
           String headingSlice = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getString("HeadingSlice");
           if (headingSlice.equals("noHeading")) {//No bits were set
              notChecked = false;
           }
           else if (headingSlice.equals("allHeadings")) {//All bits were set
              notChecked = false;
           }
           if (notChecked){
              validateHeading(headingSlice);
           }
        }
        String minuteOfTheYear = obj.getJSONObject("timContent").getJSONObject("header").getString("MinuteOfTheYear");
        validateMinuteYear(minuteOfTheYear);
        String minuteDuration = obj.getJSONObject("timContent").getJSONObject("header").getString("MinutesDuration");
        validateMinutesDuration(minuteDuration);
        String SignPriority = obj.getJSONObject("timContent").getJSONObject("header").getString("SignPriority");
        validateSign(SignPriority);
        
        //Populate pojo's for part2-region
        int index = Integer.parseInt(obj.getJSONObject("timContent").getJSONObject("region").getString("sspindex"));
        validateHeaderIndex(index);
        
        //Populate pojo's for part3-content
        int sspMsgRights1 = Integer.parseInt(obj.getJSONObject("timContent").getJSONObject("content").getString("sspMsgRights1"));
        validateHeaderIndex(sspMsgRights1);
        int sspMsgRights2 = Integer.parseInt(obj.getJSONObject("timContent").getJSONObject("content").getString("sspMsgRights2"));
        validateHeaderIndex(sspMsgRights2);
        
        //Content choice
        boolean adv = obj.getJSONObject("timContent").getJSONObject("content").isNull("advisory");
        boolean work = obj.getJSONObject("timContent").getJSONObject("content").isNull("workZone");
        boolean speed = obj.getJSONObject("timContent").getJSONObject("content").isNull("speedLimit");
        boolean exitServ = obj.getJSONObject("timContent").getJSONObject("content").isNull("exitService");
        if (!adv && !work && !speed)//ExitService
        {
           int len = obj.getJSONObject("timContent").getJSONObject("content").getJSONArray("advisory").length();
           for (int i = 1; i <=len; i++)
           {
              String it = "item" + i;
              if (obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
              {
                 String code = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                 validateITISCodes(code);
              }
              String text = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
              validateString(text);
           }
        }
        else if (!adv && !work && !exitServ)//Speed
        {
           int len = obj.getJSONObject("timContent").getJSONObject("content").getJSONArray("workZone").length();
           for (int i = 1; i <=len; i++)
           {
              String it = "item" + i;
              if (obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
              {
                 String code = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                 validateITISCodes(code);
              }
              String text = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
              validateString(text);
           }
        }
        else if (!adv && !speed && !exitServ)//work
        {
           int len = obj.getJSONObject("timContent").getJSONObject("content").getJSONArray("speedLimit").length();
           for (int i = 1; i <=len; i++)
           {
              String it = "item" + i;
              if (obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
              {
                 String code = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                 validateITISCodes(code);
              }
              String text = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
              validateString(text);
           }
        }
        else if (!work && !speed && !exitServ)//Advisory
        {
           int len = obj.getJSONObject("timContent").getJSONObject("content").getJSONArray("exitService").length();
           for (int i = 1; i <=len; i++)
           {
              String it = "item" + i;
              if (obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
              {
                 String code = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                 validateITISCodes(code);
              }
              String text = obj.getJSONObject("timContent").getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
              validateString(text);
           }
        }
        
        //Populate pojo's for SNMP
        String target = obj.getJSONObject("RSUs").getString("target");
        String userName = obj.getJSONObject("RSUs").getString("username");
        String password = obj.getJSONObject("RSUs").getString("pass");
        String retries = obj.getJSONObject("RSUs").getString("retries");
        String timeout = obj.getJSONObject("RSUs").getString("timeout");

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
    
    public void validateMessageCount(String msg){
       int myMsg = Integer.parseInt(msg);
       if (myMsg > 127 || myMsg < 0)
          throw new IllegalArgumentException("Invalid message count");
    }
    
    public void validateHeaderIndex(int count){
       if (count < 0 || count > 31)
            throw new IllegalArgumentException("Invalid header sspIndex");
    }
    
    public void validateInfoType(String num){
       int myNum = Integer.parseInt(num);
       if (myNum < 0)
          throw new IllegalArgumentException("Invalid enumeration");
    }
    
    public void validateLat(String lat){
       int myLat = Integer.parseInt(lat);
       if (myLat < -900000000 || myLat > 900000001)
          throw new IllegalArgumentException("Invalid Latitude");
    }
    
    public void validateLong(String lonng){
       int myLong = Integer.parseInt(lonng);
       if (myLong < -1799999999 || myLong > 1800000001)
          throw new IllegalArgumentException("Invalid Longitude");
    }
    
    public void validateHeading(String head){//Needs to be updated
       byte[] heads = head.getBytes();
       if (heads.length > 2)
          throw new IllegalArgumentException("Invalid BitString");
    }
    
    public void validateMinuteYear(String min){
       int myMin = Integer.parseInt(min);
       if (myMin < 0 || myMin > 527040)
             throw new IllegalArgumentException("Invalid Minute of the Year");
    }
    
    public void validateMinutesDuration(String dur){
       int myDur = Integer.parseInt(dur);
       if (myDur < 0 || myDur > 32000)
             throw new IllegalArgumentException("Invalid Duration");
    }
    
    public void validateSign(String sign){
       int mySign = Integer.parseInt(sign);
       if (mySign < 0 || mySign > 7)
             throw new IllegalArgumentException("Invalid Sign Priority");
    }
    
    public void validateITISCodes(String code){
       int myCode = Integer.parseInt(code);
       if (myCode < 0 || myCode > 65535)
          throw new IllegalArgumentException("Invalid ITIS code");
    }
    
    public void validateString(String str){
       if (str.isEmpty())
          throw new IllegalArgumentException("Invalid Empty String");
    }

}
