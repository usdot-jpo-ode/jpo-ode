package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeParseException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.GeometricProjection;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCRC;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion.Area;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.MsgId;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.Region.Circle;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.RoadSignID;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.FrameType;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MutcdCode;

public class OssTravelerMessageBuilderTest {

   @Test
   public void checkMinuteOfYear() {

      try {
         OssTravelerMessageBuilder.getMinuteOfTheYear("2017-12-01T17:47:11-05:00");
      } catch (ParseException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadMinuteOfYear() {
      try {
         OssTravelerMessageBuilder.getMinuteOfTheYear("hi");
         fail("Expected DateTimeParseException");
      } catch (DateTimeParseException | ParseException e) {
         assertEquals(DateTimeParseException.class, e.getClass());
      }
   }
   
   @Test
   public void checknullHeadingSlice() {
      String str = null;
      try {
         OssTravelerMessageBuilder.getHeadingSlice(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyHeadingSlice() {
      String str = "";
      try {
         OssTravelerMessageBuilder.getHeadingSlice(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkNullMessageCRC() {
      String testInput = null;
      MsgCRC expectedValue = new MsgCRC(new byte[2]);
      try {
         assertEquals(expectedValue, OssTravelerMessageBuilder.getMsgCrc(testInput));
      } catch (RuntimeException e) {
         fail("Unexpected exception " + e);
      }
   }

   @Test
   public void checkEmptyMessageCRC() {
      String testInput = "";
      MsgCRC expectedValue = new MsgCRC(new byte[2]);
      try {
         assertEquals(expectedValue, OssTravelerMessageBuilder.getMsgCrc(testInput));
      } catch (RuntimeException e) {
         fail("Unexpected exception " + e);
      }
   }

   @Test
   public void checkMessageCRCBinary() {
      String testInput = "0001001000110100"; // 0x1234 in binary
      String expectedValue = "1234";
      try {
         assertEquals(expectedValue, DatatypeConverter.printHexBinary(OssTravelerMessageBuilder.getMsgCrc(testInput).byteArrayValue()));
      } catch (RuntimeException e) {
         fail("Unexpected exception " + e);
      }
   }
   
   @Test
   public void checkMessageCRCBinaryUpperBound() {
      String testInput = "1111111111111111"; // 0x1234 in binary
      String expectedValue = "FFFF";
      try {
         assertEquals(expectedValue, DatatypeConverter.printHexBinary(OssTravelerMessageBuilder.getMsgCrc(testInput).byteArrayValue()));
      } catch (RuntimeException e) {
         fail("Unexpected exception " + e);
      }
   }
   
   @Test
   public void checkMessageCRCHex() {
      String testInput = "15AF";
      MsgCRC expectedValue = new MsgCRC(DatatypeConverter.parseHexBinary(testInput));
      try {
         assertEquals(expectedValue, OssTravelerMessageBuilder.getMsgCrc(testInput));
      } catch (RuntimeException e) {
         fail("Unexpected exception " + e);
      }
   }
   
   @Test
   public void checkMessageCRCInvalidLength() {
      String testInput = "15AFA";
      try {
         OssTravelerMessageBuilder.getMsgCrc(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }
   
   @Test
   public void checkContentAdvisory() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Advisory");
      String[] codes = { "250", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadContentAdvisory() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Advisory");
      String[] codes = { "-1", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkContentWorkZone() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Work Zone");
      String[] codes = { "250", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadContentWorkZone() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Work Zone");
      String[] codes = { "-1", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkContentSpeedLimit() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Speed Limit");
      String[] codes = { "250", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadContentSpeedLimit() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Speed Limit");
      String[] codes = { "-1", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkContentExitService() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Exit Service");
      String[] codes = { "250", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadContentExitService() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Exit Service");
      String[] codes = { "-1", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkContentGenericSignage() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Generic Signage");
      String[] codes = { "250", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadContentGenericSignage() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      TravelerDataFrame dataFrame = new TravelerDataFrame();
      df.setContent("Generic Signage");
      String[] codes = { "-1", "10" };
      df.setItems(codes);

      try {
         dataFrame.setContent(b.buildContent(df));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBadXRegionOffset() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(BigDecimal.valueOf(-0.0032769));
      rl.setyOffset(BigDecimal.ZERO);
      rl.setzOffset(BigDecimal.ZERO);
      myList[0] = rl;

      try {
         b.buildRegionOffsets(myList);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBadYRegionOffset() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(BigDecimal.ZERO);
      rl.setyOffset(BigDecimal.valueOf(-0.0032769));
      rl.setzOffset(BigDecimal.ZERO);
      myList[0] = rl;

      try {
         b.buildRegionOffsets(myList);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBadZRegionOffset() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(BigDecimal.ZERO);
      rl.setyOffset(BigDecimal.ZERO);
      rl.setzOffset(BigDecimal.valueOf(-0.0032769));
      myList[0] = rl;

      try {
         b.buildRegionOffsets(myList);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkRegionOffset() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(BigDecimal.ZERO);
      rl.setyOffset(BigDecimal.ZERO);
      rl.setzOffset(BigDecimal.ZERO);
      myList[0] = rl;

      try {
         b.buildRegionOffsets(myList);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadGeoCircle() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      GeometricProjection geo = new GeometricProjection();
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      OdePosition3D pos = new OdePosition3D(BigDecimal.valueOf(1.1d), BigDecimal.valueOf(2.2d), BigDecimal.valueOf(3.3d));
      c.setPosition(pos);
      c.setRadius(5);
      c.setUnits(10);
      g.setCircle(c);
      g.setDirection("1010101010101010");
      g.setExtent(-1);
      g.setLaneWidth(BigDecimal.valueOf(10));

      try {
         geo.setCircle(b.buildGeoCircle(g));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkGeoCircle() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      GeometricProjection geo = new GeometricProjection();
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      OdePosition3D pos = new OdePosition3D(BigDecimal.valueOf(1.1d), BigDecimal.valueOf(2.2d), BigDecimal.valueOf(3.3d));
      c.setPosition(pos);
      c.setRadius(5);
      c.setUnits(6);
      g.setCircle(c);
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(10));

      try {
         geo.setCircle(b.buildGeoCircle(g));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadOldCircle() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      Area area = new Area();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion r = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion();
      OdePosition3D pos = new OdePosition3D(BigDecimal.valueOf(1.1d), BigDecimal.valueOf(2.2d), BigDecimal.valueOf(3.3d));
      Circle c = new Circle();
      c.setPosition(pos);
      c.setRadius(3);
      c.setUnits(80);
      r.setCircle(c);
      try {
         area.setCircle(b.buildOldCircle(r));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBadNodeXY1() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      dataL.setPathEndpointAngle(1);
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY1");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXY1() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY1");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.ONE);
      n.setY(BigDecimal.ONE);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception: " + e);      }
   }

   @Test
   public void checkBadNodeXY2() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY2");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.valueOf(1234567890));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXY2() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY2");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeXY3() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY3");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXY3() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY3");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeXY4() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY4");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXY4() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY4");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeXY5() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY5");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXY5() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY5");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeXY6() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY6");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXY6() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY6");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeXYLatLon() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LatLon");
      n.setNodeLat(BigDecimal.valueOf(1234667909));
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeXYLatLon() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LatLon");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   
   @Test
   public void checkBadNodeXYAttribute() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY6");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setY(BigDecimal.ZERO);
      n.setAttributes(null);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBadNodeLL1() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL1");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLL1() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL1");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception: " + e);
      }
   }

   @Test
   public void checkBadNodeLL2() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL2");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLL2() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL2");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception: " + e);      }
   }

   @Test
   public void checkBadNodeLL3() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL3");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLL3() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL3");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception: " + e);
      }
   }

   @Test
   public void checkBadNodeLL4() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL4");
      n.setNodeLat(BigDecimal.valueOf(Long.MAX_VALUE));
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLL4() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL4");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLL5() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL5");
      n.setNodeLat(BigDecimal.valueOf(Long.MAX_VALUE));
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLL5() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL5");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLL6() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL6");
      n.setNodeLat(BigDecimal.valueOf(Long.MAX_VALUE));
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLL6() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL6");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLLLatLon() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LatLon");
      n.setNodeLat(BigDecimal.valueOf(Long.MAX_VALUE));
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNodeLLLatLon() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LatLon");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLLAttribute() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];

      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();

      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];

      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];

      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];

      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];

      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);

      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL6");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.valueOf(Integer.MIN_VALUE));
      n.setY(BigDecimal.valueOf(Integer.MAX_VALUE));
      n.setAttributes(null);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void checkOldCircle() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      Area area = new Area();
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion r = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion();
      OdePosition3D pos = new OdePosition3D(BigDecimal.valueOf(1.1d), BigDecimal.valueOf(2.2d), BigDecimal.valueOf(3.3d));
      Circle c = new Circle();
      c.setPosition(pos);
      c.setRadius(3);
      c.setUnits(6);
      r.setCircle(c);
      try {
         area.setCircle(b.buildOldCircle(r));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkTravelerMessageBuilderRoadSignId() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D( BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderFurtherInfo() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      MsgId msgId = new MsgId();
      msgId.setFurtherInfoID("ABCD");
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderNullFurtherInfo() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      MsgId msgId = new MsgId();
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderEmptyFurtherInfo() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      MsgId msgId = new MsgId();
      msgId.setFurtherInfoID("");
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderNullMsgID() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      df.setMsgId(null);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      OdeTravelerInformationMessage.DataFrame.Region.Geometry g = new OdeTravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      OdeTravelerInformationMessage.DataFrame.Region.Circle c = new OdeTravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathXY() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      OdeTravelerInformationMessage.DataFrame.Region.Path p = new OdeTravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];
      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();
      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];
      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];
      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];
      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];
      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);
      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL1");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathLL() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      OdeTravelerInformationMessage.DataFrame.Region.Path p = new OdeTravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("ll");
      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];
      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();
      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];
      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];
      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];
      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];
      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);
      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-LL1");
      n.setNodeLat(BigDecimal.valueOf(0.000123));
      n.setNodeLong(BigDecimal.valueOf(0.000123));
      n.setX(BigDecimal.TEN);
      n.setY(BigDecimal.TEN);
      n.setAttributes(at);
      node[0] = n;
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathComputedLaneLarge() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      OdeTravelerInformationMessage.DataFrame.Region.Path p = new OdeTravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      OdeTravelerInformationMessage.NodeXY[] node = null;
      OdeTravelerInformationMessage.ComputedLane cl = new OdeTravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(BigDecimal.valueOf(5));
      cl.setOffsetSmallX(BigDecimal.valueOf(3));
      cl.setOffsetLargeY(BigDecimal.valueOf(5));
      cl.setOffsetSmallY(BigDecimal.valueOf(3));
      cl.setxScale(BigDecimal.valueOf(2));
      cl.setyScale(BigDecimal.valueOf(2));
      p.setComputedLane(cl);
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathComputedLaneSmall() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      OdeTravelerInformationMessage.DataFrame.Region.Path p = new OdeTravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      OdeTravelerInformationMessage.NodeXY[] node = null;
      OdeTravelerInformationMessage.ComputedLane cl = new OdeTravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(BigDecimal.ZERO);
      cl.setOffsetSmallX(BigDecimal.valueOf(3));
      cl.setOffsetLargeY(BigDecimal.ZERO);
      cl.setOffsetSmallY(BigDecimal.valueOf(3));
      cl.setxScale(BigDecimal.valueOf(2));
      cl.setyScale(BigDecimal.valueOf(2));
      p.setComputedLane(cl);
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }

   @Test
   public void checkOldRegion() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion or = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("regionPointSet");
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint rp = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint();
      rp.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      rp.setScale(0);
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] rList = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList re = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      re.setxOffset(BigDecimal.valueOf(-0.000036));
      re.setyOffset(BigDecimal.valueOf(0.000021));
      re.setzOffset(BigDecimal.ZERO);
      rList[0] = re;
      rp.setRegionList(rList);
      or.setRegionPoint(rp);
      r.setOldRegion(or);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void checkShapepointXY() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion or = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("shapePointSet");
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint sp = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint();
      sp.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      sp.setLaneWidth(BigDecimal.valueOf(10));
      sp.setDirectionality(2);
      sp.setNodeType("xy");
      OdeTravelerInformationMessage.NodeXY n = new OdeTravelerInformationMessage.NodeXY();
      OdeTravelerInformationMessage.NodeXY[] node = new OdeTravelerInformationMessage.NodeXY[1];
      OdeTravelerInformationMessage.Attributes at = new OdeTravelerInformationMessage.Attributes();
      OdeTravelerInformationMessage.LocalNode ln = new OdeTravelerInformationMessage.LocalNode();
      OdeTravelerInformationMessage.LocalNode[] lnode = new OdeTravelerInformationMessage.LocalNode[1];
      OdeTravelerInformationMessage.DisabledList dl = new OdeTravelerInformationMessage.DisabledList();
      OdeTravelerInformationMessage.DisabledList[] dlist = new OdeTravelerInformationMessage.DisabledList[1];
      OdeTravelerInformationMessage.EnabledList el = new OdeTravelerInformationMessage.EnabledList();
      OdeTravelerInformationMessage.EnabledList[] elist = new OdeTravelerInformationMessage.EnabledList[1];
      OdeTravelerInformationMessage.DataList dataL = new OdeTravelerInformationMessage.DataList();
      OdeTravelerInformationMessage.DataList[] dataList = new OdeTravelerInformationMessage.DataList[1];
      OdeTravelerInformationMessage.SpeedLimits sl = new OdeTravelerInformationMessage.SpeedLimits();
      OdeTravelerInformationMessage.SpeedLimits[] slimits = new OdeTravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownCenter(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownLeft(BigDecimal.valueOf(1.5));
      dataL.setLaneCrownRight(BigDecimal.valueOf(1.5));
      slimits[0] = sl;
      dataL.setSpeedLimits(slimits);
      ln.setType(1);
      lnode[0] = ln;
      at.setLocalNodes(lnode);
      dl.setType(1);
      dlist[0] = dl;
      at.setDisabledLists(dlist);
      el.setType(1);
      elist[0] = el;
      at.setEnabledLists(elist);
      dataList[0] = dataL;
      at.setDataLists(dataList);
      at.setdWidth(BigDecimal.TEN);
      at.setdElevation(BigDecimal.TEN);
      n.setDelta("node-XY1");
      n.setNodeLat(BigDecimal.TEN);
      n.setNodeLong(BigDecimal.TEN);
      n.setX(BigDecimal.ONE);
      n.setY(BigDecimal.ONE);
      n.setAttributes(at);
      node[0] = n;
      sp.setNodexy(node);
      or.setShapepoint(sp);
      r.setOldRegion(or);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         fail("Unexpected exception: " +e);
      }
   }
   
   @Test
   public void checkShapepointComputedLane() {
      OdeTravelerInformationMessage tim = new OdeTravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID("FEDCBA987654321000");
      OdeTravelerInformationMessage.DataFrame[] dframes = new OdeTravelerInformationMessage.DataFrame[1];
      OdeTravelerInformationMessage.DataFrame df = new OdeTravelerInformationMessage.DataFrame();
      df.setSspTimRights((short) 0);
      df.setFrameType(FrameType.TravelerInfoType.advisory);
      
      RoadSignID roadSignID = new RoadSignID(); 
      roadSignID.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      roadSignID.setViewAngle("1010101010101010");
      roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.guide);
      roadSignID.setCrc("1111111111111111");
      
      MsgId msgId = new MsgId();
      msgId.setRoadSignID(roadSignID);
      
      df.setMsgId(msgId);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setSspLocationRights((short) 3);
      OdeTravelerInformationMessage.DataFrame.Region[] reg = new OdeTravelerInformationMessage.DataFrame.Region[1];
      OdeTravelerInformationMessage.DataFrame.Region r = new OdeTravelerInformationMessage.DataFrame.Region();
      df.setSspMsgTypes((short) 2);
      df.setSspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion or = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("shapePointSet");
      OdeTravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint sp = new OdeTravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint();
      sp.setPosition(new OdePosition3D(BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      sp.setLaneWidth(BigDecimal.valueOf(10));
      sp.setDirectionality(2);
      sp.setNodeType("xy");
      OdeTravelerInformationMessage.NodeXY[] node = null;
      OdeTravelerInformationMessage.ComputedLane cl = new OdeTravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(BigDecimal.valueOf(5));
      cl.setOffsetSmallX(BigDecimal.valueOf(3));
      cl.setOffsetLargeY(BigDecimal.valueOf(5));
      cl.setOffsetSmallY(BigDecimal.valueOf(3));
      cl.setxScale(BigDecimal.valueOf(2));
      cl.setyScale(BigDecimal.valueOf(2));
      sp.setComputedLane(cl);
      sp.setNodexy(node);
      or.setShapepoint(sp);
      r.setOldRegion(or);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(tim);
      } catch (Exception e) {
         e.printStackTrace();
         fail("Unexpected Exception");
      }
   }
}