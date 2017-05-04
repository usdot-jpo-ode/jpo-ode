package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.time.format.DateTimeParseException;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.GeometricProjection;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion.Area;
import us.dot.its.jpo.ode.plugin.TravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.TravelerInformationMessage.DataFrame.Region.Circle;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;

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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(-32769);
      rl.setyOffset(0);
      rl.setzOffset(0);
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
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(0);
      rl.setyOffset(-32769);
      rl.setzOffset(0);
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
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(0);
      rl.setyOffset(0);
      rl.setzOffset(-32769);
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
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList rl = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] myList = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      rl.setxOffset(0);
      rl.setyOffset(0);
      rl.setzOffset(0);
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
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      J2735Position3D pos = new J2735Position3D((long) 0.0, (long) 0.0, (long) 0.0);
      c.setPosition(pos);
      c.setRadius(5);
      c.setUnits(10);
      g.setCircle(c);
      g.setDirection("1010101010101010");
      g.setExtent(-1);
      g.setLaneWidth(10);

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
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      J2735Position3D pos = new J2735Position3D((long) 0.0, (long) 0.0, (long) 0.0);
      c.setPosition(pos);
      c.setRadius(5);
      c.setUnits(6);
      g.setCircle(c);
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(10);

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
      TravelerInformationMessage.DataFrame.Region.OldRegion r = new TravelerInformationMessage.DataFrame.Region.OldRegion();
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY1");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY1");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeXY2() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY2");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY2");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY3");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY3");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY4");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY4");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY5");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY5");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY6");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY6");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LatLon");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LatLon");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeXYList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Ignore
   @Test
   public void checkBadNodeXYAttribute() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY6");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(0);
      n.setY(0);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL1");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL1");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLL2() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL2");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL2");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLL3() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL3");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL3");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkBadNodeLL4() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL4");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL4");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL5");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL5");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL6");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL6");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LatLon");
      n.setNodeLat(Long.MAX_VALUE);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LatLon");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;

      try {
         b.buildNodeLLList(node);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Ignore
   @Test
   public void checkBadNodeLLAttribute() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();

      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];

      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();

      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];

      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];

      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];

      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];

      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];

      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL6");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(Integer.MIN_VALUE);
      n.setY(Integer.MAX_VALUE);
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
   public void checkOldCircle() {
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      Area area = new Area();
      TravelerInformationMessage.DataFrame.Region.OldRegion r = new TravelerInformationMessage.DataFrame.Region.OldRegion();
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
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(33);
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderFurtherInfo() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("FurtherInfoID");
      df.setFurtherInfoID("0000000000000001");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(33);
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderNullFurtherInfo() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("FurtherInfoID");
      df.setFurtherInfoID(null);
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(33);
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderEmptyFurtherInfo() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("FurtherInfoID");
      df.setFurtherInfoID("");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(33);
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderBadMessageID() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
      df.setsspTimRights((short) 0);
      df.setFrameType(0);
      df.setMsgID("blah");
      df.setFurtherInfoID("0000000000000001");
      df.setStartDateTime("2017-12-01T17:47:11-05:00");
      df.setDurationTime(22);
      df.setPriority(0);
      df.setsspLocationRights((short) 3);
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("geometry");
      TravelerInformationMessage.DataFrame.Region.Geometry g = new TravelerInformationMessage.DataFrame.Region.Geometry();
      g.setDirection("1010101010101010");
      g.setExtent(1);
      g.setLaneWidth(33);
      TravelerInformationMessage.DataFrame.Region.Circle c = new TravelerInformationMessage.DataFrame.Region.Circle();
      c.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      c.setRadius(15);
      c.setUnits(7);
      g.setCircle(c);
      r.setGeometry(g);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathXY() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      TravelerInformationMessage.DataFrame.Region.Path p = new TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];
      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();
      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];
      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];
      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];
      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];
      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL1");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathLL() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      TravelerInformationMessage.DataFrame.Region.Path p = new TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("ll");
      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];
      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();
      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];
      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];
      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];
      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];
      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-LL1");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathComputedLaneLarge() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      TravelerInformationMessage.DataFrame.Region.Path p = new TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      TravelerInformationMessage.NodeXY[] node = null;
      TravelerInformationMessage.ComputedLane cl = new TravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(5);
      cl.setOffsetSmallX(3);
      cl.setOffsetLargeY(5);
      cl.setOffsetSmallY(3);
      cl.setxScale(2);
      cl.setyScale(2);
      p.setComputedLane(cl);
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkTravelerMessageBuilderPathComputedLaneSmall() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("path");
      TravelerInformationMessage.DataFrame.Region.Path p = new TravelerInformationMessage.DataFrame.Region.Path();
      p.setScale(2);
      p.setType("xy");
      TravelerInformationMessage.NodeXY[] node = null;
      TravelerInformationMessage.ComputedLane cl = new TravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(0);
      cl.setOffsetSmallX(3);
      cl.setOffsetLargeY(0);
      cl.setOffsetSmallY(3);
      cl.setxScale(2);
      cl.setyScale(2);
      p.setComputedLane(cl);
      p.setNodes(node);
      r.setPath(p);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }

   @Test
   public void checkOldRegion() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      TravelerInformationMessage.DataFrame.Region.OldRegion or = new TravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("regionPointSet");
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint rp = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint();
      rp.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      rp.setScale(0);
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[] rList = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList[1];
      TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList re = new TravelerInformationMessage.DataFrame.Region.OldRegion.RegionPoint.RegionList();
      re.setxOffset(-36);
      re.setyOffset(21);
      re.setzOffset(0);
      rList[0] = re;
      rp.setRegionList(rList);
      or.setRegionPoint(rp);
      r.setOldRegion(or);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }

   @Test
   public void checkShapepointXY() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      TravelerInformationMessage.DataFrame.Region.OldRegion or = new TravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("shapePointSet");
      TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint sp = new TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint();
      sp.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      sp.setLaneWidth(10);
      sp.setDirectionality(2);
      sp.setNodeType("xy");
      TravelerInformationMessage.NodeXY n = new TravelerInformationMessage.NodeXY();
      TravelerInformationMessage.NodeXY[] node = new TravelerInformationMessage.NodeXY[1];
      TravelerInformationMessage.Attributes at = new TravelerInformationMessage.Attributes();
      TravelerInformationMessage.LocalNode ln = new TravelerInformationMessage.LocalNode();
      TravelerInformationMessage.LocalNode[] lnode = new TravelerInformationMessage.LocalNode[1];
      TravelerInformationMessage.DisabledList dl = new TravelerInformationMessage.DisabledList();
      TravelerInformationMessage.DisabledList[] dlist = new TravelerInformationMessage.DisabledList[1];
      TravelerInformationMessage.EnabledList el = new TravelerInformationMessage.EnabledList();
      TravelerInformationMessage.EnabledList[] elist = new TravelerInformationMessage.EnabledList[1];
      TravelerInformationMessage.DataList dataL = new TravelerInformationMessage.DataList();
      TravelerInformationMessage.DataList[] dataList = new TravelerInformationMessage.DataList[1];
      TravelerInformationMessage.SpeedLimits sl = new TravelerInformationMessage.SpeedLimits();
      TravelerInformationMessage.SpeedLimits[] slimits = new TravelerInformationMessage.SpeedLimits[1];
      sl.setType(1);
      sl.setVelocity(1);
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
      at.setdWidth(10);
      at.setdElevation(10);
      n.setDelta("node-XY1");
      n.setNodeLat(10);
      n.setNodeLong(10);
      n.setX(10);
      n.setY(10);
      n.setAttributes(at);
      node[0] = n;
      sp.setNodexy(node);
      or.setShapepoint(sp);
      r.setOldRegion(or);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         fail("Unexpected exception");
      }
   }
   
   @Test
   public void checkShapepointComputedLane() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformationMessage.TIM tim = new TravelerInformationMessage.TIM();
      tim.setMsgCnt(10);
      tim.setTimeStamp("2017-03-13T01:07:11-05:00");
      tim.setUrlB("www");
      tim.setPacketID(10);
      TravelerInformationMessage.DataFrame[] dframes = new TravelerInformationMessage.DataFrame[1];
      TravelerInformationMessage.DataFrame df = new TravelerInformationMessage.DataFrame();
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
      TravelerInformationMessage.DataFrame.Region[] reg = new TravelerInformationMessage.DataFrame.Region[1];
      TravelerInformationMessage.DataFrame.Region r = new TravelerInformationMessage.DataFrame.Region();
      df.setsspMsgTypes((short) 2);
      df.setsspMsgContent((short) 3);
      df.setUrl("www");
      r.setName("bob");
      r.setRegulatorID(23);
      r.setSegmentID(33);
      r.setAnchorPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      r.setLaneWidth(7);
      r.setDirectionality((long) 3);
      r.setClosedPath(false);
      r.setDirection("1010101010101010");
      r.setDescription("oldRegion");
      TravelerInformationMessage.DataFrame.Region.OldRegion or = new TravelerInformationMessage.DataFrame.Region.OldRegion();
      or.setDirection("1010101010101010");
      or.setExtent(1);
      or.setArea("shapePointSet");
      TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint sp = new TravelerInformationMessage.DataFrame.Region.OldRegion.ShapePoint();
      sp.setPosition(new J2735Position3D((long) -41.678473, (long) -108.782775, (long) 917.1432));
      sp.setLaneWidth(10);
      sp.setDirectionality(2);
      sp.setNodeType("xy");
      TravelerInformationMessage.NodeXY[] node = null;
      TravelerInformationMessage.ComputedLane cl = new TravelerInformationMessage.ComputedLane();
      cl.setAngle(5);
      cl.setLaneID(1);
      cl.setOffsetLargeX(5);
      cl.setOffsetSmallX(3);
      cl.setOffsetLargeY(5);
      cl.setOffsetSmallY(3);
      cl.setxScale(2);
      cl.setyScale(2);
      sp.setComputedLane(cl);
      sp.setNodexy(node);
      or.setShapepoint(sp);
      r.setOldRegion(or);
      reg[0] = r;
      df.setRegions(reg);
      dframes[0] = df;
      tim.setDataframes(dframes);
      ti.setTim(tim);
      OssTravelerMessageBuilder b = new OssTravelerMessageBuilder();
      try {
         b.buildTravelerInformation(ti);
      } catch (Exception e) {
         e.printStackTrace();
         fail("Unexpected Exception");
      }
   }
}