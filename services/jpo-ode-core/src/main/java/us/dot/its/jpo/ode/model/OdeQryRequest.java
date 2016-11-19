package us.dot.its.jpo.ode.model;


public class OdeQryRequest extends OdeRequest {
   private static final long serialVersionUID = 2596739570485872508L;

   private String startDate;
   private String endDate;
   private Integer skip;
   private Integer limit;
   
   
   
   public String getStartDate() {
      return startDate;
   }

   public OdeQryRequest setStartDate(String startDate) {
      this.startDate = startDate;
      return this;
   }

   public String getEndDate() {
      return endDate;
   }

   public OdeQryRequest setEndDate(String endDate) {
      this.endDate = endDate;
      return this;
   }

   public Integer getSkip() {
      return skip;
   }

   public OdeQryRequest setSkip(Integer skip) {
      this.skip = skip;
      return this;
   }

   public Integer getLimit() {
      return limit;
   }

   public OdeQryRequest setLimit(Integer limit) {
      this.limit = limit;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
      result = prime * result + ((limit == null) ? 0 : limit.hashCode());
      result = prime * result + ((skip == null) ? 0 : skip.hashCode());
      result = prime * result
            + ((startDate == null) ? 0 : startDate.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeQryRequest other = (OdeQryRequest) obj;
      if (endDate == null) {
         if (other.endDate != null)
            return false;
      } else if (!endDate.equals(other.endDate))
         return false;
      if (limit == null) {
         if (other.limit != null)
            return false;
      } else if (!limit.equals(other.limit))
         return false;
      if (skip == null) {
         if (other.skip != null)
            return false;
      } else if (!skip.equals(other.skip))
         return false;
      if (startDate == null) {
         if (other.startDate != null)
            return false;
      } else if (!startDate.equals(other.startDate))
         return false;
      return true;
   }


}
