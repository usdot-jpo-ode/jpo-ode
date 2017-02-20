package us.dot.its.jpo.ode.traveler;

/**
 * Created by anthonychen on 2/17/17.
 */

public class TravelerInputData {

//    private static final Logger logger = Logger.getLogger(TravelerInputData.class);
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
//
//    public Region[] regions;
//    public AnchorPoint anchorPoint;
//    public VerifiedPoint verifiedPoint;
//    public Deposit deposit;
//
//    @Override
//    public String toString() {
//        return "TravelerInputData [regions=" + Arrays.toString(regions)
//                + ", anchorPoint=" + anchorPoint + ", verifiedPoint="
//                + verifiedPoint + ", deposit=" + deposit + "]";
//    }
//
//
//
//
//    public static class LaneNode {
//        public int nodeNumber;
//        public double nodeLat;
//        public double nodeLong;
//        public double nodeElevation;
//        public short laneWidth;			// lane width delta -512 to 511
//
//        @Override
//        public String toString() {
//            return "LaneNode [nodeNumber=" + nodeNumber
//                    + ", nodeLat=" + nodeLat
//                    + ", nodeLong=" + nodeLong
//                    + ", nodeElevation=" + nodeElevation
//                    + ", laneWidth=" + laneWidth
//                    + "]";
//        }
//    }
//
//    public static class Region {
//        public String regionType;
//        public LaneNode[] laneNodes;
//        public int extent = -1;
//        public GeoPoint refPoint;
//
//        @Override
//        public String toString() {
//            return "Region [regionType=" + regionType
//                    + ", laneNodes=" + Arrays.toString(laneNodes)
//                    + ", extent=" + extent
//                    + ", refPoint=" + refPoint
//                    + "]";
//        }
//    }
//
//    public static class AnchorPoint {
//        public String name;
//        public double referenceLat;
//        public double referenceLon;
//        public double referenceElevation;
//        public short masterLaneWidth;
//        public short sspTimRights;
//        public long packetID;
//        public String[] content;
//        public short sspTypeRights;
//        public short sspContentRights;
//        public short sspLocationRights;
//        public int direction;
//        public int mutcd;
//        public int priority;
//        public String startTime;
//        public String endTime;
//        public String[] heading;
//        public int infoType;
//
//        @Override
//        public String toString() {
//            return "AnchorPoint [name=" + name + ", referenceLat="
//                    + referenceLat + ", referenceLon=" + referenceLon
//                    + ", referenceElevation=" + referenceElevation
//                    + ", masterLaneWidth=" + masterLaneWidth
//                    + ", sspTimRights=" + sspTimRights
//                    + ", sspTypeRights=" + sspTypeRights
//                    + ", sspContentRights=" + sspContentRights
//                    + ", sspLocationRights=" + sspLocationRights
//                    + ", packetID=" + packetID + ", content="
//                    + Arrays.toString(content) + ", direction=" + direction
//                    + ", mutcd=" + mutcd + ", priority=" + priority
//                    + ", startTime=" + startTime + ", endTime=" + endTime
//                    + ", heading=" + Arrays.toString(heading) + ", infoType="
//                    + infoType + "]";
//        }
//    }
//
//    public static class VerifiedPoint {
//        public double verifiedMapLat;
//        public double verifiedMapLon;
//        public double verifiedMapElevation;
//        public double verifiedSurveyedLat;
//        public double verifiedSurveyedLon;
//        public double verifiedSurveyedElevation;
//
//        public short getVerifiedMapElevation() {
//            return IntersectionInputData.convertElevation(verifiedMapElevation);
//        }
//
//        public short getVerifiedSurveyedElevation() {
//            return IntersectionInputData.convertElevation(verifiedSurveyedElevation);
//        }
//
//        @Override
//        public String toString() {
//            return "VerifiedPoint [verifiedMapLat=" + verifiedMapLat
//                    + ", verifiedMapLon=" + verifiedMapLon
//                    + ", verifiedMapElevation=" + getVerifiedMapElevation()
//                    + ", verifiedSurveyedLat=" + verifiedSurveyedLat
//                    + ", verifiedSurveyedLon=" + verifiedSurveyedLon
//                    + ", verifiedSurveyedElevation=" + getVerifiedSurveyedElevation()
//                    + "]";
//        }
//    }
//
//    public static class Deposit {
//        public String systemName;
//        public double nwLat;
//        public double nwLon;
//        public double seLat;
//        public double seLon;
//        public short timeToLive = -1;
//
//        @Override
//        public String toString() {
//            return "Deposit [systemName=" + systemName + ", nwLat=" + nwLat
//                    + ", nwLon=" + nwLon + ", seLat=" + seLat + ", seLon="
//                    + seLon + ", timeToLive=" + timeToLive + "]";
//        }
//    }
//
//
//    public static void main(String args[]) throws JsonParseException, JsonMappingException, IOException {
//        String travJson = "{\r\n    \"regions\": [\r\n        {\r\n            \"laneWidth\": \"366\",\r\n            \"laneNodes\": [\r\n                {\r\n                    \"nodeNumber\": 0,\r\n                    \"nodeLat\": 42.33757684267676,\r\n                    \"nodeLong\": -83.05125328295235\r\n                },\r\n                {\r\n                    \"nodeNumber\": 1,\r\n                    \"nodeLat\": 42.33688687290945,\r\n                    \"nodeLong\": -83.05078657858425\r\n                }\r\n            ],\r\n            \"extent\": \"5\"\r\n        }\r\n    ],\r\n    \"anchorPoint\": {\r\n        \"name\": \"Work Zone\",\r\n        \"referenceLat\": 42.337656942112716,\r\n        \"referenceLon\": -83.05142065277923,\r\n        \"referenceElevation\": \"184\",\r\n        \"content\": \"testing\",\r\n        \"direction\": \"0\",\r\n        \"mutcd\": \"2\",\r\n        \"priority\": \"5\",\r\n        \"startTime\": \"05/26/2015 10:56 AM\",\r\n        \"endTime\": \"05/30/2015 10:56 AM\",\r\n        \"heading\": [\r\n            \"0001\",\r\n            \"0080\",\r\n            \"0100\",\r\n            \"8000\"\r\n        ],\r\n        \"infoType\": \"2\"\r\n    },\r\n    \"verifiedPoint\": {\r\n        \"verifiedMapLat\": 42.33791859880715,\r\n        \"verifiedMapLon\": -83.05089362151372,\r\n        \"verifiedMapElevation\": 180,\r\n        \"verifiedSurveyedLat\": \"42.13791859880715\",\r\n        \"verifiedSurveyedLon\": -83.01089362151372,\r\n        \"verifiedSurveyedElevation\": \"184\"\r\n    }\r\n}";
//        TravelerInputData trav = JSONMapper.jsonStringToPojo(travJson, TravelerInputData.class);
//        System.out.println(trav);
//    }

}

