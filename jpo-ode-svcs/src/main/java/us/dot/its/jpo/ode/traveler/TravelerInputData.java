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
       public String crc;
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
          public LaneNode[] laneNodes;
          public int extent = -1;
          public String description;
          public Path path;

          public static class Path {
              public int scale;
              public Node[] nodes;

              public static class Node {
                  public String nodeNumber;
                  public String nodeLat;
                  public String nodeLong;
              }
          }
          @Override
          public String toString() {
              return "Region [regionType=" + regionType
                      + ", laneNodes=" + Arrays.toString(laneNodes)
                      + ", extent=" + extent
//                      + ", refPoint=" + refPoint
                      + "]";
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
                    + ", startTime=" + startTime
                    + ", regions="+Arrays.toString(regions)+"]";
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

