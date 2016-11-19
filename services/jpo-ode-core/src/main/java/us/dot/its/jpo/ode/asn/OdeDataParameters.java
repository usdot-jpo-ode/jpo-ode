package us.dot.its.jpo.ode.asn;

import us.dot.its.jpo.ode.j2735.dsrc.DataParameters;
import us.dot.its.jpo.ode.model.OdeObject;

public class OdeDataParameters extends OdeObject {

   private static final long serialVersionUID = 1172060149473804017L;

   private String processMethod; 
   private String processAgency; 
   private String lastCheckedDate; 
   private String geiodUsed;
   
   public OdeDataParameters(DataParameters dataParameters) {
      if (dataParameters.hasGeoidUsed())
         setGeiodUsed(dataParameters.getGeoidUsed().stringValue());
      
      if (dataParameters.hasLastCheckedDate())
         setLastCheckedDate(dataParameters.getLastCheckedDate().stringValue());
      
      if (dataParameters.hasProcessAgency())
         setProcessAgency(dataParameters.getProcessAgency().stringValue());
      
      if (dataParameters.hasProcessMethod())
         setProcessMethod(dataParameters.getProcessMethod().stringValue());
   }
   
   
   public String getProcessMethod() {
      return processMethod;
   }


   public void setProcessMethod(String processMethod) {
      this.processMethod = processMethod;
   }


   public String getProcessAgency() {
      return processAgency;
   }


   public void setProcessAgency(String processAgency) {
      this.processAgency = processAgency;
   }


   public String getLastCheckedDate() {
      return lastCheckedDate;
   }


   public void setLastCheckedDate(String lastCheckedDate) {
      this.lastCheckedDate = lastCheckedDate;
   }


   public String getGeiodUsed() {
      return geiodUsed;
   }


   public void setGeiodUsed(String geiodUsed) {
      this.geiodUsed = geiodUsed;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((geiodUsed == null) ? 0 : geiodUsed.hashCode());
      result = prime * result
            + ((lastCheckedDate == null) ? 0 : lastCheckedDate.hashCode());
      result = prime * result
            + ((processAgency == null) ? 0 : processAgency.hashCode());
      result = prime * result
            + ((processMethod == null) ? 0 : processMethod.hashCode());
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
      OdeDataParameters other = (OdeDataParameters) obj;
      if (geiodUsed == null) {
         if (other.geiodUsed != null)
            return false;
      } else if (!geiodUsed.equals(other.geiodUsed))
         return false;
      if (lastCheckedDate == null) {
         if (other.lastCheckedDate != null)
            return false;
      } else if (!lastCheckedDate.equals(other.lastCheckedDate))
         return false;
      if (processAgency == null) {
         if (other.processAgency != null)
            return false;
      } else if (!processAgency.equals(other.processAgency))
         return false;
      if (processMethod == null) {
         if (other.processMethod != null)
            return false;
      } else if (!processMethod.equals(other.processMethod))
         return false;
      return true;
   }
   
   
}
