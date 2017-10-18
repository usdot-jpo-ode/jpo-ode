package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class TravelerMessageFromHumanToAsnConverter {

   public static JsonNode changeTravelerInformationToAsnValues(JsonNode timData) {

      // replace data frames
      // INPUT: 
      // "dataframes": [{},{}]
      // OUTPUT:
      // "dataFrames": [
      //   {"TravelerDataFrame" : {}},
      //   {"TravelerDataFrame" : {}}
      //  ]
      
      // First thing to do is cast to ObjectNode so we can edit values in place
      ObjectNode timDataObjectNode = (ObjectNode) timData;
      
      replaceDataFrames(timDataObjectNode.get("tim").get("dataframes"));
      
      return timDataObjectNode;

   }

   public static JsonNode replaceDataFrames(JsonNode dataFrames) {

      if (dataFrames == null) {
         return JsonUtils.newNode();
      }
      
      ArrayNode replacedDataFrames = JsonUtils.newNode().arrayNode();

      if (dataFrames.isArray()) {
         Iterator<JsonNode> dataFramesIter = dataFrames.elements();

         while (dataFramesIter.hasNext()) {
            JsonNode oldFrame = dataFramesIter.next();
            replacedDataFrames.add(replaceDataFrame(oldFrame));
         }
      } else {
         replacedDataFrames.add(replaceDataFrame(dataFrames));
      }
         

      return replacedDataFrames;
   }

   /**
    * Convert necessary fields within the dataframe. For now just pos3d.
    * 
    * @param dataFrame
    */
   public static ObjectNode replaceDataFrame(JsonNode dataFrame) {
      
      ObjectNode updatedNode = (ObjectNode) dataFrame;
      
      // replace the msgID and relevant fields
      replaceMsgId(updatedNode);

      
      
      // replace the geographical path regions
      replaceGeographicalPathRegions(dataFrame.get("regions"));
      
      return updatedNode;
   }
   
   public static ObjectNode replaceMsgId(JsonNode msgIDNode) {
      
//    <msgId>
//    <roadSignID>
//       <position>
//          <lat>416784730</lat>
//          <long>-1087827750</long>
//          <elevation>9171</elevation>
//       </position>
//       <viewAngle>0101010101010100</viewAngle>
//       <mutcdCode>
//          <guide />
//       </mutcdCode>
//       <crc>0000</crc>
//    </roadSignID>
// </msgId>
    
    // replace the messageID
    // TODO WRONG SCHEMA STRUCTURE - postion3d here should be inside the RoadSignID element
      
      ObjectNode updatedNode = (ObjectNode) msgIDNode;
      
      JsonNode msgID = updatedNode.get("msgID");
      if (msgID != null) {
         if (msgID.asText().equals("RoadSignID")) {

            ObjectNode roadSignID = JsonUtils.newNode();
            JsonUtils.addNode(roadSignID, "position", translateAnchor(updatedNode.get("position")));
            JsonUtils.addNode(roadSignID, "viewAngle", updatedNode.get("viewAngle").asText());
            JsonUtils.addNode(roadSignID, "mutcdCode", updatedNode.get("mutcd").asText());
            roadSignID.put("crc", updatedNode.get("crc").asText());
            // TODO - we can't do the following because .addNode calls as POJO
            //JsonUtils.addNode(roadSignID, "crc", dataFrame.get("crc").asText());

            updatedNode.remove("msgID");
            updatedNode.remove("position");
            updatedNode.remove("viewAngle");
            updatedNode.remove("mutcd");
            updatedNode.remove("crc");

            ObjectNode msgId = JsonUtils.newNode();
            msgId.set("roadSignID", roadSignID);

            updatedNode.set("msgID", msgId);

         } else if (msgID.asText().equals("FurtherInfoID")) {
            
            // TODO - this may not be correct since msgID schema is inconsistent
            
            updatedNode.remove("msgID");
            updatedNode.remove("position");
            updatedNode.remove("viewAngle");
            updatedNode.remove("mutcd");
            updatedNode.remove("crc");
            
            ObjectNode msgId = JsonUtils.newNode();
            msgId.put("furtherInfoID", msgID.get("FurtherInfoID").asText());
            
            updatedNode.set("msgID", msgId);
         }
      }
      
      return updatedNode;
   }

   public static JsonNode replaceGeographicalPathRegions(JsonNode regions) {
      ArrayNode replacedRegions = JsonUtils.newNode().arrayNode();

      if (regions.isArray()) {
         Iterator<JsonNode> regionsIter = regions.elements();

         while (regionsIter.hasNext()) {
            JsonNode curRegion = regionsIter.next();
            replacedRegions.add(translateGeoGraphicalPathRegion(curRegion));
         }
      } 

      return replacedRegions;
   }
   
   public static ObjectNode translateGeoGraphicalPathRegion(JsonNode region) {
      
      
      // Step 1 - Translate Position3D
      // replace "anchorPosition" with "anchor" and translate values
      ObjectNode updatedNode = JsonUtils.setElement("anchor", region, translateAnchor(region.get("anchorPosition")));
      updatedNode = JsonUtils.removeElement("anchorPosition", updatedNode);
      
      // Step 2 - Translate LaneWidth
      updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));
      
      // Step 3 - translate regions
      if (updatedNode.get("description").get("geometry") != null) {
         updatedNode = replaceGeometry(updatedNode);
      }
      
      if (updatedNode.get("description").get("oldRegion") != null) {
         updatedNode = replaceOldRegion(updatedNode);
      }
      
      
      
      return updatedNode;
      
   }

   public static ObjectNode translateAnchor(JsonNode region) {
      // takes anchor (position3d) and replaces lat/long/elev
      return Position3DBuilder.position3D(region);

   }
   
   public static ObjectNode replaceGeometry(JsonNode geometry) {
      
      ObjectNode updatedNode = (ObjectNode) geometry;
      
      // replace lane width
      updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));
      
      return updatedNode;
   }
   
   public static ObjectNode replaceOldRegion(JsonNode oldRegion) {
      
      // old region == ValidRegion
      // elements:
      // direction - no changes
      // extent - no changes
      // area - needs changes
      
      ObjectNode updatedNode = (ObjectNode) oldRegion;
      
      updatedNode.set("area", replaceArea(updatedNode.get("area")));
      
      return updatedNode;
   }
   
   public static ObjectNode replaceArea(JsonNode area) {
      
      // area contains one of:
      // shapePointSet
      // circle
      // regionPointSet
      
      ObjectNode updatedNode = (ObjectNode) area;
      
      
      if (updatedNode.get("shapePointSet") != null) {
         updatedNode.set("shapePointSet", replaceShapePointSet(updatedNode.get("shapePointSet")));
         
      } else if (updatedNode.get("circle") != null) {
         updatedNode.set("circle", replaceCircle(updatedNode.get("circle")));
         
      } else if (updatedNode.get("regionPointSet") != null) {
         updatedNode.set("regionPointSet", replaceRegionPointSet(updatedNode.get("regionPointSet")));
      }
      
      return updatedNode;
   }
   
   private static ObjectNode replaceRegionPointSet(JsonNode regionPointSet) {
      // TODO Auto-generated method stub
      ObjectNode updatedNode = (ObjectNode) regionPointSet;
      return updatedNode;
   }

   public static ObjectNode replaceCircle(JsonNode circle) {
      // TODO Auto-generated method stub
      ObjectNode updatedNode = (ObjectNode) circle;
      return updatedNode;
   }

   public static ObjectNode replaceShapePointSet(JsonNode shapePointSet) {
      // shape point set contains:
      // anchor
      // lane width
      // directionality
      // node list
      
      ObjectNode updatedNode = (ObjectNode) shapePointSet;
      
      // replace anchor
      if (updatedNode.get("anchor") != null) {
         updatedNode = JsonUtils.setElement("anchor", updatedNode, translateAnchor(updatedNode.get("anchorPosition")));
         updatedNode = JsonUtils.removeElement("anchorPosition", updatedNode);
      }
      
      // replace lane width
      if (updatedNode.get("laneWidth") != null) {
         updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));
      }
      
      // directionality does not need replacement
      
      // replace node list
      updatedNode.set("nodeList", updatedNode.get("nodeList"));
      
      return updatedNode;
   }
   
   public static ObjectNode replaceNodeListXY (JsonNode nodeList) {
      // nodeListXY contains either NodeSetXY or ComputedLane
      
      ObjectNode updatedNode = (ObjectNode) nodeList;
      
      if (updatedNode.get("nodes") != null) {
         updatedNode.set("nodes", replaceNodeSetXY(updatedNode.get("nodes")));
      }
      
      return updatedNode;
   }
   
   public static ObjectNode replaceNodeSetXY(JsonNode nodeSet) {
      ObjectNode updatedNode = (ObjectNode) nodeSet;
      
      return updatedNode;
   }

//   public TravelerInformation buildTravelerInformation(JsonNode jsonNode)
//         throws ParseException, EncodeFailedException, EncodeNotSupportedException, IllegalArgumentException {
//
//      travelerInfo = new TravelerInformation();
//      TimFieldValidator.validateMessageCount(jsonNode.getMsgCnt());
//      travelerInfo.setMsgCnt(new MsgCount(jsonNode.getMsgCnt()));
//      travelerInfo.setTimeStamp(
//            new MinuteOfTheYear(getMinuteOfTheYear(jsonNode.getTimeStamp())));
//      ByteBuffer buf = ByteBuffer.allocate(9).put((byte) 0).putLong(jsonNode.getPacketID());
//      travelerInfo.setPacketID(new UniqueMSGID(buf.array()));
//      TimFieldValidator.validateURL(jsonNode.getUrlB());
//      travelerInfo.setUrlB(new URL_Base(jsonNode.getUrlB()));
//      travelerInfo.setDataFrames(buildDataFrames(jsonNode));
//
//      return travelerInfo;
//   }
//
//   private TravelerDataFrameList buildDataFrames(
//         J2735TravelerInformationMessage tim) 
//         throws ParseException {
//      TravelerDataFrameList dataFrames = new TravelerDataFrameList();
//
//      TimFieldValidator.validateFrameCount(tim.getDataframes().length);
//      int len = tim.getDataframes().length;
//      for (int i = 0; i < len; i++) {
//         J2735TravelerInformationMessage.DataFrame inputDataFrame = tim.getDataframes()[i];
//         TravelerDataFrame dataFrame = new TravelerDataFrame();
//
//         // Part I, header
//         TIMHeaderBuilder.buildTimHeader(inputDataFrame, dataFrame);
//
//         // -- Part II, Applicable Regions of Use
//         TimFieldValidator.validateHeaderIndex(inputDataFrame.getsspLocationRights());
//         dataFrame.setSspLocationRights(new SSPindex(inputDataFrame.getsspLocationRights()));
//         dataFrame.setRegions(buildRegions(inputDataFrame.getRegions()));
//
//         // -- Part III, Content
//         TimFieldValidator.validateHeaderIndex(inputDataFrame.getsspMsgTypes());
//         dataFrame.setSspMsgRights1(new SSPindex(inputDataFrame.getsspMsgTypes())); // allowed
//         // message
//         // types
//         TimFieldValidator.validateHeaderIndex(inputDataFrame.getsspMsgContent());
//         dataFrame.setSspMsgRights2(new SSPindex(inputDataFrame.getsspMsgContent())); // allowed
//         // message
//         // content
//         dataFrame.setContent(buildContent(inputDataFrame));
//         TimFieldValidator.validateURLShort(inputDataFrame.getUrl());
//         dataFrame.setUrl(new URL_Short(inputDataFrame.getUrl()));
//         dataFrames.add(dataFrame);
//      }
//      return dataFrames;
//   }
//
//   public String encodeTravelerInformationToHex() 
//         throws EncodeFailedException, EncodeNotSupportedException {
//      Coder coder = J2735.getPERUnalignedCoder();
//      ByteArrayOutputStream sink = new ByteArrayOutputStream();
//      coder.encode(travelerInfo, sink);
//      byte[] bytes = sink.toByteArray();
//      return CodecUtils.toHex(bytes);
//   }
//
//   public Content buildContent(J2735TravelerInformationMessage.DataFrame inputDataFrame) {
//      String contentType = inputDataFrame.getContent();
//      String[] codes = inputDataFrame.getItems();
//      Content content = new Content();
//      if ("Advisory".equals(contentType)) {
//         content.setAdvisory(buildAdvisory(codes));
//      } else if ("Work Zone".equals(contentType)) {
//         content.setWorkZone(buildWorkZone(codes));
//      } else if ("Speed Limit".equals(contentType)) {
//         content.setSpeedLimit(buildSpeedLimit(codes));
//      } else if ("Exit Service".equals(contentType)) {
//         content.setExitService(buildExitService(codes));
//      } else if ("Generic Signage".equals(contentType)) {
//         content.setGenericSign(buildGenericSignage(codes));
//      }
//      return content;
//   }
//
//   public ITIScodesAndText buildAdvisory(String[] codes) {
//      ITIScodesAndText itisText = new ITIScodesAndText();
//      for (String code : codes) {
//         TimFieldValidator.validateITISCodes(code);
//         ITIScodesAndText.Sequence_ seq = new ITIScodesAndText.Sequence_();
//         ITIScodesAndText.Sequence_.Item item = new ITIScodesAndText.Sequence_.Item();
//         item.setItis(Long.parseLong(code));
//         seq.setItem(item);
//         itisText.add(seq);
//      }
//      return itisText;
//   }
//
//   public WorkZone buildWorkZone(String[] codes) {
//      WorkZone wz = new WorkZone();
//      for (String code : codes) {
//         TimFieldValidator.validateContentCodes(code);
//         WorkZone.Sequence_ seq = new WorkZone.Sequence_();
//         WorkZone.Sequence_.Item item = new WorkZone.Sequence_.Item();
//         item.setItis(Long.parseLong(code));
//         seq.setItem(item);
//         wz.add(seq);
//      }
//      return wz;
//   }
//
//   public SpeedLimit buildSpeedLimit(String[] codes) {
//      SpeedLimit sl = new SpeedLimit();
//      for (String code : codes) {
//         TimFieldValidator.validateContentCodes(code);
//         SpeedLimit.Sequence_ seq = new SpeedLimit.Sequence_();
//         SpeedLimit.Sequence_.Item item = new SpeedLimit.Sequence_.Item();
//         item.setItis(Long.parseLong(code));
//         seq.setItem(item);
//         sl.add(seq);
//      }
//      return sl;
//   }
//
//   public ExitService buildExitService(String[] codes) {
//      ExitService es = new ExitService();
//      for (String code : codes) {
//         TimFieldValidator.validateContentCodes(code);
//         ExitService.Sequence_ seq = new ExitService.Sequence_();
//         ExitService.Sequence_.Item item = new ExitService.Sequence_.Item();
//         item.setItis(Long.parseLong(code));
//         seq.setItem(item);
//         es.add(seq);
//      }
//      return es;
//   }
//
//   public GenericSignage buildGenericSignage(String[] codes) {
//      GenericSignage gs = new GenericSignage();
//      for (String code : codes) {
//         TimFieldValidator.validateContentCodes(code);
//         GenericSignage.Sequence_ seq = new GenericSignage.Sequence_();
//         GenericSignage.Sequence_.Item item = new GenericSignage.Sequence_.Item();
//         item.setItis(Long.parseLong(code));
//         seq.setItem(item);
//         gs.add(seq);
//      }
//      return gs;
//   }
//
//   private Regions buildRegions(J2735TravelerInformationMessage.DataFrame.Region[] inputRegions) {
//      Regions regions = new Regions();
//      for (J2735TravelerInformationMessage.DataFrame.Region inputRegion : inputRegions) {
//         GeographicalPath geoPath = new GeographicalPath();
//         Description description = new Description();
//         TimFieldValidator.validateGeoName(inputRegion.getName());
//         geoPath.setName(new DescriptiveName(inputRegion.getName()));
//         TimFieldValidator.validateRoadID(inputRegion.getRegulatorID());
//         TimFieldValidator.validateRoadID(inputRegion.getSegmentID());
//         geoPath.setId(new RoadSegmentReferenceID(new RoadRegulatorID(inputRegion.getRegulatorID()),
//               new RoadSegmentID(inputRegion.getSegmentID())));
//         geoPath.setAnchor(OssPosition3D.position3D(inputRegion.getAnchorPosition()));
//         TimFieldValidator.validateLaneWidth(inputRegion.getLaneWidth());
//         geoPath.setLaneWidth(OssLaneWidth.laneWidth(inputRegion.getLaneWidth()));
//         TimFieldValidator.validateDirectionality(inputRegion.getDirectionality());
//         geoPath.setDirectionality(new DirectionOfUse(inputRegion.getDirectionality()));
//         geoPath.setClosedPath(inputRegion.isClosedPath());
//         TimFieldValidator.validateHeading(inputRegion.getDirection());
//         geoPath.setDirection(getHeadingSlice(inputRegion.getDirection()));
//
//         if ("path".equals(inputRegion.getDescription())) {
//            OffsetSystem offsetSystem = new OffsetSystem();
//            TimFieldValidator.validateZoom(inputRegion.getPath().getScale());
//            offsetSystem.setScale(new Zoom(inputRegion.getPath().getScale()));
//            if ("xy".equals(inputRegion.getPath().getType())) {
//               if (inputRegion.getPath().getNodes() != null) {
//                  offsetSystem.setOffset(new OffsetSystem.Offset());
//                  offsetSystem.offset.setXy(buildNodeXYList(inputRegion.getPath().getNodes()));
//               } else {
//                  offsetSystem.setOffset(new OffsetSystem.Offset());
//                  offsetSystem.offset.setXy(buildComputedLane(inputRegion.getPath().getComputedLane()));
//               }
//            } else if ("ll".equals(inputRegion.getPath().getType()) && inputRegion.getPath().getNodes().length > 0) {
//               offsetSystem.setOffset(new OffsetSystem.Offset());
//               offsetSystem.offset.setLl(buildNodeLLList(inputRegion.getPath().getNodes()));
//            }
//            description.setPath(offsetSystem);
//            geoPath.setDescription(description);
//         } else if ("geometry".equals(inputRegion.getDescription())) {
//            GeometricProjection geo = new GeometricProjection();
//            TimFieldValidator.validateHeading(inputRegion.getGeometry().getDirection());
//            geo.setDirection(getHeadingSlice(inputRegion.getGeometry().getDirection()));
//            TimFieldValidator.validateExtent(inputRegion.getGeometry().getExtent());
//            geo.setExtent(new Extent(inputRegion.getGeometry().getExtent()));
//            TimFieldValidator.validateLaneWidth(inputRegion.getGeometry().getLaneWidth());
//            geo.setLaneWidth(OssLaneWidth.laneWidth(inputRegion.getGeometry().getLaneWidth()));
//            geo.setCircle(buildGeoCircle(inputRegion.getGeometry()));
//            description.setGeometry(geo);
//            geoPath.setDescription(description);
//
//         } else { // oldRegion
//            ValidRegion validRegion = new ValidRegion();
//            TimFieldValidator.validateHeading(inputRegion.getOldRegion().getDirection());
//            validRegion.setDirection(getHeadingSlice(inputRegion.getOldRegion().getDirection()));
//            TimFieldValidator.validateExtent(inputRegion.getOldRegion().getExtent());
//            validRegion.setExtent(new Extent(inputRegion.getOldRegion().getExtent()));
//            Area area = new Area();
//            if ("shapePointSet".equals(inputRegion.getOldRegion().getArea())) {
//               ShapePointSet sps = new ShapePointSet();
//               sps.setAnchor(OssPosition3D.position3D(inputRegion.getOldRegion().getShapepoint().getPosition()));
//               TimFieldValidator.validateLaneWidth(inputRegion.getOldRegion().getShapepoint().getLaneWidth());
//               sps.setLaneWidth(OssLaneWidth.laneWidth(inputRegion.getOldRegion().getShapepoint().getLaneWidth()));
//               TimFieldValidator.validateDirectionality(inputRegion.getOldRegion().getShapepoint().getDirectionality());
//               sps.setDirectionality(
//                     new DirectionOfUse(inputRegion.getOldRegion().getShapepoint().getDirectionality()));
//               if (inputRegion.getOldRegion().getShapepoint().getNodexy() != null) {
//                  sps.setNodeList(buildNodeXYList(inputRegion.getOldRegion().getShapepoint().getNodexy()));
//               } else {
//                  sps.setNodeList(buildComputedLane(inputRegion.getOldRegion().getShapepoint().getComputedLane()));
//               }
//               area.setShapePointSet(sps);
//               validRegion.setArea(area);
//            } else if ("regionPointSet".equals(inputRegion.getOldRegion().getArea())) {
//               RegionPointSet rps = new RegionPointSet();
//               rps.setAnchor(OssPosition3D.position3D(inputRegion.getOldRegion().getRegionPoint().getPosition()));
//               TimFieldValidator.validateZoom(inputRegion.getOldRegion().getRegionPoint().getScale());
//               rps.setScale(new Zoom(inputRegion.getOldRegion().getRegionPoint().getScale()));
//               RegionList rl = buildRegionOffsets(inputRegion.getOldRegion().getRegionPoint().getRegionList());
//               rps.setNodeList(rl);
//               area.setRegionPointSet(rps);
//               validRegion.setArea(area);
//            } else {// circle
//               area.setCircle(buildOldCircle(inputRegion.getOldRegion()));
//               validRegion.setArea(area);
//            }
//            description.setOldRegion(validRegion);
//            geoPath.setDescription(description);
//         }
//         regions.add(geoPath);
//      }
//      return regions;
//   }
//
//   public RegionList buildRegionOffsets(
//         J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] list) {
//      RegionList myList = new RegionList();
//      for (int i = 0; i < list.length; i++) {
//         RegionOffsets ele = new RegionOffsets();
//         TimFieldValidator.validatex16Offset(list[i].getxOffset());
//         ele.setXOffset(OssOffsetLLB16.offsetLLB16(list[i].getxOffset()));
//         TimFieldValidator.validatey16Offset(list[i].getyOffset());
//         ele.setYOffset(OssOffsetLLB16.offsetLLB16(list[i].getyOffset()));
//         TimFieldValidator.validatez16Offset(list[i].getzOffset());
//         ele.setZOffset(OssOffsetLLB16.offsetLLB16(list[i].getzOffset()));
//         myList.add(ele);
//      }
//      return myList;
//   }
//
//   public Circle buildGeoCircle(J2735TravelerInformationMessage.DataFrame.Region.Geometry geo) {
//      Circle circle = new Circle();
//      circle.setCenter(OssPosition3D.position3D(geo.getCircle().getPosition()));
//      TimFieldValidator.validateRadius(geo.getCircle().getRadius());
//      circle.setRadius(new Radius_B12(geo.getCircle().getRadius()));
//      TimFieldValidator.validateUnits(geo.getCircle().getUnits());
//      circle.setUnits(new DistanceUnits(geo.getCircle().getUnits()));
//      return circle;
//   }
//
//   public Circle buildOldCircle(J2735TravelerInformationMessage.DataFrame.Region.OldRegion reg) {
//      Circle circle = new Circle();
//      circle.setCenter(OssPosition3D.position3D(reg.getCircle().getPosition()));
//      TimFieldValidator.validateRadius(reg.getCircle().getRadius());
//      circle.setRadius(new Radius_B12(reg.getCircle().getRadius()));
//      TimFieldValidator.validateUnits(reg.getCircle().getUnits());
//      circle.setUnits(new DistanceUnits(reg.getCircle().getUnits()));
//      return circle;
//   }
//
//   public NodeListXY buildNodeXYList(J2735TravelerInformationMessage.NodeXY[] inputNodes) {
//      NodeListXY nodeList = new NodeListXY();
//      NodeSetXY nodes = new NodeSetXY();
//      for (int i = 0; i < inputNodes.length; i++) {
//         J2735TravelerInformationMessage.NodeXY point = inputNodes[i];
//
//         NodeXY node = new NodeXY();
//         NodeOffsetPointXY nodePoint = new NodeOffsetPointXY();
//
//         switch (point.getDelta()) {
//         case "node-XY1":
//            TimFieldValidator.validateB10Offset(point.getX());
//            TimFieldValidator.validateB10Offset(point.getY());
//            Node_XY_20b xy = new Node_XY_20b(OssOffsetB10.offsetB10(point.getX()), OssOffsetB10.offsetB10(point.getY()));
//            nodePoint.setNode_XY1(xy);
//            break;
//         case "node-XY2":
//            TimFieldValidator.validateB11Offset(point.getX());
//            TimFieldValidator.validateB11Offset(point.getY());
//            Node_XY_22b xy2 = new Node_XY_22b(OssOffsetB11.offsetB11(point.getX()), OssOffsetB11.offsetB11(point.getY()));
//            nodePoint.setNode_XY2(xy2);
//            break;
//         case "node-XY3":
//            TimFieldValidator.validateB12Offset(point.getX());
//            TimFieldValidator.validateB12Offset(point.getY());
//            Node_XY_24b xy3 = new Node_XY_24b(OssOffsetB12.offsetB12(point.getX()), OssOffsetB12.offsetB12(point.getY()));
//            nodePoint.setNode_XY3(xy3);
//            break;
//         case "node-XY4":
//            TimFieldValidator.validateB13Offset(point.getX());
//            TimFieldValidator.validateB13Offset(point.getY());
//            Node_XY_26b xy4 = new Node_XY_26b(OssOffsetB13.offsetB13(point.getX()), OssOffsetB13.offsetB13(point.getY()));
//            nodePoint.setNode_XY4(xy4);
//            break;
//         case "node-XY5":
//            TimFieldValidator.validateB14Offset(point.getX());
//            TimFieldValidator.validateB14Offset(point.getY());
//            Node_XY_28b xy5 = new Node_XY_28b(OssOffsetB14.offsetB14(point.getX()), OssOffsetB14.offsetB14(point.getY()));
//            nodePoint.setNode_XY5(xy5);
//            break;
//         case "node-XY6":
//            TimFieldValidator.validateB16Offset(point.getX());
//            TimFieldValidator.validateB16Offset(point.getY());
//            Node_XY_32b xy6 = new Node_XY_32b(OssOffsetB16.offsetB16(point.getX()), OssOffsetB16.offsetB16(point.getY()));
//            nodePoint.setNode_XY6(xy6);
//            break;
//         case "node-LatLon":
//            TimFieldValidator.validateLatitude(point.getNodeLat());
//            TimFieldValidator.validateLongitude(point.getNodeLong());
//            Node_LLmD_64b nodeLatLong = new Node_LLmD_64b(OssLongitude.longitude(point.getNodeLong()),
//                  OssLatitude.latitude(point.getNodeLat()));
//            nodePoint.setNode_LatLon(nodeLatLong);
//            break;
//         default:
//            break;
//         }
//
//         node.setDelta(nodePoint);
//         if (point.getAttributes() != null) {
//            NodeAttributeSetXY attributes = new NodeAttributeSetXY();
//
//            if (point.getAttributes().getLocalNodes().length > 0) {
//               NodeAttributeXYList localNodeList = new NodeAttributeXYList();
//               for (J2735TravelerInformationMessage.LocalNode localNode : point.getAttributes().getLocalNodes()) {
//                  localNodeList.add(new NodeAttributeXY(localNode.getType()));
//               }
//               attributes.setLocalNode(localNodeList);
//            }
//
//            if (point.getAttributes().getDisabledLists().length > 0) {
//               SegmentAttributeXYList disabledNodeList = new SegmentAttributeXYList();
//               for (J2735TravelerInformationMessage.DisabledList disabledList : point.getAttributes().getDisabledLists()) {
//                  disabledNodeList.add(new SegmentAttributeXY(disabledList.getType()));
//               }
//               attributes.setDisabled(disabledNodeList);
//            }
//
//            if (point.getAttributes().getEnabledLists().length > 0) {
//               SegmentAttributeXYList enabledNodeList = new SegmentAttributeXYList();
//               for (J2735TravelerInformationMessage.EnabledList enabledList : point.getAttributes().getEnabledLists()) {
//                  enabledNodeList.add(new SegmentAttributeXY(enabledList.getType()));
//               }
//               attributes.setEnabled(enabledNodeList);
//            }
//
//            if (point.getAttributes().getDataLists().length > 0) {
//               LaneDataAttributeList dataNodeList = new LaneDataAttributeList();
//               for (J2735TravelerInformationMessage.DataList dataList : point.getAttributes().getDataLists()) {
//
//                  LaneDataAttribute dataAttribute = new LaneDataAttribute();
//
//                  dataAttribute.setPathEndPointAngle(new DeltaAngle(dataList.getPathEndpointAngle()));
//                  dataAttribute.setLaneCrownPointCenter(OssRoadwayCrownAngle.roadwayCrownAngle(dataList.getLaneCrownCenter()));
//                  dataAttribute.setLaneCrownPointLeft(OssRoadwayCrownAngle.roadwayCrownAngle(dataList.getLaneCrownLeft()));
//                  dataAttribute.setLaneCrownPointRight(OssRoadwayCrownAngle.roadwayCrownAngle(dataList.getLaneCrownRight()));
//                  dataAttribute.setLaneAngle(OssMergeDivergeNodeAngle.mergeDivergeNodeAngle(dataList.getLaneAngle()));
//
//                  SpeedLimitList speedDataList = new SpeedLimitList();
//                  for (J2735TravelerInformationMessage.SpeedLimits speedLimit : dataList.getSpeedLimits()) {
//                     speedDataList.add(new RegulatorySpeedLimit(new SpeedLimitType(speedLimit.getType()),
//                           OssVelocity.velocity(speedLimit.getVelocity())));
//                  }
//
//                  dataAttribute.setSpeedLimits(speedDataList);
//                  dataNodeList.add(dataAttribute);
//               }
//
//               attributes.setData(dataNodeList);
//            }
//
//            attributes.setDWidth(OssOffsetB10.offsetB10(point.getAttributes().getdWidth()));
//            attributes.setDElevation(OssOffsetB10.offsetB10(point.getAttributes().getdElevation()));
//
//            node.setAttributes(attributes);
//
//         }
//         nodes.add(node);
//      }
//
//      nodeList.setNodes(nodes);
//      return nodeList;
//   }
//
//   private NodeListXY buildComputedLane(J2735TravelerInformationMessage.ComputedLane inputLane) {
//      NodeListXY nodeList = new NodeListXY();
//
//      ComputedLane computedLane = new ComputedLane();
//      OffsetXaxis ox = new OffsetXaxis();
//      OffsetYaxis oy = new OffsetYaxis();
//
//      computedLane.setReferenceLaneId(new LaneID(inputLane.getLaneID()));
//      if (inputLane.getOffsetLargeX().doubleValue() > 0) {
//         ox.setLarge(OssDrivenLineOffsetLg.drivenLineOffsetLg(inputLane.getOffsetLargeX()));
//         computedLane.offsetXaxis = ox;
//      } else {
//         ox.setSmall(OssDrivenLineOffsetSm.drivenLaneOffsetSm(inputLane.getOffsetSmallX()));
//         computedLane.offsetXaxis = ox;
//      }
//
//      if (inputLane.getOffsetLargeY().doubleValue() > 0) {
//         oy.setLarge(OssDrivenLineOffsetLg.drivenLineOffsetLg(inputLane.getOffsetLargeY()));
//         computedLane.offsetYaxis = oy;
//      } else {
//         oy.setSmall(OssDrivenLineOffsetSm.drivenLaneOffsetSm(inputLane.getOffsetSmallY()));
//         computedLane.offsetYaxis = oy;
//      }
//      computedLane.setRotateXY(OssAngle.angle(inputLane.getAngle()));
//      computedLane.setScaleXaxis(OssScaleB12.scaleB12(inputLane.getxScale()));
//      computedLane.setScaleYaxis(OssScaleB12.scaleB12(inputLane.getyScale()));
//
//      nodeList.setComputed(computedLane);
//      return nodeList;
//   }
//
//   public NodeListLL buildNodeLLList(J2735TravelerInformationMessage.NodeXY[] inputNodes) {
//      NodeListLL nodeList = new NodeListLL();
//      NodeSetLL nodes = new NodeSetLL();
//      for (int i = 0; i < inputNodes.length; i++) {
//         J2735TravelerInformationMessage.NodeXY point = inputNodes[i];
//
//         NodeLL node = new NodeLL();
//         NodeOffsetPointLL nodePoint = new NodeOffsetPointLL();
//
//         switch (point.getDelta()) {
//         case "node-LL1":
//            TimFieldValidator.validateLL12Offset(point.getNodeLat());
//            TimFieldValidator.validateLL12Offset(point.getNodeLong());
//            Node_LL_24B xy1 = new Node_LL_24B(OssOffsetLLB12.offsetLLB12(point.getNodeLat()),
//                  OssOffsetLLB12.offsetLLB12(point.getNodeLong()));
//            nodePoint.setNode_LL1(xy1);
//            break;
//         case "node-LL2":
//            TimFieldValidator.validateLL14Offset(point.getNodeLat());
//            TimFieldValidator.validateLL14Offset(point.getNodeLong());
//            Node_LL_28B xy2 = new Node_LL_28B(OssOffsetLLB14.offsetLLB14(point.getNodeLat()),
//                  OssOffsetLLB14.offsetLLB14(point.getNodeLong()));
//            nodePoint.setNode_LL2(xy2);
//            break;
//         case "node-LL3":
//            TimFieldValidator.validateLL16Offset(point.getNodeLat());
//            TimFieldValidator.validateLL16Offset(point.getNodeLong());
//            Node_LL_32B xy3 = new Node_LL_32B(OssOffsetLLB16.offsetLLB16(point.getNodeLat()),
//                  OssOffsetLLB16.offsetLLB16(point.getNodeLong()));
//            nodePoint.setNode_LL3(xy3);
//            break;
//         case "node-LL4":
//            TimFieldValidator.validateLL18Offset(point.getNodeLat());
//            TimFieldValidator.validateLL18Offset(point.getNodeLong());
//            Node_LL_36B xy4 = new Node_LL_36B(OssOffsetLLB18.offsetLLB18(point.getNodeLat()),
//                  OssOffsetLLB18.offsetLLB18(point.getNodeLong()));
//            nodePoint.setNode_LL4(xy4);
//            break;
//         case "node-LL5":
//            TimFieldValidator.validateLL22Offset(point.getNodeLat());
//            TimFieldValidator.validateLL22Offset(point.getNodeLong());
//            Node_LL_44B xy5 = new Node_LL_44B(OssOffsetLLB22.offsetLLB22(point.getNodeLat()),
//                  OssOffsetLLB22.offsetLLB22(point.getNodeLong()));
//            nodePoint.setNode_LL5(xy5);
//            break;
//         case "node-LL6":
//            TimFieldValidator.validateLL24Offset(point.getNodeLat());
//            TimFieldValidator.validateLL24Offset(point.getNodeLong());
//            Node_LL_48B xy6 = new Node_LL_48B(OssOffsetLLB24.offsetLLB24(point.getNodeLat()),
//                  OssOffsetLLB24.offsetLLB24(point.getNodeLong()));
//            nodePoint.setNode_LL6(xy6);
//            break;
//         case "node-LatLon":
//            TimFieldValidator.validateLatitude(point.getNodeLat());
//            TimFieldValidator.validateLongitude(point.getNodeLong());
//            Node_LLmD_64b nodeLatLong = new Node_LLmD_64b(OssLongitude.longitude(point.getNodeLong()),
//                  OssLatitude.latitude(point.getNodeLat()));
//            nodePoint.setNode_LatLon(nodeLatLong);
//            break;
//         default:
//            break;
//         }
//
//         node.setDelta(nodePoint);
//         if (point.getAttributes() != null) {
//            NodeAttributeSetLL attributes = new NodeAttributeSetLL();
//
//            if (point.getAttributes().getLocalNodes().length > 0) {
//               NodeAttributeLLList localNodeList = new NodeAttributeLLList();
//               for (J2735TravelerInformationMessage.LocalNode localNode : point.getAttributes().getLocalNodes()) {
//                  localNodeList.add(new NodeAttributeLL(localNode.getType()));
//               }
//               attributes.setLocalNode(localNodeList);
//            }
//
//            if (point.getAttributes().getDisabledLists().length > 0) {
//               SegmentAttributeLLList disabledNodeList = new SegmentAttributeLLList();
//               for (J2735TravelerInformationMessage.DisabledList disabledList : point.getAttributes().getDisabledLists()) {
//                  disabledNodeList.add(new SegmentAttributeLL(disabledList.getType()));
//               }
//               attributes.setDisabled(disabledNodeList);
//            }
//
//            if (point.getAttributes().getEnabledLists().length > 0) {
//               SegmentAttributeLLList enabledNodeList = new SegmentAttributeLLList();
//               for (J2735TravelerInformationMessage.EnabledList enabledList : point.getAttributes().getEnabledLists()) {
//                  enabledNodeList.add(new SegmentAttributeLL(enabledList.getType()));
//               }
//               attributes.setEnabled(enabledNodeList);
//            }
//
//            if (point.getAttributes().getDataLists().length > 0) {
//               LaneDataAttributeList dataNodeList = new LaneDataAttributeList();
//               for (J2735TravelerInformationMessage.DataList dataList : point.getAttributes().getDataLists()) {
//
//                  LaneDataAttribute dataAttribute = new LaneDataAttribute();
//
//                  dataAttribute.setPathEndPointAngle(new DeltaAngle(dataList.getPathEndpointAngle()));
//                  dataAttribute.setLaneCrownPointCenter(OssRoadwayCrownAngle.roadwayCrownAngle(dataList.getLaneCrownCenter()));
//                  dataAttribute.setLaneCrownPointLeft(OssRoadwayCrownAngle.roadwayCrownAngle(dataList.getLaneCrownLeft()));
//                  dataAttribute.setLaneCrownPointRight(OssRoadwayCrownAngle.roadwayCrownAngle(dataList.getLaneCrownRight()));
//                  dataAttribute.setLaneAngle(OssMergeDivergeNodeAngle.mergeDivergeNodeAngle(dataList.getLaneAngle()));
//
//                  SpeedLimitList speedDataList = new SpeedLimitList();
//                  for (J2735TravelerInformationMessage.SpeedLimits speedLimit : dataList.getSpeedLimits()) {
//                     speedDataList.add(new RegulatorySpeedLimit(new SpeedLimitType(speedLimit.getType()),
//                           OssVelocity.velocity(speedLimit.getVelocity())));
//                  }
//
//                  dataAttribute.setSpeedLimits(speedDataList);
//                  dataNodeList.add(dataAttribute);
//               }
//
//               attributes.setData(dataNodeList);
//            }
//
//            attributes.setDWidth(OssOffsetB10.offsetB10(point.getAttributes().getdWidth()));
//            attributes.setDElevation(OssOffsetB10.offsetB10(point.getAttributes().getdElevation()));
//
//            node.setAttributes(attributes);
//         }
//         nodes.add(node);
//      }
//      nodeList.setNodes(nodes);
//      return nodeList;
//   }
//   
//   public static long getMinuteOfTheYear(String timestamp) throws ParseException {
//      ZonedDateTime start = DateTimeUtils.isoDateTime(timestamp);
//      long diff = DateTimeUtils.difference(DateTimeUtils.isoDateTime(start.getYear() + "-01-01T00:00:00+00:00"), start);
//      long minutes = diff / 60000;
//      TimFieldValidator.validateStartTime(minutes);
//      return minutes;
//   }
//   
//   public static HeadingSlice getHeadingSlice(String heading) {
//      return new HeadingSlice(CodecUtils.shortStringToByteArray(heading));
//   }
//   
//   public static MsgCRC getMsgCrc(String crc) {
//      return new MsgCRC(CodecUtils.shortStringToByteArray(crc));
//   }

}
