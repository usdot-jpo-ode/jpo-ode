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
      public String timeStamp; //0-527040
      public int sampleStart; //0-255
      public int sampleEnd; //0-255
      public String heading; //16 bit string
      public String term; //Choice of termtime or termDistance
      public String snapshot; //Choice of SnapshotTime or SnapshotDistance
      public int second; //0-61
      public dataElements[] dataList; //1-32
   }
   
   public static class dataElements {
      
   }
}
