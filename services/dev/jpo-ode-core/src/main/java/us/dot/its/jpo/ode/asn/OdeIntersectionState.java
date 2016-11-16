package us.dot.its.jpo.ode.asn;

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.IntersectionState;
import com.bah.ode.asn.oss.dsrc.MovementState;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeIntersectionState extends OdeObject {

   private static final long serialVersionUID = -5700990776441110879L;


   private String name;
   private String id;
   private OdeIntersectionStatusObject status;
   private Integer timeMark;
   private Long lanesCnt;
   private List<OdeMovementState> states = new ArrayList<OdeMovementState>();
   private String priority;
   private String preempt;
   
   
   public OdeIntersectionState(IntersectionState intersections) {
      if (intersections.id != null)
         setId(CodecUtils.toHex(intersections.id.byteArrayValue()));
      
      if (intersections.hasLanesCnt())
         setLanesCnt(intersections.getLanesCnt());
      
      if (intersections.hasName())
         setName(intersections.getName().stringValue());
      
      if (intersections.hasPreempt())
         setPreempt(CodecUtils.toHex(intersections.getPreempt().byteArrayValue()));
      
      if (intersections.hasPriority())
         setPriority(CodecUtils.toHex(intersections.getPriority().byteArrayValue()));
      
      if (intersections.states != null) {
         for (MovementState state : intersections.states.elements) {
            this.states.add(new OdeMovementState(state));
         }
      }      
      if (intersections.status != null)
         setStatus(new OdeIntersectionStatusObject(intersections.status));
      
      if (intersections.hasTimeStamp())
         setTimeMark(intersections.getTimeStamp().intValue());
   }
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   public OdeIntersectionStatusObject getStatus() {
      return status;
   }
   public void setStatus(OdeIntersectionStatusObject status) {
      this.status = status;
   }
   public Integer getTimeMark() {
      return timeMark;
   }
   public void setTimeMark(Integer timeMark) {
      this.timeMark = timeMark;
   }
   public Long getLanesCnt() {
      return lanesCnt;
   }
   public void setLanesCnt(Long lanesCnt) {
      this.lanesCnt = lanesCnt;
   }
   public List<OdeMovementState> getStates() {
      return states;
   }
   public void setStates(List<OdeMovementState> states) {
      this.states = states;
   }
   public String getPriority() {
      return priority;
   }
   public void setPriority(String priority) {
      this.priority = priority;
   }
   public String getPreempt() {
      return preempt;
   }
   public void setPreempt(String preempt) {
      this.preempt = preempt;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((lanesCnt == null) ? 0 : lanesCnt.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((preempt == null) ? 0 : preempt.hashCode());
      result = prime * result + ((priority == null) ? 0 : priority.hashCode());
      result = prime * result + ((states == null) ? 0 : states.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((timeMark == null) ? 0 : timeMark.hashCode());
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
      OdeIntersectionState other = (OdeIntersectionState) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (lanesCnt == null) {
         if (other.lanesCnt != null)
            return false;
      } else if (!lanesCnt.equals(other.lanesCnt))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (preempt == null) {
         if (other.preempt != null)
            return false;
      } else if (!preempt.equals(other.preempt))
         return false;
      if (priority == null) {
         if (other.priority != null)
            return false;
      } else if (!priority.equals(other.priority))
         return false;
      if (states == null) {
         if (other.states != null)
            return false;
      } else if (!states.equals(other.states))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (timeMark == null) {
         if (other.timeMark != null)
            return false;
      } else if (!timeMark.equals(other.timeMark))
         return false;
      return true;
   }
   
   
}
