package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.ZonedDateTime;

import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.Angle;
import us.dot.its.jpo.ode.j2735.dsrc.Circle;
import us.dot.its.jpo.ode.j2735.dsrc.ComputedLane;
import us.dot.its.jpo.ode.j2735.dsrc.ComputedLane.OffsetXaxis;
import us.dot.its.jpo.ode.j2735.dsrc.ComputedLane.OffsetYaxis;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.DeltaAngle;
import us.dot.its.jpo.ode.j2735.dsrc.DescriptiveName;
import us.dot.its.jpo.ode.j2735.dsrc.DirectionOfUse;
import us.dot.its.jpo.ode.j2735.dsrc.DistanceUnits;
import us.dot.its.jpo.ode.j2735.dsrc.ExitService;
import us.dot.its.jpo.ode.j2735.dsrc.Extent;
import us.dot.its.jpo.ode.j2735.dsrc.FurtherInfoID;
import us.dot.its.jpo.ode.j2735.dsrc.GenericSignage;
import us.dot.its.jpo.ode.j2735.dsrc.GeographicalPath;
import us.dot.its.jpo.ode.j2735.dsrc.GeographicalPath.Description;
import us.dot.its.jpo.ode.j2735.dsrc.GeometricProjection;
import us.dot.its.jpo.ode.j2735.dsrc.HeadingSlice;
import us.dot.its.jpo.ode.j2735.dsrc.LaneDataAttribute;
import us.dot.its.jpo.ode.j2735.dsrc.LaneDataAttributeList;
import us.dot.its.jpo.ode.j2735.dsrc.LaneID;
import us.dot.its.jpo.ode.j2735.dsrc.LaneWidth;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.MUTCDCode;
import us.dot.its.jpo.ode.j2735.dsrc.MergeDivergeNodeAngle;
import us.dot.its.jpo.ode.j2735.dsrc.MinuteOfTheYear;
import us.dot.its.jpo.ode.j2735.dsrc.MinutesDuration;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.NodeAttributeLL;
import us.dot.its.jpo.ode.j2735.dsrc.NodeAttributeLLList;
import us.dot.its.jpo.ode.j2735.dsrc.NodeAttributeSetLL;
import us.dot.its.jpo.ode.j2735.dsrc.NodeAttributeSetXY;
import us.dot.its.jpo.ode.j2735.dsrc.NodeAttributeXY;
import us.dot.its.jpo.ode.j2735.dsrc.NodeAttributeXYList;
import us.dot.its.jpo.ode.j2735.dsrc.NodeLL;
import us.dot.its.jpo.ode.j2735.dsrc.NodeListLL;
import us.dot.its.jpo.ode.j2735.dsrc.NodeListXY;
import us.dot.its.jpo.ode.j2735.dsrc.NodeOffsetPointLL;
import us.dot.its.jpo.ode.j2735.dsrc.NodeOffsetPointXY;
import us.dot.its.jpo.ode.j2735.dsrc.NodeSetLL;
import us.dot.its.jpo.ode.j2735.dsrc.NodeSetXY;
import us.dot.its.jpo.ode.j2735.dsrc.NodeXY;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LL_24B;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LL_28B;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LL_32B;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LL_36B;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LL_44B;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LL_48B;
import us.dot.its.jpo.ode.j2735.dsrc.Node_LLmD_64b;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_20b;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_22b;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_24b;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_26b;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_28b;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_32b;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B12;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B14;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B16;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B18;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B22;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetLL_B24;
import us.dot.its.jpo.ode.j2735.dsrc.OffsetSystem;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B10;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B11;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B13;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B14;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B16;
import us.dot.its.jpo.ode.j2735.dsrc.Radius_B12;
import us.dot.its.jpo.ode.j2735.dsrc.RegionList;
import us.dot.its.jpo.ode.j2735.dsrc.RegionOffsets;
import us.dot.its.jpo.ode.j2735.dsrc.RegionPointSet;
import us.dot.its.jpo.ode.j2735.dsrc.RegulatorySpeedLimit;
import us.dot.its.jpo.ode.j2735.dsrc.RoadRegulatorID;
import us.dot.its.jpo.ode.j2735.dsrc.RoadSegmentID;
import us.dot.its.jpo.ode.j2735.dsrc.RoadSegmentReferenceID;
import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
import us.dot.its.jpo.ode.j2735.dsrc.RoadwayCrownAngle;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.Scale_B12;
import us.dot.its.jpo.ode.j2735.dsrc.SegmentAttributeLL;
import us.dot.its.jpo.ode.j2735.dsrc.SegmentAttributeLLList;
import us.dot.its.jpo.ode.j2735.dsrc.SegmentAttributeXY;
import us.dot.its.jpo.ode.j2735.dsrc.SegmentAttributeXYList;
import us.dot.its.jpo.ode.j2735.dsrc.ShapePointSet;
import us.dot.its.jpo.ode.j2735.dsrc.SignPrority;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedLimit;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedLimitList;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedLimitType;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Content;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Regions;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrameList;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInfoType;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.j2735.dsrc.URL_Base;
import us.dot.its.jpo.ode.j2735.dsrc.URL_Short;
import us.dot.its.jpo.ode.j2735.dsrc.UniqueMSGID;
import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion;
import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion.Area;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.dsrc.WorkZone;
import us.dot.its.jpo.ode.j2735.dsrc.Zoom;
import us.dot.its.jpo.ode.j2735.itis.ITIScodesAndText;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OssTravelerMessageBuilder {
   public TravelerInformation travelerInfo;

   public TravelerInformation buildTravelerInformation(J2735TravelerInputData travInputData)
         throws ParseException, EncodeFailedException, EncodeNotSupportedException {

      travelerInfo = new TravelerInformation();
      validateMessageCount(travInputData.getTim().getMsgCnt());
      travelerInfo.setMsgCnt(new MsgCount(travInputData.getTim().getMsgCnt()));
      travelerInfo.setTimeStamp(new MinuteOfTheYear(getMinuteOfTheYear(travInputData.getTim().getTimeStamp())));
      ByteBuffer buf = ByteBuffer.allocate(9).put((byte) 0).putLong(travInputData.getTim().getPacketID());
      travelerInfo.setPacketID(new UniqueMSGID(buf.array()));
      validateURL(travInputData.getTim().getUrlB());
      travelerInfo.setUrlB(new URL_Base(travInputData.getTim().getUrlB()));
      travelerInfo.setDataFrames(buildDataFrames(travInputData));

      return travelerInfo;
   }

   private TravelerDataFrameList buildDataFrames(J2735TravelerInputData travInputData) throws ParseException {
      TravelerDataFrameList dataFrames = new TravelerDataFrameList();

      validateFrameCount(travInputData.getTim().getDataframes().length);
      int len = travInputData.getTim().getDataframes().length;
      for (int i = 0; i < len; i++) {
         J2735TravelerInputData.DataFrame inputDataFrame = travInputData.getTim().getDataframes()[i];
         TravelerDataFrame dataFrame = new TravelerDataFrame();

         // Part I, header
         validateHeaderIndex(inputDataFrame.getsspTimRights());
         dataFrame.setSspTimRights(new SSPindex(inputDataFrame.getsspTimRights()));
         validateInfoType(inputDataFrame.getFrameType());
         dataFrame.setFrameType(TravelerInfoType.valueOf(inputDataFrame.getFrameType()));
         dataFrame.setMsgId(getMessageId(inputDataFrame));
         dataFrame.setStartYear(new DYear(DateTimeUtils.isoDateTime(inputDataFrame.getStartDateTime()).getYear()));
         dataFrame.setStartTime(new MinuteOfTheYear(getMinuteOfTheYear(inputDataFrame.getStartDateTime())));
         validateMinutesDuration(inputDataFrame.getDurationTime());
         dataFrame.setDuratonTime(new MinutesDuration(inputDataFrame.getDurationTime()));
         validateSign(inputDataFrame.getPriority());
         dataFrame.setPriority(new SignPrority(inputDataFrame.getPriority()));

         // -- Part II, Applicable Regions of Use
         validateHeaderIndex(inputDataFrame.getsspLocationRights());
         dataFrame.setSspLocationRights(new SSPindex(inputDataFrame.getsspLocationRights()));
         dataFrame.setRegions(buildRegions(inputDataFrame.getRegions()));

         // -- Part III, Content
         validateHeaderIndex(inputDataFrame.getsspMsgTypes());
         dataFrame.setSspMsgRights1(new SSPindex(inputDataFrame.getsspMsgTypes())); // allowed
                                                                               // message
                                                                               // types
         validateHeaderIndex(inputDataFrame.getsspMsgContent());
         dataFrame.setSspMsgRights2(new SSPindex(inputDataFrame.getsspMsgContent())); // allowed
                                                                                 // message
                                                                                 // content
         dataFrame.setContent(buildContent(inputDataFrame));
         validateURLShort(inputDataFrame.getUrl());
         dataFrame.setUrl(new URL_Short(inputDataFrame.getUrl()));
         dataFrames.add(dataFrame);
      }
      return dataFrames;
   }

   public String getHexTravelerInformation() throws EncodeFailedException, EncodeNotSupportedException {
      Coder coder = J2735.getPERUnalignedCoder();
      ByteArrayOutputStream sink = new ByteArrayOutputStream();
      coder.encode(travelerInfo, sink);
      byte[] bytes = sink.toByteArray();
      return CodecUtils.toHex(bytes);
   }

   public Content buildContent(J2735TravelerInputData.DataFrame inputDataFrame) {
      String contentType = inputDataFrame.getContent();
      String[] codes = inputDataFrame.getItems();
      Content content = new Content();
      if ("Advisory".equals(contentType)) {
         content.setAdvisory(buildAdvisory(codes));
      } else if ("Work Zone".equals(contentType)) {
         content.setWorkZone(buildWorkZone(codes));
      } else if ("Speed Limit".equals(contentType)) {
         content.setSpeedLimit(buildSpeedLimit(codes));
      } else if ("Exit Service".equals(contentType)) {
         content.setExitService(buildExitService(codes));
      } else if ("Generic Signage".equals(contentType)) {
         content.setGenericSign(buildGenericSignage(codes));
      }
      return content;
   }

   public ITIScodesAndText buildAdvisory(String[] codes) {
      ITIScodesAndText itisText = new ITIScodesAndText();
      for (String code : codes) {
         validateITISCodes(code);
         ITIScodesAndText.Sequence_ seq = new ITIScodesAndText.Sequence_();
         ITIScodesAndText.Sequence_.Item item = new ITIScodesAndText.Sequence_.Item();
         item.setItis(Long.parseLong(code));
         seq.setItem(item);
         itisText.add(seq);
      }
      return itisText;
   }

   public WorkZone buildWorkZone(String[] codes) {
      WorkZone wz = new WorkZone();
      for (String code : codes) {
         validateContentCodes(code);
         WorkZone.Sequence_ seq = new WorkZone.Sequence_();
         WorkZone.Sequence_.Item item = new WorkZone.Sequence_.Item();
         item.setItis(Long.parseLong(code));
         seq.setItem(item);
         wz.add(seq);
      }
      return wz;
   }

   public SpeedLimit buildSpeedLimit(String[] codes) {
      SpeedLimit sl = new SpeedLimit();
      for (String code : codes) {
         validateContentCodes(code);
         SpeedLimit.Sequence_ seq = new SpeedLimit.Sequence_();
         SpeedLimit.Sequence_.Item item = new SpeedLimit.Sequence_.Item();
         item.setItis(Long.parseLong(code));
         seq.setItem(item);
         sl.add(seq);
      }
      return sl;
   }

   public ExitService buildExitService(String[] codes) {
      ExitService es = new ExitService();
      for (String code : codes) {
         validateContentCodes(code);
         ExitService.Sequence_ seq = new ExitService.Sequence_();
         ExitService.Sequence_.Item item = new ExitService.Sequence_.Item();
         item.setItis(Long.parseLong(code));
         seq.setItem(item);
         es.add(seq);
      }
      return es;
   }

   public GenericSignage buildGenericSignage(String[] codes) {
      GenericSignage gs = new GenericSignage();
      for (String code : codes) {
         validateContentCodes(code);
         GenericSignage.Sequence_ seq = new GenericSignage.Sequence_();
         GenericSignage.Sequence_.Item item = new GenericSignage.Sequence_.Item();
         item.setItis(Long.parseLong(code));
         seq.setItem(item);
         gs.add(seq);
      }
      return gs;
   }

   private MsgId getMessageId(J2735TravelerInputData.DataFrame dataFrame) {
      MsgId msgId = new MsgId();
      validateMessageID(dataFrame.getMsgID());

      if ("RoadSignID".equals(dataFrame.getMsgID())) {
         msgId.setChosenFlag(MsgId.roadSignID_chosen);
         RoadSignID roadSignID = new RoadSignID();
         validatePosition(dataFrame.getPosition());
         roadSignID.setPosition(OssPosition3D.position3D(dataFrame.getPosition()));
         validateHeading(dataFrame.getViewAngle());
         roadSignID.setViewAngle(getHeadingSlice(dataFrame.getViewAngle()));
         validateMUTCDCode(dataFrame.getMutcd());
         roadSignID.setMutcdCode(MUTCDCode.valueOf(dataFrame.getMutcd()));
         // roadSignID.setCrc(new MsgCRC(new byte[] { 0xC0,0x2F })); //Causing
         // error while encoding
         msgId.setRoadSignID(roadSignID);
      } else {
         msgId.setChosenFlag(MsgId.furtherInfoID_chosen);
         msgId.setFurtherInfoID(new FurtherInfoID(new byte[] { 0x00, 0x00 })); // TODO
                                                                               // check
                                                                               // this
                                                                               // for
                                                                               // actual
                                                                               // value
      }
      return msgId;
   }

   private HeadingSlice getHeadingSlice(String heading) {
      if (heading == null || heading.length() == 0) {
         return new HeadingSlice(new byte[] { 0x00, 0x00 });
      } else {
         short result = 0;
         for (int i = 0; i < 16; i++) {
            if (heading.charAt(i) == '1') {
               result |= 1;
            }
            result <<= 1;
         }
         return new HeadingSlice(ByteBuffer.allocate(2).putShort(result).array());
      }
   }

   private Regions buildRegions(J2735TravelerInputData.DataFrame.Region[] inputRegions) {
      Regions regions = new Regions();
      for (J2735TravelerInputData.DataFrame.Region inputRegion : inputRegions) {
         GeographicalPath geoPath = new GeographicalPath();
         Description description = new Description();
         validateGeoName(inputRegion.getName());
         geoPath.setName(new DescriptiveName(inputRegion.getName()));
         validateRoadID(inputRegion.getRegulatorID());
         validateRoadID(inputRegion.getSegmentID());
         geoPath.setId(new RoadSegmentReferenceID(new RoadRegulatorID(inputRegion.getRegulatorID()),
               new RoadSegmentID(inputRegion.getSegmentID())));
         geoPath.setAnchor(OssPosition3D.position3D(inputRegion.getAnchorPosition()));
         validateLaneWidth(inputRegion.getLaneWidth());
         geoPath.setLaneWidth(new LaneWidth(inputRegion.getLaneWidth()));
         validateDirectionality(inputRegion.getDirectionality());
         geoPath.setDirectionality(new DirectionOfUse(inputRegion.getDirectionality()));
         geoPath.setClosedPath(inputRegion.isClosedPath());
         validateHeading(inputRegion.getDirection());
         geoPath.setDirection(getHeadingSlice(inputRegion.getDirection()));

         if ("path".equals(inputRegion.getDescription())) {
            OffsetSystem offsetSystem = new OffsetSystem();
            validateZoom(inputRegion.getPath().getScale());
            offsetSystem.setScale(new Zoom(inputRegion.getPath().getScale()));
            if ("xy".equals(inputRegion.getPath().getType())) {
               if (inputRegion.getPath().getNodes().length > 0) {
                  offsetSystem.setOffset(new OffsetSystem.Offset());
                  offsetSystem.offset.setXy(buildNodeXYList(inputRegion.getPath().getNodes()));
               } else {
                  offsetSystem.setOffset(new OffsetSystem.Offset());
                  offsetSystem.offset.setXy(buildComputedLane(inputRegion.getPath().getComputedLane()));
               }
            } else if ("ll".equals(inputRegion.getPath().getType()) && inputRegion.getPath().getNodes().length > 0) {
                  offsetSystem.setOffset(new OffsetSystem.Offset());
                  offsetSystem.offset.setLl(buildNodeLLList(inputRegion.getPath().getNodes()));
            }
            description.setPath(offsetSystem);
            geoPath.setDescription(description);
         } else if ("geometry".equals(inputRegion.getDescription())) {
            GeometricProjection geo = new GeometricProjection();
            validateHeading(inputRegion.getGeometry().getDirection());
            geo.setDirection(getHeadingSlice(inputRegion.getGeometry().getDirection()));
            validateExtent(inputRegion.getGeometry().getExtent());
            geo.setExtent(new Extent(inputRegion.getGeometry().getExtent()));
            validateLaneWidth(inputRegion.getGeometry().getLaneWidth());
            geo.setLaneWidth(new LaneWidth(inputRegion.getGeometry().getLaneWidth()));
            geo.setCircle(buildGeoCircle(inputRegion.getGeometry()));
            description.setGeometry(geo);
            geoPath.setDescription(description);

         } else { // oldRegion
            ValidRegion validRegion = new ValidRegion();
            validateHeading(inputRegion.getOldRegion().getDirection());
            validRegion.setDirection(getHeadingSlice(inputRegion.getOldRegion().getDirection()));
            validateExtent(inputRegion.getOldRegion().getExtent());
            validRegion.setExtent(new Extent(inputRegion.getOldRegion().getExtent()));
            Area area = new Area();
            if ("shapePointSet".equals(inputRegion.getOldRegion().getArea())) {
               ShapePointSet sps = new ShapePointSet();
               sps.setAnchor(OssPosition3D.position3D(inputRegion.getOldRegion().getShapepoint().getPosition()));
               validateLaneWidth(inputRegion.getOldRegion().getShapepoint().getLaneWidth());
               sps.setLaneWidth(new LaneWidth(inputRegion.getOldRegion().getShapepoint().getLaneWidth()));
               validateDirectionality(inputRegion.getOldRegion().getShapepoint().getDirectionality());
               sps.setDirectionality(new DirectionOfUse(inputRegion.getOldRegion().getShapepoint().getDirectionality()));
               if (inputRegion.getOldRegion().getShapepoint().getNodexy() != null) {
                  sps.setNodeList(buildNodeXYList(inputRegion.getOldRegion().getShapepoint().getNodexy()));
               } else {
                  sps.setNodeList(buildComputedLane(inputRegion.getOldRegion().getShapepoint().getComputedLane()));
               }
               area.setShapePointSet(sps);
               validRegion.setArea(area);
            } else if ("regionPointSet".equals(inputRegion.getOldRegion().getArea())) {
               RegionPointSet rps = new RegionPointSet();
               rps.setAnchor(OssPosition3D.position3D(inputRegion.getOldRegion().getRegionPoint().getPosition()));
               validateZoom(inputRegion.getOldRegion().getRegionPoint().getScale());
               rps.setScale(new Zoom(inputRegion.getOldRegion().getRegionPoint().getScale()));
               RegionList rl = buildRegionOffsets(inputRegion.getOldRegion().getRegionPoint().getRegionList());
               rps.setNodeList(rl);
               area.setRegionPointSet(rps);
               validRegion.setArea(area);
            } else {// circle
               area.setCircle(buildOldCircle(inputRegion.getOldRegion()));
               validRegion.setArea(area);
            }
            description.setOldRegion(validRegion);
            geoPath.setDescription(description);
         }
         regions.add(geoPath);
      }
      return regions;
   }

   public RegionList buildRegionOffsets(
         J2735TravelerInputData.DataFrame.Region.OldRegion.RegionPoint.RegionList[] list) {
      RegionList myList = new RegionList();
      for (int i = 0; i < list.length; i++) {
         RegionOffsets ele = new RegionOffsets();
         validatex16Offset(list[i].getxOffset());
         ele.setXOffset(new OffsetLL_B16(list[i].getxOffset()));
         validatey16Offset(list[i].getyOffset());
         ele.setYOffset(new OffsetLL_B16(list[i].getyOffset()));
         validatez16Offset(list[i].getzOffset());
         ele.setZOffset(new OffsetLL_B16(list[i].getzOffset()));
         myList.add(ele);
      }
      return myList;
   }

   public Circle buildGeoCircle(J2735TravelerInputData.DataFrame.Region.Geometry geo) {
      Circle circle = new Circle();
      circle.setCenter(OssPosition3D.position3D(geo.getCircle().getPosition()));
      validateRadius(geo.getCircle().getRadius());
      circle.setRadius(new Radius_B12(geo.getCircle().getRadius()));
      validateUnits(geo.getCircle().getUnits());
      circle.setUnits(new DistanceUnits(geo.getCircle().getUnits()));
      return circle;
   }

   public Circle buildOldCircle(J2735TravelerInputData.DataFrame.Region.OldRegion reg) {
      Circle circle = new Circle();
      circle.setCenter(OssPosition3D.position3D(reg.getCircle().getPosition()));
      validateRadius(reg.getCircle().getRadius());
      circle.setRadius(new Radius_B12(reg.getCircle().getRadius()));
      validateUnits(reg.getCircle().getUnits());
      circle.setUnits(new DistanceUnits(reg.getCircle().getUnits()));
      return circle;
   }

   public NodeListXY buildNodeXYList(J2735TravelerInputData.NodeXY[] inputNodes) {
      NodeListXY nodeList = new NodeListXY();
      NodeSetXY nodes = new NodeSetXY();
      for (int i = 0; i < inputNodes.length; i++) {
         J2735TravelerInputData.NodeXY point = inputNodes[i];

         NodeXY node = new NodeXY();
         NodeOffsetPointXY nodePoint = new NodeOffsetPointXY();

         switch (point.getDelta()) {
         case "node-XY1":
            validateB10Offset(point.getX());
            validateB10Offset(point.getY());
            Node_XY_20b xy = new Node_XY_20b(new Offset_B10(point.getX()), new Offset_B10(point.getY()));
            nodePoint.setNode_XY1(xy);
            break;
         case "node-XY2":
            validateB11Offset(point.getX());
            validateB11Offset(point.getY());
            Node_XY_22b xy2 = new Node_XY_22b(new Offset_B11(point.getX()), new Offset_B11(point.getY()));
            nodePoint.setNode_XY2(xy2);
            break;
         case "node-XY3":
            validateB12Offset(point.getX());
            validateB12Offset(point.getY());
            Node_XY_24b xy3 = new Node_XY_24b(new Offset_B12(point.getX()), new Offset_B12(point.getY()));
            nodePoint.setNode_XY3(xy3);
            break;
         case "node-XY4":
            validateB13Offset(point.getX());
            validateB13Offset(point.getY());
            Node_XY_26b xy4 = new Node_XY_26b(new Offset_B13(point.getX()), new Offset_B13(point.getY()));
            nodePoint.setNode_XY4(xy4);
            break;
         case "node-XY5":
            validateB14Offset(point.getX());
            validateB14Offset(point.getY());
            Node_XY_28b xy5 = new Node_XY_28b(new Offset_B14(point.getX()), new Offset_B14(point.getY()));
            nodePoint.setNode_XY5(xy5);
            break;
         case "node-XY6":
            validateB16Offset(point.getX());
            validateB16Offset(point.getY());
            Node_XY_32b xy6 = new Node_XY_32b(new Offset_B16(point.getX()), new Offset_B16(point.getY()));
            nodePoint.setNode_XY6(xy6);
            break;
         case "node-LatLon":
            validateLatitude(point.getNodeLat());
            validateLongitude(point.getNodeLong());
            Node_LLmD_64b nodeLatLong = new Node_LLmD_64b(new Longitude(point.getNodeLong()), new Latitude(point.getNodeLat()));
            nodePoint.setNode_LatLon(nodeLatLong);
            break;
         default:
            break;
         }

         node.setDelta(nodePoint);
         if (point.getAttributes() != null) {
            NodeAttributeSetXY attributes = new NodeAttributeSetXY();

            if (point.getAttributes().getLocalNodes().length > 0) {
               NodeAttributeXYList localNodeList = new NodeAttributeXYList();
               for (J2735TravelerInputData.LocalNode localNode : point.getAttributes().getLocalNodes()) {
                  localNodeList.add(new NodeAttributeXY(localNode.getType()));
               }
               attributes.setLocalNode(localNodeList);
            }

            if (point.getAttributes().getDisabledLists().length > 0) {
               SegmentAttributeXYList disabledNodeList = new SegmentAttributeXYList();
               for (J2735TravelerInputData.DisabledList disabledList : point.getAttributes().getDisabledLists()) {
                  disabledNodeList.add(new SegmentAttributeXY(disabledList.getType()));
               }
               attributes.setDisabled(disabledNodeList);
            }

            if (point.getAttributes().getEnabledLists().length > 0) {
               SegmentAttributeXYList enabledNodeList = new SegmentAttributeXYList();
               for (J2735TravelerInputData.EnabledList enabledList : point.getAttributes().getEnabledLists()) {
                  enabledNodeList.add(new SegmentAttributeXY(enabledList.getType()));
               }
               attributes.setEnabled(enabledNodeList);
            }

            if (point.getAttributes().getDataLists().length > 0) {
               LaneDataAttributeList dataNodeList = new LaneDataAttributeList();
               for (J2735TravelerInputData.DataList dataList : point.getAttributes().getDataLists()) {

                  LaneDataAttribute dataAttribute = new LaneDataAttribute();

                  dataAttribute.setPathEndPointAngle(new DeltaAngle(dataList.getPathEndpointAngle()));
                  dataAttribute.setLaneCrownPointCenter(new RoadwayCrownAngle(dataList.getLaneCrownCenter()));
                  dataAttribute.setLaneCrownPointLeft(new RoadwayCrownAngle(dataList.getLaneCrownLeft()));
                  dataAttribute.setLaneCrownPointRight(new RoadwayCrownAngle(dataList.getLaneCrownRight()));
                  dataAttribute.setLaneAngle(new MergeDivergeNodeAngle(dataList.getLaneAngle()));

                  SpeedLimitList speedDataList = new SpeedLimitList();
                  for (J2735TravelerInputData.SpeedLimits speedLimit : dataList.getSpeedLimits()) {
                     speedDataList.add(new RegulatorySpeedLimit(new SpeedLimitType(speedLimit.getType()),
                           new Velocity(speedLimit.getVelocity())));
                  }

                  dataAttribute.setSpeedLimits(speedDataList);
                  dataNodeList.add(dataAttribute);
               }

               attributes.setData(dataNodeList);
            }

            attributes.setDWidth(new Offset_B10(point.getAttributes().getdWidth()));
            attributes.setDElevation(new Offset_B10(point.getAttributes().getdElevation()));

            node.setAttributes(attributes);

         }
         nodes.add(node);
      }

      nodeList.setNodes(nodes);
      return nodeList;
   }

   private NodeListXY buildComputedLane(J2735TravelerInputData.ComputedLane inputLane) {
      NodeListXY nodeList = new NodeListXY();

      ComputedLane computedLane = new ComputedLane();
      OffsetXaxis ox = new OffsetXaxis();
      OffsetYaxis oy = new OffsetYaxis();
      
      computedLane.setReferenceLaneId(new LaneID(inputLane.getLaneID()));
      if (inputLane.getOffsetLargeX() > 0) {
         ox.setLarge(inputLane.getOffsetLargeX());
         computedLane.offsetXaxis = ox;
      } else {
         ox.setSmall(inputLane.getOffsetSmallX());
         computedLane.offsetXaxis = ox;
      }

      if (inputLane.getOffsetLargeY() > 0) {
         oy.setLarge(inputLane.getOffsetLargeY());
         computedLane.offsetYaxis = oy;
      } else {
         oy.setSmall(inputLane.getOffsetSmallY());
         computedLane.offsetYaxis = oy;
      }
      computedLane.setRotateXY(new Angle(inputLane.getAngle()));
      computedLane.setScaleXaxis(new Scale_B12(inputLane.getxScale()));
      computedLane.setScaleYaxis(new Scale_B12(inputLane.getyScale()));

      nodeList.setComputed(computedLane);
      return nodeList;
   }

   public NodeListLL buildNodeLLList(J2735TravelerInputData.NodeXY[] inputNodes) {
      NodeListLL nodeList = new NodeListLL();
      NodeSetLL nodes = new NodeSetLL();
      for (int i = 0; i < inputNodes.length; i++) {
         J2735TravelerInputData.NodeXY point = inputNodes[i];

         NodeLL node = new NodeLL();
         NodeOffsetPointLL nodePoint = new NodeOffsetPointLL();

         switch (point.getDelta()) {
         case "node-LL1":
            validateLL12Offset(point.getNodeLat());
            validateLL12Offset(point.getNodeLong());
            Node_LL_24B xy1 = new Node_LL_24B(new OffsetLL_B12(point.getNodeLat()), new OffsetLL_B12(point.getNodeLong()));
            nodePoint.setNode_LL1(xy1);
            break;
         case "node-LL2":
            validateLL14Offset(point.getNodeLat());
            validateLL14Offset(point.getNodeLong());
            Node_LL_28B xy2 = new Node_LL_28B(new OffsetLL_B14(point.getNodeLat()), new OffsetLL_B14(point.getNodeLong()));
            nodePoint.setNode_LL2(xy2);
            break;
         case "node-LL3":
            validateLL16Offset(point.getNodeLat());
            validateLL16Offset(point.getNodeLong());
            Node_LL_32B xy3 = new Node_LL_32B(new OffsetLL_B16(point.getNodeLat()), new OffsetLL_B16(point.getNodeLong()));
            nodePoint.setNode_LL3(xy3);
            break;
         case "node-LL4":
            validateLL18Offset(point.getNodeLat());
            validateLL18Offset(point.getNodeLong());
            Node_LL_36B xy4 = new Node_LL_36B(new OffsetLL_B18(point.getNodeLat()), new OffsetLL_B18(point.getNodeLong()));
            nodePoint.setNode_LL4(xy4);
            break;
         case "node-LL5":
            validateLL22Offset(point.getNodeLat());
            validateLL22Offset(point.getNodeLong());
            Node_LL_44B xy5 = new Node_LL_44B(new OffsetLL_B22(point.getNodeLat()), new OffsetLL_B22(point.getNodeLong()));
            nodePoint.setNode_LL5(xy5);
            break;
         case "node-LL6":
            validateLL24Offset(point.getNodeLat());
            validateLL24Offset(point.getNodeLong());
            Node_LL_48B xy6 = new Node_LL_48B(new OffsetLL_B24(point.getNodeLat()), new OffsetLL_B24(point.getNodeLong()));
            nodePoint.setNode_LL6(xy6);
            break;
         case "node-LatLon":
            validateLatitude(point.getNodeLat());
            validateLongitude(point.getNodeLong());
            Node_LLmD_64b nodeLatLong = new Node_LLmD_64b(new Longitude(point.getNodeLong()), new Latitude(point.getNodeLat()));
            nodePoint.setNode_LatLon(nodeLatLong);
            break;
         default:
            break;
         }

         node.setDelta(nodePoint);
         if (point.getAttributes() != null) {
            NodeAttributeSetLL attributes = new NodeAttributeSetLL();

            if (point.getAttributes().getLocalNodes().length > 0) {
               NodeAttributeLLList localNodeList = new NodeAttributeLLList();
               for (J2735TravelerInputData.LocalNode localNode : point.getAttributes().getLocalNodes()) {
                  localNodeList.add(new NodeAttributeLL(localNode.getType()));
               }
               attributes.setLocalNode(localNodeList);
            }

            if (point.getAttributes().getDisabledLists().length > 0) {
               SegmentAttributeLLList disabledNodeList = new SegmentAttributeLLList();
               for (J2735TravelerInputData.DisabledList disabledList : point.getAttributes().getDisabledLists()) {
                  disabledNodeList.add(new SegmentAttributeLL(disabledList.getType()));
               }
               attributes.setDisabled(disabledNodeList);
            }

            if (point.getAttributes().getEnabledLists().length > 0) {
               SegmentAttributeLLList enabledNodeList = new SegmentAttributeLLList();
               for (J2735TravelerInputData.EnabledList enabledList : point.getAttributes().getEnabledLists()) {
                  enabledNodeList.add(new SegmentAttributeLL(enabledList.getType()));
               }
               attributes.setEnabled(enabledNodeList);
            }

            if (point.getAttributes().getDataLists().length > 0) {
               LaneDataAttributeList dataNodeList = new LaneDataAttributeList();
               for (J2735TravelerInputData.DataList dataList : point.getAttributes().getDataLists()) {

                  LaneDataAttribute dataAttribute = new LaneDataAttribute();

                  dataAttribute.setPathEndPointAngle(new DeltaAngle(dataList.getPathEndpointAngle()));
                  dataAttribute.setLaneCrownPointCenter(new RoadwayCrownAngle(dataList.getLaneCrownCenter()));
                  dataAttribute.setLaneCrownPointLeft(new RoadwayCrownAngle(dataList.getLaneCrownLeft()));
                  dataAttribute.setLaneCrownPointRight(new RoadwayCrownAngle(dataList.getLaneCrownRight()));
                  dataAttribute.setLaneAngle(new MergeDivergeNodeAngle(dataList.getLaneAngle()));

                  SpeedLimitList speedDataList = new SpeedLimitList();
                  for (J2735TravelerInputData.SpeedLimits speedLimit : dataList.getSpeedLimits()) {
                     speedDataList.add(new RegulatorySpeedLimit(new SpeedLimitType(speedLimit.getType()),
                           new Velocity(speedLimit.getVelocity())));
                  }

                  dataAttribute.setSpeedLimits(speedDataList);
                  dataNodeList.add(dataAttribute);
               }

               attributes.setData(dataNodeList);
            }

            attributes.setDWidth(new Offset_B10(point.getAttributes().getdWidth()));
            attributes.setDElevation(new Offset_B10(point.getAttributes().getdElevation()));

            node.setAttributes(attributes);
         }
         nodes.add(node);
      }
      nodeList.setNodes(nodes);
      return nodeList;
   }

   public long getMinuteOfTheYear(String timestamp) throws ParseException {
      ZonedDateTime start = DateTimeUtils.isoDateTime(timestamp);
      long diff = DateTimeUtils.difference(DateTimeUtils.isoDateTime(start.getYear() + "-01-01T00:00:00+00:00"), start);
      long minutes = diff / 60000;
      validateStartTime(minutes);
      return minutes;
   }

   public static void validateMessageCount(int msg) {
      if (msg > 127 || msg < 0)
         throw new IllegalArgumentException("Invalid message count [0-127]");
   }

   public static void validateURL(String url) {
      if (url.isEmpty())
         throw new IllegalArgumentException("Invalid empty url");
      if (url.length() < 1 || url.length() > 45)
         throw new IllegalArgumentException("Invalid URL length [1-45]");
   }

   public static void validateURLShort(String url) {
      if (url.isEmpty())
         throw new IllegalArgumentException("Invalid empty Short url");
      if (url.length() < 1 || url.length() > 15)
         throw new IllegalArgumentException("Invalid URL lenth [1-15]");
   }

   public static void validateFrameCount(int count) {
      if (count < 1 || count > 8)
         throw new IllegalArgumentException("Invalid number of dataFrames[1-8]");
   }

   public static void validateMessageID(String str) {
      validateString(str);
      if (!("RoadSignID").equals(str) && !("furtherInfoID").equals(str))
         throw new IllegalArgumentException("Invalid messageID \"RoadSignID or furtherInfoID\"");
   }

   public static void validateStartYear(int year) {
      if (year < 0 || year > 4095)
         throw new IllegalArgumentException("Not a valid start year [0-4095]");
   }

   public static void validateStartTime(long time) {
      if (time < 0 || time > 527040)
         throw new IllegalArgumentException("Invalid start Time [0-527040]");
   }

   public static void validateMinutesDuration(long dur) {
      if (dur < 0 || dur > 32000)
         throw new IllegalArgumentException("Invalid Duration [0-32000]");
   }

   public static void validateHeaderIndex(short count) {
      if (count < 0 || count > 31)
         throw new IllegalArgumentException("Invalid header sspIndex[0-31]");
   }

   public static void validateInfoType(int num) {
      if (num < 0)
         throw new IllegalArgumentException("Invalid enumeration [0<]");
   }

   public static void validatePosition(J2735Position3D position) {
      if (position.getLatitude().doubleValue() < -90.0 || position.getLatitude().doubleValue() > 90.0)
         throw new IllegalArgumentException("Invalid Latitude [-90 - 90]");
      if (position.getLongitude().doubleValue() < -180.0 || position.getLongitude().doubleValue() > 180.0)
         throw new IllegalArgumentException("Invalid Longitude [-180 - 180]");
      if (position.getElevation().doubleValue() < -409.5 || position.getElevation().doubleValue() > 6143.9)
         throw new IllegalArgumentException("Invalid Elevation [-409.5 - 6143.9]");
   }

   public static void validateLatitude(long lat) {
      if (lat < -90.0 || lat > 90)
         throw new IllegalArgumentException("Invalid Latitude[-90 - 90]");
   }

   public static void validateLongitude(long lonng) {
      if (lonng < -180.0 || lonng > 180.0)
         throw new IllegalArgumentException("Invalid Longitude[-180 - 180]");
   }

   public static void validateHeading(String head) {
      validateString(head);
      if (head.length() != 16) {
         throw new IllegalArgumentException("Invalid BitString, must be 16 bits!");
      }
   }

   public static void validateMUTCDCode(int mutc) {
      if (mutc < 0 || mutc > 6)
         throw new IllegalArgumentException("Invalid Enumeration [0-6]");
   }

   public static void validateSign(int sign) {
      if (sign < 0 || sign > 7)
         throw new IllegalArgumentException("Invalid Sign Priority [0-7]");
   }

   public static void validateITISCodes(String code) {
      int cd;
      try {
         cd = Integer.parseInt(code);
         if (cd < 0 || cd > 65535)
            throw new IllegalArgumentException("Invalid ITIS code [0-65535]");
      } catch (NumberFormatException e) {
         if (code.isEmpty())
            throw new IllegalArgumentException("Invalid empty string");
         if (code.length() < 1 || code.length() > 500)
            throw new IllegalArgumentException("Invalid test Phrase length [1-500]");
      }
   }

   public static void validateContentCodes(String code) {
      int cd;
      try {
         cd = Integer.parseInt(code);
         if (cd < 0 || cd > 65535)
            throw new IllegalArgumentException("Invalid ITIS code [0-65535]");
      } catch (NumberFormatException e) {
         if (code.isEmpty())
            throw new IllegalArgumentException("Invalid empty string");
         if (code.length() < 1 || code.length() > 16)
            throw new IllegalArgumentException("Invalid test Phrase length [1-16]");
      }
   }

   public static void validateString(String str) {
      if (str.isEmpty())
         throw new IllegalArgumentException("Invalid Empty String");
   }

   public static void validateGeoName(String name) {
      if (name.length() < 1 || name.length() > 63)
         throw new IllegalArgumentException("Invalid Descriptive name length [1-63]");
   }

   public static void validateRoadID(int id) {
      if (id < 0 || id > 65535)
         throw new IllegalArgumentException("Invalid RoadID [0-65535]");
   }

   public static void validateLaneWidth(int width) {
      if (width < 0 || width > 32767)
         throw new IllegalArgumentException("Invalid lane width [0-32767]");
   }

   public static void validateDirectionality(long dir) {
      if (dir < 0 || dir > 3)
         throw new IllegalArgumentException("Invalid enumeration [0-3]");
   }

   public static void validateZoom(int z) {
      if (z < 0 || z > 15)
         throw new IllegalArgumentException("Invalid zoom [0-15]");
   }

   public static void validateExtent(int ex) {
      if (ex < 0 || ex > 15)
         throw new IllegalArgumentException("Invalid extent enumeration [0-15]");
   }

   public static void validateRadius(int rad) {
      if (rad < 0 || rad > 4095)
         throw new IllegalArgumentException("Invalid radius [0-4095]");
   }

   public static void validateUnits(int unit) {
      if (unit < 0 || unit > 7)
         throw new IllegalArgumentException("Invalid units enumeration [0-7]");
   }

   public static void validatex16Offset(int x) {
      if (x < -32768 || x > 32767)
         throw new IllegalArgumentException("Invalid x offset [-32768 - 32767]");
   }

   public static void validatey16Offset(int y) {
      if (y < -32768 || y > 32767)
         throw new IllegalArgumentException("Invalid y offset [-32768 - 32767]");
   }

   public static void validatez16Offset(int z) {
      if (z < -32768 || z > 32767)
         throw new IllegalArgumentException("Invalid z offset [-32768 - 32767]");
   }

   public static void validateB10Offset(int b) {
      if (b < -512 || b > 511)
         throw new IllegalArgumentException("Invalid B10_Offset [-512 - 511]");
   }

   public static void validateB11Offset(int b) {
      if (b < -1024 || b > 1023)
         throw new IllegalArgumentException("Invalid B11_Offset [-1024 - 1023]");
   }

   public static void validateB12Offset(int b) {
      if (b < -2048 || b > 2047)
         throw new IllegalArgumentException("Invalid B12_Offset [-2048 - 2047]");
   }

   public static void validateB13Offset(int b) {
      if (b < -4096 || b > 4095)
         throw new IllegalArgumentException("Invalid B13_Offset [-4096 - 4095]");
   }

   public static void validateB14Offset(int b) {
      if (b < -8192 || b > 8191)
         throw new IllegalArgumentException("Invalid B14_Offset [-8192 - 8191]");
   }

   public static void validateB16Offset(int b) {
      if (b < -32768 || b > 32767)
         throw new IllegalArgumentException("Invalid B16_Offset [-32768 - 32767]");
   }

   public static void validateLL12Offset(long b) {
      if (b < -2048 || b > 2047)
         throw new IllegalArgumentException("Invalid B10_Offset [-2048 - 2047]");
   }

   public static void validateLL14Offset(long b) {
      if (b < -8192 || b > 8191)
         throw new IllegalArgumentException("Invalid B11_Offset [-8192 - 8191]");
   }

   public static void validateLL16Offset(long b) {
      if (b < -32768 || b > 32767)
         throw new IllegalArgumentException("Invalid B12_Offset [-32768 - 32767]");
   }

   public static void validateLL18Offset(long b) {
      if (b < -131072 || b > 131071)
         throw new IllegalArgumentException("Invalid B13_Offset [-131072 - 131071]");
   }

   public static void validateLL22Offset(long b) {
      if (b < -2097152 || b > 2097151)
         throw new IllegalArgumentException("Invalid B14_Offset [-2097152 - 2097151]");
   }

   public static void validateLL24Offset(long b) {
      if (b < -8388608 || b > 8388607)
         throw new IllegalArgumentException("Invalid B16_Offset [-8388608 - 8388608]");
   }

   public static void validateLaneID(int lane) {
      if (lane < 0 || lane > 255)
         throw new IllegalArgumentException("Invalid LaneID [0 - 255]");
   }

   public static void validateSmallDrivenLine(int line) {
      if (line < -2047 || line > 2047)
         throw new IllegalArgumentException("Invalid Small Offset [-2047 - 2047]");
   }

   public static void validateLargeDrivenLine(int line) {
      if (line < -32767 || line > 32767)
         throw new IllegalArgumentException("Invalid Large Offset [-32767 - 32767]");
   }

   public static void validateAngle(int ang) {
      if (ang < 0 || ang > 28800)
         throw new IllegalArgumentException("Invalid Angle [0 - 28800]");
   }

   public static void validateB12Scale(int b) {
      if (b < -2048 || b > 2047)
         throw new IllegalArgumentException("Invalid B12 Scale [-2048 - 2047]");
   }

   public static void validateNodeAttribute(String str) {
      String myString = "reserved stopLine roundedCapStyleA roundedCapStyleB mergePoint divergePoint downstreamStopLine donwstreamStartNode closedToTraffic safeIsland curbPresentAtStepOff hydrantPresent";
      CharSequence cs = str;
      if (myString.contains(cs)) {
         return;
      } else {
         throw new IllegalArgumentException("Invalid NodeAttribute Enumeration");
      }
   }

   public static void validateSegmentAttribute(String str) {
      String myString = "reserved doNotBlock whiteLine mergingLaneLeft mergingLaneRight curbOnLeft curbOnRight loadingzoneOnLeft loadingzoneOnRight turnOutPointOnLeft turnOutPointOnRight adjacentParkingOnLeft adjacentParkingOnRight sharedBikeLane bikeBoxInFront transitStopOnLeft transitStopOnRight transitStopInLane sharedWithTrackedVehicle safeIsland lowCurbsPresent rumbleStripPresent audibleSignalingPresent adaptiveTimingPresent rfSignalRequestPresent partialCurbIntrusion taperToLeft taperToRight taperToCenterLine parallelParking headInParking freeParking timeRestrictionsOnParking costToPark midBlockCurbPresent unEvenPavementPresent";
      CharSequence cs = str;
      if (myString.contains(cs)) {
         return;
      } else {
         throw new IllegalArgumentException("Invalid SegmentAttribute Enumeration");
      }
   }

   public static void validateSpeedLimitType(String str) {
      String myString = "unknown maxSpeedInSchoolZone maxSpeedInSchoolZoneWhenChildrenArePresent maxSpeedInConstructionZone vehicleMinSpeed vehicleMaxSpeed vehicleNightMaxSpeed truckMinSpeed truckMaxSpeed truckNightMaxSpeed vehiclesWithTrailerMinSpeed vehiclesWithTrailersMaxSpeed vehiclesWithTrailersNightMaxSpeed";
      CharSequence cs = str;
      if (myString.contains(cs)) {
         return;
      } else {
         throw new IllegalArgumentException("Invalid SpeedLimitAttribute Enumeration");
      }
   }

   public static void validateVelocity(int vel) {
      if (vel < 0 || vel > 8191)
         throw new IllegalArgumentException("Invalid Velocity [0 - 8191]");
   }

   public static void validateDeltaAngle(int d) {
      if (d < -150 || d > 150)
         throw new IllegalArgumentException("Invalid Delta Angle [-150 - 150]");
   }

   public static void validateCrownPoint(int c) {
      if (c < -128 || c > 127)
         throw new IllegalArgumentException("Invalid Crown Point [-128 - 127]");
   }

   public static void validateLaneAngle(int a) {
      if (a < -180 || a > 180)
         throw new IllegalArgumentException("Invalid LaneAngle [-180 -180]");
   }
}
