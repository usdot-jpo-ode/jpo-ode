package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.GenericSnmp.SNMP;
import us.dot.its.jpo.ode.plugin.RoadSignUnit.RSU;
import us.dot.its.jpo.ode.plugin.pdm.PdmVehicleStatusEntry;

public class J2735ProbeDataManagement extends OdeObject {

   private static final long serialVersionUID = 2154315328067723844L;

   private ODE ode;
   private RSU[] rsu;
   private SNMP snmp;

   public static class ODE {
      private int version = 1;

      public int getVersion() {
         return version;
      }

      public void setVersion(int version) {
         this.version = version;
      }
   }

   public static class PdmParameters {
      private int rsuPDMSampleStart; // 15628.4.1.200.1 INTEGER(0..255)
      private int rsuPDMSampleEnd; // 15628.4.1.200.2 INTEGER(0..255)
      private int rsuPDMDirections; // 15628.4.1.200.3 INTEGER(0..65535):16-Bit
                                   // String
      private int rsuPDMTermChoice; // 15628.4.1.200.4 INTEGER:
                                   // 1(Time),2(Distance)
      private int rsuPDMTermTime; // 15628.4.1.200.5 INTEGER(1..1800)
      private int rsuPDMTermDistance;// 15628.4.1.200.6 INTEGER(1..30000),
      private int rsuPDMSnapshotChoice; // 15628.4.1.200.7 INTEGER:
                                       // 1(Time),2(Distance)
      private int rsuPDMMinSnapshotTime; // 15628.4.1.200.9 INTEGER (0..61) --
                                        // units of seconds
      private int rsuPDMMaxSnapshotTime; // 15628.4.1.200.10 INTEGER (0..61) --
                                        // units of seconds
      private int rsuPDMMinSnapshotDistance; // 15628.4.1.200.11 INTEGER
                                            // (0..1023) -- Units of 1.00 meters
      private int rsuPDMMaxSnapshotDistance; // 15628.4.1.200.12 INTEGER
                                            // (0..1023) -- Units of 1.00 meters
      private int rsuPDMSnapshotMinSpeed; // 15628.4.1.200.13 INTEGER (0..31) --
                                         // Units of 1.00 m/s
      private int rsuPDMSnapshotMaxSpeed; // 15628.4.1.200.14 INTEGER (0..31) --
                                         // Units of 1.00 m/s
      private int rsuPDMTxInterval; // 15628.4.1.200.15 INTEGER (0..61) -- units
                                   // of seconds

      private List<PdmVehicleStatusEntry> rsuPDMVSReqListTable; // 15628.4.1.200.16

      public int getRsuPDMSampleStart() {
         return rsuPDMSampleStart;
      }

      public void setRsuPDMSampleStart(int rsuPDMSampleStart) {
         this.rsuPDMSampleStart = rsuPDMSampleStart;
      }

      public int getRsuPDMSampleEnd() {
         return rsuPDMSampleEnd;
      }

      public void setRsuPDMSampleEnd(int rsuPDMSampleEnd) {
         this.rsuPDMSampleEnd = rsuPDMSampleEnd;
      }

      public int getRsuPDMDirections() {
         return rsuPDMDirections;
      }

      public void setRsuPDMDirections(int rsuPDMDirections) {
         this.rsuPDMDirections = rsuPDMDirections;
      }

      public int getRsuPDMTermChoice() {
         return rsuPDMTermChoice;
      }

      public void setRsuPDMTermChoice(int rsuPDMTermChoice) {
         this.rsuPDMTermChoice = rsuPDMTermChoice;
      }

      public int getRsuPDMTermTime() {
         return rsuPDMTermTime;
      }

      public void setRsuPDMTermTime(int rsuPDMTermTime) {
         this.rsuPDMTermTime = rsuPDMTermTime;
      }

      public int getRsuPDMTermDistance() {
         return rsuPDMTermDistance;
      }

      public void setRsuPDMTermDistance(int rsuPDMTermDistance) {
         this.rsuPDMTermDistance = rsuPDMTermDistance;
      }

      public int getRsuPDMSnapshotChoice() {
         return rsuPDMSnapshotChoice;
      }

      public void setRsuPDMSnapshotChoice(int rsuPDMSnapshotChoice) {
         this.rsuPDMSnapshotChoice = rsuPDMSnapshotChoice;
      }

      public int getRsuPDMMinSnapshotTime() {
         return rsuPDMMinSnapshotTime;
      }

      public void setRsuPDMMinSnapshotTime(int rsuPDMMinSnapshotTime) {
         this.rsuPDMMinSnapshotTime = rsuPDMMinSnapshotTime;
      }

      public int getRsuPDMMaxSnapshotTime() {
         return rsuPDMMaxSnapshotTime;
      }

      public void setRsuPDMMaxSnapshotTime(int rsuPDMMaxSnapshotTime) {
         this.rsuPDMMaxSnapshotTime = rsuPDMMaxSnapshotTime;
      }

      public int getRsuPDMMinSnapshotDistance() {
         return rsuPDMMinSnapshotDistance;
      }

      public void setRsuPDMMinSnapshotDistance(int rsuPDMMinSnapshotDistance) {
         this.rsuPDMMinSnapshotDistance = rsuPDMMinSnapshotDistance;
      }

      public int getRsuPDMMaxSnapshotDistance() {
         return rsuPDMMaxSnapshotDistance;
      }

      public void setRsuPDMMaxSnapshotDistance(int rsuPDMMaxSnapshotDistance) {
         this.rsuPDMMaxSnapshotDistance = rsuPDMMaxSnapshotDistance;
      }

      public int getRsuPDMSnapshotMinSpeed() {
         return rsuPDMSnapshotMinSpeed;
      }

      public void setRsuPDMSnapshotMinSpeed(int rsuPDMSnapshotMinSpeed) {
         this.rsuPDMSnapshotMinSpeed = rsuPDMSnapshotMinSpeed;
      }

      public int getRsuPDMSnapshotMaxSpeed() {
         return rsuPDMSnapshotMaxSpeed;
      }

      public void setRsuPDMSnapshotMaxSpeed(int rsuPDMSnapshotMaxSpeed) {
         this.rsuPDMSnapshotMaxSpeed = rsuPDMSnapshotMaxSpeed;
      }

      public int getRsuPDMTxInterval() {
         return rsuPDMTxInterval;
      }

      public void setRsuPDMTxInterval(int rsuPDMTxInterval) {
         this.rsuPDMTxInterval = rsuPDMTxInterval;
      }

      public List<PdmVehicleStatusEntry> getRsuPDMVSReqListTable() {
         return rsuPDMVSReqListTable;
      }

      public void setRsuPDMVSReqListTable(List<PdmVehicleStatusEntry> rsuPDMVSReqListTable) {
         this.rsuPDMVSReqListTable = rsuPDMVSReqListTable;
      }
   }

   public static class SnapshotTime {
      private int speed1; // 0-31
      private int second1; // 0-61
      private int speed2; // 0-31
      private int second2; // 0-61
      public int getSpeed1() {
         return speed1;
      }
      public void setSpeed1(int speed1) {
         this.speed1 = speed1;
      }
      public int getSecond1() {
         return second1;
      }
      public void setSecond1(int second1) {
         this.second1 = second1;
      }
      public int getSpeed2() {
         return speed2;
      }
      public void setSpeed2(int speed2) {
         this.speed2 = speed2;
      }
      public int getSecond2() {
         return second2;
      }
      public void setSecond2(int second2) {
         this.second2 = second2;
      }
   }

   public static class SnapshotDistance {
      private int distance1; // 0-1023
      private int speed1; // 0-31
      private int distance2; // 0-1023
      private int speed2; // 0-31
      public int getDistance1() {
         return distance1;
      }
      public void setDistance1(int distance1) {
         this.distance1 = distance1;
      }
      public int getSpeed1() {
         return speed1;
      }
      public void setSpeed1(int speed1) {
         this.speed1 = speed1;
      }
      public int getDistance2() {
         return distance2;
      }
      public void setDistance2(int distance2) {
         this.distance2 = distance2;
      }
      public int getSpeed2() {
         return speed2;
      }
      public void setSpeed2(int speed2) {
         this.speed2 = speed2;
      }
   }

   public static class DataElements {
      private int deviceTag; // Enumeration 0-28
      private int subType; // 0-15
      private int lessThan; // -32767-32767
      private int moreThan; // -32767-32767
      private boolean sendAll; // true or false
      public int getDeviceTag() {
         return deviceTag;
      }
      public void setDeviceTag(int deviceTag) {
         this.deviceTag = deviceTag;
      }
      public int getSubType() {
         return subType;
      }
      public void setSubType(int subType) {
         this.subType = subType;
      }
      public int getLessThan() {
         return lessThan;
      }
      public void setLessThan(int lessThan) {
         this.lessThan = lessThan;
      }
      public int getMoreThan() {
         return moreThan;
      }
      public void setMoreThan(int moreThan) {
         this.moreThan = moreThan;
      }
      public boolean isSendAll() {
         return sendAll;
      }
      public void setSendAll(boolean sendAll) {
         this.sendAll = sendAll;
      }
   }

   public ODE getOde() {
      return ode;
   }

   public void setOde(ODE ode) {
      this.ode = ode;
   }
   public SNMP getSnmp() {
      return snmp;
   }

   public void setSnmp(SNMP snmp) {
      this.snmp = snmp;
   }

   public RSU[] getRsu() {
      return rsu;
   }

   public void setRsu(RSU[] rsu) {
      this.rsu = rsu;
   }
}
