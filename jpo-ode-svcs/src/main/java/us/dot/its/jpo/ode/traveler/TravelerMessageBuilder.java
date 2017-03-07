package us.dot.its.jpo.ode.traveler;


import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import org.apache.commons.codec.binary.Hex;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.*;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Content;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Regions;
import us.dot.its.jpo.ode.j2735.itis.ITIScodesAndText;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssPosition3D;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by anthonychen on 2/16/17.
 */
public class TravelerMessageBuilder {
    public static TravelerInformation travelerInfo;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm a", Locale.ENGLISH);
    private static final int MAX_MINUTES_DURATION = 32000; // DSRC spec


    public TravelerInformation buildTravelerInformation(TravelerInputData travInputData)
            throws ParseException, EncodeFailedException, EncodeNotSupportedException {

        travelerInfo = new TravelerInformation();
        travelerInfo.setMsgCnt(new MsgCount(travInputData.MsgCount));
        ByteBuffer buf = ByteBuffer.allocate(9).put((byte)0).putLong(travInputData.UniqueMSGID);
        travelerInfo.setPacketID(new UniqueMSGID(buf.array()));
        travelerInfo.setUrlB(new URL_Base(travInputData.urlB));
        travelerInfo.setDataFrames(buildDataFrames(travInputData));

        return travelerInfo;
    }

    private TravelerDataFrameList buildDataFrames(TravelerInputData travInputData) throws ParseException {
        TravelerDataFrameList dataFrames = new TravelerDataFrameList();

        int len = travInputData.dataframes.length;
        for (int i =0 ; i <len; i++)
        {
            TravelerInputData.DataFrame inputDataFrame = travInputData.dataframes[i];
            TravelerDataFrame dataFrame = new TravelerDataFrame();

            // Part I, header
            validateHeaderIndex(inputDataFrame.sspTimRights);
            dataFrame.setSspTimRights(new SSPindex(inputDataFrame.sspTimRights));
            
            validateInfoType(inputDataFrame.frameType);
            dataFrame.setFrameType(TravelerInfoType.valueOf(inputDataFrame.frameType));
            
            dataFrame.setMsgId(getMessageId(inputDataFrame));
            dataFrame.setStartYear(new DYear(getStartYear(inputDataFrame)));
            dataFrame.setStartTime(new MinuteOfTheYear(getStartTime(inputDataFrame)));
            dataFrame.setDuratonTime(new MinutesDuration(inputDataFrame.durationTime));
            validateSign(inputDataFrame.priority);
            dataFrame.setPriority(new SignPrority(inputDataFrame.priority));

            // -- Part II, Applicable Regions of Use
            validateHeaderIndex(inputDataFrame.sspLocationRights);
            dataFrame.setSspLocationRights(new SSPindex(inputDataFrame.sspLocationRights));
//            dataFrame.setRegions(buildRegions(inputDataFrame.regions));
            Regions regions = new Regions();
            regions.add(new GeographicalPath());
            dataFrame.setRegions(regions);

            // -- Part III, Content
            validateHeaderIndex(inputDataFrame.sspMsgTypes);
            dataFrame.setSspMsgRights1(new SSPindex(inputDataFrame.sspMsgTypes));		// allowed message types
            validateHeaderIndex(inputDataFrame.sspMsgContent);
            dataFrame.setSspMsgRights2(new SSPindex(inputDataFrame.sspMsgContent));	    // allowed message content
            dataFrame.setContent(buildContent(inputDataFrame));
            dataFrame.setUrl(new URL_Short(inputDataFrame.url));

            dataFrames.add(dataFrame);
        }
        return dataFrames;
    }
    
    public static String getHexTravelerInformation(TravelerInformation ti) throws EncodeFailedException, EncodeNotSupportedException {
       Coder coder = J2735.getPERUnalignedCoder();
       ByteArrayOutputStream sink = new ByteArrayOutputStream();
       coder.encode(ti, sink);
       byte[] bytes = sink.toByteArray();
       return Hex.encodeHexString(bytes);
   }
    
    private Content buildContent(TravelerInputData.DataFrame inputDataFrame) {
        String contentType = inputDataFrame.content;
        String[] codes = inputDataFrame.items;
        Content content = new Content();
        if ("Advisory".equals(contentType)) {
            content.setAdvisory(buildAdvisory(codes));
        } else if ("Work Zone".equals(contentType)) {
            content.setWorkZone(buildWorkZone(codes));
        } else if ("Speed Limit".equals(contentType)) {
            content.setSpeedLimit(buildSpeedLimit(codes));
        } else if ("Exit Service".equals(contentType)) {
            content.setExitService(buildExitService(codes));
        } else {
            content.setGenericSign(buildGenericSignage(codes));
        }
        return content;
    }

    private ITIScodesAndText buildAdvisory(String[] codes) {
        ITIScodesAndText itisText = new ITIScodesAndText();
        for (String code: codes) {
            validateITISCodes(code);
            ITIScodesAndText.Sequence_ seq = new ITIScodesAndText.Sequence_();
            ITIScodesAndText.Sequence_.Item item = new ITIScodesAndText.Sequence_.Item();
            item.setItis(Long.parseLong(code));
            seq.setItem(item);
            itisText.add(seq);
        }
        return itisText;
    }

    private WorkZone buildWorkZone(String[] codes) {
        WorkZone wz = new WorkZone();
        for (String code: codes) {
            WorkZone.Sequence_ seq = new WorkZone.Sequence_();
            WorkZone.Sequence_.Item item = new WorkZone.Sequence_.Item();
            item.setItis(Long.parseLong(code));
            seq.setItem(item);
            wz.add(seq);
        }
        return wz;
    }

    private SpeedLimit buildSpeedLimit(String[] codes) {
        SpeedLimit sl = new SpeedLimit();
        for (String code: codes) {
            SpeedLimit.Sequence_ seq = new SpeedLimit.Sequence_();
            SpeedLimit.Sequence_.Item item = new SpeedLimit.Sequence_.Item();
            item.setItis(Long.parseLong(code));
            seq.setItem(item);
            sl.add(seq);
        }
        return sl;
    }

    private ExitService buildExitService(String[] codes) {
        ExitService es = new ExitService();
        for (String code: codes) {
            ExitService.Sequence_ seq = new ExitService.Sequence_();
            ExitService.Sequence_.Item item = new ExitService.Sequence_.Item();
            item.setItis(Long.parseLong(code));
            seq.setItem(item);
            es.add(seq);
        }
        return es;
    }

    private GenericSignage buildGenericSignage(String[] codes) {
        GenericSignage gs = new GenericSignage();
        for (String code: codes) {
            GenericSignage.Sequence_ seq = new GenericSignage.Sequence_();
            GenericSignage.Sequence_.Item item = new GenericSignage.Sequence_.Item();
            item.setItis(Long.parseLong(code));
            seq.setItem(item);
            gs.add(seq);
        }
        return gs;
    }

    private MsgId getMessageId(TravelerInputData.DataFrame dataFrame) {
        MsgId msgId = new MsgId();

        if ("RoadSignID".equals(dataFrame.msgID)) {
            msgId.setChosenFlag(MsgId.roadSignID_chosen);
            RoadSignID roadSignID = new RoadSignID();
            validateLong(dataFrame.longitude);
            validateLat(dataFrame.latitude);
            validateElevation(dataFrame.elevation);
            roadSignID.setPosition(getPosition3D(dataFrame.latitude, dataFrame.longitude, dataFrame.elevation));
            validateHeading(dataFrame.viewAngle);
            roadSignID.setViewAngle(getHeadingSlice(dataFrame.viewAngle));
            validateMUTDCode(dataFrame.mutcd);
            roadSignID.setMutcdCode(MUTCDCode.valueOf(dataFrame.mutcd));
//            ByteBuffer buf = ByteBuffer.allocate(2).put((byte)0).putLong(dataFrame.crc);
//            roadSignID.setCrc(new MsgCRC(new byte[] { 0xC0,0x2F })); //Causing error while encoding
            msgId.setRoadSignID(roadSignID);
        } else {
            msgId.setChosenFlag(MsgId.furtherInfoID_chosen);
            msgId.setFurtherInfoID(new FurtherInfoID(new byte[] { 0x00,0x00 })); //TODO check this for actual value
        }
        return msgId;
    }

    private Position3D getPosition3D(long latitude, long longitude, long elevation) {
        J2735Position3D position = new J2735Position3D(latitude, longitude, elevation);
        return OssPosition3D.position3D(position);

    }

    private HeadingSlice getHeadingSlice(String heading) {
        if (heading == null || heading.length() == 0) {
            return new HeadingSlice(new byte[]{0x00, 0x00});
        } else {
            int[] nums = new int[heading.length()];
            for (int i = 0; i < heading.length(); i++) {
                nums[i] = Integer.parseInt(heading.valueOf(i));
            }
            short result = 0;
            for (int i = 0; i < nums.length; i++) {
                result |= nums[i];
            }
            return new HeadingSlice(ByteBuffer.allocate(2).putShort(result).array());
        }
    }



//    private HeadingSlice getHeadingSlice(TravelerInputData.DataFrame dataFrame) {
//        String[] heading = dataFrame.heading;
//        if (heading == null || heading.length == 0) {
//            return new HeadingSlice(new byte[] { 0x00,0x00 });
//        } else {
//            int[] nums = new int[heading.length];
//            for (int i=0; i<heading.length; i++) {
//                nums[i] = Integer.parseInt(heading[i], 16);
//            }
//            short result = 0;
//            for (int i=0; i<nums.length; i++) {
//                result|= nums[i];
//            }
//            return new HeadingSlice(ByteBuffer.allocate(2).putShort(result).array());
//        }
//    }
//    private static Position3D getAnchorPointPosition(TravelerInputData.DataFrame anchorPoint) {
//        assert(anchorPoint != null);
//        final int elev = anchorPoint.getReferenceElevation();
//        Position3D anchorPos = new Position3D(
//                new Latitude(J2735Util.convertGeoCoordinateToInt(anchorPoint.referenceLat)),
//                new Longitude(J2735Util.convertGeoCoordinateToInt(anchorPoint.referenceLon)));
//        anchorPos.setElevation(new Elevation(elev));
//        return anchorPos;
//    }
//
//    private static Position3D build3DPosition(TravelerInputData.DataFrame anchorPoint) {
//        assert(anchorPoint != null);
//        final int elev = anchorPoint.getReferenceElevation();
//        Position3D anchorPos = new Position3D(
//                new Latitude(J2735Util.convertGeoCoordinateToInt(anchorPoint.referenceLat)),
//                new Longitude(J2735Util.convertGeoCoordinateToInt(anchorPoint.referenceLon)));
//        anchorPos.setElevation(new Elevation(elev));
//        return anchorPos;
//    }
//
    private Regions buildRegions(TravelerInputData.DataFrame.Region[] inputRegions) {
        Regions regions = new Regions();
        for (TravelerInputData.DataFrame.Region inputRegion: inputRegions) {
            GeographicalPath geoPath = new GeographicalPath();
            geoPath.setName(new DescriptiveName(inputRegion.name));
            geoPath.setId(new RoadSegmentReferenceID(new RoadRegulatorID(inputRegion.regulatorID), new RoadSegmentID(inputRegion.segmentID)));
            geoPath.setAnchor(getPosition3D(inputRegion.anchor_lat, inputRegion.anchor_long, inputRegion.anchor_elevation));
            geoPath.setLaneWidth(new LaneWidth(inputRegion.laneWidth));
            geoPath.setDirectionality(new DirectionOfUse(inputRegion.directionality));
            geoPath.setClosedPath(Boolean.valueOf(inputRegion.closedPath));
            geoPath.setDirection(getHeadingSlice(inputRegion.direction));
            
            /*if (inputRegion.extent != -1) {
                validRegion.setExtent(Extent.valueOf(inputRegion.extent));
            }*/
            if ("path".equals(inputRegion.description)){

                OffsetSystem offsetSystem = new OffsetSystem();
                offsetSystem.setScale(new Zoom(inputRegion.path.scale));
                buildNodeList(inputRegion.path.nodes);
                for (TravelerInputData.DataFrame.Region.Path.Node node: inputRegion.path.nodes){

                }
//                NodeListXY nodeList = new NodeListXY(new NodeSetXY(new NodeXY[]));

            }
            else if ("geometry".equals(inputRegion.description)){
               
            }
            else { //oldRegion
               ValidRegion validRegion = new ValidRegion();
            }
//            validRegion.setArea(buildArea(travInputData, inputRegion));
//            Description description = new Description();
//            description.setOldRegion(validRegion);
//            geoPath.setDescription(description);
            regions.add(geoPath);
        }
        return regions;
    }

    private NodeListXY buildNodeList(TravelerInputData.DataFrame.Region.Path.Node[] inputNodes) {
        NodeListXY nodeList = new NodeListXY();
        NodeSetXY nodes = new NodeSetXY();
        for (int i=0; i < inputNodes.length; i++) {
            TravelerInputData.DataFrame.Region.Path.Node point = inputNodes[i];

//            GeoPoint nextPoint = new GeoPoint(laneNode.nodeLat, laneNode.nodeLong);
            NodeXY node = new NodeXY();
            NodeOffsetPointXY nodePoint = new NodeOffsetPointXY();

            if ("node-XY1" == point.delta){
                Node_XY_20b xy = new Node_XY_20b(new Offset_B10(point.x), new Offset_B10(point.y));
                nodePoint.setNode_XY1(xy);
            }

            if (point.delta == "node-XY2"){
                Node_XY_22b xy = new Node_XY_22b(new Offset_B11(point.x), new Offset_B11(point.y));
                nodePoint.setNode_XY2(xy);
            }

            if (point.delta == "node-XY3"){
                Node_XY_24b xy = new Node_XY_24b(new Offset_B12(point.x), new Offset_B12(point.y));
                nodePoint.setNode_XY3(xy);
            }

            if (point.delta == "node-XY4"){
                Node_XY_26b xy = new Node_XY_26b(new Offset_B13(point.x), new Offset_B13(point.y));
                nodePoint.setNode_XY4(xy);
            }

            if (point.delta == "node-XY5"){
                Node_XY_28b xy = new Node_XY_28b(new Offset_B14(point.x), new Offset_B14(point.y));
                nodePoint.setNode_XY5(xy);
            }

            if (point.delta == "node-XY6"){
                Node_XY_32b xy = new Node_XY_32b(new Offset_B16(point.x), new Offset_B16(point.y));
                nodePoint.setNode_XY6(xy);
            }

            if (point.delta == "node-LatLon") {
                Node_LLmD_64b nodeLatLong= new Node_LLmD_64b(new Longitude(point.nodeLat), new Latitude(point.nodeLong));
                nodePoint.setNode_LatLon(nodeLatLong);
            }

            node.setDelta(nodePoint);

//            NodeAttributeSetXY attributes = new NodeAttributeSetXY();
//            boolean hasAttributes = false;
//            if ( laneNode.laneWidth != 0 ) {
//                attributes.setDWidth(new Offset_B10(laneNode.laneWidth));
//                hasAttributes = true;
//            }
//            short elevDelta = IntersectionSituationDataBuilder.getElevationDelta(laneNode.nodeElevation, curElevation);
//            if ( elevDelta != 0 ) {
//                curElevation = laneNode.nodeElevation;
//                attributes.setDElevation(new Offset_B10(elevDelta));
//                hasAttributes = true;
//            }
//            if ( hasAttributes )
//                node.setAttributes(attributes);

            nodes.add(node);
        }
        nodeList.setNodes(nodes);
        return nodeList;
    }
//    private Area buildArea(TravelerInputData travInputData, Region inputRegion) {
//        Area area = new Area();
//        Position3D anchorPos = getAnchorPointPosition(travInputData.anchorPoint);
//        if (inputRegion.regionType.equals("lane")) {
//            ShapePointSet sps = new ShapePointSet();
//            sps.setAnchor(anchorPos);
//            sps.setLaneWidth(new LaneWidth(travInputData.anchorPoint.masterLaneWidth));
//            sps.setDirectionality(DirectionOfUse.valueOf(travInputData.anchorPoint.direction));
//            sps.setNodeList(buildNodeList(inputRegion.laneNodes, travInputData.anchorPoint.referenceElevation));
//            area.setShapePointSet(sps);
//        } else if (inputRegion.regionType.equals("region")) {
//            RegionPointSet rps = new RegionPointSet();
//            rps.setAnchor(anchorPos);
//            RegionList regionList = new RegionList();
//            GeoPoint refPoint = inputRegion.refPoint;
//            for (int i=0; i < inputRegion.laneNodes.length; i++) {
//                GeoPoint nextPoint = new GeoPoint(inputRegion.laneNodes[i].nodeLat, inputRegion.laneNodes[i].nodeLong);
//                regionList.add(buildRegionOffset(refPoint, nextPoint));
//                refPoint = nextPoint;
//            }
//            rps.setNodeList(regionList);
//            area.setRegionPointSet(rps);
//        }
//        return area;
//    }

    private long getStartTime(TravelerInputData.DataFrame dataFrame) throws ParseException {
        Date startDate = sdf.parse(dataFrame.startTime);
        String startOfYearTime = "01/01/" + getStartYear(dataFrame) + " 12:00 AM";
        Date startOfYearDate = sdf.parse(startOfYearTime);
        long minutes = ((startDate.getTime() - startOfYearDate.getTime()) / 60000);
        return minutes;
    }

   private int getStartYear(TravelerInputData.DataFrame dataFrame) throws ParseException {
        Date startDate = sdf.parse(dataFrame.startTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        return cal.get(Calendar.YEAR);
    }

//    private int getDurationTime(TravelerInputData.DataFrame dataFrame) throws ParseException {
//        Date startDate = sdf.parse(dataFrame.startTime);
//        Date endDate = sdf.parse(dataFrame.endTime);
//
//        long diff = endDate.getTime() - startDate.getTime();
//        int durationInMinutes = (int) diff / 1000 / 60;
//        if (durationInMinutes > MAX_MINUTES_DURATION)
//            durationInMinutes = MAX_MINUTES_DURATION;
//        return durationInMinutes;
//    }

//    private RegionOffsets buildRegionOffset(GeoPoint refPoint, GeoPoint nextPoint) {
//        short xOffset = nextPoint.getLonOffsetInMeters(refPoint);
//        short yOffset = nextPoint.getLatOffsetInMeters(refPoint);
//        RegionOffsets offsets = new RegionOffsets(new OffsetLL_B16(xOffset), new OffsetLL_B16(yOffset));
//        return offsets;
//    }
    
    public static void validateMessageCount(String msg){
       int myMsg = Integer.parseInt(msg);
       if (myMsg > 127 || myMsg < 0)
           throw new IllegalArgumentException("Invalid message count");
   }

   public static void validateHeaderIndex(short count){
       if (count < 0 || count > 31)
           throw new IllegalArgumentException("Invalid header sspIndex");
   }

   public static void validateInfoType(int num){
       if (num < 0)
           throw new IllegalArgumentException("Invalid enumeration");
   }

   public static void validateLat(long lat){
       if (lat < -900000000 || lat > 900000001)
           throw new IllegalArgumentException("Invalid Latitude");
   }

   public static void validateLong(long lonng){
       if (lonng < -1799999999 || lonng > 1800000001)
           throw new IllegalArgumentException("Invalid Longitude");
   }
   
   public static void validateElevation(long elev) {
      if (elev > 61439 || elev < -4096)
         throw new IllegalArgumentException("Invalid Elevation");
   }

   public static void validateHeading(String head){
       byte[] heads = head.getBytes();
       if (heads.length != 16)
       {
          throw new IllegalArgumentException("Invalid BitString");
       }
   }
   
   public static void validateMUTDCode(int mutc){
      if(mutc > 6 || mutc < 0)
         throw new IllegalArgumentException("Invalid Enumeration");
   }

   public static void validateMinuteYear(String min){
       int myMin = Integer.parseInt(min);
       if (myMin < 0 || myMin > 527040)
           throw new IllegalArgumentException("Invalid Minute of the Year");
   }

   public static void validateMinutesDuration(String dur){
       int myDur = Integer.parseInt(dur);
       if (myDur < 0 || myDur > 32000)
           throw new IllegalArgumentException("Invalid Duration");
   }

   public static void validateSign(int sign){
       if (sign < 0 || sign > 7)
           throw new IllegalArgumentException("Invalid Sign Priority");
   }

   public static void validateITISCodes(String code){
       int myCode = Integer.parseInt(code);
       if (myCode < 0 || myCode > 65535)
           throw new IllegalArgumentException("Invalid ITIS code");
   }

   public static void validateString(String str){
       if (str.isEmpty())
           throw new IllegalArgumentException("Invalid Empty String");
   }

}
