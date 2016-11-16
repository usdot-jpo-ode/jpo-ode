/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.asn;

import java.util.List;

import com.bah.ode.asn.oss.dsrc.MapData;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeMapData extends OdeObject {

   private static final long serialVersionUID = -3064674155859566724L;

   public enum OdeLayerType {
      NONE, MIXED_CONTENT, GENERAL_MAP_DATA, INTERSECTION_DATA, CURVE_DATA, ROADWAY_SECTION_DATA, PARKING_AREA_DATA, SHARED_LANE_DATA;

      public static final OdeLayerType[] values = values();
   }
   
   private OdeDSRCmsgID msgID;
   private Integer msgCnt; 
   private String name;
   private OdeLayerType layerType;
   private Integer layerId;
   private List<OdeIntersection> intersections;
   private OdeDataParameters dataParameters; 

   public OdeMapData(MapData mapData) {
      if (mapData.getDataParameters() != null)
         this.setDataParameters(new OdeDataParameters(mapData.getDataParameters()));
      
      if (mapData.getMsgCnt() != null)
         this.setMsgCnt(mapData.getMsgCnt().intValue());
      
      if (mapData.getName() != null)
         this.setName(mapData.getName().stringValue());
      
      if (mapData.getLayerType() != null)
         this.setLayerType(OdeLayerType.values[(int) mapData.getLayerType().longValue()]);
      
      if (mapData.getLayerID() != null)
         this.setLayerId(mapData.getLayerID().intValue());
      
      if (mapData.getIntersections() != null)
         this.setIntersections(OdeIntersection.createList(mapData.getIntersections()));
   }

   public OdeDSRCmsgID getMsgID() {
      return msgID;
   }

   public void setMsgID(OdeDSRCmsgID msgID) {
      this.msgID = msgID;
   }

   public Integer getMsgCnt() {
      return msgCnt;
   }

   public void setMsgCnt(Integer msgCnt) {
      this.msgCnt = msgCnt;
   }

   public OdeDataParameters getDataParameters() {
      return dataParameters;
   }

   public void setDataParameters(OdeDataParameters dataParameters) {
      this.dataParameters = dataParameters;
   }

   public String getName() {
      return name;
   }

   public OdeMapData setName(String name) {
      this.name = name;
      return this;
   }

   public OdeLayerType getLayerType() {
      return layerType;
   }

   public OdeMapData setLayerType(OdeLayerType layerType) {
      this.layerType = layerType;
      return this;
   }

   public Integer getLayerId() {
      return layerId;
   }

   public OdeMapData setLayerId(Integer layerId) {
      this.layerId = layerId;
      return this;
   }

   public List<OdeIntersection> getIntersections() {
      return intersections;
   }

   public OdeMapData setIntersections(List<OdeIntersection> intersections) {
      this.intersections = intersections;
      return this;
   }

}
