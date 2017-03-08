package us.dot.its.jpo.ode.traveler;

import us.dot.its.jpo.ode.model.OdeObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
/**
 * Created by anthonychen on 2/17/17.
 */

public class TravelerInputData extends OdeObject {

//    private static final Logger logger = Logger.getLogger(TravelerInputData.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm a");

    public int MsgCount;
    public String MinuteOfTheYear;
    public int UniqueMSGID;
    public String urlB;
    public DataFrame[] dataframes;
    public RSUs[] rsus;
    public SNMP snmp;
    
    public static class SNMP {
       public int rsuid;
       public int msgid;
       public int mode;
       public int channel;
       public int interval;
       public String deliverystart;
       public String deliverystop;
       public int enable;
       public int status;
    }
    
    public static class RSUs {
       public String target;
       public String username;
       public String password;
       public String retries;
       public int timeout;
       
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
        public int nodeLat;
        public int nodeLong;
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
        public short laneWidth;			// lane width delta -512 to 511

        @Override
        public String toString() {
            return "LaneNode [nodeNumber=" + nodeNumber
                    + ", nodeLat=" + nodeLat
                    + ", nodeLong=" + nodeLong
                    + ", nodeElevation=" + nodeElevation
                    + ", laneWidth=" + laneWidth
                    + "]";
        }
    }

    public static class DataFrame {
       public short sspTimRights;//Start Header Information
       public int  frameType;
       public String msgID;
       public long latitude;
       public long longitude;
       public long elevation;
       public String viewAngle;
       public int mutcd;
       public int crc;
       public String startYear;
       public String startTime;
       public int durationTime;
       public int priority;//End header Information
       public short sspLocationRights;//Start Region Information
       public Region[] regions;
       public short sspMsgTypes;//Start content Information
       public short sspMsgContent;
       public String content;
       public String[] items;
       public String url;//End content Information
       
       public static class Region {
          public String name;
          public int regulatorID;
          public int segmentID;
          public long anchor_lat;
          public long anchor_long;
          public long anchor_elevation;
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
                public int latitude;
                public int longitude;
                public int elevation;
                public int laneWidth;
                public int directionality;
                public String nodeType;
                public ComputedLane computedLane;
                public NodeXY[] nodexy;
             }
             public static class RegionPoint {
                public int latitude;
                public int longitude;
                public int elevation;
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
             public int latitude;
             public int longitude;
             public int elevation;
             public int radius;
             public int units;
          }
          
          public static class Path {
              public int scale;
              public String type;
              public NodeXY[] nodes;

          }
          @Override
          public String toString() {
              return "Region [regionType=" + regionType
                      + ", laneNodes="
                      + ", extent=" +
//                      + ", refPoint=" + refPoint
                       "]";
          }
      }
        
        @Override
        public String toString() {
            return "Frame [name=" + ", referenceLat="
                    + latitude + ", referenceLon=" + longitude
                    + ", referenceElevation=" + elevation
                    + ", sspTimRights=" + sspTimRights
                    + ", sspTypeRights=" + sspMsgTypes
                    + ", sspContentRights=" + sspMsgContent
                    + ", sspLocationRights=" + sspLocationRights
                    + ", content=" + content
                    + ", items=" + Arrays.toString(items)
                    + ", mutcd=" + mutcd
                    + ", priority=" + priority
                    + ", startTime=" + startTime;
//                    + ", regions="+Arrays.toString(regions)+"]";
        }
    }

        public static class RoadSign {
            public long latitude;
            public long longitude;
            public long elevation;
            public String[] viewAngle;
            public int mutcdCode;
            public byte[] msgCrc;

            @Override
            public String toString() {
                return "Road Sign [latitude=" + latitude
                        + ", longitude=" + longitude
                        + ", elevation=" + elevation
                        + ", viewAngle=" + viewAngle
                        + ", mutcdCode=" + mutcdCode
                        + ", msgCrc=" + msgCrc
                        + "]";
            }
        }

    public static class Deposit {
        public String systemName;
        public double nwLat;
        public double nwLon;
        public double seLat;
        public double seLon;
        public short timeToLive = -1;

        @Override
        public String toString() {
            return "Deposit [systemName=" + systemName + ", nwLat=" + nwLat
                    + ", nwLon=" + nwLon + ", seLat=" + seLat + ", seLon="
                    + seLon + ", timeToLive=" + timeToLive + "]";
        }
    }

}

