package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.pdm.PdmVehicleStatusEntry;

public class J2735ProbeDataManagement extends OdeObject {

   private static final long serialVersionUID = 2154315328067723844L;

   public ODE ode;
   public PDM pdm;
   public SNMP snmp;

   public static class ODE {
      public int version = 1;
   }

   public static class PDM {
      public String timeStamp; // 0-527040
      public int sampleStart; // 0-255
      public int sampleEnd; // 0-255
      public String heading; // 16 bit string
      public String term; // Choice of termtime or termDistance
      public int termTime; // 1-1800
      public int termDistance; // 1-30000
      public String snapshot; // Choice of SnapshotTime or SnapshotDistance
      public SnapshotTime sTime;
      public SnapshotDistance sDistance;
      public int second; // 0-61
      public DataElements[] dataList; // 1-32
      public PdmParameters params;
   }

   public static class SnapshotTime {
      public int speed1; // 0-31
      public int second1; // 0-61
      public int speed2; // 0-31
      public int second2; // 0-61
   }

   public static class SnapshotDistance {
      public int distance1; // 0-1023
      public int speed1; // 0-31
      public int distance2; // 0-1023
      public int speed2; // 0-31
   }

   public static class DataElements {
      public int deviceTag; // Enumeration 0-28
      public int subType; // 0-15
      public int lessThan; // -32767-32767
      public int moreThan; // -32767-32767
      public boolean sendAll; // true or false
   }

   public static class PdmParameters {
      public int rsuPDMSampleStart; // 15628.4.1.200.1 INTEGER(0..255)
      public int rsuPDMSampleEnd; // 15628.4.1.200.2 INTEGER(0..255)
      public int rsuPDMDirections; // 15628.4.1.200.3 INTEGER(0..65535):16-Bit
                                   // String
      public int rsuPDMTermChoice; // 15628.4.1.200.4 INTEGER:
                                   // 1(Time),2(Distance)
      public int rsuPDMTermTime; // 15628.4.1.200.5 INTEGER(1..1800)
      public int rsuPDMTermDistance;// 15628.4.1.200.6 INTEGER(1..30000),
      public int rsuPDMSnapshotChoice; // 15628.4.1.200.7 INTEGER:
                                       // 1(Time),2(Distance)
      public int rsuPDMMinSnapshotTime; // 15628.4.1.200.9 INTEGER (0..61) --
                                        // units of seconds
      public int rsuPDMMaxSnapshotTime; // 15628.4.1.200.10 INTEGER (0..61) --
                                        // units of seconds
      public int rsuPDMMinSnapshotDistnace; // 15628.4.1.200.11 INTEGER
                                            // (0..1023) -- Units of 1.00 meters
      public int rsuPDMMaxSnapshotDistnace; // 15628.4.1.200.12 INTEGER
                                            // (0..1023) -- Units of 1.00 meters
      public int rsuPDMSnapshotMinSpeed; // 15628.4.1.200.13 INTEGER (0..31) --
                                         // Units of 1.00 m/s
      public int rsuPDMSnapshotMaxSpeed; // 15628.4.1.200.14 INTEGER (0..31) --
                                         // Units of 1.00 m/s
      public int rsuPDMTxInterval; // 15628.4.1.200.15 INTEGER (0..61) -- units
                                   // of seconds

      public List<PdmVehicleStatusEntry> rsuPDMVSReqListTable; // 15628.4.1.200.16
   }

   public static class SNMP {
      public String rsuid;
      public int msgid;
      public int mode;
      public int channel;
      public int interval;
      public String deliverystart;
      public String deliverystop;
      public int enable;
      public int status;
   }
}
