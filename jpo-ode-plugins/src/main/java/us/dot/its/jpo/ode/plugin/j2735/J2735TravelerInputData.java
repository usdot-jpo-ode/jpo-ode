package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.GenericSnmp.SNMP;
import us.dot.its.jpo.ode.plugin.OperationalDataEnvironment.ODE;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.TravelerInformationMessage.TIM;

public class J2735TravelerInputData extends OdeObject {

   private static final long serialVersionUID = 8769107278440796699L;

   private TIM tim;
   private RSU[] rsus;
   private SNMP snmp;
   private ODE ode;
   private SDW sdw;

   public static class ComputedLane extends OdeObject {

    private static final long serialVersionUID = 7337344402648755924L;
    private int laneID;
      private int offsetSmallX;
      private int offsetLargeX;
      private int offsetSmallY;
      private int offsetLargeY;
      private int angle;
      private int xScale;
      private int yScale;
      public int getLaneID() {
         return laneID;
      }
      public void setLaneID(int laneID) {
         this.laneID = laneID;
      }
      public int getOffsetSmallX() {
         return offsetSmallX;
      }
      public void setOffsetSmallX(int offsetSmallX) {
         this.offsetSmallX = offsetSmallX;
      }
      public int getOffsetLargeX() {
         return offsetLargeX;
      }
      public void setOffsetLargeX(int offsetLargeX) {
         this.offsetLargeX = offsetLargeX;
      }
      public int getOffsetSmallY() {
         return offsetSmallY;
      }
      public void setOffsetSmallY(int offsetSmallY) {
         this.offsetSmallY = offsetSmallY;
      }
      public int getOffsetLargeY() {
         return offsetLargeY;
      }
      public void setOffsetLargeY(int offsetLargeY) {
         this.offsetLargeY = offsetLargeY;
      }
      public int getAngle() {
         return angle;
      }
      public void setAngle(int angle) {
         this.angle = angle;
      }
      public int getxScale() {
         return xScale;
      }
      public void setxScale(int xScale) {
         this.xScale = xScale;
      }
      public int getyScale() {
         return yScale;
      }
      public void setyScale(int yScale) {
         this.yScale = yScale;
      }
   }

   public static class NodeXY extends OdeObject {

    private static final long serialVersionUID = -3250256624514759524L;
    private String delta;
      private long nodeLat;
      private long nodeLong;
      private int x;
      private int y;
      private Attributes attributes;
      public String getDelta() {
         return delta;
      }
      public void setDelta(String delta) {
         this.delta = delta;
      }
      public long getNodeLat() {
         return nodeLat;
      }
      public void setNodeLat(long nodeLat) {
         this.nodeLat = nodeLat;
      }
      public long getNodeLong() {
         return nodeLong;
      }
      public void setNodeLong(long nodeLong) {
         this.nodeLong = nodeLong;
      }
      public int getX() {
         return x;
      }
      public void setX(int x) {
         this.x = x;
      }
      public int getY() {
         return y;
      }
      public void setY(int y) {
         this.y = y;
      }
      public Attributes getAttributes() {
         return attributes;
      }
      public void setAttributes(Attributes attributes) {
         this.attributes = attributes;
      }

   }

   public static class LocalNode extends OdeObject {

    private static final long serialVersionUID = 3872400520330034244L;
    private long type;

      public long getType() {
         return type;
      }

      public void setType(long type) {
         this.type = type;
      }
   }

   public static class DisabledList extends OdeObject {

    private static final long serialVersionUID = 1009869811306803991L;
    private long type;

      public long getType() {
         return type;
      }

      public void setType(long type) {
         this.type = type;
      }
   }

   public static class EnabledList extends OdeObject {

    private static final long serialVersionUID = 5797889223766230223L;
    private long type;

      public long getType() {
         return type;
      }

      public void setType(long type) {
         this.type = type;
      }
   }

   public static class SpeedLimits extends OdeObject {

    private static final long serialVersionUID = -8729406522600137038L;
    private long type;
      private int velocity;
      public long getType() {
         return type;
      }
      public void setType(long type) {
         this.type = type;
      }
      public int getVelocity() {
         return velocity;
      }
      public void setVelocity(int velocity) {
         this.velocity = velocity;
      }
   }

   public static class DataList extends OdeObject {

    private static final long serialVersionUID = -1391200532738540024L;
    private int pathEndpointAngle;
      private int laneCrownCenter;
      private int laneCrownLeft;
      private int laneCrownRight;
      private int laneAngle;
      private SpeedLimits[] speedLimits;
      public int getPathEndpointAngle() {
         return pathEndpointAngle;
      }
      public void setPathEndpointAngle(int pathEndpointAngle) {
         this.pathEndpointAngle = pathEndpointAngle;
      }
      public int getLaneCrownCenter() {
         return laneCrownCenter;
      }
      public void setLaneCrownCenter(int laneCrownCenter) {
         this.laneCrownCenter = laneCrownCenter;
      }
      public int getLaneCrownLeft() {
         return laneCrownLeft;
      }
      public void setLaneCrownLeft(int laneCrownLeft) {
         this.laneCrownLeft = laneCrownLeft;
      }
      public int getLaneCrownRight() {
         return laneCrownRight;
      }
      public void setLaneCrownRight(int laneCrownRight) {
         this.laneCrownRight = laneCrownRight;
      }
      public int getLaneAngle() {
         return laneAngle;
      }
      public void setLaneAngle(int laneAngle) {
         this.laneAngle = laneAngle;
      }
      public SpeedLimits[] getSpeedLimits() {
         return speedLimits;
      }
      public void setSpeedLimits(SpeedLimits[] speedLimits) {
         this.speedLimits = speedLimits;
      }
   }

   public static class Attributes extends OdeObject {

    private static final long serialVersionUID = -6476758554962944513L;
    private LocalNode[] localNodes;
      private DisabledList[] disabledLists;
      private EnabledList[] enabledLists;
      private DataList[] dataLists;
      private int dWidth;
      private int dElevation;
      public LocalNode[] getLocalNodes() {
         return localNodes;
      }
      public void setLocalNodes(LocalNode[] localNodes) {
         this.localNodes = localNodes;
      }
      public DisabledList[] getDisabledLists() {
         return disabledLists;
      }
      public void setDisabledLists(DisabledList[] disabledLists) {
         this.disabledLists = disabledLists;
      }
      public EnabledList[] getEnabledLists() {
         return enabledLists;
      }
      public void setEnabledLists(EnabledList[] enabledLists) {
         this.enabledLists = enabledLists;
      }
      public DataList[] getDataLists() {
         return dataLists;
      }
      public void setDataLists(DataList[] dataLists) {
         this.dataLists = dataLists;
      }
      public int getdWidth() {
         return dWidth;
      }
      public void setdWidth(int dWidth) {
         this.dWidth = dWidth;
      }
      public int getdElevation() {
         return dElevation;
      }
      public void setdElevation(int dElevation) {
         this.dElevation = dElevation;
      }
   }

   public static class DataFrame extends OdeObject {

    private static final long serialVersionUID = 537503046055742396L;
    private short sspTimRights;// Start Header Information
      private int frameType;
      private String msgID;
      private String furtherInfoID;
      private J2735Position3D position;
      private String viewAngle;
      private int mutcd;
      private String crc;
      private String startDateTime;
      private int durationTime;
      private int priority;// End header Information
      private short sspLocationRights;// Start Region Information
      private Region[] regions;
      private short sspMsgTypes;// Start content Information
      private short sspMsgContent;
      private String content;
      private String[] items;
      private String url;// End content Information

      public static class Region extends OdeObject {

        private static final long serialVersionUID = 8011973280114768008L;
        private String name;
         private int regulatorID;
         private int segmentID;
         private J2735Position3D anchorPosition;
         private int laneWidth;
         private long directionality;
         private boolean closedPath;
         private String direction;
         private String regionType;
         private String description;
         private Path path;
         private Geometry geometry;
         private OldRegion oldRegion;

         public static class OldRegion extends OdeObject {

            private static final long serialVersionUID = 1650366042178220073L;
            private String direction;
            private int extent;
            private String area;
            private ShapePoint shapepoint;
            private Circle circle;
            private RegionPoint regionPoint;

            public static class ShapePoint extends OdeObject {

                private static final long serialVersionUID = -209874574767391032L;
            private J2735Position3D position;
               private int laneWidth;
               private int directionality;
               private String nodeType;
               private ComputedLane computedLane;
               private NodeXY[] nodexy;
               public NodeXY[] getNodexy() {
                  return nodexy;
               }
               public void setNodexy(NodeXY[] nodexy) {
                  this.nodexy = nodexy;
               }
               public ComputedLane getComputedLane() {
                  return computedLane;
               }
               public void setComputedLane(ComputedLane computedLane) {
                  this.computedLane = computedLane;
               }
               public String getNodeType() {
                  return nodeType;
               }
               public void setNodeType(String nodeType) {
                  this.nodeType = nodeType;
               }
               public int getDirectionality() {
                  return directionality;
               }
               public void setDirectionality(int directionality) {
                  this.directionality = directionality;
               }
               public int getLaneWidth() {
                  return laneWidth;
               }
               public void setLaneWidth(int laneWidth) {
                  this.laneWidth = laneWidth;
               }
               public J2735Position3D getPosition() {
                  return position;
               }
               public void setPosition(J2735Position3D position) {
                  this.position = position;
               }
            }

            public static class RegionPoint extends OdeObject {

                private static final long serialVersionUID = -3978458353968571032L;
            private J2735Position3D position;
               private int scale;
               private RegionList[] regionList;

               public static class RegionList extends OdeObject {

                private static final long serialVersionUID = -5307620155601900634L;
                private int xOffset;
                  private int yOffset;
                  private int zOffset;
                  public int getzOffset() {
                     return zOffset;
                  }
                  public void setzOffset(int zOffset) {
                     this.zOffset = zOffset;
                  }
                  public int getyOffset() {
                     return yOffset;
                  }
                  public void setyOffset(int yOffset) {
                     this.yOffset = yOffset;
                  }
                  public int getxOffset() {
                     return xOffset;
                  }
                  public void setxOffset(int xOffset) {
                     this.xOffset = xOffset;
                  }
               }

               public RegionList[] getRegionList() {
                  return regionList;
               }

               public void setRegionList(RegionList[] regionList) {
                  this.regionList = regionList;
               }

               public int getScale() {
                  return scale;
               }

               public void setScale(int scale) {
                  this.scale = scale;
               }

               public J2735Position3D getPosition() {
                  return position;
               }

               public void setPosition(J2735Position3D position) {
                  this.position = position;
               }
            }

            public RegionPoint getRegionPoint() {
               return regionPoint;
            }

            public void setRegionPoint(RegionPoint regionPoint) {
               this.regionPoint = regionPoint;
            }

            public Circle getCircle() {
               return circle;
            }

            public void setCircle(Circle circle) {
               this.circle = circle;
            }

            public ShapePoint getShapepoint() {
               return shapepoint;
            }

            public void setShapepoint(ShapePoint shapepoint) {
               this.shapepoint = shapepoint;
            }

            public String getArea() {
               return area;
            }

            public void setArea(String area) {
               this.area = area;
            }

            public int getExtent() {
               return extent;
            }

            public void setExtent(int extent) {
               this.extent = extent;
            }

            public String getDirection() {
               return direction;
            }

            public void setDirection(String direction) {
               this.direction = direction;
            }
         }

         public static class Geometry extends OdeObject {

            private static final long serialVersionUID = -7664796173893464468L;
            private String direction;
            private int extent;
            private int laneWidth;
            private Circle circle;
            public Circle getCircle() {
               return circle;
            }
            public void setCircle(Circle circle) {
               this.circle = circle;
            }
            public int getLaneWidth() {
               return laneWidth;
            }
            public void setLaneWidth(int laneWidth) {
               this.laneWidth = laneWidth;
            }
            public int getExtent() {
               return extent;
            }
            public void setExtent(int extent) {
               this.extent = extent;
            }
            public String getDirection() {
               return direction;
            }
            public void setDirection(String direction) {
               this.direction = direction;
            }
         }

         public static class Circle extends OdeObject {

            private static final long serialVersionUID = -8156052898034497978L;
            private J2735Position3D position;
            private int radius;
            private int units;
            public int getUnits() {
               return units;
            }
            public void setUnits(int units) {
               this.units = units;
            }
            public int getRadius() {
               return radius;
            }
            public void setRadius(int radius) {
               this.radius = radius;
            }
            public J2735Position3D getPosition() {
               return position;
            }
            public void setPosition(J2735Position3D position) {
               this.position = position;
            }
         }

         public static class Path extends OdeObject {

            private static final long serialVersionUID = 3293758823626661508L;
            private int scale;
            private String type;
            private NodeXY[] nodes;
            private ComputedLane computedLane;
            public ComputedLane getComputedLane() {
               return computedLane;
            }
            public void setComputedLane(ComputedLane computedLane) {
               this.computedLane = computedLane;
            }
            public NodeXY[] getNodes() {
               return nodes;
            }
            public void setNodes(NodeXY[] nodes) {
               this.nodes = nodes;
            }
            public String getType() {
               return type;
            }
            public void setType(String type) {
               this.type = type;
            }
            public int getScale() {
               return scale;
            }
            public void setScale(int scale) {
               this.scale = scale;
            }

         }

         public OldRegion getOldRegion() {
            return oldRegion;
         }

         public void setOldRegion(OldRegion oldRegion) {
            this.oldRegion = oldRegion;
         }

         public Geometry getGeometry() {
            return geometry;
         }

         public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
         }

         public Path getPath() {
            return path;
         }

         public void setPath(Path path) {
            this.path = path;
         }

         public String getDescription() {
            return description;
         }

         public void setDescription(String description) {
            this.description = description;
         }
         
         public String getregionType() {
            return regionType;
         }
         
         public void setregionType(String regionType) {
            this.regionType = regionType;
         }

         public String getDirection() {
            return direction;
         }

         public void setDirection(String direction) {
            this.direction = direction;
         }

         public boolean isClosedPath() {
            return closedPath;
         }

         public void setClosedPath(boolean closedPath) {
            this.closedPath = closedPath;
         }

         public long getDirectionality() {
            return directionality;
         }

         public void setDirectionality(long directionality) {
            this.directionality = directionality;
         }

         public int getLaneWidth() {
            return laneWidth;
         }

         public void setLaneWidth(int laneWidth) {
            this.laneWidth = laneWidth;
         }

         public J2735Position3D getAnchorPosition() {
            return anchorPosition;
         }

         public void setAnchorPosition(J2735Position3D anchorPosition) {
            this.anchorPosition = anchorPosition;
         }

         public int getSegmentID() {
            return segmentID;
         }

         public void setSegmentID(int segmentID) {
            this.segmentID = segmentID;
         }

         public int getRegulatorID() {
            return regulatorID;
         }

         public void setRegulatorID(int regulatorID) {
            this.regulatorID = regulatorID;
         }

         public String getName() {
            return name;
         }

         public void setName(String name) {
            this.name = name;
         }
         
      }

      public String getUrl() {
         return url;
      }

      public void setUrl(String url) {
         this.url = url;
      }
      
      public String[] getItems() {
         return items;
      }
      
      public void setItems(String[] items) {
         this.items = items;
      }
      
      public String getContent() {
         return content;
      }
      
      public void setContent(String content) {
         this.content = content;
      }
      
      public short getsspMsgContent() {
         return sspMsgContent;
      }
      
      public void setsspMsgContent(short sspMsgContent) {
         this.sspMsgContent = sspMsgContent;
      }
      
      public short getsspMsgTypes() {
         return sspMsgTypes;
      }
      
      public void setsspMsgTypes(short sspMsgTypes) {
         this.sspMsgTypes = sspMsgTypes;
      }
      
      public Region[] getRegions() {
         return regions;
      }
      
      public void setRegions(Region[] regions) {
         this.regions = regions;
      }
      
      public short getsspLocationRights() {
         return sspLocationRights;
      }
      
      public void setsspLocationRights(short sspLocationRights) {
         this.sspLocationRights = sspLocationRights;
      }
      
      public int getPriority() {
         return priority;
      }
      
      public void setPriority(int priority) {
         this.priority = priority;
      }

      public int getDurationTime() {
         return durationTime;
      }

      public void setDurationTime(int durationTime) {
         this.durationTime = durationTime;
      }
      
      public String getStartDateTime() {
         return startDateTime;
      }
      
      public void setStartDateTime(String startDateTime) {
         this.startDateTime = startDateTime;
      }

      public String getCrc() {
         return crc;
      }

      public void setCrc(String crc) {
         this.crc = crc;
      }
      
      public int getMutcd() {
         return mutcd;
      }
      
      public void setMutcd(int mutcd) {
         this.mutcd = mutcd;
      }

      public String getViewAngle() {
         return viewAngle;
      }

      public void setViewAngle(String viewAngle) {
         this.viewAngle = viewAngle;
      }

      public String getMsgID() {
         return msgID;
      }

      public void setMsgID(String msgID) {
         this.msgID = msgID;
      }

      public int getFrameType() {
         return frameType;
      }

      public void setFrameType(int frameType) {
         this.frameType = frameType;
      }
      
      public short getsspTimRights() {
         return sspTimRights;
      }
      
      public void setsspTimRights(short sspTimRights) {
         this.sspTimRights = sspTimRights;
      }

      public J2735Position3D getPosition() {
         return position;
      }

      public void setPosition(J2735Position3D position) {
         this.position = position;
      }

      public String getFurtherInfoID() {
         return furtherInfoID;
      }

      public void setFurtherInfoID(String furtherInfoID) {
         this.furtherInfoID = furtherInfoID;
      }
      
   }

   public TIM getTim() {
      return tim;
   }

   public void setTim(TIM tim) {
      this.tim = tim;
   }

   public RSU[] getRsus() {
      return rsus;
   }

   public void setRsus(RSU[] rsus) {
      this.rsus = rsus;
   }

   public SNMP getSnmp() {
      return snmp;
   }

   public void setSnmp(SNMP snmp) {
      this.snmp = snmp;
   }

   public ODE getOde() {
      return ode;
   }

   public void setOde(ODE ode) {
      this.ode = ode;
   }

   public SDW getSdw() {
      return sdw;
   }

   public void setSdw(SDW sdw) {
      this.sdw = sdw;
   }

}
