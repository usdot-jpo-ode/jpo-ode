package us.dot.its.jpo.ode.dds;


public class DdsQryRequest extends DdsRequest {
   private static final long serialVersionUID = 2425686751232706772L;

   private String systemQueryName;
   private String startDate;
   private String startDateOperator = "GTE";
   private String endDate;
   private String endDateOperator = "LTE";
   private String orderByField = "createdAt";
   private int orderByOrder = 1;
   private int skip = 0;
   private int limit = -1;

   public String getSystemQueryName() {
      return systemQueryName;
   }

   public DdsQryRequest setSystemQueryName(String systemQueryName) {
      this.systemQueryName = systemQueryName;
      return this;
   }

   public String getStartDate() {
      return startDate;
   }

   public DdsQryRequest setStartDate(String startDate) {
      this.startDate = startDate;
      return this;
   }

   public String getStartDateOperator() {
      return startDateOperator;
   }

   public DdsQryRequest setStartDateOperator(String startDateOperator) {
      this.startDateOperator = startDateOperator;
      return this;
   }

   public String getEndDate() {
      return endDate;
   }

   public DdsQryRequest setEndDate(String endDate) {
      this.endDate = endDate;
      return this;
   }

   public String getEndDateOperator() {
      return endDateOperator;
   }

   public DdsQryRequest setEndDateOperator(String endDateOperator) {
      this.endDateOperator = endDateOperator;
      return this;
   }

   public String getOrderByField() {
      return orderByField;
   }

   public DdsQryRequest setOrderByField(String orderByField) {
      this.orderByField = orderByField;
      return this;
   }

   public int getOrderByOrder() {
      return orderByOrder;
   }

   public DdsQryRequest setOrderByOrder(int orderByOrder) {
      this.orderByOrder = orderByOrder;
      return this;
   }

   public int getSkip() {
      return skip;
   }

   public DdsQryRequest setSkip(int skip) {
      this.skip = skip;
      return this;
   }

   public int getLimit() {
      return limit;
   }

   public DdsQryRequest setLimit(int limit) {
      this.limit = limit;
      return this;
   }

   @Override
   public String toString() {
      return "QUERY:" + this.toJson(false);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
      result = prime * result
            + ((endDateOperator == null) ? 0 : endDateOperator.hashCode());
      result = prime * result + limit;
      result = prime * result
            + ((orderByField == null) ? 0 : orderByField.hashCode());
      result = prime * result + orderByOrder;
      result = prime * result + skip;
      result = prime * result
            + ((startDate == null) ? 0 : startDate.hashCode());
      result = prime * result
            + ((startDateOperator == null) ? 0 : startDateOperator.hashCode());
      result = prime * result
            + ((systemQueryName == null) ? 0 : systemQueryName.hashCode());
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
      DdsQryRequest other = (DdsQryRequest) obj;
      if (endDate == null) {
         if (other.endDate != null)
            return false;
      } else if (!endDate.equals(other.endDate))
         return false;
      if (endDateOperator == null) {
         if (other.endDateOperator != null)
            return false;
      } else if (!endDateOperator.equals(other.endDateOperator))
         return false;
      if (limit != other.limit)
         return false;
      if (orderByField == null) {
         if (other.orderByField != null)
            return false;
      } else if (!orderByField.equals(other.orderByField))
         return false;
      if (orderByOrder != other.orderByOrder)
         return false;
      if (skip != other.skip)
         return false;
      if (startDate == null) {
         if (other.startDate != null)
            return false;
      } else if (!startDate.equals(other.startDate))
         return false;
      if (startDateOperator == null) {
         if (other.startDateOperator != null)
            return false;
      } else if (!startDateOperator.equals(other.startDateOperator))
         return false;
      if (systemQueryName == null) {
         if (other.systemQueryName != null)
            return false;
      } else if (!systemQueryName.equals(other.systemQueryName))
         return false;
      return true;
   }

}
