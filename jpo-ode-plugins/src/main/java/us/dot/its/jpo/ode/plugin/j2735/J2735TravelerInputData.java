package us.dot.its.jpo.ode.plugin.j2735;

import java.util.Arrays;

import us.dot.its.jpo.ode.model.OdeObject;

public class J2735TravelerInputData extends OdeObject {

   private static final long serialVersionUID = 8769107278440796699L;

   public TIM tim;
   public RSU[] rsus;
   public SNMP snmp;
   public ODE ode;
   public SDW sdw;

   public static class ODE {
      public int version = 1;
   }
   
   public static class SDW {
      public enum TimeToLive {
         oneMinute,
         ThirtyMinutes,
         oneDay,
         oneWeek,
         oneMonth,
         oneYear
      }

      public J2735GeoRegion serviceRegion;
      public TimeToLive ttl = TimeToLive.ThirtyMinutes;
   }
   
   public static class SNMP {
      public String rsuid;
      public int msgid;
      public int mode;
      public int channel;
      public int interval;
      public String deliverystart;
      public String deliverystop;
      public int enable;
      public int status;
   }

   public static class RSU {
      public String target;
      public String username;
      public String password;
      public int retries;
      public int timeout;
   }

   public static class TIM { 
      public int msgCnt;
      public String timeStamp;
      public int packetID;
      public String urlB;
      public DataFrame[] dataframes;
   }

   public static class ComputedLane {
      public int laneID;
      public int offsetSmallX;
      public int offsetLargeX;
      public int offsetSmallY;
      public int offsetLargeY;
      public int angle;
      public int xScale;
      public int yScale;
   }

   public static class NodeXY {
      public String delta;
      public long nodeLat;
      public long nodeLong;
      public int x;
      public int y;
      public Attributes attributes;

   }

   public static class LocalNode {
      public long type;
   }

   public static class DisabledList {
      public long type;
   }

   public static class EnabledList {
      public long type;
   }

   public static class SpeedLimits {
      public long type;
      public int velocity;
   }

   public static class DataList {
      public int pathEndpointAngle;
      public int laneCrownCenter;
      public int laneCrownLeft;
      public int laneCrownRight;
      public int laneAngle;
      public SpeedLimits[] speedLimits;
   }

   public static class Attributes {
      public LocalNode[] localNodes;
      public DisabledList[] disabledLists;
      public EnabledList[] enabledLists;
      public DataList[] dataLists;
      public int dWidth;
      public int dElevation;
   }

   public static class LaneNode {
      public int nodeNumber;
      public double nodeLat;
      public double nodeLong;
      public double nodeElevation;
      public short laneWidth; // lane width delta -512 to 511

      @Override
      public String toString() {
         return "LaneNode [nodeNumber=" + nodeNumber + ", nodeLat=" + nodeLat + ", nodeLong=" + nodeLong
               + ", nodeElevation=" + nodeElevation + ", laneWidth=" + laneWidth + "]";
      }
   }

   public static class DataFrame {
      public short sspTimRights;// Start Header Information
      public int frameType;
      public String msgID;
      public J2735Position3D position;
      public String viewAngle;
      public int mutcd;
      public int crc;
      public String startDateTime;
      public int durationTime;
      public int priority;// End header Information
      public short sspLocationRights;// Start Region Information
      public Region[] regions;
      public short sspMsgTypes;// Start content Information
      public short sspMsgContent;
      public String content;
      public String[] items;
      public String url;// End content Information

      public static class Region {
         public String name;
         public int regulatorID;
         public int segmentID;
         public J2735Position3D anchorPosition;
         public int laneWidth;
         public long directionality;
         public boolean closedPath;
         public String direction;
         public String regionType;
         public String description;
         public Path path;
         public Geometry geometry;
         public OldRegion oldRegion;

         public static class OldRegion {
            public String direction;
            public int extent;
            public String area;
            public ShapePoint shapepoint;
            public Circle circle;
            public RegionPoint regionPoint;

            public static class ShapePoint {
               public J2735Position3D position;
               public int laneWidth;
               public int directionality;
               public String nodeType;
               public ComputedLane computedLane;
               public NodeXY[] nodexy;
            }

            public static class RegionPoint {
               public J2735Position3D position;
               public int scale;
               public RegionList[] regionList;

               public static class RegionList {
                  public int xOffset;
                  public int yOffset;
                  public int zOffset;
               }
            }
         }

         public static class Geometry {
            public String direction;
            public int extent;
            public int laneWidth;
            public Circle circle;
         }

         public static class Circle {
            public J2735Position3D position;
            public int radius;
            public int units;
         }

         public static class Path {
            public int scale;
            public String type;
            public NodeXY[] nodes;
            public ComputedLane computedLane;

         }

         @Override
         public String toString() {
            return "Region [regionType=" + regionType + ", laneNodes=" + ", extent=" +
            // + ", refPoint=" + refPoint
                  "]";
         }
      }

      @Override
      public String toString() {
         return "Frame [name=" + ", referencePosition=" + position.toJson() + ", sspTimRights=" + sspTimRights + ", sspTypeRights=" + sspMsgTypes + ", sspContentRights="
               + sspMsgContent + ", sspLocationRights=" + sspLocationRights + ", content=" + content + ", items="
               + Arrays.toString(items) + ", mutcd=" + mutcd + ", priority=" + priority + ", startDateTime=" + startDateTime;
         // + ", regions="+Arrays.toString(regions)+"]";
      }
   }

   public static class RoadSign {
      public J2735Position3D position;
      public String[] viewAngle;
      public int mutcdCode;
      public byte[] msgCrc;

      @Override
      public String toString() {
         return "Road Sign [position=" + position.toJson()
               + ", viewAngle=" + viewAngle + ", mutcdCode=" + mutcdCode + ", msgCrc=" + msgCrc + "]";
      }
   }

}
