//package us.dot.its.jpo.ode.traveler;
//
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
//import java.nio.ByteBuffer;
//import us.dot.its.jpo.ode.j2735.J2735;
//import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
//
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Content;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrameList;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Regions;
//import us.dot.its.jpo.ode.j2735.dsrc.UniqueMSGID;
//import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerInfoType;
//import us.dot.its.jpo.ode.j2735.dsrc.DYear;
//import us.dot.its.jpo.ode.j2735.dsrc.MinuteOfTheYear;
//import us.dot.its.jpo.ode.j2735.dsrc.SignPrority;
//import us.dot.its.jpo.ode.j2735.dsrc.MinutesDuration;
//import us.dot.its.jpo.ode.j2735.itis.ITIScodesAndText;
//import us.dot.its.jpo.ode.j2735.dsrc.WorkZone;
//import us.dot.its.jpo.ode.j2735.dsrc.SpeedLimit;
//import us.dot.its.jpo.ode.j2735.dsrc.ExitService;
//import us.dot.its.jpo.ode.j2735.dsrc.GenericSignage;
//import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
//import us.dot.its.jpo.ode.j2735.dsrc.MUTCDCode;
//import us.dot.its.jpo.ode.j2735.dsrc.FurtherInfoID;
//import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
//import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
//import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
//import us.dot.its.jpo.ode.j2735.dsrc.Traveler.MsgId;
//import us.dot.its.jpo.ode.j2735.dsrc.GeographicalPath;
//import us.dot.its.jpo.ode.j2735.dsrc.GeographicalPath.Description;
//
//import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion.Area;
//import us.dot.its.jpo.ode.j2735.dsrc.ShapePointSet;
//import us.dot.its.jpo.ode.j2735.dsrc.LaneWidth;
//
//import java.text.ParseException;
//import com.oss.asn1.EncodeFailedException;
//import com.oss.asn1.EncodeNotSupportedException;
//
//
//
//
//
///**
// * Created by anthonychen on 2/16/17.
// */
//public class TravelerMessageBuilder {
//    public TravelerInformation travelerInfo;
//
//    private TravelerInformation buildTravelerInformation(TravelerInputData travInputData)
//            throws ParseException, EncodeFailedException, EncodeNotSupportedException {
//
//
//        travelerInfo = new TravelerInformation();
//        travelerInfo.setDataFrames(buildDataFrames(travInputData));
//        travelerInfo.setMsgCnt(new MsgCount(travelerInfo.getDataFrames().getSize()));
////        ByteBuffer buf = ByteBuffer.allocate(9).put((byte)0).putLong(travInputData.anchorPoint.packetID);
////        travelerInfo.setPacketID(new UniqueMSGID(buf.array()));
//        return travelerInfo;
//    }
//
//    private TravelerDataFrameList buildDataFrames(TravelerInputData travInputData) throws ParseException {
//        TravelerDataFrameList dataFrames = new TravelerDataFrameList();
//        TravelerDataFrame dataFrame = new TravelerDataFrame();
//
//        // -- Part I, Frame header
//        dataFrame.setSspTimRights(new SSPindex(travInputData.anchorPoint.sspTimRights));
//        dataFrame.setFrameType(TravelerInfoType.valueOf(travInputData.anchorPoint.infoType));
//        dataFrame.setMsgId(getMessageId(travInputData));
////        dataFrame.setStartYear(new DYear(getStartYear(travInputData)));
//        dataFrame.setStartTime(new MinuteOfTheYear(getStartTime(travInputData)));
//        dataFrame.setDuratonTime(new MinutesDuration(getDurationTime(travInputData)));
//        dataFrame.setPriority(new SignPrority(travInputData.anchorPoint.priority));
//
//        // -- Part II, Applicable Regions of Use
//        dataFrame.setSspLocationRights(new SSPindex(travInputData.anchorPoint.sspLocationRights));
//        dataFrame.setRegions(buildRegions(travInputData));
//
//        // -- Part III, Content
//        dataFrame.setSspMsgRights1(new SSPindex(travInputData.anchorPoint.sspTypeRights));		// allowed message types
//        dataFrame.setSspMsgRights2(new SSPindex(travInputData.anchorPoint.sspContentRights));	// allowed message content
//        dataFrame.setContent(buildContent(travInputData));
//
//        dataFrames.add(dataFrame);
//        return dataFrames;
//    }
//    private Content buildContent(TravelerInputData travInputData) {
//        String contentType = travInputData.anchorPoint.name;
//        String[] codes = travInputData.anchorPoint.content;
//        Content content = new Content();
//        if ("Advisory".equals(contentType)) {
//            content.setAdvisory(buildAdvisory(codes));
//        } else if ("Work Zone".equals(contentType)) {
//            content.setWorkZone(buildWorkZone(codes));
//        } else if ("Speed Limit".equals(contentType)) {
//            content.setSpeedLimit(buildSpeedLimit(codes));
//        } else if ("Exit Service".equals(contentType)) {
//            content.setExitService(buildExitService(codes));
//        } else {
//            content.setGenericSign(buildGenericSignage(codes));
//        }
//        return content;
//    }
//
//    private ITIScodesAndText buildAdvisory(String[] codes) {
//        ITIScodesAndText itisText = new ITIScodesAndText();
//        for (String code: codes) {
//            ITIScodesAndText.Sequence_ seq = new ITIScodesAndText.Sequence_();
//            ITIScodesAndText.Sequence_.Item item = new ITIScodesAndText.Sequence_.Item();
//            item.setItis(Long.parseLong(code));
//            seq.setItem(item);
//            itisText.add(seq);
//        }
//        return itisText;
//    }
//
//    private WorkZone buildWorkZone(String[] codes) {
//        WorkZone wz = new WorkZone();
//        for (String code: codes) {
//            WorkZone.Sequence_ seq = new WorkZone.Sequence_();
//            WorkZone.Sequence_.Item item = new WorkZone.Sequence_.Item();
//            item.setItis(Long.parseLong(code));
//            seq.setItem(item);
//            wz.add(seq);
//        }
//
//        return wz;
//    }
//
//    private SpeedLimit buildSpeedLimit(String[] codes) {
//        SpeedLimit sl = new SpeedLimit();
//        for (String code: codes) {
//            SpeedLimit.Sequence_ seq = new SpeedLimit.Sequence_();
//            SpeedLimit.Sequence_.Item item = new SpeedLimit.Sequence_.Item();
//            item.setItis(Long.parseLong(code));
//            seq.setItem(item);
//            sl.add(seq);
//        }
//        return sl;
//    }
//
//    private ExitService buildExitService(String[] codes) {
//        ExitService es = new ExitService();
//        for (String code: codes) {
//            ExitService.Sequence_ seq = new ExitService.Sequence_();
//            ExitService.Sequence_.Item item = new ExitService.Sequence_.Item();
//            item.setItis(Long.parseLong(code));
//            seq.setItem(item);
//            es.add(seq);
//        }
//        return es;
//    }
//
//    private GenericSignage buildGenericSignage(String[] codes) {
//        GenericSignage gs = new GenericSignage();
//        for (String code: codes) {
//            GenericSignage.Sequence_ seq = new GenericSignage.Sequence_();
//            GenericSignage.Sequence_.Item item = new GenericSignage.Sequence_.Item();
//            item.setItis(Long.parseLong(code));
//            seq.setItem(item);
//            gs.add(seq);
//        }
//        return gs;
//    }
//    private MsgId getMessageId(TravelerInputData travInputData) {
//        MsgId msgId = new MsgId();
//        // always using RoadSign for now
//        boolean isRoadSign = true;
//        if (isRoadSign) {
//            msgId.setChosenFlag(MsgId.roadSignID_chosen);
//            RoadSignID roadSignID = new RoadSignID();
//            roadSignID.setPosition(getAnchorPointPosition(travInputData.anchorPoint));
//            roadSignID.setViewAngle(getHeadingSlice(travInputData));
//            roadSignID.setMutcdCode(MUTCDCode.valueOf(travInputData.anchorPoint.mutcd));
//            msgId.setRoadSignID(roadSignID);
//        } else {
//            msgId.setChosenFlag(MsgId.furtherInfoID_chosen);
//            msgId.setFurtherInfoID(new FurtherInfoID(new byte[] { 0x00,0x00 }));
//        }
//        return msgId;
//    }
//    private static Position3D getAnchorPointPosition(AnchorPoint anchorPoint) {
//        assert(anchorPoint != null);
//        final int elev = anchorPoint.getReferenceElevation();
//        Position3D anchorPos = new Position3D(
//                new Latitude(J2735Util.convertGeoCoordinateToInt(anchorPoint.referenceLat)),
//                new Longitude(J2735Util.convertGeoCoordinateToInt(anchorPoint.referenceLon)));
//        anchorPos.setElevation(new Elevation(elev));
//        return anchorPos;
//    }
//    private Regions buildRegions(TravelerInputData travInputData) {
//        Regions regions = new Regions();
//        for (Region inputRegion: travInputData.regions) {
//            GeographicalPath geoPath = new GeographicalPath();
//            ValidRegion validRegion = new ValidRegion();
//            validRegion.setDirection(getHeadingSlice(travInputData));
//            if (inputRegion.extent != -1) {
//                validRegion.setExtent(Extent.valueOf(inputRegion.extent));
//            }
//            validRegion.setArea(buildArea(travInputData, inputRegion));
//            Description description = new Description();
//            description.setOldRegion(validRegion);
//            geoPath.setDescription(description);
//            regions.add(geoPath);
//        }
//        return regions;
//    }
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
//}
