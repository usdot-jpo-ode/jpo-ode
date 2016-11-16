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

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.LaneNumber;
import com.bah.ode.asn.oss.dsrc.Offsets;
import com.bah.ode.asn.oss.dsrc.SignalControlZone;
import com.bah.ode.asn.oss.dsrc.SignalControlZone.Data.Zones.Sequence_;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeSignalControlZone extends OdeObject {
   
   private static final long serialVersionUID = 1967144808009116234L;


   public class Zone extends OdeObject {

      private static final long serialVersionUID = -2316923797127077251L;

      public ArrayList<Integer> enclosed = new ArrayList<Integer>();
      public Integer laneWidth;
      public ArrayList<OdeLaneOffsets> nodeList = new ArrayList<OdeLaneOffsets>();

      public Zone(Sequence_ zone) {
         if (zone.hasEnclosed()) {
            for (LaneNumber laneNum : zone.getEnclosed().elements) {
               if (laneNum != null && laneNum.byteArrayValue() != null)
                  this.enclosed.add((int) laneNum.byteArrayValue()[0]);
            }
         }
         
         if (zone.hasLaneWidth()) {
            this.laneWidth = zone.getLaneWidth().intValue(); 
         }
         
         for (Offsets ofs : zone.getNodeList().elements) {
            if (ofs != null) {
               this.nodeList.add(new OdeLaneOffsets(ofs));
            }
         }
         
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result
               + ((enclosed == null) ? 0 : enclosed.hashCode());
         result = prime * result
               + ((laneWidth == null) ? 0 : laneWidth.hashCode());
         result = prime * result
               + ((nodeList == null) ? 0 : nodeList.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Zone other = (Zone) obj;
         if (!getOuterType().equals(other.getOuterType()))
            return false;
         if (enclosed == null) {
            if (other.enclosed != null)
               return false;
         } else if (!enclosed.equals(other.enclosed))
            return false;
         if (laneWidth == null) {
            if (other.laneWidth != null)
               return false;
         } else if (!laneWidth.equals(other.laneWidth))
            return false;
         if (nodeList == null) {
            if (other.nodeList != null)
               return false;
         } else if (!nodeList.equals(other.nodeList))
            return false;
         return true;
      }

      private OdeSignalControlZone getOuterType() {
         return OdeSignalControlZone.this;
      }

      
   }

   public class Data extends OdeChoice {

      private static final long serialVersionUID = 5898800161287010070L;

      public ArrayList<Integer> laneSet_chosen;
      public ArrayList<Zone> zones_chosen;
      
      public Data(com.bah.ode.asn.oss.dsrc.SignalControlZone.Data data) {
         super();
         
         int flag = data.getChosenFlag();
         
         switch (flag) {
         case com.bah.ode.asn.oss.dsrc.SignalControlZone.Data.laneSet_chosen:
            if (data.hasLaneSet()) {
               setChosenField("laneSet_chosen", new ArrayList<Integer>());
               
               com.bah.ode.asn.oss.dsrc.SignalControlZone.Data.LaneSet laneSet = data.getLaneSet();
               for (LaneNumber laneNum : laneSet.elements) {
                  if (laneNum != null && laneNum.byteArrayValue() != null) {
                     this.laneSet_chosen.add((int) laneNum.byteArrayValue()[0]);
                  }
               }
            }
            break;
            
         case com.bah.ode.asn.oss.dsrc.SignalControlZone.Data.zones_chosen:
            if (data.hasZones()) {
               setChosenField("zones_chosen", new ArrayList<Zone>());
               com.bah.ode.asn.oss.dsrc.SignalControlZone.Data.Zones zones = data
                     .getZones();
               for (Sequence_ zone : zones.elements) {
                  if (zone != null) {
                     this.zones_chosen.add(new Zone(zone));
                  }
               } 
            }
            break;
         }
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result
               + ((laneSet_chosen == null) ? 0 : laneSet_chosen.hashCode());
         result = prime * result
               + ((zones_chosen == null) ? 0 : zones_chosen.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Data other = (Data) obj;
         if (!getOuterType().equals(other.getOuterType()))
            return false;
         if (laneSet_chosen == null) {
            if (other.laneSet_chosen != null)
               return false;
         } else if (!laneSet_chosen.equals(other.laneSet_chosen))
            return false;
         if (zones_chosen == null) {
            if (other.zones_chosen != null)
               return false;
         } else if (!zones_chosen.equals(other.zones_chosen))
            return false;
         return true;
      }

      private OdeSignalControlZone getOuterType() {
         return OdeSignalControlZone.this;
      }
      
      
   }

   public String name;
   public String pValue;
   public Data data;
   

   public OdeSignalControlZone(SignalControlZone scz) {
      super();
      if (scz.data != null)
         this.data = new Data(scz.data);
      
      if (scz.hasName())
         this.name = scz.getTypeName();
      
      if (scz.pValue != null)
         this.pValue = CodecUtils.toHex(scz.pValue.byteArrayValue());
   }


   public static List<OdeSignalControlZone> createList(
         ArrayList<SignalControlZone> elements) {
      if (elements == null)
         return null;

      List<OdeSignalControlZone> list = new ArrayList<OdeSignalControlZone>();
      for (SignalControlZone scz : elements) {
         if (scz != null)
            list.add(new OdeSignalControlZone(scz));
      }
      return list;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((data == null) ? 0 : data.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((pValue == null) ? 0 : pValue.hashCode());
      return result;
   }


   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeSignalControlZone other = (OdeSignalControlZone) obj;
      if (data == null) {
         if (other.data != null)
            return false;
      } else if (!data.equals(other.data))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (pValue == null) {
         if (other.pValue != null)
            return false;
      } else if (!pValue.equals(other.pValue))
         return false;
      return true;
   }

   
}
