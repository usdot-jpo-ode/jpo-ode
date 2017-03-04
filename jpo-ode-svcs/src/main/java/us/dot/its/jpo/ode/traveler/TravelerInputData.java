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

    public DataFrame[] dataframes;
    public Deposit deposit;
    public long packetID;
    public String urlB;

    @Override
    public String toString() {
        return "TravelerInputData [anchorPoint=" + Arrays.toString(dataframes)
                + ", deposit=" + deposit + "]";
    }

    public static class SDWObject {
        public int test;

        @Override
        public String toString() {
            return "test= "+test;
        }
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
        public String name;
        public double latitude;
        public double longitude;
        public double elevation;
        public short masterLaneWidth; // do we want to do something
        public int  crc;
        public int  frameType;
        public String msgID;
        public String infoString;
        public long packetID;
        public String content;
        public String[] items;
        public short sspTimRights;
        public short sspMsgTypes;
        public short sspMsgContent;
        public short sspLocationRights;
        public int mutcd;
        public int priority;
        public String startTime;
        public String startYear;
        public int durationTime;
        public String[] heading;
        public int infoType;
        public Region[] regions;
        public RoadSign roadSign;
        public String url;


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
            public String[] direction;
            public String regionType;
            public LaneNode[] laneNodes;
            public int extent = -1;


//            public GeoPoint refPoint;

            @Override
            public String toString() {
                return "Region [regionType=" + regionType
                        + ", laneNodes=" + Arrays.toString(laneNodes)
                        + ", extent=" + extent
//                        + ", refPoint=" + refPoint
                        + "]";
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

        @Override
        public String toString() {
            return "AnchorPoint [name=" + name + ", referenceLat="
                    + latitude + ", referenceLon=" + longitude
                    + ", referenceElevation=" + elevation
                    + ", masterLaneWidth=" + masterLaneWidth
                    + ", sspTimRights=" + sspTimRights
                    + ", sspTypeRights=" + sspMsgTypes
                    + ", sspContentRights=" + sspMsgContent
                    + ", sspLocationRights=" + sspLocationRights
                    + ", packetID=" + packetID
                    + ", RoadSign=" + roadSign
                    + ", content=" + content
                    + ", items=" + Arrays.toString(items)
                    + ", mutcd=" + mutcd
                    + ", priority=" + priority
                    + ", startTime=" + startTime
                    + ", heading=" + Arrays.toString(heading)
                    + ", infoType=" + infoType
                    + ", regions="+Arrays.toString(regions)+"]";
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

