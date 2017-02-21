package us.dot.its.jpo.ode.traveler;

import com.oss.asn1.PERUnalignedCoder;
import com.oss.util.HexTool;
import org.json.JSONObject;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.*;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Content;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;

import us.dot.its.jpo.ode.j2735.itis.ITIScodesAndText;

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
    public TravelerSerializer(String jsonInfo){
        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

        travelerInfo = new TravelerInformation();

        //Get fully populated TIMcontent string
        JSONObject obj = new JSONObject(jsonInfo);

        int frameList = obj.getJSONObject("timContent").getJSONArray("travelerDataFrame").length(); //Check the dataframe count

        //Populate pojo's for TIM
        String msgcnt = obj.getJSONObject("timContent").getString("msgcnt");
        validateMessageCount(msgcnt);
        travelerInfo.setMsgCnt(new MsgCount(Integer.parseInt(msgcnt)));

        //TODO the travelerDataFrame can be a list
        TravelerDataFrameList dataFrames = new TravelerDataFrameList();
        for (int z = 1; z <= frameList; z++)
        {
            TravelerDataFrame dataFrame = new TravelerDataFrame();
            String curFrame = "df" + z;
            //Populate pojo's for part1-header
            TravelerDataFrame part1 = buildTravelerMessagePart1(dataFrame, obj.getJSONObject("timContent").getJSONArray("travelerDataFrame").getJSONObject(0));

            //Populate pojo's for part2-region
            String index = obj.getJSONObject("timContent").getJSONArray("travelerDataFrame").getJSONObject(0).getJSONObject("region").getString("sspindex");
            validateHeaderIndex(index);
            part1.setSspLocationRights(new SSPindex(Short.valueOf(index)));
            //TODO populate part 2 information


            //Populate pojo's for part3-content
            TravelerDataFrame part3 = buildTravelerMessagePart3(part1, obj.getJSONObject("timContent").getJSONArray("travelerDataFrame").getJSONObject(0));

            //Populate pojo's for SNMP
            String target = obj.getJSONArray("RSUs").getJSONObject(0).getString("target");
            String userName = obj.getJSONArray("RSUs").getJSONObject(0).getString("username");
            String password = obj.getJSONArray("RSUs").getJSONObject(0).getString("pass");
            String retries = obj.getJSONArray("RSUs").getJSONObject(0).getString("retries");
            String timeout = obj.getJSONArray("RSUs").getJSONObject(0).getString("timeout");


            //Generate List of Data Frames
            dataFrames.add(part3);

        }

        // Adding data frames into TIM Object
        travelerInfo.setDataFrames(dataFrames);
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

//        MsgCount cnt = new MsgCount(Integer.parseInt(msgcnt));
//        travelerInfo.setMsgCnt(cnt);
    }

    private TravelerInformation.Regional setRegional(JSONObject dataFrame){
        return null;
    }
    private TravelerDataFrameList setDataFrames(JSONObject dataFrame){
        return null ;
    }

    private TravelerDataFrame buildTravelerMessagePart1(TravelerDataFrame dataFrame, JSONObject ob){
        ArrayList<String> p1 = new ArrayList<>();
        String sspindex = ob.getJSONObject("header").getString("sspindex");
        validateHeaderIndex(sspindex);
        p1.add(sspindex);
        dataFrame.setSspTimRights(new SSPindex(Integer.parseInt(sspindex)));

        String travelerInfoType = ob.getJSONObject("header").getJSONObject("msgId").getString("FurtherInfoID");
        validateInfoType(travelerInfoType);
        MsgId msgId = new MsgId();


        if (travelerInfoType.equals(null)) //Choice for msgid was roadsign
        {
            msgId.setChosenFlag(MsgId.roadSignID_chosen);

            p1.add(travelerInfoType);
            dataFrame.setFrameType(TravelerInfoType.valueOf(Long.parseLong(travelerInfoType)));
            JSONObject pos = ob.getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID");

            boolean notChecked = true;
            String latitude = pos.getJSONObject("position3D").getString("latitude");
            validateLat(latitude);
            p1.add(latitude);
            String longitude = pos.getJSONObject("position3D").getString("longitude");
            validateLong(longitude);
            p1.add(longitude);
            //String elevation = obj.getJSONObject("timContent").getJSONObject("header").getJSONObject("msgId").getJSONObject("RoadSignID").getJSONObject("position3D").getString("elevation");
            String headingSlice = pos.getString("HeadingSlice");


//            final int elev = anchorPoint.getReferenceElevation();
            Position3D anchorPos = new Position3D(
                    new Latitude(Short.parseShort(latitude)) ,
                    new Longitude(Short.parseShort(longitude)));

//            TODO Elevation Optional
//            anchorPos.setElevation(new Elevation(elev));

            if (headingSlice.equals("noHeading")) {//No bits were set
                notChecked = false;
            }
            else if (headingSlice.equals("allHeadings")) {//All bits were set
                notChecked = false;
            }
            if (notChecked){
                validateHeading(headingSlice);
                p1.add(headingSlice);
            }
            p1.add(headingSlice);

            RoadSignID roadSignID = new RoadSignID();
            roadSignID.setPosition(anchorPos);
            roadSignID.setViewAngle(new HeadingSlice(headingSlice.getBytes()));

            //            roadSignID.setMutcdCode(MUTCDCode.valueOf(travInputData.anchorPoint.mutcd));
            msgId.setRoadSignID(roadSignID);
        }
        else
        {
            msgId.setChosenFlag(MsgId.furtherInfoID_chosen);

            //Not Sure where this @ToDO
            msgId.setFurtherInfoID(new FurtherInfoID(new byte[] { 0x00,0x00 }));

            p1.add(travelerInfoType);
            dataFrame.setFrameType(TravelerInfoType.valueOf(Long.parseLong(travelerInfoType)));

            String minuteOfTheYear = ob.getJSONObject("header").getString("MinuteOfTheYear");
            validateMinuteYear(minuteOfTheYear);
            p1.add(minuteOfTheYear);
            dataFrame.setStartTime(new MinuteOfTheYear(Integer.parseInt(minuteOfTheYear)));


            String minuteDuration = ob.getJSONObject("header").getString("MinutesDuration");
            validateMinutesDuration(minuteDuration);
            p1.add(minuteDuration);
            dataFrame.setDuratonTime(new MinutesDuration(Integer.parseInt(minuteDuration)));


            String SignPriority = ob.getJSONObject("header").getString("SignPriority");
            validateSign(SignPriority);
            p1.add(SignPriority);
            dataFrame.setPriority(new SignPrority(Integer.parseInt(SignPriority)));

        }

        dataFrame.setMsgId(msgId);

        return dataFrame;
    }

    private TravelerDataFrame buildTravelerMessagePart3(TravelerDataFrame dataFrame, JSONObject ob){
        ArrayList<String> p3 = new ArrayList<>();

        String sspMsgRights1 = ob.getJSONObject("content").getString("sspMsgRights1");
        validateHeaderIndex(sspMsgRights1);
        p3.add(sspMsgRights1);
        System.out.println("msgrights: "+sspMsgRights1);
        dataFrame.setSspMsgRights1(new SSPindex(Short.parseShort(sspMsgRights1)));

        String sspMsgRights2 = ob.getJSONObject("content").getString("sspMsgRights2");
        validateHeaderIndex(sspMsgRights2);
        p3.add(sspMsgRights2);
        dataFrame.setSspMsgRights2(new SSPindex(Short.parseShort(sspMsgRights1)));
        JSONObject pos = ob.getJSONObject("content").getJSONObject("contentType");

        //Content choice
        int alen = pos.getJSONArray("advisory").length();
        int wlen = pos.getJSONArray("workZone").length();
        int silen = pos.getJSONArray("genericSign").length();
        int splen = pos.getJSONArray("speedLimit").length();
        int elen = pos.getJSONArray("exitService").length();
        boolean adv = false;
        boolean work = false;
        boolean sign = false;
        boolean speed = false;
        boolean exitServ = false;
        if (alen > 0){
           adv = true;
        }
        if (wlen > 0){
           work = true;
        }
        if (silen > 0){
           sign = true;
        }
        if (splen > 0){
           speed = true;
        }
        if (elen > 0){
           exitServ = true;
        }
        
        Content content = new Content();
        
        if (!adv && !work && !speed && !sign)//ExitService "4"
        {
            ExitService es = new ExitService();

            int len = pos.getJSONArray("exitService").length();
            for (int i = 0; i <len; i++)
            {
                ExitService.Sequence_ seq = new ExitService.Sequence_();
                ExitService.Sequence_.Item item = new ExitService.Sequence_.Item();

                if (pos.getJSONArray("exitService").getJSONObject(i).isNull("ITIStext"))//ITISCode
                {
                   System.out.println("pass: " + i + " code"); 
                   String code = pos.getJSONArray("exitService").getJSONObject(i).getString("ITISCodes");
                    validateITISCodes(code);
                    p3.add(code);
                    item.setItis(Long.parseLong(code));
                }
                else {
                   System.out.println("pass: " + i + " text"); 
                   String text = pos.getJSONArray("exitService").getJSONObject(i).getString("ITIStext");
                   System.out.println(text);
                   validateString(text);
                   p3.add(text);
                   item.setItis(Long.parseLong(text));
                }
                seq.setItem(item);
                es.add(seq);
            }
            content.setExitService(es);

        }
        else if (!adv && !work && !exitServ && !sign)//Speed "3"
        {
            SpeedLimit sl = new SpeedLimit();

            int len = pos.getJSONArray("speedLimit").length();
            for (int i = 1; i <=len; i++)
            {
                SpeedLimit.Sequence_ seq = new SpeedLimit.Sequence_();
                SpeedLimit.Sequence_.Item item = new SpeedLimit.Sequence_.Item();

                if (pos.getJSONArray("speedLimit").getJSONObject(i).isNull("ITIStext"))
                {
                    String code = pos.getJSONArray("speedLimit").getJSONObject(i).getString("ITISCodes");
                    validateITISCodes(code);
                    p3.add(code);
                    item.setItis(Long.parseLong(code));

                }

                // TODO not suer where text is set
                String text = pos.getJSONArray("speedLimit").getJSONObject(i).getString("ITIStext");
                validateString(text);
                p3.add(text);

                seq.setItem(item);
                sl.add(seq);
            }
            content.setSpeedLimit(sl);

        }
        else if (!adv && !speed && !exitServ && !sign)//work "1"
        {
            int len = pos.getJSONArray("workZone").length();
            WorkZone wz = new WorkZone();

            for (int i = 1; i <=len; i++)
            {
                WorkZone.Sequence_ seq = new WorkZone.Sequence_();
                WorkZone.Sequence_.Item item = new WorkZone.Sequence_.Item();

                if (pos.getJSONArray("workZone").getJSONObject(i).isNull("ITIStext"))
                {
                    String code = pos.getJSONArray("workZone").getJSONObject(i).getString("ITISCodes");
                    validateITISCodes(code);
                    p3.add(code);
                    item.setItis(Long.parseLong(code));

                }
                // TODO No sure where content is set
                String text = pos.getJSONArray("workZone").getJSONObject(i).getString("ITIStext");
                validateString(text);
                p3.add(text);

                seq.setItem(item);
                wz.add(seq);
            }
            content.setWorkZone(wz);

        }
        else if (!work && !speed && !exitServ && !sign)//Advisory "0"
        {
            int len = pos.getJSONArray("advisory").length();
            ITIScodesAndText itisText = new ITIScodesAndText();

            for (int i = 1; i <=len; i++)
            {
                ITIScodesAndText.Sequence_ seq = new ITIScodesAndText.Sequence_();
                ITIScodesAndText.Sequence_.Item item = new ITIScodesAndText.Sequence_.Item();

                if (pos.getJSONArray("advisory").getJSONObject(i).isNull("ITIStext"))
                {
                    String code = pos.getJSONArray("advisory").getJSONObject(i).getString("ITISCodes");
                    validateITISCodes(code);
                    p3.add(code);
                    item.setItis(Long.parseLong(code));
                }
                seq.setItem(item);

                // TODO Not Exaclty sure where this goes into the ITIS Object
                String text = pos.getJSONArray("advisory").getJSONObject(i).getString("ITIStext");
                validateString(text);
                p3.add(text);

                itisText.add(seq);
            }
            content.setAdvisory(itisText);
        }
        else//GenericSign
        {
           int len = pos.getJSONArray("genericSign").length();
           contentType = 2;
           for (int i = 1; i <=len; i++)
           {
              if (pos.getJSONArray("advisory").getJSONObject(i).isNull("ITIStext"))
              {
                 String code = pos.getJSONArray("advisory").getJSONObject(i).getString("ITISCodes");
                 validateITISCodes(code);
                 p3.add(code);
              }
              String text = pos.getJSONArray("advisory").getJSONObject(i).getString("ITIStext");
              validateString(text);
              p3.add(text);
           }
        }
//      TODO Generic Signs
//        content.setGenericSign(buildGenericSignage(codes));

        dataFrame.setContent(content);
        return dataFrame;
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
