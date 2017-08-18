package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeParseException;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.GeometricProjection;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion.Area;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage.DataFrame.Region.Circle;

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
   public void checknullMessageCRC() {
      String str = null;
      try {
         OssTravelerMessageBuilder.getMsgCrc(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyMessageCRC() {
      String str = "";
      try {
         OssTravelerMessageBuilder.getMsgCrc(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkMessageCRC() {
      String str = "1010101010101010";
      try {
         OssTravelerMessageBuilder.getMsgCrc(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkContentAdvisory() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
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
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
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
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
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
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
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
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
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
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      J2735Position3D pos = new J2735Position3D((long) 0.0, (long) 0.0, (long) 0.0);
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
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      J2735Position3D pos = new J2735Position3D((long) 0.0, (long) 0.0, (long) 0.0);
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
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion r = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion();
      J2735Position3D pos = new J2735Position3D((long) 0.0, (long) 0.0, (long) 0.0);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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

      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];

      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();

      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];

      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];

      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];

      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];

      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion r = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion();
      J2735Position3D pos = new J2735Position3D((long) 0.0, (long) 0.0, (long) 0.0);
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
   public void checkTravelerMessageBuilder() {
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D( BigDecimal.valueOf(-41.678473), BigDecimal.valueOf(-108.782775), BigDecimal.valueOf(917.1432)));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("FurtherInfoID");
      df.setFurtherInfoID("0000000000000001");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("FurtherInfoID");
      df.setFurtherInfoID(null);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
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
   public void checkTravelerMessageBuilderEmptyFurtherInfo() {
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("FurtherInfoID");
      df.setFurtherInfoID("");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
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
   public void checkTravelerMessageBuilderBadMessageID() {
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("blah");
      df.setFurtherInfoID("0000000000000001");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      J2735TravelerInformationMessage.DataFrame.Region.Geometry g = new J2735TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(BigDecimal.valueOf(33));
      J2735TravelerInformationMessage.DataFrame.Region.Circle c = new J2735TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      J2735TravelerInformationMessage.DataFrame.Region.Path p = new J2735TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];
      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();
      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];
      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];
      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];
      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];
      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      J2735TravelerInformationMessage.DataFrame.Region.Path p = new J2735TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("ll");
      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];
      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();
      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];
      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];
      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];
      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];
      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      J2735TravelerInformationMessage.DataFrame.Region.Path p = new J2735TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      J2735TravelerInformationMessage.NodeXY[] node = null;
      J2735TravelerInformationMessage.ComputedLane cl = new J2735TravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(5);
      cl.setOffsetSmallX(3);
      cl.setOffsetLargeY(5);
      cl.setOffsetSmallY(3);
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      J2735TravelerInformationMessage.DataFrame.Region.Path p = new J2735TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      J2735TravelerInformationMessage.NodeXY[] node = null;
      J2735TravelerInformationMessage.ComputedLane cl = new J2735TravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(0);
      cl.setOffsetSmallX(3);
      cl.setOffsetLargeY(0);
      cl.setOffsetSmallY(3);
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion or = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("regionPointSet");
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint rp = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint();
      rp.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      rp.setScale(0);
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] rList = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList re = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion or = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("shapePointSet");
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint sp = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint();
      sp.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      sp.setLaneWidth(BigDecimal.valueOf(10));
      sp.setDirectionality(2);
      sp.setNodeType("xy");
      J2735TravelerInformationMessage.NodeXY n = new J2735TravelerInformationMessage.NodeXY();
      J2735TravelerInformationMessage.NodeXY[] node = new J2735TravelerInformationMessage.NodeXY[1];
      J2735TravelerInformationMessage.Attributes at = new J2735TravelerInformationMessage.Attributes();
      J2735TravelerInformationMessage.LocalNode ln = new J2735TravelerInformationMessage.LocalNode();
      J2735TravelerInformationMessage.LocalNode[] lnode = new J2735TravelerInformationMessage.LocalNode[1];
      J2735TravelerInformationMessage.DisabledList dl = new J2735TravelerInformationMessage.DisabledList();
      J2735TravelerInformationMessage.DisabledList[] dlist = new J2735TravelerInformationMessage.DisabledList[1];
      J2735TravelerInformationMessage.EnabledList el = new J2735TravelerInformationMessage.EnabledList();
      J2735TravelerInformationMessage.EnabledList[] elist = new J2735TravelerInformationMessage.EnabledList[1];
      J2735TravelerInformationMessage.DataList dataL = new J2735TravelerInformationMessage.DataList();
      J2735TravelerInformationMessage.DataList[] dataList = new J2735TravelerInformationMessage.DataList[1];
      J2735TravelerInformationMessage.SpeedLimits sl = new J2735TravelerInformationMessage.SpeedLimits();
      J2735TravelerInformationMessage.SpeedLimits[] slimits = new J2735TravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(BigDecimal.ONE);
      dataL.setLaneAngle(1);
      dataL.setLaneCrownCenter(1);
      dataL.setLaneCrownLeft(1);
      dataL.setLaneCrownRight(1);
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
      J2735TravelerInformationMessage tim = new J2735TravelerInformationMessage();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      J2735TravelerInformationMessage.DataFrame[] dframes = new J2735TravelerInformationMessage.DataFrame[1];
      J2735TravelerInformationMessage.DataFrame df = new J2735TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("RoadSignID");
      df.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      df.setViewAngle("1010101010101010");
      df.setMutcd(5);
      df.setCrc("1111111111111111");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      J2735TravelerInformationMessage.DataFrame.Region[] reg = new J2735TravelerInformationMessage.DataFrame.Region[1];
      J2735TravelerInformationMessage.DataFrame.Region r = new J2735TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(BigDecimal.valueOf(7));
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion or = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("shapePointSet");
      J2735TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint sp = new J2735TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint();
      sp.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      sp.setLaneWidth(BigDecimal.valueOf(10));
      sp.setDirectionality(2);
      sp.setNodeType("xy");
      J2735TravelerInformationMessage.NodeXY[] node = null;
      J2735TravelerInformationMessage.ComputedLane cl = new J2735TravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(5);
      cl.setOffsetSmallX(3);
      cl.setOffsetLargeY(5);
      cl.setOffsetSmallY(3);
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