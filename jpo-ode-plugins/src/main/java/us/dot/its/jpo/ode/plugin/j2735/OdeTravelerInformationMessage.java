/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.Region.Circle;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.Region.OldRegion.RegionPointSet;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.Region.OldRegion.ShapePointSet;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.DirectionOfUse.DirectionOfUseEnum;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.DistanceUnits.DistanceUnitsEnum;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.Extent.ExtentEnum;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.FrameType;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MutcdCode;

/**
 * OdeTravelerInformationMessage.
 */
@EqualsAndHashCode(callSuper = false)
public class OdeTravelerInformationMessage extends OdeObject {

  private static final long serialVersionUID = -200529140190872305L;

  private int msgCnt;
  private String timeStamp;
  private String packetID;
  private String urlB;
  private DataFrame[] dataframes;
  private transient JsonNode asnDataFrames;

  public int getMsgCnt() {
    return msgCnt;
  }

  public void setMsgCnt(int msgCnt) {
    this.msgCnt = msgCnt;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getPacketID() {
    return packetID;
  }

  public void setPacketID(String packetID) {
    this.packetID = packetID;
  }

  public DataFrame[] getDataframes() {
    return dataframes;
  }

  public void setDataframes(DataFrame[] dataframes) {
    this.dataframes = dataframes;
  }

  public String getUrlB() {
    return urlB;
  }

  public void setUrlB(String urlB) {
    this.urlB = urlB;
  }

  public JsonNode getAsnDataFrames() {
    return asnDataFrames;
  }

  public void setAsnDataFrames(JsonNode stringDataFrames) {
    this.asnDataFrames = stringDataFrames;
  }

  /**
   * NodeListXY.
   */
  public static class NodeListXY extends OdeObject {
    private static final long serialVersionUID = 1L;
    private ComputedLane computedLane;
    private NodeXY[] nodexy;

    public ComputedLane getComputedLane() {
      return computedLane;
    }

    public void setComputedLane(ComputedLane computedLane) {
      this.computedLane = computedLane;
    }

    public NodeXY[] getNodexy() {
      return nodexy;
    }

    public void setNodexy(NodeXY[] nodexy) {
      this.nodexy = nodexy;
    }
  }

  /**
   * Area.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class Area extends OdeObject {
    private static final long serialVersionUID = 1L;

    private ShapePointSet shapepoint;
    private Circle circle;
    private RegionPointSet regionPoint;

    public ShapePointSet getShapepoint() {
      return shapepoint;
    }

    public void setShapepoint(ShapePointSet shapepoint) {
      this.shapepoint = shapepoint;
    }

    public Circle getCircle() {
      return circle;
    }

    public void setCircle(Circle circle) {
      this.circle = circle;
    }

    public RegionPointSet getRegionPoint() {
      return regionPoint;
    }

    public void setRegionPoint(RegionPointSet regionPoint) {
      this.regionPoint = regionPoint;
    }
  }

  /**
   * ComputedLane.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class ComputedLane extends OdeObject {

    private static final long serialVersionUID = 7337344402648755924L;
    private int referenceLaneId;
    private BigDecimal offsetXaxis;
    private BigDecimal offsetYaxis;
    private BigDecimal rotateXY;
    private BigDecimal scaleXaxis;
    private BigDecimal scaleYaxis;

    public int getReferenceLaneId() {
      return referenceLaneId;
    }

    public void setReferenceLaneId(int referenceLaneId) {
      this.referenceLaneId = referenceLaneId;
    }

    public BigDecimal getOffsetXaxis() {
      return offsetXaxis;
    }

    public void setOffsetXaxis(BigDecimal offsetXaxis) {
      this.offsetXaxis = offsetXaxis;
    }

    public BigDecimal getOffsetYaxis() {
      return offsetYaxis;
    }

    public void setOffsetYaxis(BigDecimal offsetYaxis) {
      this.offsetYaxis = offsetYaxis;
    }

    public BigDecimal getRotateXY() {
      return rotateXY;
    }

    public void setRotateXY(BigDecimal rotateXY) {
      this.rotateXY = rotateXY;
    }

    public BigDecimal getScaleXaxis() {
      return scaleXaxis;
    }

    public void setScaleXaxis(BigDecimal scaleXaxis) {
      this.scaleXaxis = scaleXaxis;
    }

    public BigDecimal getScaleYaxis() {
      return scaleYaxis;
    }

    public void setScaleYaxis(BigDecimal scaleYaxis) {
      this.scaleYaxis = scaleYaxis;
    }
  }

  /**
   * NodeXY.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class NodeXY extends OdeObject {

    private static final long serialVersionUID = -3250256624514759524L;
    private String delta;
    private BigDecimal nodeLat;
    private BigDecimal nodeLong;
    @JsonProperty("x")
    private BigDecimal xpos;
    @JsonProperty("y")
    private BigDecimal ypos;
    private Attributes attributes;

    public String getDelta() {
      return delta;
    }

    public void setDelta(String delta) {
      this.delta = delta;
    }

    public BigDecimal getNodeLat() {
      return nodeLat;
    }

    public void setNodeLat(BigDecimal nodeLat) {
      this.nodeLat = nodeLat;
    }

    public BigDecimal getNodeLong() {
      return nodeLong;
    }

    public void setNodeLong(BigDecimal nodeLong) {
      this.nodeLong = nodeLong;
    }

    public BigDecimal getXpos() {
      return xpos;
    }

    public void setXpos(BigDecimal xpos) {
      this.xpos = xpos;
    }

    public BigDecimal getYpos() {
      return ypos;
    }

    public void setYpos(BigDecimal ypos) {
      this.ypos = ypos;
    }

    public Attributes getAttributes() {
      return attributes;
    }

    public void setAttributes(Attributes attributes) {
      this.attributes = attributes;
    }
  }

  /**
   * LocalNode.
   */
  @EqualsAndHashCode(callSuper = false)
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

  /**
   * DisabledList.
   */
  @EqualsAndHashCode(callSuper = false)
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

  /**
   * EnabledList.
   */
  @EqualsAndHashCode(callSuper = false)
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

  /**
   * SpeedLimits.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class SpeedLimits extends OdeObject {

    private static final long serialVersionUID = -8729406522600137038L;
    private long type;
    private BigDecimal velocity;

    public long getType() {
      return type;
    }

    public void setType(long type) {
      this.type = type;
    }

    public BigDecimal getVelocity() {
      return velocity;
    }

    public void setVelocity(BigDecimal velocity) {
      this.velocity = velocity;
    }
  }

  /**
   * DataList.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class DataList extends OdeObject {

    private static final long serialVersionUID = -1391200532738540024L;
    private int pathEndpointAngle;
    private BigDecimal laneCrownCenter;
    private BigDecimal laneCrownLeft;
    private BigDecimal laneCrownRight;
    private BigDecimal laneAngle;
    private SpeedLimits[] speedLimits;

    public int getPathEndpointAngle() {
      return pathEndpointAngle;
    }

    public void setPathEndpointAngle(int pathEndpointAngle) {
      this.pathEndpointAngle = pathEndpointAngle;
    }

    public BigDecimal getLaneCrownCenter() {
      return laneCrownCenter;
    }

    public void setLaneCrownCenter(BigDecimal laneCrownCenter) {
      this.laneCrownCenter = laneCrownCenter;
    }

    public BigDecimal getLaneCrownLeft() {
      return laneCrownLeft;
    }

    public void setLaneCrownLeft(BigDecimal laneCrownLeft) {
      this.laneCrownLeft = laneCrownLeft;
    }

    public BigDecimal getLaneCrownRight() {
      return laneCrownRight;
    }

    public void setLaneCrownRight(BigDecimal laneCrownRight) {
      this.laneCrownRight = laneCrownRight;
    }

    public BigDecimal getLaneAngle() {
      return laneAngle;
    }

    public void setLaneAngle(BigDecimal laneAngle) {
      this.laneAngle = laneAngle;
    }

    public SpeedLimits[] getSpeedLimits() {
      return speedLimits;
    }

    public void setSpeedLimits(SpeedLimits[] speedLimits) {
      this.speedLimits = speedLimits;
    }
  }

  /**
   * Attributes.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class Attributes extends OdeObject {

    private static final long serialVersionUID = -6476758554962944513L;
    private LocalNode[] localNodes;
    private DisabledList[] disabledLists;
    private EnabledList[] enabledLists;
    private DataList[] dataLists;
    @JsonProperty("dWidth")
    private BigDecimal dwidth;
    @JsonProperty("dElevation")
    private BigDecimal delevation;

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

    public BigDecimal getDwidth() {
      return dwidth;
    }

    public void setDwidth(BigDecimal dwidth) {
      this.dwidth = dwidth;
    }

    public BigDecimal getDelevation() {
      return delevation;
    }

    public void setDelevation(BigDecimal delevation) {
      this.delevation = delevation;
    }
  }

  /**
   * DataFrame.
   */
  @EqualsAndHashCode(callSuper = false)
  public static class DataFrame extends OdeObject {

    private static final long serialVersionUID = 537503046055742396L;
    @JsonAlias({"sspTimRights", "notUsed"})
    private short doNotUse1; // Start Header Information
    private FrameType.TravelerInfoType frameType;
    private MsgId msgId;
    private String startDateTime;
    @JsonAlias("duratonTime")
    private int durationTime;
    private int priority; // End header Information
    @JsonAlias({"sspLocationRights", "notUsed1"})
    private short doNotUse2; // Start Region Information
    private Region[] regions;
    @JsonAlias({"sspMsgContent", "sspMsgRights1", "notUsed2"})
    private short doNotUse3; // Start content Information
    @JsonAlias({"sspMsgTypes", "sspMsgRights2", "notUsed3"})
    private short doNotUse4;
    private String content;
    private String[] items;
    private String url; // End content Information

    /**
     * Region.
     */
    @EqualsAndHashCode(callSuper = false)
    public static class Region extends OdeObject {

      private static final long serialVersionUID = 8011973280114768008L;
      private String name;
      private int regulatorID;
      private int segmentID;
      private OdePosition3D anchorPosition;
      private BigDecimal laneWidth;
      private String directionality;
      private boolean closedPath;
      private String direction;
      private String description;
      private Path path;
      private Geometry geometry;
      private OldRegion oldRegion;

      /**
       * OldRegion.
       */
      @EqualsAndHashCode(callSuper = false)
      public static class OldRegion extends OdeObject {

        private static final long serialVersionUID = 1L;
        private String direction;
        private String extent;
        private Area area;

        /**
         * ShapePointSet.
         */
        @EqualsAndHashCode(callSuper = false)
        public static class ShapePointSet extends OdeObject {

          private static final long serialVersionUID = 1L;
          private OdePosition3D anchor;
          private BigDecimal laneWidth;
          private int directionality;
          private NodeListXY nodeList;

          public OdePosition3D getAnchor() {
            return anchor;
          }

          public void setAnchor(OdePosition3D anchor) {
            this.anchor = anchor;
          }

          public BigDecimal getLaneWidth() {
            return laneWidth;
          }

          public void setLaneWidth(BigDecimal laneWidth) {
            this.laneWidth = laneWidth;
          }

          public int getDirectionality() {
            return directionality;
          }

          public void setDirectionality(int directionality) {
            this.directionality = directionality;
          }

          public void setDirectionalityEnum(DirectionOfUseEnum directionalityEnum) {
            this.directionality = directionalityEnum.ordinal();
          }

          public NodeListXY getNodeList() {
            return nodeList;
          }

          public void setNodeList(NodeListXY nodeList) {
            this.nodeList = nodeList;
          }
        }

        /**
         * RegionPointSet.
         */
        @EqualsAndHashCode(callSuper = false)
        public static class RegionPointSet extends OdeObject {

          private static final long serialVersionUID = 1L;
          private OdePosition3D position;
          private int scale;
          private RegionList[] regionList;

          /**
           * RegionList.
           */
          @EqualsAndHashCode(callSuper = false)
          public static class RegionList extends OdeObject {

            private static final long serialVersionUID = -5307620155601900634L;
            @JsonProperty("xOffset")
            private BigDecimal offsetX;
            @JsonProperty("yOffset")
            private BigDecimal offsetY;
            @JsonProperty("zOffset")
            private BigDecimal offsetZ;

            public BigDecimal getOffsetZ() {
              return offsetZ;
            }

            public void setOffsetZ(BigDecimal offsetZ) {
              this.offsetZ = offsetZ;
            }

            public BigDecimal getOffsetY() {
              return offsetY;
            }

            public void setOffsetY(BigDecimal offsetY) {
              this.offsetY = offsetY;
            }

            public BigDecimal getOffsetX() {
              return offsetX;
            }

            public void setOffsetX(BigDecimal offsetX) {
              this.offsetX = offsetX;
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

          public OdePosition3D getPosition() {
            return position;
          }

          public void setPosition(OdePosition3D position) {
            this.position = position;
          }
        }

        public Area getArea() {
          return area;
        }

        public void setArea(Area area) {
          this.area = area;
        }

        public String getExtent() {
          return extent;
        }

        public void setExtent(String extent) {
          this.extent = extent;
        }

        public void setExtent(ExtentEnum extent) {
          this.extent = extent.name();
        }

        public String getDirection() {
          return direction;
        }

        public void setDirection(String direction) {
          this.direction = direction;
        }
      }

      /**
       * Geometry.
       */
      public static class Geometry extends OdeObject {

        private static final long serialVersionUID = -7664796173893464468L;
        private String direction;
        private int extent;
        private BigDecimal laneWidth;
        private Circle circle;

        public Circle getCircle() {
          return circle;
        }

        public void setCircle(Circle circle) {
          this.circle = circle;
        }

        public BigDecimal getLaneWidth() {
          return laneWidth;
        }

        public void setLaneWidth(BigDecimal laneWidth) {
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

      /**
       * Circle.
       */
      @EqualsAndHashCode(callSuper = false)
      public static class Circle extends OdeObject {

        private static final long serialVersionUID = -8156052898034497978L;
        private OdePosition3D position;
        private OdePosition3D center;
        private int radius;
        private String units;

        public String getUnits() {
          return units;
        }

        public void setUnits(String units) {
          this.units = units;
        }

        public void setUnits(DistanceUnitsEnum units) {
          this.units = units.name();
        }

        public int getRadius() {
          return radius;
        }

        public void setRadius(int radius) {
          this.radius = radius;
        }

        public OdePosition3D getPosition() {
          return position;
        }

        public void setPosition(OdePosition3D position) {
          this.position = position;
        }

        public OdePosition3D getCenter() {
          return center;
        }

        public void setCenter(OdePosition3D center) {
          this.center = center;
        }
      }

      /**
       * Path.
       */
      @EqualsAndHashCode(callSuper = false)
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

      public String getDirectionality() {
        return directionality;
      }

      public void setDirectionality(String directionality) {
        this.directionality = directionality;
      }

      public BigDecimal getLaneWidth() {
        return laneWidth;
      }

      public void setLaneWidth(BigDecimal laneWidth) {
        this.laneWidth = laneWidth;
      }

      public OdePosition3D getAnchorPosition() {
        return anchorPosition;
      }

      public void setAnchorPosition(OdePosition3D anchorPosition) {
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

    /**
     * RoadSignID.
     */
    @EqualsAndHashCode(callSuper = false)
    public static class RoadSignID extends OdeObject {

      private static final long serialVersionUID = 1L;

      private OdePosition3D position;
      private String viewAngle;
      private MutcdCode.MutcdCodeEnum mutcdCode;
      private String crc;

      public OdePosition3D getPosition() {
        return position;
      }

      public void setPosition(OdePosition3D position) {
        this.position = position;
      }

      public String getViewAngle() {
        return viewAngle;
      }

      public void setViewAngle(String viewAngle) {
        this.viewAngle = viewAngle;
      }

      public MutcdCode.MutcdCodeEnum getMutcdCode() {
        return mutcdCode;
      }

      public void setMutcdCode(MutcdCode.MutcdCodeEnum mutcdCode) {
        this.mutcdCode = mutcdCode;
      }

      public String getCrc() {
        return crc;
      }

      public void setCrc(String crc) {
        this.crc = crc;
      }
    }

    /**
     * MsgId.
     */
    @EqualsAndHashCode(callSuper = false)
    public static class MsgId extends Asn1Object {
      private static final long serialVersionUID = 1L;

      private RoadSignID roadSignID;
      private String furtherInfoID;

      public RoadSignID getRoadSignID() {
        return roadSignID;
      }

      public void setRoadSignID(RoadSignID roadSignID) {
        this.roadSignID = roadSignID;
      }

      public String getFurtherInfoID() {
        return furtherInfoID;
      }

      public void setFurtherInfoID(String furtherInfoID) {
        this.furtherInfoID = furtherInfoID;
      }
    }

    public MsgId getMsgId() {
      return msgId;
    }

    public void setMsgId(MsgId msgId) {
      this.msgId = msgId;
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

    public Region[] getRegions() {
      return regions;
    }

    public void setRegions(Region[] regions) {
      this.regions = regions;
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

    public FrameType.TravelerInfoType getFrameType() {
      return frameType;
    }

    public void setFrameType(FrameType.TravelerInfoType frameType) {
      this.frameType = frameType;
    }

    public short getDoNotUse1() {
      return doNotUse1;
    }

    public void setDoNotUse1(short doNotUse1) {
      this.doNotUse1 = doNotUse1;
    }

    public short getDoNotUse2() {
      return doNotUse2;
    }

    public void setDoNotUse2(short doNotUse2) {
      this.doNotUse2 = doNotUse2;
    }

    public short getDoNotUse3() {
      return doNotUse3;
    }

    public void setDoNotUse3(short doNotUse3) {
      this.doNotUse3 = doNotUse3;
    }

    public short getDoNotUse4() {
      return doNotUse4;
    }

    public void setDoNotUse4(short doNotUse4) {
      this.doNotUse4 = doNotUse4;
    }

  }

}
