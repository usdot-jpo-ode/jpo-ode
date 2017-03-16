package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData.ODE;

public class J2735ProbeDataManagement extends OdeObject {

   private static final long serialVersionUID = 2154315328067723844L;

   public ODE ode;
   public PDM pdm;

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
}
