package us.dot.its.jpo.ode.asn;

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.MovementState;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeMovementState extends OdeObject {

   private static final long serialVersionUID = 6714056228131949547L;

   public enum PedestrianSignalState {
      unavailable, 
      stop, 
      caution, 
      walk
   }

   public enum SpecialSignalState {
      unknown, 
      notInUse, 
      arriving, 
      present, 
      departing
   }

   public enum StateConfidence {
      unKnownEstimate, 
      minTime, 
      maxTime, 
      timeLikelyToChange
   }

   public enum PedestrianDetect {
      none, 
      maybe, 
      one, 
      some
   }

   private String movementName;
   private Integer laneCnt;
   private List<Integer> laneSet = new ArrayList<Integer>();
   private Integer currState;
   private PedestrianSignalState pedState;
   private SpecialSignalState specialState;
   private Integer timeToChange;
   private StateConfidence stateConfidence;
   private String yellState;
   private PedestrianSignalState yellPedState;
   private Integer yellTimeToChange;
   private StateConfidence yellStateConfidence;
   private Integer vehicleCount;
   private PedestrianDetect pedDetect;
   private Integer pedCount;

   public OdeMovementState(MovementState state) {
      if (state.hasCurrState())
         setCurrState(state.getCurrState().intValue());
      
      if (state.hasLaneCnt())
         setLaneCnt(state.getLaneCnt().intValue());
      
      if (state.laneSet != null) {
         for (byte ln : state.laneSet.byteArrayValue()) {
            this.laneSet.add((int) ln);
         }
      }

      if (state.hasMovementName())
         setMovementName(state.getMovementName().stringValue());
      
      if (state.hasPedCount())
         setPedCount(state.getPedCount().intValue());
      
      if (state.hasPedDetect())
         setPedDetect(PedestrianDetect.valueOf(state.getPedDetect().name()));
      
      if (state.hasPedState())
         setPedState(PedestrianSignalState.valueOf(state.getPedState().name()));
      
      if (state.hasSpecialState())
         setSpecialState(SpecialSignalState.valueOf(state.getSpecialState().name()));
      
      if (state.hasStateConfidence()) {
         setStateConfidence(correctSpelling(state.getStateConfidence()));
      }
      
      if (state.timeToChange != null)
         setTimeToChange(state.timeToChange.intValue());
      
      if (state.hasVehicleCount())
         setVehicleCount(state.getVehicleCount().intValue());
      
      if (state.hasYellPedState())
         setYellPedState(PedestrianSignalState.valueOf(state.getYellPedState().name()));
      
      if (state.hasYellState())
         setYellState(String.format("%4X", state.getYellState().intValue()));
      
      if (state.hasYellStateConfidence()) {
         setYellStateConfidence(correctSpelling(state.getYellStateConfidence()));
      }
      
      if (state.hasYellTimeToChange())
         setYellTimeToChange(state.getYellTimeToChange().intValue());
   }

   private StateConfidence correctSpelling(com.bah.ode.asn.oss.dsrc.StateConfidence sc) {
      /*
       * Due to a typo in the DSRC schema definition (DSRC_R36_Source.asn),
       * we need to do this.
       */
      if (sc.longValue() == com.bah.ode.asn.oss.dsrc.StateConfidence.timeLikeklyToChange.longValue())
         return StateConfidence.timeLikelyToChange;
      else
         return StateConfidence.valueOf(sc.name());
   }

   public String getMovementName() {
      return movementName;
   }

   public void setMovementName(String movementName) {
      this.movementName = movementName;
   }

   public Integer getLaneCnt() {
      return laneCnt;
   }

   public void setLaneCnt(Integer laneCnt) {
      this.laneCnt = laneCnt;
   }

   public List<Integer> getLaneSet() {
      return laneSet;
   }

   public void setLaneSet(List<Integer> laneSet) {
      this.laneSet = laneSet;
   }

   public Integer getCurrState() {
      return currState;
   }

   public void setCurrState(Integer currState) {
      this.currState = currState;
   }

   public PedestrianSignalState getPedState() {
      return pedState;
   }

   public void setPedState(PedestrianSignalState pedState) {
      this.pedState = pedState;
   }

   public SpecialSignalState getSpecialState() {
      return specialState;
   }

   public void setSpecialState(SpecialSignalState specialState) {
      this.specialState = specialState;
   }

   public Integer getTimeToChange() {
      return timeToChange;
   }

   public void setTimeToChange(Integer timeToChange) {
      this.timeToChange = timeToChange;
   }

   public StateConfidence getStateConfidence() {
      return stateConfidence;
   }

   public void setStateConfidence(StateConfidence stateConfidence) {
      this.stateConfidence = stateConfidence;
   }

   public String getYellState() {
      return yellState;
   }

   public void setYellState(String yellState) {
      this.yellState = yellState;
   }

   public PedestrianSignalState getYellPedState() {
      return yellPedState;
   }

   public void setYellPedState(PedestrianSignalState yellPedState) {
      this.yellPedState = yellPedState;
   }

   public Integer getYellTimeToChange() {
      return yellTimeToChange;
   }

   public void setYellTimeToChange(Integer yellTimeToChange) {
      this.yellTimeToChange = yellTimeToChange;
   }

   public StateConfidence getYellStateConfidence() {
      return yellStateConfidence;
   }

   public void setYellStateConfidence(StateConfidence yellStateConfidence) {
      this.yellStateConfidence = yellStateConfidence;
   }

   public Integer getVehicleCount() {
      return vehicleCount;
   }

   public void setVehicleCount(Integer vehicleCount) {
      this.vehicleCount = vehicleCount;
   }

   public PedestrianDetect getPedDetect() {
      return pedDetect;
   }

   public void setPedDetect(PedestrianDetect pedDetect) {
      this.pedDetect = pedDetect;
   }

   public Integer getPedCount() {
      return pedCount;
   }

   public void setPedCount(Integer pedCount) {
      this.pedCount = pedCount;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((currState == null) ? 0 : currState.hashCode());
      result = prime * result + ((laneCnt == null) ? 0 : laneCnt.hashCode());
      result = prime * result + ((laneSet == null) ? 0 : laneSet.hashCode());
      result = prime * result
            + ((movementName == null) ? 0 : movementName.hashCode());
      result = prime * result + ((pedCount == null) ? 0 : pedCount.hashCode());
      result = prime * result
            + ((pedDetect == null) ? 0 : pedDetect.hashCode());
      result = prime * result + ((pedState == null) ? 0 : pedState.hashCode());
      result = prime * result
            + ((specialState == null) ? 0 : specialState.hashCode());
      result = prime * result
            + ((stateConfidence == null) ? 0 : stateConfidence.hashCode());
      result = prime * result
            + ((timeToChange == null) ? 0 : timeToChange.hashCode());
      result = prime * result
            + ((vehicleCount == null) ? 0 : vehicleCount.hashCode());
      result = prime * result
            + ((yellPedState == null) ? 0 : yellPedState.hashCode());
      result = prime * result
            + ((yellState == null) ? 0 : yellState.hashCode());
      result = prime * result + ((yellStateConfidence == null) ? 0
            : yellStateConfidence.hashCode());
      result = prime * result
            + ((yellTimeToChange == null) ? 0 : yellTimeToChange.hashCode());
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
      OdeMovementState other = (OdeMovementState) obj;
      if (currState == null) {
         if (other.currState != null)
            return false;
      } else if (!currState.equals(other.currState))
         return false;
      if (laneCnt == null) {
         if (other.laneCnt != null)
            return false;
      } else if (!laneCnt.equals(other.laneCnt))
         return false;
      if (laneSet == null) {
         if (other.laneSet != null)
            return false;
      } else if (!laneSet.equals(other.laneSet))
         return false;
      if (movementName == null) {
         if (other.movementName != null)
            return false;
      } else if (!movementName.equals(other.movementName))
         return false;
      if (pedCount == null) {
         if (other.pedCount != null)
            return false;
      } else if (!pedCount.equals(other.pedCount))
         return false;
      if (pedDetect != other.pedDetect)
         return false;
      if (pedState != other.pedState)
         return false;
      if (specialState != other.specialState)
         return false;
      if (stateConfidence != other.stateConfidence)
         return false;
      if (timeToChange == null) {
         if (other.timeToChange != null)
            return false;
      } else if (!timeToChange.equals(other.timeToChange))
         return false;
      if (vehicleCount == null) {
         if (other.vehicleCount != null)
            return false;
      } else if (!vehicleCount.equals(other.vehicleCount))
         return false;
      if (yellPedState != other.yellPedState)
         return false;
      if (yellState == null) {
         if (other.yellState != null)
            return false;
      } else if (!yellState.equals(other.yellState))
         return false;
      if (yellStateConfidence != other.yellStateConfidence)
         return false;
      if (yellTimeToChange == null) {
         if (other.yellTimeToChange != null)
            return false;
      } else if (!yellTimeToChange.equals(other.yellTimeToChange))
         return false;
      return true;
   }
   
   
}
