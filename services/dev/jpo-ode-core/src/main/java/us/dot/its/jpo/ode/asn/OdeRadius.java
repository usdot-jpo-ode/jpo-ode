package us.dot.its.jpo.ode.asn;

import java.math.BigDecimal;

import com.bah.ode.asn.oss.dsrc.Circle.Raduis;

public class OdeRadius extends OdeChoice {

   private static final long serialVersionUID = -1615313015072982197L;

   public Integer km_chosen;
   public Integer miles_chosen;
   public BigDecimal cm_chosen;
   
   public OdeRadius(Raduis radius) {
      super();

      int flag = radius.getChosenFlag();
      switch (flag) {
      case Raduis.km_chosen:
         if (radius.hasKm())
            setChosenField("km_chosen", Integer.valueOf(radius.getKm().intValue()));
         break;
      case Raduis.miles_chosen:
         if (radius.hasMiles())
            setChosenField("miles_chosen", Integer.valueOf(radius.getMiles().intValue()));
         break;
      case Raduis.radiusSteps_chosen:
         if (radius.hasRadiusSteps())
            setChosenField("cm_chosen", BigDecimal.valueOf(radius.getRadiusSteps().intValue() * 2.5));
         break;
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((cm_chosen == null) ? 0 : cm_chosen.hashCode());
      result = prime * result
            + ((km_chosen == null) ? 0 : km_chosen.hashCode());
      result = prime * result
            + ((miles_chosen == null) ? 0 : miles_chosen.hashCode());
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
      OdeRadius other = (OdeRadius) obj;
      if (cm_chosen == null) {
         if (other.cm_chosen != null)
            return false;
      } else if (!cm_chosen.equals(other.cm_chosen))
         return false;
      if (km_chosen == null) {
         if (other.km_chosen != null)
            return false;
      } else if (!km_chosen.equals(other.km_chosen))
         return false;
      if (miles_chosen == null) {
         if (other.miles_chosen != null)
            return false;
      } else if (!miles_chosen.equals(other.miles_chosen))
         return false;
      return true;
   }

   
}