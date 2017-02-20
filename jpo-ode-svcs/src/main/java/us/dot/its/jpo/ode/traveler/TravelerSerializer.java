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
import java.util.ArrayList;


/**
 * Created by anthonychen on 2/8/17.
 */
public class TravelerSerializer {

    public TravelerInformation travelerInfo;
    public final int ADVISORY = 0;
    public final int WORKZONE = 1;
    public final int GENERICSIGN = 2;
    public final int SPEEDLIMIT = 3;
    public final int EXITSERVICE = 4;
    int contentType;
    int contentLength;
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
        
        int frameList = obj.getJSONObject("timContent").getJSONObject("travelerDataFrame").length(); //Check the dataframe count
        
        //Populate pojo's for TIM
        String msgcnt = obj.getJSONObject("timContent").getString("msgcnt");
        validateMessageCount(msgcnt);
        
        for (int z = 1; z <= frameList; z++)
        {
           String curFrame = "df" + z;
           //Populate pojo's for part1-header.
           //Length will be 6 if FurtherInfoID is set, or 8 if RoadSign was selected
           ArrayList<String> part1 = buildTravelerMessagePart1(obj.getJSONObject("timContent").getJSONObject("travelerDataFrame").getJSONObject(curFrame));
           
           //Populate pojo's for part2-region
           //Currently only length of 1
           ArrayList<String> part2 = buildTravelerMessagePart2(obj.getJSONObject("timContent").getJSONObject("travelerDataFrame").getJSONObject(curFrame));
           
           //Populate pojo's for part3-content
           //Reference global variable contentType for the type of content in the data frame
           //Reference global variable contentLength for the length of the content when populating the TIM object
           ArrayList<String> part3 = buildTravelerMessagePart3(obj.getJSONObject("timContent").getJSONObject("travelerDataFrame").getJSONObject(curFrame));
           
           //Populate pojo's for SNMP
           String target = obj.getJSONObject("RSUs").getString("target");
           String userName = obj.getJSONObject("RSUs").getString("username");
           String password = obj.getJSONObject("RSUs").getString("pass");
           String retries = obj.getJSONObject("RSUs").getString("retries");
           String timeout = obj.getJSONObject("RSUs").getString("timeout");
        }
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
    
    private ArrayList<String> buildTravelerMessagePart1(JSONObject ob){
       ArrayList<String> p1 = null;
       
       String sspindex = ob.getJSONObject("header").getString("sspindex");
       validateHeaderIndex(sspindex);
       p1.add(sspindex);
       String travelerInfoType = ob.getJSONObject("header").getString("travelerInfoType");
       validateInfoType(travelerInfoType);
       p1.add("travelerInfoType");
       String msg = ob.getJSONObject("header").getJSONObject("msgId").getString("FurtherInfoID");
       
       if (msg.equals(null)) //Choice for msgid was roadsign
       {
          boolean notChecked = true;
          String latitude = ob.getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("latitude");
          validateLat(latitude);
          p1.add(latitude);
          String longitude = ob.getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("longitude");
          validateLong(longitude);
          p1.add(longitude);
          //String elevation = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("elevation");
          String headingSlice = ob.getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getString("HeadingSlice");
          if (headingSlice.equals("noHeading")) {//No bits were set
             notChecked = false;
          }
          else if (headingSlice.equals("allHeadings")) {//All bits were set
             notChecked = false;
          }
          if (notChecked){
             validateHeading(headingSlice);
          }
          p1.add(headingSlice);
       }
       else
       {
          p1.add(msg);
       }
       
       String minuteOfTheYear = ob.getJSONObject("header").getString("MinuteOfTheYear");
       validateMinuteYear(minuteOfTheYear);
       p1.add(minuteOfTheYear);
       String minuteDuration = ob.getJSONObject("header").getString("MinutesDuration");
       validateMinutesDuration(minuteDuration);
       p1.add(minuteDuration);
       String SignPriority = ob.getJSONObject("header").getString("SignPriority");
       validateSign(SignPriority);
       p1.add(SignPriority);
       return p1;
    }
    
    private ArrayList<String> buildTravelerMessagePart2(JSONObject ob){
       ArrayList<String> p2 = null;
       String index = ob.getJSONObject("region").getString("sspindex");
       validateHeaderIndex(index);
       p2.add(index);
       
       return p2;
    }
    private ArrayList<String> buildTravelerMessagePart3(JSONObject ob){
       ArrayList<String> p3 = null;
       String sspMsgRights1 = ob.getJSONObject("content").getString("sspMsgRights1");
       validateHeaderIndex(sspMsgRights1);
       p3.add(sspMsgRights1);
       String sspMsgRights2 = ob.getJSONObject("content").getString("sspMsgRights2");
       validateHeaderIndex(sspMsgRights2);
       p3.add(sspMsgRights2);
       
       //Content choice
       boolean adv = ob.getJSONObject("content").isNull("advisory");
       boolean work = ob.getJSONObject("content").isNull("workZone");
       boolean sign = ob.getJSONObject("content").isNull("genericSign");
       boolean speed = ob.getJSONObject("content").isNull("speedLimit");
       boolean exitServ = ob.getJSONObject("content").isNull("exitService");
       
       if (!adv && !work && !speed && !sign)//ExitService "4"
       {
          int len = ob.getJSONObject("content").getJSONArray("advisory").length();
          contentType = 4;
          contentLength = len;
          for (int i = 1; i <=len; i++)
          {
             String it = "item" + i;
             if (ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
             {
                String code = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                validateITISCodes(code);
                p3.add(code);
             }
             String text = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
             validateString(text);
             p3.add(text);
          }
       }
       else if (!adv && !work && !exitServ && !sign)//Speed "3"
       {
          int len = ob.getJSONObject("content").getJSONArray("workZone").length();
          contentType = 3;
          contentLength = len;
          for (int i = 1; i <=len; i++)
          {
             String it = "item" + i;
             if (ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
             {
                String code = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                validateITISCodes(code);
                p3.add(code);
             }
             String text = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
             validateString(text);
             p3.add(text);
          }
       }
       else if (!adv && !speed && !exitServ && !sign)//work "1"
       {
          int len = ob.getJSONObject("content").getJSONArray("speedLimit").length();
          contentType = 1;
          contentLength = len;
          for (int i = 1; i <=len; i++)
          {
             String it = "item" + i;
             if (ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
             {
                String code = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                validateITISCodes(code);
                p3.add(code);
             }
             String text = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
             validateString(text);
             p3.add(text);
          }
       }
       else if (!work && !speed && !exitServ && !sign)//Advisory "0"
       {
          int len = ob.getJSONObject("content").getJSONArray("exitService").length();
          contentType = 0;
          contentLength = len;
          for (int i = 1; i <=len; i++)
          {
             String it = "item" + i;
             if (ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
             {
                String code = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                validateITISCodes(code);
                p3.add(code);
             }
             String text = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
             validateString(text);
             p3.add(text);
          }
       }
       else
       {
          int len = ob.getJSONObject("content").getJSONArray("genericSign").length();
          contentType = 2;
          contentLength = len;
          for (int i = 1; i <=len; i++)
          {
             String it = "item" + i;
             if (ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).isNull("ITIStext"))
             {
                String code = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITISCodes");
                validateITISCodes(code);
                p3.add(code);
             }
             String text = ob.getJSONObject("content").getJSONObject("advisory").getJSONObject(it).getString("ITIStext");
             validateString(text);
             p3.add(text);
          }
       }
       return p3;
    }

    public TravelerInformation getTravelerInformationObject(){
        return travelerInfo;
    }
    
    public void validateMessageCount(String msg){
       int myMsg = Integer.parseInt(msg);
       if (myMsg > 127 || myMsg < 0)
          throw new IllegalArgumentException("Invalid message count");
    }
    
    public void validateHeaderIndex(String count){
       int cnt = Integer.parseInt(count);
       if (cnt < 0 || cnt > 31)
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
